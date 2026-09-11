package com.smartexam.service;

import com.smartexam.dao.UserDAO;
import com.smartexam.model.User;
import com.smartexam.util.PasswordUtil;
import com.smartexam.util.ValidationUtil;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class UserService {
    private final UserDAO userDAO = new UserDAO();

    public User loginUser(String email, String password) throws ServiceException {
        if (!ValidationUtil.isEmail(email) || ValidationUtil.isBlank(password)) {
            throw new ServiceException("Enter a valid email and password.");
        }
        try {
            Optional<User> user = userDAO.findByEmail(email.trim().toLowerCase(Locale.ROOT));
            if (user.isEmpty() || !PasswordUtil.verifyPassword(password, user.get().getPasswordHash())) {
                throw new ServiceException("Invalid email or password.");
            }
            return user.get();
        } catch (SQLException e) {
            throw new ServiceException("Unable to login right now.", e);
        }
    }

    public User registerUser(String name, String email, String password, String role) throws ServiceException {
        validateRegistration(name, email, password, role);
        try {
            String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
            if (userDAO.findByEmail(normalizedEmail).isPresent()) {
                throw new ServiceException("An account with this email already exists.");
            }
            User user = new User();
            user.setName(name.trim());
            user.setEmail(normalizedEmail);
            user.setPasswordHash(PasswordUtil.hashPassword(password));
            user.setRole(role.toLowerCase(Locale.ROOT));
            user.setId(userDAO.create(user));
            return user;
        } catch (SQLException e) {
            throw new ServiceException("Unable to register this account.", e);
        }
    }

    public List<User> findAllUsers() throws ServiceException {
        try {
            return userDAO.findAll();
        } catch (SQLException e) {
            throw new ServiceException("Unable to load users.", e);
        }
    }

    public void updateRole(int userId, String role) throws ServiceException {
        if (!ValidationUtil.isRole(role)) {
            throw new ServiceException("Invalid role.");
        }
        try {
            userDAO.updateRole(userId, role.toLowerCase(Locale.ROOT));
        } catch (SQLException e) {
            throw new ServiceException("Unable to update user role.", e);
        }
    }

    public void deleteUser(int userId) throws ServiceException {
        try {
            userDAO.delete(userId);
        } catch (SQLException e) {
            throw new ServiceException("Unable to delete user. Check related exams/results first.", e);
        }
    }

    private void validateRegistration(String name, String email, String password, String role) throws ServiceException {
        if (ValidationUtil.isBlank(name)) {
            throw new ServiceException("Name is required.");
        }
        if (!ValidationUtil.isEmail(email)) {
            throw new ServiceException("A valid email is required.");
        }
        if (password == null || password.length() < 6) {
            throw new ServiceException("Password must be at least 6 characters.");
        }
        if (!ValidationUtil.isRole(role)) {
            throw new ServiceException("Choose admin, teacher, or student.");
        }
    }
}
