package com.serli.overquizz;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.serli.overquizz.metier.Session;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ScoreActivity extends AppCompatActivity {

    @Bind(R.id.score)
    TextView score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        ButterKnife.bind(this);

        Session session = (Session) getIntent().getSerializableExtra("session");
        score.setText(session.getCorrectAnswers()+"/"+session.getHowManyAnsweredQuestions());
    }
}
