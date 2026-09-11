package com.smartexam.controller;

import com.smartexam.model.Exam;
import com.smartexam.model.Question;
import com.smartexam.model.Result;
import com.smartexam.model.User;
import com.smartexam.service.ServiceException;
import com.smartexam.util.AuthUtil;
import com.smartexam.util.JsonUtil;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

abstract class BaseController extends HttpServlet {
    protected User requireRole(HttpServletRequest request, HttpServletResponse response, String... roles) throws IOException {
        User user = AuthUtil.currentUser(request);
        if (user == null) {
            error(response, HttpServletResponse.SC_UNAUTHORIZED, "Please login first.");
            return null;
        }
        if (!AuthUtil.hasRole(request, roles)) {
            error(response, HttpServletResponse.SC_FORBIDDEN, "You do not have permission for this action.");
            return null;
        }
        return user;
    }

    protected void ok(HttpServletResponse response, String json) throws IOException {
        JsonUtil.writeJson(response, json);
    }

    protected void error(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        JsonUtil.writeJson(response, JsonUtil.object("success", "false", "message", JsonUtil.quote(message)));
    }

    protected void serviceError(HttpServletResponse response, ServiceException e) throws IOException {
        error(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
    }

    protected int intValue(String value, int defaultValue) {
        try {
            return value == null || value.isBlank() ? defaultValue : Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    protected boolean boolValue(String value) {
        return value != null && ("true".equalsIgnoreCase(value) || "on".equalsIgnoreCase(value) || "1".equals(value));
    }

    protected String usersJson(List<User> users) {
        return "[" + users.stream().map(this::userJson).collect(Collectors.joining(",")) + "]";
    }

    protected String userJson(User user) {
        return JsonUtil.object(
                "id", String.valueOf(user.getId()),
                "name", JsonUtil.quote(user.getName()),
                "email", JsonUtil.quote(user.getEmail()),
                "role", JsonUtil.quote(user.getRole())
        );
    }

    protected String examsJson(List<Exam> exams) {
        return "[" + exams.stream().map(this::examJson).collect(Collectors.joining(",")) + "]";
    }

    protected String examJson(Exam exam) {
        return JsonUtil.object(
                "examId", String.valueOf(exam.getExamId()),
                "title", JsonUtil.quote(exam.getTitle()),
                "subject", JsonUtil.quote(exam.getSubject()),
                "description", JsonUtil.quote(exam.getDescription()),
                "durationMinutes", String.valueOf(exam.getDurationMinutes()),
                "createdBy", String.valueOf(exam.getCreatedBy()),
                "active", JsonUtil.bool(exam.isActive())
        );
    }

    protected String questionsJson(List<Question> questions, boolean includeAnswers) {
        return "[" + questions.stream().map(question -> questionJson(question, includeAnswers)).collect(Collectors.joining(",")) + "]";
    }

    protected String questionJson(Question question, boolean includeAnswer) {
        String base = JsonUtil.object(
                "questionId", String.valueOf(question.getQuestionId()),
                "examId", String.valueOf(question.getExamId()),
                "questionText", JsonUtil.quote(question.getQuestionText()),
                "optionA", JsonUtil.quote(question.getOptionA()),
                "optionB", JsonUtil.quote(question.getOptionB()),
                "optionC", JsonUtil.quote(question.getOptionC()),
                "optionD", JsonUtil.quote(question.getOptionD()),
                "marks", String.valueOf(question.getMarks())
        );
        if (!includeAnswer) {
            return base;
        }
        return base.substring(0, base.length() - 1) + ",\"correctAnswer\":" + JsonUtil.quote(question.getCorrectAnswer()) + "}";
    }

    protected String resultsJson(List<Result> results) {
        return "[" + results.stream().map(this::resultJson).collect(Collectors.joining(",")) + "]";
    }

    protected String resultJson(Result result) {
        return JsonUtil.object(
                "resultId", String.valueOf(result.getResultId()),
                "studentId", String.valueOf(result.getStudentId()),
                "examId", String.valueOf(result.getExamId()),
                "studentName", JsonUtil.quote(result.getStudentName()),
                "examTitle", JsonUtil.quote(result.getExamTitle()),
                "score", String.valueOf(result.getScore()),
                "totalMarks", String.valueOf(result.getTotalMarks()),
                "timeTakenSeconds", String.valueOf(result.getTimeTakenSeconds()),
                "submittedAt", JsonUtil.quote(result.getSubmittedAt() == null ? "" : result.getSubmittedAt().toString())
        );
    }
}
