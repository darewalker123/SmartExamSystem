package com.smartexam.dao;

import com.smartexam.model.Result;
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

public class ResultDAO {
    public boolean existsForStudentExam(int studentId, int examId) throws SQLException {
        String sql = "SELECT result_id FROM results WHERE student_id = ? AND exam_id = ?";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, studentId);
            statement.setInt(2, examId);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        }
    }

    public int create(Connection connection, Result result) throws SQLException {
        String sql = "INSERT INTO results (student_id, exam_id, score, total_marks, time_taken_seconds) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, result.getStudentId());
            statement.setInt(2, result.getExamId());
            statement.setInt(3, result.getScore());
            statement.setInt(4, result.getTotalMarks());
            statement.setInt(5, result.getTimeTakenSeconds());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        }
    }

    public Optional<Result> findById(int resultId) throws SQLException {
        String sql = baseSql() + " WHERE r.result_id = ?";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, resultId);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    public List<Result> findByStudent(int studentId) throws SQLException {
        String sql = baseSql() + " WHERE r.student_id = ? ORDER BY r.submitted_at DESC";
        return findMany(sql, studentId);
    }

    public List<Result> findByExam(int examId) throws SQLException {
        String sql = baseSql() + " WHERE r.exam_id = ? ORDER BY r.score DESC, r.time_taken_seconds ASC";
        return findMany(sql, examId);
    }

    public List<Result> leaderboard(int examId) throws SQLException {
        String sql = baseSql() + " WHERE r.exam_id = ? ORDER BY r.score DESC, r.time_taken_seconds ASC, r.submitted_at ASC LIMIT 20";
        return findMany(sql, examId);
    }

    private List<Result> findMany(String sql, int id) throws SQLException {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                List<Result> results = new ArrayList<>();
                while (rs.next()) {
                    results.add(map(rs));
                }
                return results;
            }
        }
    }

    private String baseSql() {
        return "SELECT r.result_id, r.student_id, r.exam_id, u.name AS student_name, e.title AS exam_title, "
                + "r.score, r.total_marks, r.time_taken_seconds, r.submitted_at "
                + "FROM results r JOIN users u ON r.student_id = u.id JOIN exams e ON r.exam_id = e.exam_id";
    }

    private Result map(ResultSet rs) throws SQLException {
        Result result = new Result();
        result.setResultId(rs.getInt("result_id"));
        result.setStudentId(rs.getInt("student_id"));
        result.setExamId(rs.getInt("exam_id"));
        result.setStudentName(rs.getString("student_name"));
        result.setExamTitle(rs.getString("exam_title"));
        result.setScore(rs.getInt("score"));
        result.setTotalMarks(rs.getInt("total_marks"));
        result.setTimeTakenSeconds(rs.getInt("time_taken_seconds"));
        Timestamp submittedAt = rs.getTimestamp("submitted_at");
        if (submittedAt != null) {
            result.setSubmittedAt(submittedAt.toLocalDateTime());
        }
        return result;
    }
}
