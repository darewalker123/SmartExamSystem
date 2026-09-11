package com.smartexam.controller;

import com.smartexam.model.User;
import com.smartexam.service.ServiceException;
import com.smartexam.service.UserService;
import com.smartexam.util.AuthUtil;
import com.smartexam.util.JsonUtil;
import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/api/auth/*")
public class AuthController extends BaseController {
    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if ("/me".equals(path(request))) {
            User user = AuthUtil.currentUser(request);
            if (user == null) {
                error(response, HttpServletResponse.SC_UNAUTHORIZED, "Not logged in.");
                return;
            }
            ok(response, JsonUtil.object("success", "true", "user", userJson(user)));
            return;
        }
        error(response, HttpServletResponse.SC_NOT_FOUND, "Unknown auth endpoint.");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Map<String, String> data = JsonUtil.readFormOrJson(request);
        try {
            switch (path(request)) {
                case "/login":
                    User user = userService.loginUser(data.get("email"), data.get("password"));
                    request.getSession(true).setAttribute(AuthUtil.SESSION_USER, user);
                    ok(response, JsonUtil.object("success", "true", "user", userJson(user)));
                    break;
                case "/register":
                    String role = data.getOrDefault("role", "student");
                    if ("admin".equalsIgnoreCase(role) && !com.smartexam.util.AuthUtil.hasRole(request, "admin")) {
                        error(response, HttpServletResponse.SC_FORBIDDEN, "Admin accounts cannot be created from public registration.");
                        return;
                    }
                    User created = userService.registerUser(data.get("name"), data.get("email"), data.get("password"), role);
                    request.getSession(true).setAttribute(AuthUtil.SESSION_USER, created);
                    ok(response, JsonUtil.object("success", "true", "user", userJson(created)));
                    break;
                case "/logout":
                    if (request.getSession(false) != null) {
                        request.getSession(false).invalidate();
                    }
                    ok(response, JsonUtil.object("success", "true"));
                    break;
                default:
                    error(response, HttpServletResponse.SC_NOT_FOUND, "Unknown auth endpoint.");
            }
        } catch (ServiceException e) {
            serviceError(response, e);
        }
    }

    private String path(HttpServletRequest request) {
        return request.getPathInfo() == null ? "" : request.getPathInfo();
    }
}
