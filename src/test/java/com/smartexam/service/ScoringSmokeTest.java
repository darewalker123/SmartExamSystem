package com.smartexam.service;

import com.smartexam.model.Question;
import com.smartexam.model.Result;
import java.util.List;
import java.util.Map;

public class ScoringSmokeTest {
    public static void main(String[] args) {
        Question first = question(1, "A", 2);
        Question second = question(2, "D", 3);
        Result result = new ExamService().calculateResult(3, 1, List.of(first, second), Map.of(1, "A", 2, "B"), 90);
        if (result.getScore() != 2 || result.getTotalMarks() != 5 || result.getTimeTakenSeconds() != 90) {
            throw new AssertionError("Score calculation failed");
        }
        System.out.println("Scoring smoke test passed.");
    }

    private static Question question(int id, String correctAnswer, int marks) {
        Question question = new Question();
        question.setQuestionId(id);
        question.setCorrectAnswer(correctAnswer);
        question.setMarks(marks);
        return question;
    }
}
