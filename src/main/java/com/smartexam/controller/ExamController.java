package com.smartexam.controller;

import com.smartexam.model.Exam;
import com.smartexam.model.Question;
import com.smartexam.model.User;
import com.smartexam.service.ExamService;
import com.smartexam.service.ServiceException;
import com.smartexam.util.JsonUtil;
import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/api/exams/*")
public class ExamController extends BaseController {
    private final ExamService examService = new ExamService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String path = path(request);
            if ("/available".equals(path)) {
                if (requireRole(request, response, "student", "admin", "teacher") == null) return;
                ok(response, JsonUtil.object("success", "true", "exams", examsJson(examService.availableExams())));
            } else if ("/teacher".equals(path)) {
                User user = requireRole(request, response, "teacher", "admin");
                if (user == null) return;
                ok(response, JsonUtil.object("success", "true", "exams", examsJson(examService.examsForTeacher(user.getId()))));
            } else if ("/all".equals(path)) {
                if (requireRole(request, response, "admin") == null) return;
                ok(response, JsonUtil.object("success", "true", "exams", examsJson(examService.allExams())));
            } else if (path.matches("/\\d+/questions")) {
                int examId = Integer.parseInt(path.split("/")[1]);
                boolean staff = requireRole(request, response, "student", "teacher", "admin") != null;
                if (!staff) return;
                boolean includeAnswers = request.isUserInRole("teacher") || request.isUserInRole("admin");
                User user = com.smartexam.util.AuthUtil.currentUser(request);
                includeAnswers = user != null && ("teacher".equals(user.getRole()) || "admin".equals(user.getRole()));
                ok(response, JsonUtil.object("success", "true", "questions", questionsJson(examService.questionsForExam(examId), includeAnswers)));
            } else if (path.matches("/\\d+/results")) {
                if (requireRole(request, response, "teacher", "admin") == null) return;
                int examId = Integer.parseInt(path.split("/")[1]);
                ok(response, JsonUtil.object("success", "true", "results", resultsJson(examService.resultsForExam(examId))));
            } else if (path.matches("/\\d+/leaderboard")) {
                if (requireRole(request, response, "student", "teacher", "admin") == null) return;
                int examId = Integer.parseInt(path.split("/")[1]);
                ok(response, JsonUtil.object("success", "true", "results", resultsJson(examService.leaderboard(examId))));
            } else if (path.matches("/\\d+")) {
                if (requireRole(request, response, "student", "teacher", "admin") == null) return;
                int examId = Integer.parseInt(path.substring(1));
                ok(response, JsonUtil.object("success", "true", "exam", examJson(examService.getExam(examId))));
            } else {
                error(response, HttpServletResponse.SC_NOT_FOUND, "Unknown exam endpoint.");
            }
        } catch (ServiceException e) {
            serviceError(response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        User user = requireRole(request, response, "teacher", "admin");
        if (user == null) return;
        Map<String, String> data = JsonUtil.readFormOrJson(request);
        try {
            if ("/create".equals(path(request))) {
                int id = examService.createExam(data.get("title"), data.get("subject"), data.get("description"),
                        intValue(data.get("durationMinutes"), 30), user.getId(), boolValue(data.get("active")));
                ok(response, JsonUtil.object("success", "true", "examId", String.valueOf(id)));
            } else if ("/update".equals(path(request))) {
                Exam exam = new Exam();
                exam.setExamId(intValue(data.get("examId"), 0));
                exam.setTitle(data.get("title"));
                exam.setSubject(data.get("subject"));
                exam.setDescription(data.get("description"));
                exam.setDurationMinutes(intValue(data.get("durationMinutes"), 30));
                exam.setActive(boolValue(data.get("active")));
                examService.updateExam(exam);
                ok(response, JsonUtil.object("success", "true"));
            } else if ("/delete".equals(path(request))) {
                examService.deleteExam(intValue(data.get("examId"), 0));
                ok(response, JsonUtil.object("success", "true"));
            } else if ("/question/create".equals(path(request))) {
                int id = examService.addQuestion(readQuestion(data));
                ok(response, JsonUtil.object("success", "true", "questionId", String.valueOf(id)));
            } else if ("/question/update".equals(path(request))) {
                Question question = readQuestion(data);
                question.setQuestionId(intValue(data.get("questionId"), 0));
                examService.updateQuestion(question);
                ok(response, JsonUtil.object("success", "true"));
            } else if ("/question/delete".equals(path(request))) {
                examService.deleteQuestion(intValue(data.get("questionId"), 0));
                ok(response, JsonUtil.object("success", "true"));
            } else {
                error(response, HttpServletResponse.SC_NOT_FOUND, "Unknown exam endpoint.");
            }
        } catch (ServiceException e) {
            serviceError(response, e);
        }
    }

    private Question readQuestion(Map<String, String> data) {
        Question question = new Question();
        question.setExamId(intValue(data.get("examId"), 0));
        question.setQuestionText(data.get("questionText"));
        question.setOptionA(data.get("optionA"));
        question.setOptionB(data.get("optionB"));
        question.setOptionC(data.get("optionC"));
        question.setOptionD(data.get("optionD"));
        question.setCorrectAnswer(data.getOrDefault("correctAnswer", "").toUpperCase());
        question.setMarks(intValue(data.get("marks"), 1));
        return question;
    }

    private String path(HttpServletRequest request) {
        return request.getPathInfo() == null ? "" : request.getPathInfo();
    }
}
