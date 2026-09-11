package com.smartexam.dao;

import com.smartexam.model.Question;
import com.smartexam.util.DatabaseUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class QuestionDAO {
    public int create(Question question) throws SQLException {
        String sql = "INSERT INTO questions (exam_id, question_text, option_a, option_b, option_c, option_d, correct_answer, marks) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            fill(statement, question);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        }
    }

    public void update(Question question) throws SQLException {
        String sql = "UPDATE questions SET exam_id = ?, question_text = ?, option_a = ?, option_b = ?, option_c = ?, option_d = ?, correct_answer = ?, marks = ? WHERE question_id = ?";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            fill(statement, question);
            statement.setInt(9, question.getQuestionId());
            statement.executeUpdate();
        }
    }

    public void delete(int questionId) throws SQLException {
        String sql = "DELETE FROM questions WHERE question_id = ?";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, questionId);
            statement.executeUpdate();
        }
    }

    public List<Question> findByExam(int examId) throws SQLException {
        String sql = "SELECT question_id, exam_id, question_text, option_a, option_b, option_c, option_d, correct_answer, marks FROM questions WHERE exam_id = ? ORDER BY question_id";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, examId);
            try (ResultSet rs = statement.executeQuery()) {
                List<Question> questions = new ArrayList<>();
                while (rs.next()) {
                    questions.add(map(rs));
                }
                return questions;
            }
        }
    }

    public Optional<Question> findById(int questionId) throws SQLException {
        String sql = "SELECT question_id, exam_id, question_text, option_a, option_b, option_c, option_d, correct_answer, marks FROM questions WHERE question_id = ?";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, questionId);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    private void fill(PreparedStatement statement, Question question) throws SQLException {
        statement.setInt(1, question.getExamId());
        statement.setString(2, question.getQuestionText());
        statement.setString(3, question.getOptionA());
        statement.setString(4, question.getOptionB());
        statement.setString(5, question.getOptionC());
        statement.setString(6, question.getOptionD());
        statement.setString(7, question.getCorrectAnswer());
        statement.setInt(8, question.getMarks());
    }

    private Question map(ResultSet rs) throws SQLException {
        Question question = new Question();
        question.setQuestionId(rs.getInt("question_id"));
        question.setExamId(rs.getInt("exam_id"));
        question.setQuestionText(rs.getString("question_text"));
        question.setOptionA(rs.getString("option_a"));
        question.setOptionB(rs.getString("option_b"));
        question.setOptionC(rs.getString("option_c"));
        question.setOptionD(rs.getString("option_d"));
        question.setCorrectAnswer(rs.getString("correct_answer"));
        question.setMarks(rs.getInt("marks"));
        return question;
    }
}
