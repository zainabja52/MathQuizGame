package com.example.myapplication2;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class QuizActivity extends AppCompatActivity {

    private TextView questionTextView;
    private RadioGroup optionsRadioGroup;
    private RadioButton option1RadioButton, option2RadioButton, option3RadioButton, option4RadioButton;
    private Button nextButton;
    private CountDownTimer countDownTimer;
    private TextView timerTextView;
    private DataBaseHelper dbHelper;  // Database Helper Object
    private String nickname;  // String to store nickname

    private List<Question> questionList;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private int correctAnswers = 0;
    private boolean scoreSaved = false;  // Flag to check if the score has been saved already

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        getSupportActionBar().setTitle("Math Quiz Game");

        questionTextView = findViewById(R.id.questionTextView);
        optionsRadioGroup = findViewById(R.id.optionsRadioGroup);
        option1RadioButton = findViewById(R.id.option1RadioButton);
        option2RadioButton = findViewById(R.id.option2RadioButton);
        option3RadioButton = findViewById(R.id.option3RadioButton);
        option4RadioButton = findViewById(R.id.option4RadioButton);
        nextButton = findViewById(R.id.nextButton);
        timerTextView = findViewById(R.id.timerTextView);

        dbHelper = new DataBaseHelper(this);

        nickname = getIntent().getStringExtra("nickname");
        if (nickname == null || nickname.isEmpty()) {
            Toast.makeText(this, "Nickname not set or invalid", Toast.LENGTH_SHORT).show();
            finish(); // Close this activity if there is no valid nickname
            return;
        }

        questionList = loadQuestions();
        if (questionList == null || questionList.size() < 5) {
            Toast.makeText(this, "Not enough questions available", Toast.LENGTH_SHORT).show();
            finish(); // Close this activity if there aren't enough questions
            return;
        }
        Collections.shuffle(questionList);
        questionList = questionList.subList(0, 5); // Select 5 random questions

        displayQuestion();

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentQuestionIndex < questionList.size()) {
                    checkAnswer();
                    currentQuestionIndex++;
                    if (currentQuestionIndex < questionList.size()) {
                        displayQuestion();
                    } else {
                        if (!scoreSaved) {
                            saveScoreToDatabase();
                            endQuiz();
                        }
                    }
                } else {
                    if (!scoreSaved) {
                        saveScoreToDatabase();
                        endQuiz();
                    }
                }
            }
        });
    }

    private void displayQuestion() {
        if (currentQuestionIndex < questionList.size()) {
            Question currentQuestion = questionList.get(currentQuestionIndex);
            questionTextView.setText(currentQuestion.getQuestionText());
            option1RadioButton.setText(currentQuestion.getOption1());
            option2RadioButton.setText(currentQuestion.getOption2());
            option3RadioButton.setText(currentQuestion.getOption3());
            option4RadioButton.setText(currentQuestion.getOption4());
            optionsRadioGroup.clearCheck();

            startTimer();
        } else {
            endQuiz();
        }
    }

    private void startTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(10000, 1000) {
            public void onTick(long millisUntilFinished) {
                timerTextView.setText("Time remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                if (currentQuestionIndex < questionList.size()) {
                    checkAnswer();
                    currentQuestionIndex++;
                    displayQuestion();
                } else {
                    if (!scoreSaved) {
                        saveScoreToDatabase();
                        endQuiz();
                    }
                }
            }
        }.start();
    }

    private void checkAnswer() {
        int selectedOptionId = optionsRadioGroup.getCheckedRadioButtonId();
        if (selectedOptionId != -1 && currentQuestionIndex < questionList.size()) {
            RadioButton selectedRadioButton = findViewById(selectedOptionId);
            String selectedAnswer = selectedRadioButton.getText().toString();
            Question currentQuestion = questionList.get(currentQuestionIndex);

            if (selectedAnswer.equals(currentQuestion.getCorrectAnswer())) {
                correctAnswers++;
                score++;
                Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
            } else {
                score--;  // Decrement score for wrong answer
                Toast.makeText(this, "Wrong!", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Handle timeout or no answer selected
            score--;  // Decrement score for timeout or no answer selected
        }
        System.out.println(score);
    }

    private void saveScoreToDatabase() {
        if (!scoreSaved) {
            if (nickname == null || nickname.isEmpty()) {
                Toast.makeText(this, "Nickname not set or invalid", Toast.LENGTH_SHORT).show();
                return;
            }
            long result = dbHelper.insertScore(nickname, score);
            if (result == -1) {
                Toast.makeText(this, "Failed to save score", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Score saved successfully", Toast.LENGTH_SHORT).show();
                scoreSaved = true;  // Set the flag to true after the score is successfully saved
            }
        }
    }

    private void endQuiz() {
        Intent intent = new Intent(QuizActivity.this, EndActivity.class);
        intent.putExtra("score", score);
        intent.putExtra("correctAnswers", correctAnswers);
        intent.putExtra("totalQuestions", questionList.size());
        startActivity(intent);
        finish();
    }

    private List<Question> loadQuestions() {
        List<Question> questions = new ArrayList<>();
        InputStream is = getResources().openRawResource(R.raw.questions);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String questionText = parts[0];
                    String correctAnswer = parts[1];
                    String option1 = correctAnswer;
                    String option2 = generateRandomOption();
                    String option3 = generateRandomOption();
                    String option4 = generateRandomOption();
                    questions.add(new Question(questionText, option1, option2, option3, option4, correctAnswer));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return questions;
    }

    private String generateRandomOption() {
        Random random = new Random();
        return String.valueOf(random.nextInt(100)); // Generate a random number as a string
    }
}
