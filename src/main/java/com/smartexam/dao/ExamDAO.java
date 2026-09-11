package com.smartexam.dao;

import com.smartexam.model.Exam;
import com.smartexam.util.DatabaseUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExamDAO {
    public int create(Exam exam) throws SQLException {
        String sql = "INSERT INTO exams (title, subject, description, duration_minutes, created_by, active) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, exam.getTitle());
            statement.setString(2, exam.getSubject());
            statement.setString(3, exam.getDescription());
            statement.setInt(4, exam.getDurationMinutes());
            statement.setInt(5, exam.getCreatedBy());
            statement.setBoolean(6, exam.isActive());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        }
    }

    public void update(Exam exam) throws SQLException {
        String sql = "UPDATE exams SET title = ?, subject = ?, description = ?, duration_minutes = ?, active = ? WHERE exam_id = ?";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, exam.getTitle());
            statement.setString(2, exam.getSubject());
            statement.setString(3, exam.getDescription());
            statement.setInt(4, exam.getDurationMinutes());
            statement.setBoolean(5, exam.isActive());
            statement.setInt(6, exam.getExamId());
            statement.executeUpdate();
        }
    }

    public void delete(int examId) throws SQLException {
        String sql = "DELETE FROM exams WHERE exam_id = ?";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, examId);
            statement.executeUpdate();
        }
    }

    public Optional<Exam> findById(int examId) throws SQLException {
        String sql = "SELECT exam_id, title, subject, description, duration_minutes, created_by, active, created_at FROM exams WHERE exam_id = ?";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, examId);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    public List<Exam> findAll() throws SQLException {
        return findBySql("SELECT exam_id, title, subject, description, duration_minutes, created_by, active, created_at FROM exams ORDER BY created_at DESC");
    }

    public List<Exam> findActive() throws SQLException {
        return findBySql("SELECT exam_id, title, subject, description, duration_minutes, created_by, active, created_at FROM exams WHERE active = TRUE ORDER BY created_at DESC");
    }

    public List<Exam> findByTeacher(int teacherId) throws SQLException {
        String sql = "SELECT exam_id, title, subject, description, duration_minutes, created_by, active, created_at FROM exams WHERE created_by = ? ORDER BY created_at DESC";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, teacherId);
            try (ResultSet rs = statement.executeQuery()) {
                return mapAll(rs);
            }
        }
    }

    private List<Exam> findBySql(String sql) throws SQLException {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            return mapAll(rs);
        }
    }

    private List<Exam> mapAll(ResultSet rs) throws SQLException {
        List<Exam> exams = new ArrayList<>();
        while (rs.next()) {
            exams.add(map(rs));
        }
        return exams;
    }

    private Exam map(ResultSet rs) throws SQLException {
        Exam exam = new Exam();
        exam.setExamId(rs.getInt("exam_id"));
        exam.setTitle(rs.getString("title"));
        exam.setSubject(rs.getString("subject"));
        exam.setDescription(rs.getString("description"));
        exam.setDurationMinutes(rs.getInt("duration_minutes"));
        exam.setCreatedBy(rs.getInt("created_by"));
        exam.setActive(rs.getBoolean("active"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            exam.setCreatedAt(createdAt.toLocalDateTime());
        }
        return exam;
    }
}
