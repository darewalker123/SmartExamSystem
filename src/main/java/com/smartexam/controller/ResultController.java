package com.smartexam.controller;

import com.smartexam.model.Result;
import com.smartexam.model.User;
import com.smartexam.service.ExamService;
import com.smartexam.service.ServiceException;
import com.smartexam.util.JsonUtil;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/api/results/*")
public class ResultController extends BaseController {
    private final ExamService examService = new ExamService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = requireRole(request, response, "student", "teacher", "admin");
        if (user == null) return;
        try {
            if ("/student".equals(path(request))) {
                ok(response, JsonUtil.object("success", "true", "results", resultsJson(examService.resultsForStudent(user.getId()))));
            } else {
                error(response, HttpServletResponse.SC_NOT_FOUND, "Unknown result endpoint.");
            }
        } catch (ServiceException e) {
            serviceError(response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        User user = requireRole(request, response, "student");
        if (user == null) return;
        Map<String, String> data = JsonUtil.readFormOrJson(request);
        try {
            if ("/submit".equals(path(request))) {
                int examId = intValue(data.get("examId"), 0);
                int timeTakenSeconds = intValue(data.get("timeTakenSeconds"), 0);
                Map<Integer, String> answers = new HashMap<>();
                data.forEach((key, value) -> {
                    if (key.startsWith("answer_")) {
                        answers.put(intValue(key.substring(7), 0), value);
                    }
                });
                Result result = examService.submitExam(user.getId(), examId, answers, timeTakenSeconds);
                ok(response, JsonUtil.object("success", "true", "result", resultJson(result)));
            } else {
                error(response, HttpServletResponse.SC_NOT_FOUND, "Unknown result endpoint.");
            }
        } catch (ServiceException e) {
            serviceError(response, e);
        }
    }

    private String path(HttpServletRequest request) {
        return request.getPathInfo() == null ? "" : request.getPathInfo();
    }
}
