package com.example.rozin.trivia;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Logger;

import java.util.ArrayList;
import java.util.Collections;

public class GameActivity extends AppCompatActivity implements TriviaHelper.Callback {

    // properties of the class
    private float score = 10;
    private Question question;
    private TriviaHelper triviaHelper;
    private int questionsGot = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // hide the status and action bar
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        getSupportActionBar().hide();

        // set your triviahelper
        triviaHelper = new TriviaHelper(this);
        if (savedInstanceState == null) {

            // if there was no game going, ask for a new question
            triviaHelper.getNextQuestion(this);
        }
        else {

            // otherwise get the data from the last game and display it
            question = (Question) savedInstanceState.getSerializable("question");
            score = savedInstanceState.getFloat("score");
            questionsGot = savedInstanceState.getInt("questionsGot");
            viewQuestion();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        // put all your data into a bundle
        outState.putFloat("score", score);
        outState.putInt("questionsGot", questionsGot);
        outState.putSerializable("question", question);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void gotQuestions(Question nextQuestion) {

        // display the question
        question = nextQuestion;
        viewQuestion();
    }

    @Override
    public void gotQuestionsError(String message) {

        // print an error message in case of an error
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(this, message, duration);
        toast.show();
    }

    public void onButtonClicked(View view) {
        Button button = (Button) view;

        // multiply the score by the correct number if the answer was correct
        if (button.getText().equals(question.getCorrectAnswer())) {
            if (question.getDifficulty().equals("hard")){
                score = score * 5;
            }
            else if (question.getDifficulty().equals("medium")) {
                score = score * 3;
            }
            else {
                score = score * 2;
            }
        }

        // otherwise divide by 2
        else {
            score = score / 2;
        }

        // if the user has played 10 questions, end the game, otherwise get a new question
        questionsGot += 1;
        if (questionsGot < 10) {
            triviaHelper.getNextQuestion(this);
        }
        else {
            Intent intent = new Intent(GameActivity.this, GameFinishedActivity.class);
            intent.putExtra("score", score);
            startActivity(intent);
        }
    }

    public void viewQuestion() {

        // instantiate your views
        TextView questionView = findViewById(R.id.questionView);
        TextView scoreView = findViewById(R.id.score);
        TextView difficultyView = findViewById(R.id.difficulty);
        Button answer1 = findViewById(R.id.answer1);
        Button answer2 = findViewById(R.id.answer2);
        Button answer3 = findViewById(R.id.answer3);
        Button answer4 = findViewById(R.id.answer4);

        // set correct texts to your textviews
        questionView.setText(question.getQuestion());
        scoreView.setText("Score: "+ Integer.toString(Math.round(score)));
        difficultyView.setText("Difficulty: " + question.getDifficulty());

        // make answers appear in a random order
        ArrayList<String> answers = question.getAnswers();
        Collections.shuffle(answers);
        answer1.setText(answers.get(0));
        answer2.setText(answers.get(1));
        answer3.setText(answers.get(2));
        answer4.setText(answers.get(3));
    }
}
