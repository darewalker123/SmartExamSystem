const api = async (url, options = {}) => {
  const response = await fetch(url, { credentials: 'same-origin', ...options });
  const data = await response.json().catch(() => ({}));
  if (!response.ok || data.success === false) throw new Error(data.message || 'Request failed');
  return data;
};

const formBody = (form) => new URLSearchParams(new FormData(form));
const $ = (selector) => document.querySelector(selector);
const byId = (id) => document.getElementById(id);

async function currentUser(requiredRole) {
  try {
    const { user } = await api('../api/auth/me');
    if (requiredRole && user.role !== requiredRole) {
      location.href = `dashboard.html`;
      return null;
    }
    const name = byId('currentUser');
    if (name) name.textContent = `${user.name} (${user.role})`;
    return user;
  } catch {
    location.href = 'login.html';
    return null;
  }
}

async function logout() {
  await api('../api/auth/logout', { method: 'POST' });
  location.href = 'login.html';
}

function bindAuthPage(kind) {
  const form = $('form');
  form.addEventListener('submit', async (event) => {
    event.preventDefault();
    const message = byId('message');
    message.textContent = '';
    try {
      const data = await api(`../api/auth/${kind}`, { method: 'POST', body: formBody(form) });
      routeForRole(data.user.role);
    } catch (error) {
      message.textContent = error.message;
    }
  });
}

function routeForRole(role) {
  if (role === 'admin') location.href = 'admin.html';
  else if (role === 'teacher') location.href = 'teacher.html';
  else location.href = 'dashboard.html';
}

async function loadStudentDashboard() {
  await currentUser('student');
  const exams = (await api('../api/exams/available')).exams;
  byId('examList').innerHTML = exams.map(exam => `
    <article class="card">
      <h3>${escapeHtml(exam.title)}</h3>
      <p>${escapeHtml(exam.subject)} · ${exam.durationMinutes} minutes</p>
      <p>${escapeHtml(exam.description || '')}</p>
      <a class="btn" href="exam.html?id=${exam.examId}">Start Exam</a>
      <a class="btn secondary" href="result.html?examId=${exam.examId}">Leaderboard</a>
    </article>`).join('') || '<p>No active exams are available.</p>';
  const results = (await api('../api/results/student')).results;
  renderResults('resultList', results);
}

let examState = { exam: null, questions: [], answers: {}, current: 0, startedAt: Date.now(), timerId: null };

async function loadExamPage() {
  await currentUser('student');
  const examId = new URLSearchParams(location.search).get('id');
  const examData = await api(`../api/exams/${examId}`);
  const questionData = await api(`../api/exams/${examId}/questions`);
  examState.exam = examData.exam;
  examState.questions = questionData.questions;
  byId('examTitle').textContent = examState.exam.title;
  byId('examMeta').textContent = `${examState.exam.subject} · ${examState.exam.durationMinutes} minutes`;
  renderQuestion();
  renderQuestionNav();
  startTimer(examState.exam.durationMinutes * 60);
}

function renderQuestion() {
  const q = examState.questions[examState.current];
  if (!q) {
    byId('questionBox').innerHTML = '<p>This exam has no questions.</p>';
    return;
  }
  byId('questionBox').innerHTML = `
    <h2>Question ${examState.current + 1}</h2>
    <p>${escapeHtml(q.questionText)}</p>
    ${['A', 'B', 'C', 'D'].map(letter => `
      <label class="option">
        <input type="radio" name="answer" value="${letter}" ${examState.answers[q.questionId] === letter ? 'checked' : ''}>
        ${letter}. ${escapeHtml(q[`option${letter}`])}
      </label>`).join('')}`;
  document.querySelectorAll('input[name="answer"]').forEach(input => {
    input.addEventListener('change', () => {
      examState.answers[q.questionId] = input.value;
      renderQuestionNav();
    });
  });
}

function renderQuestionNav() {
  byId('questionNav').innerHTML = examState.questions.map((q, index) => `
    <button class="dot ${index === examState.current ? 'active' : ''} ${examState.answers[q.questionId] ? 'answered' : ''}" onclick="goQuestion(${index})">${index + 1}</button>
  `).join('');
}

function goQuestion(index) {
  examState.current = index;
  renderQuestion();
  renderQuestionNav();
}

function nextQuestion(step) {
  examState.current = Math.min(Math.max(examState.current + step, 0), examState.questions.length - 1);
  renderQuestion();
  renderQuestionNav();
}

function startTimer(seconds) {
  const timer = byId('timer');
  examState.timerId = setInterval(() => {
    const elapsed = Math.floor((Date.now() - examState.startedAt) / 1000);
    const remaining = Math.max(0, seconds - elapsed);
    const mins = String(Math.floor(remaining / 60)).padStart(2, '0');
    const secs = String(remaining % 60).padStart(2, '0');
    timer.textContent = `${mins}:${secs}`;
    if (remaining <= 0) submitExam();
  }, 1000);
}

async function submitExam() {
  clearInterval(examState.timerId);
  const body = new URLSearchParams();
  body.set('examId', examState.exam.examId);
  body.set('timeTakenSeconds', Math.floor((Date.now() - examState.startedAt) / 1000));
  Object.entries(examState.answers).forEach(([questionId, answer]) => body.set(`answer_${questionId}`, answer));
  try {
    const { result } = await api('../api/results/submit', { method: 'POST', body });
    sessionStorage.setItem('lastResult', JSON.stringify(result));
    location.href = `result.html?resultId=${result.resultId}&examId=${result.examId}`;
  } catch (error) {
    byId('message').textContent = error.message;
  }
}

async function loadResultPage() {
  await currentUser();
  const params = new URLSearchParams(location.search);
  const examId = params.get('examId');
  const last = JSON.parse(sessionStorage.getItem('lastResult') || 'null');
  if (last) {
    byId('latestResult').innerHTML = `<h2>${escapeHtml(last.examTitle || 'Submitted Exam')}</h2><p class="timer">${last.score}/${last.totalMarks}</p>`;
  }
  if (examId) {
    const results = (await api(`../api/exams/${examId}/leaderboard`)).results;
    renderResults('leaderboard', results);
  }
}

async function loadTeacherPage() {
  await currentUser('teacher');
  await refreshTeacherData();
  byId('examForm').addEventListener('submit', async (event) => {
    event.preventDefault();
    await api('../api/exams/create', { method: 'POST', body: formBody(event.target) });
    event.target.reset();
    await refreshTeacherData();
  });
  byId('questionForm').addEventListener('submit', async (event) => {
    event.preventDefault();
    await api('../api/exams/question/create', { method: 'POST', body: formBody(event.target) });
    event.target.reset();
    await refreshTeacherData();
  });
}

async function refreshTeacherData() {
  const exams = (await api('../api/exams/teacher')).exams;
  const options = exams.map(exam => `<option value="${exam.examId}">${escapeHtml(exam.title)}</option>`).join('');
  byId('questionExamId').innerHTML = options;
  byId('teacherExams').innerHTML = exams.map(exam => `<article class="card"><h3>${escapeHtml(exam.title)}</h3><p>${escapeHtml(exam.subject)} · ${exam.durationMinutes} minutes</p><button class="secondary" onclick="loadExamResults(${exam.examId})">View Results</button></article>`).join('');
}

async function loadExamResults(examId) {
  const results = (await api(`../api/exams/${examId}/results`)).results;
  renderResults('teacherResults', results);
}

async function loadAdminPage() {
  await currentUser('admin');
  await refreshAdmin();
}

async function refreshAdmin() {
  const users = (await api('../api/admin/users')).users;
  byId('users').innerHTML = `<table class="table"><thead><tr><th>Name</th><th>Email</th><th>Role</th><th></th></tr></thead><tbody>${users.map(user => `
    <tr>
      <td>${escapeHtml(user.name)}</td>
      <td>${escapeHtml(user.email)}</td>
      <td><select onchange="changeRole(${user.id}, this.value)">${['admin', 'teacher', 'student'].map(role => `<option ${role === user.role ? 'selected' : ''}>${role}</option>`).join('')}</select></td>
      <td><button class="danger" onclick="deleteUser(${user.id})">Delete</button></td>
    </tr>`).join('')}</tbody></table>`;
  const exams = (await api('../api/exams/all')).exams;
  byId('adminExams').innerHTML = exams.map(exam => `<article class="card"><h3>${escapeHtml(exam.title)}</h3><p>${escapeHtml(exam.subject)} · ${exam.active ? 'Active' : 'Inactive'}</p></article>`).join('');
}

async function changeRole(userId, role) {
  await api('../api/admin/user/role', { method: 'POST', body: new URLSearchParams({ userId, role }) });
  await refreshAdmin();
}

async function deleteUser(userId) {
  if (!confirm('Delete this user?')) return;
  await api('../api/admin/user/delete', { method: 'POST', body: new URLSearchParams({ userId }) });
  await refreshAdmin();
}

function renderResults(targetId, results) {
  byId(targetId).innerHTML = `<table class="table"><thead><tr><th>Student</th><th>Exam</th><th>Score</th><th>Time</th><th>Submitted</th></tr></thead><tbody>${results.map(r => `
    <tr><td>${escapeHtml(r.studentName || '')}</td><td>${escapeHtml(r.examTitle || '')}</td><td>${r.score}/${r.totalMarks}</td><td>${Math.round((r.timeTakenSeconds || 0) / 60)} min</td><td>${escapeHtml(r.submittedAt || '')}</td></tr>`).join('')}</tbody></table>`;
}

function escapeHtml(value) {
  return String(value ?? '').replace(/[&<>"']/g, char => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[char]));
}
