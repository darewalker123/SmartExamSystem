package com.smartexam.service;

import com.smartexam.dao.ExamDAO;
import com.smartexam.dao.QuestionDAO;
import com.smartexam.dao.ResultDAO;
import com.smartexam.model.Exam;
import com.smartexam.model.Question;
import com.smartexam.model.Result;
import com.smartexam.util.DatabaseUtil;
import com.smartexam.util.ValidationUtil;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ExamService {
    private final ExamDAO examDAO = new ExamDAO();
    private final QuestionDAO questionDAO = new QuestionDAO();
    private final ResultDAO resultDAO = new ResultDAO();

    public List<Exam> availableExams() throws ServiceException {
        try {
            return examDAO.findActive();
        } catch (SQLException e) {
            throw new ServiceException("Unable to load available exams.", e);
        }
    }

    public List<Exam> examsForTeacher(int teacherId) throws ServiceException {
        try {
            return examDAO.findByTeacher(teacherId);
        } catch (SQLException e) {
            throw new ServiceException("Unable to load teacher exams.", e);
        }
    }

    public List<Exam> allExams() throws ServiceException {
        try {
            return examDAO.findAll();
        } catch (SQLException e) {
            throw new ServiceException("Unable to load exams.", e);
        }
    }

    public Exam getExam(int examId) throws ServiceException {
        try {
            return examDAO.findById(examId).orElseThrow(() -> new ServiceException("Exam not found."));
        } catch (SQLException e) {
            throw new ServiceException("Unable to load exam.", e);
        }
    }

    public int createExam(String title, String subject, String description, int durationMinutes, int teacherId, boolean active) throws ServiceException {
        if (ValidationUtil.isBlank(title) || ValidationUtil.isBlank(subject)) {
            throw new ServiceException("Title and subject are required.");
        }
        if (durationMinutes < 1 || durationMinutes > 360) {
            throw new ServiceException("Duration must be between 1 and 360 minutes.");
        }
        try {
            Exam exam = new Exam();
            exam.setTitle(title.trim());
            exam.setSubject(subject.trim());
            exam.setDescription(description == null ? "" : description.trim());
            exam.setDurationMinutes(durationMinutes);
            exam.setCreatedBy(teacherId);
            exam.setActive(active);
            return examDAO.create(exam);
        } catch (SQLException e) {
            throw new ServiceException("Unable to create exam.", e);
        }
    }

    public void updateExam(Exam exam) throws ServiceException {
        if (exam.getExamId() <= 0) {
            throw new ServiceException("Invalid exam.");
        }
        try {
            examDAO.update(exam);
        } catch (SQLException e) {
            throw new ServiceException("Unable to update exam.", e);
        }
    }

    public void deleteExam(int examId) throws ServiceException {
        try {
            examDAO.delete(examId);
        } catch (SQLException e) {
            throw new ServiceException("Unable to delete exam.", e);
        }
    }

    public List<Question> questionsForExam(int examId) throws ServiceException {
        try {
            return questionDAO.findByExam(examId);
        } catch (SQLException e) {
            throw new ServiceException("Unable to load questions.", e);
        }
    }

    public int addQuestion(Question question) throws ServiceException {
        validateQuestion(question);
        try {
            return questionDAO.create(question);
        } catch (SQLException e) {
            throw new ServiceException("Unable to add question.", e);
        }
    }

    public void updateQuestion(Question question) throws ServiceException {
        validateQuestion(question);
        try {
            questionDAO.update(question);
        } catch (SQLException e) {
            throw new ServiceException("Unable to update question.", e);
        }
    }

    public void deleteQuestion(int questionId) throws ServiceException {
        try {
            questionDAO.delete(questionId);
        } catch (SQLException e) {
            throw new ServiceException("Unable to delete question.", e);
        }
    }

    public Result submitExam(int studentId, int examId, Map<Integer, String> answers, int timeTakenSeconds) throws ServiceException {
        try (Connection connection = DatabaseUtil.getConnection()) {
            connection.setAutoCommit(false);
            try {
                Optional<Exam> exam = examDAO.findById(examId);
                if (exam.isEmpty() || !exam.get().isActive()) {
                    throw new ServiceException("This exam is not available.");
                }
                if (resultDAO.existsForStudentExam(studentId, examId)) {
                    throw new ServiceException("You have already submitted this exam.");
                }
                List<Question> questions = questionDAO.findByExam(examId);
                if (questions.isEmpty()) {
                    throw new ServiceException("This exam has no questions.");
                }
                Result result = calculateResult(studentId, examId, questions, answers, timeTakenSeconds);
                int resultId = resultDAO.create(connection, result);
                connection.commit();
                return resultDAO.findById(resultId).orElse(result);
            } catch (ServiceException e) {
                connection.rollback();
                throw e;
            } catch (SQLException e) {
                connection.rollback();
                throw new ServiceException("Unable to submit exam.", e);
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new ServiceException("Unable to submit exam.", e);
        }
    }

    public Result calculateResult(int studentId, int examId, List<Question> questions, Map<Integer, String> answers, int timeTakenSeconds) {
        int score = 0;
        int total = 0;
        for (Question question : questions) {
            total += question.getMarks();
            String answer = answers.get(question.getQuestionId());
            if (answer != null && answer.equalsIgnoreCase(question.getCorrectAnswer())) {
                score += question.getMarks();
            }
        }
        Result result = new Result();
        result.setStudentId(studentId);
        result.setExamId(examId);
        result.setScore(score);
        result.setTotalMarks(total);
        result.setTimeTakenSeconds(Math.max(0, timeTakenSeconds));
        return result;
    }

    public List<Result> resultsForStudent(int studentId) throws ServiceException {
        try {
            return resultDAO.findByStudent(studentId);
        } catch (SQLException e) {
            throw new ServiceException("Unable to load results.", e);
        }
    }

    public List<Result> resultsForExam(int examId) throws ServiceException {
        try {
            return resultDAO.findByExam(examId);
        } catch (SQLException e) {
            throw new ServiceException("Unable to load exam results.", e);
        }
    }

    public List<Result> leaderboard(int examId) throws ServiceException {
        try {
            return resultDAO.leaderboard(examId);
        } catch (SQLException e) {
            throw new ServiceException("Unable to load leaderboard.", e);
        }
    }

    private void validateQuestion(Question question) throws ServiceException {
        if (question.getExamId() <= 0 || ValidationUtil.isBlank(question.getQuestionText())) {
            throw new ServiceException("Question text and exam are required.");
        }
        if (ValidationUtil.isBlank(question.getOptionA()) || ValidationUtil.isBlank(question.getOptionB())
                || ValidationUtil.isBlank(question.getOptionC()) || ValidationUtil.isBlank(question.getOptionD())) {
            throw new ServiceException("All four options are required.");
        }
        if (!ValidationUtil.isAnswer(question.getCorrectAnswer())) {
            throw new ServiceException("Correct answer must be A, B, C, or D.");
        }
        if (question.getMarks() < 1 || question.getMarks() > 100) {
            throw new ServiceException("Marks must be between 1 and 100.");
        }
    }
}
