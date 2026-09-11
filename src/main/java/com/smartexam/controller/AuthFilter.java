package com.smartexam.controller;

import com.smartexam.model.User;
import com.smartexam.util.AuthUtil;
import java.io.IOException;
import java.util.Map;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebFilter("/pages/*")
public class AuthFilter implements Filter {
    private static final Map<String, String[]> PROTECTED_PAGES = Map.of(
            "/pages/admin.html", new String[]{"admin"},
            "/pages/teacher.html", new String[]{"teacher"},
            "/pages/dashboard.html", new String[]{"student"},
            "/pages/exam.html", new String[]{"student"},
            "/pages/result.html", new String[]{"student", "teacher", "admin"}
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String path = httpRequest.getServletPath();
        String[] roles = PROTECTED_PAGES.get(path);
        if (roles == null) {
            chain.doFilter(request, response);
            return;
        }

        User user = AuthUtil.currentUser(httpRequest);
        if (user == null) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/pages/login.html");
            return;
        }
        for (String role : roles) {
            if (role.equalsIgnoreCase(user.getRole())) {
                chain.doFilter(request, response);
                return;
            }
        }
        httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "You do not have permission to open this page.");
    }
}
