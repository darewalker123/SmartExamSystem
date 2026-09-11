package com.smartexam.controller;

import com.smartexam.model.User;
import com.smartexam.service.ServiceException;
import com.smartexam.service.UserService;
import com.smartexam.util.JsonUtil;
import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/api/admin/*")
public class AdminController extends BaseController {
    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (requireRole(request, response, "admin") == null) return;
        try {
            if ("/users".equals(path(request))) {
                ok(response, JsonUtil.object("success", "true", "users", usersJson(userService.findAllUsers())));
            } else {
                error(response, HttpServletResponse.SC_NOT_FOUND, "Unknown admin endpoint.");
            }
        } catch (ServiceException e) {
            serviceError(response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        User admin = requireRole(request, response, "admin");
        if (admin == null) return;
        Map<String, String> data = JsonUtil.readFormOrJson(request);
        try {
            if ("/user/role".equals(path(request))) {
                userService.updateRole(intValue(data.get("userId"), 0), data.get("role"));
                ok(response, JsonUtil.object("success", "true"));
            } else if ("/user/delete".equals(path(request))) {
                int userId = intValue(data.get("userId"), 0);
                if (userId == admin.getId()) {
                    error(response, HttpServletResponse.SC_BAD_REQUEST, "You cannot delete your own admin account.");
                    return;
                }
                userService.deleteUser(userId);
                ok(response, JsonUtil.object("success", "true"));
            } else {
                error(response, HttpServletResponse.SC_NOT_FOUND, "Unknown admin endpoint.");
            }
        } catch (ServiceException e) {
            serviceError(response, e);
        }
    }

    private String path(HttpServletRequest request) {
        return request.getPathInfo() == null ? "" : request.getPathInfo();
    }
}
