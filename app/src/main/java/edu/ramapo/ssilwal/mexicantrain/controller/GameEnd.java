package edu.ramapo.ssilwal.mexicantrain.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import edu.ramapo.ssilwal.mexicantrain.model.Game;

/*
* This class displays the round scores and game scores for each player after a round ends and asks the user
* if they want to play next round.
* */
public class GameEnd extends AppCompatActivity {

    private TextView mRoundScore, mGameScore;
    private Button mPlayNextRndBtn, mNoBtn;
    private int mHRoundScore, mCRoundScore, mHGameScore, mCGameScore;
    private Game mGameObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_end);

        InitializeUI();
        String roundInfo = " Round Score:\nHuman : " + mHRoundScore + "\nComputer : " + mCRoundScore;
        String gameInfo = " Game Score:\nHuman : " + mHGameScore + "\nComputer : " + mCGameScore;
        mRoundScore.setText(roundInfo);
        mGameScore.setText(gameInfo);
    }

    /**
     Start new round and pass the Game information to new round activity
     @param view "Yes" button clicked to start new round
     */
    public void PlayNextRound(View view){

        if(mHGameScore < mCGameScore) mGameObj.SetPlayer(0);
        else if(mCGameScore < mHGameScore) mGameObj.SetPlayer(1);
        // go to main activity to toss
        else{
            Intent intent = new Intent(GameEnd.this, MainActivity.class);
            intent.putExtra("gameType", "newGame");
            intent.putExtra("gameObj", mGameObj);
            startActivity(intent);
        }

        Intent intent = new Intent(GameEnd.this, RoundPlayActivity.class);
        intent.putExtra("gameType", "newGame");
        intent.putExtra("gameObj", mGameObj);
        intent.putExtra("roundNum", Integer.toString(mGameObj.GetRoundNum()));
        intent.putExtra("player", Integer.toString(mGameObj.GetPlayer()));
        startActivity(intent);
    }

    /**
     End game and show the game winner
     @param view "No" button clicked to end game
     */
    public void EndGame(View view){
        if(mHGameScore < mCGameScore){
            DialogMessage.DialogBox("Congratulation!!!", "Congratulation, you won the game!!", GameEnd.this);
        }else if(mCGameScore < mHGameScore){
            DialogMessage.DialogBox("Game Ended!!!", "You lost the game :( \nComputer Won the game !!", GameEnd.this);
        }else{
            DialogMessage.DialogBox("Draw Game!!!", "No body won the game. It was a draw", GameEnd.this);
        }
    }

    /**
    Initializes the GUI views
     */
    private void InitializeUI(){
        mRoundScore = findViewById(R.id.t_RoundScores);
        mGameScore = findViewById(R.id.t_GameScores);
        mPlayNextRndBtn = findViewById(R.id.b_yes);
        mNoBtn = findViewById(R.id.b_no);

        //data received from called activity
        mHRoundScore = getIntent().getIntExtra("roundScoreHuman", 0);
        mCRoundScore = getIntent().getIntExtra("roundScoreComputer", 0);
        mGameObj = (Game)getIntent().getSerializableExtra("gameObj");

        mHGameScore = mGameObj.GetHumanScore();
        mCGameScore = mGameObj.GetComputerScore();
    }
}