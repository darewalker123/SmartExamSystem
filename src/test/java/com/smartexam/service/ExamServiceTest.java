package com.smartexam.service;

import com.smartexam.model.Question;
import com.smartexam.model.Result;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ExamServiceTest {
    @Test
    void calculatesScoreWithoutTrustingBrowserTotals() {
        Question first = question(11, "A", 2);
        Question second = question(12, "C", 3);
        Question third = question(13, "D", 1);

        Result result = new ExamService().calculateResult(
                3,
                9,
                List.of(first, second, third),
                Map.of(11, "A", 12, "B"),
                145
        );

        assertEquals(2, result.getScore());
        assertEquals(6, result.getTotalMarks());
        assertEquals(145, result.getTimeTakenSeconds());
    }

    private Question question(int id, String correctAnswer, int marks) {
        Question question = new Question();
        question.setQuestionId(id);
        question.setCorrectAnswer(correctAnswer);
        question.setMarks(marks);
        return question;
    }
}
