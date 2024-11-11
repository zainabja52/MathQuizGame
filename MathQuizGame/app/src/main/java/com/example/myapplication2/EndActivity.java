package com.example.myapplication2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class EndActivity extends AppCompatActivity {

    private TextView summaryTextView, topScoresTextView, totalPlayersTextView, playerScoresTextView, averageScoreTextView, highestScoreTextView;
    private EditText playerNameEditText;
    private Button viewPlayerScoresButton, startNewQuizButton;
    private DataBaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);
        getSupportActionBar().setTitle("Math Quiz Game");

        summaryTextView = findViewById(R.id.summaryTextView);
        topScoresTextView = findViewById(R.id.topScoresTextView);
        totalPlayersTextView = findViewById(R.id.totalPlayersTextView);
        playerScoresTextView = findViewById(R.id.playerScoresTextView);
        averageScoreTextView = findViewById(R.id.averageScoreTextView);
        highestScoreTextView = findViewById(R.id.highestScoreTextView);
        playerNameEditText = findViewById(R.id.playerNameEditText);
        viewPlayerScoresButton = findViewById(R.id.viewPlayerScoresButton);
        startNewQuizButton = findViewById(R.id.startNewQuizButton);

        databaseHelper = new DataBaseHelper(this);

        // Display summary and statistics
        displaySummary();
        displayTopScores();
        displayTotalPlayers();
        displayAverageScore();
        displayHighestScore();

        viewPlayerScoresButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String playerName = playerNameEditText.getText().toString().trim();
                if (!playerName.isEmpty()) {
                    displayPlayerScores(playerName);
                } else {
                    Toast.makeText(EndActivity.this, "Please enter a player name", Toast.LENGTH_SHORT).show();
                }
            }
        });

        startNewQuizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EndActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void displaySummary() {
        int correctAnswers = getIntent().getIntExtra("correctAnswers", 0);
        int totalQuestions = getIntent().getIntExtra("totalQuestions", 0);
        int score = getIntent().getIntExtra("score", 0); // Retrieve the score
        summaryTextView.setText("You scored " + score + " points, answering " + correctAnswers + " out of " + totalQuestions + " questions correctly.");
    }


    private void displayTopScores() {
        List<Score> topScores = databaseHelper.getTopScores(5);
        StringBuilder topScoresText = new StringBuilder("Top 5 Scores:\n");
        for (Score score : topScores) {
            topScoresText.append(score.getNickname()).append(": ").append(score.getScore()).append("\n");
        }
        topScoresTextView.setText(topScoresText.toString());
    }

    private void displayTotalPlayers() {
        int totalPlayers = databaseHelper.getTotalPlayers();
        totalPlayersTextView.setText("Total Players: " + totalPlayers);
    }

    private void displayPlayerScores(String playerName) {
        List<Score> playerScores = databaseHelper.getPlayerScores(playerName);
        if (playerScores.isEmpty()) {
            Toast.makeText(this, "We don't have someone with this name", Toast.LENGTH_SHORT).show();
            playerScoresTextView.setText("No scores found for " + playerName);
        } else {
            StringBuilder playerScoresText = new StringBuilder("Scores for " + playerName + ":\n");
            for (Score score : playerScores) {
                playerScoresText.append(score.getScore()).append("\n");
            }
            playerScoresTextView.setText(playerScoresText.toString());
        }
    }

    private void displayAverageScore() {
        double averageScore = databaseHelper.getAverageScore();
        averageScoreTextView.setText("Average Score: " + averageScore);
    }

    private void displayHighestScore() {
        Score highestScore = databaseHelper.getHighestScore();
        if (highestScore != null) {
            highestScoreTextView.setText("Highest Score: " + highestScore.getNickname() + ": " + highestScore.getScore());
        } else {
            highestScoreTextView.setText("Highest Score: None");
        }
    }
}
