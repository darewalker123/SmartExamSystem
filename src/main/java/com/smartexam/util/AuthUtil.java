package com.smartexam.util;

import com.smartexam.model.User;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public final class AuthUtil {
    public static final String SESSION_USER = "currentUser";

    private AuthUtil() {
    }

    public static User currentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        Object user = session.getAttribute(SESSION_USER);
        return user instanceof User ? (User) user : null;
    }

    public static boolean hasRole(HttpServletRequest request, String... roles) {
        User user = currentUser(request);
        if (user == null) {
            return false;
        }
        for (String role : roles) {
            if (user.getRole().equalsIgnoreCase(role)) {
                return true;
            }
        }
        return false;
    }
}
