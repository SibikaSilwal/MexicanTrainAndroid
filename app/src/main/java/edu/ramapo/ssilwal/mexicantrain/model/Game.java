package edu.ramapo.ssilwal.mexicantrain.model;

import java.io.Serializable;
import java.util.Random;

public class Game  implements Serializable {

    /*Constructor for Game Class*/
    public Game(){
        mGameRound = 1;
        mGameScoreHuman = 0;
        mGameScoreComputer = 0;
    }

    /**
     Function to toss between head and tail
     @param humanChoice: string which represents if human chose "h" or "t"
     @return true if human won the toss, false otherwise
    */
     public boolean TossACoin(String humanChoice) {

        int head = 0, tail = 1, humanCall;

        humanCall = humanChoice == "H" ? head : tail;

        //generate random number between 0 and 1 for coin toss
        Random rand = new Random();
        int toss = rand.nextInt(2);

        //compare human call with random # generated, if same human won the toss
        if (humanCall == toss) {
            mPlayer = 0;
            return true; // human won the toss, therefore return true meaning human plays first
        }
        else {
            mPlayer = 1;
            return false; // human lost the toss, therefore return false meaning human plays second
        }
    }


    /**
     Function to update the Game info like player scores, and round number
     @param aHumanRoundScore: human score for the round as integer
     @param aComputerRoundScore : computer score for the round as integer
     */
    public void UpdateGameInfo(int aHumanRoundScore, int aComputerRoundScore){
        mGameScoreHuman = mGameScoreHuman + aHumanRoundScore;
        mGameScoreComputer = mGameScoreComputer + aComputerRoundScore;
        mGameRound = mGameRound + 1;
    }

    /*Getter and Setter for first player*/
    public int GetPlayer(){ return mPlayer; }
    public void SetPlayer(int aPlayer){ mPlayer = aPlayer;}

    /*Getter and Setter for round number*/
    public int GetRoundNum(){ return mGameRound; }
    public void SetRoundNum(int aRoundnum){ mGameRound = aRoundnum; }

    /*Getter and Setter for human score*/
    public int GetHumanScore(){return mGameScoreHuman;}
    public void SetHumanGameScore(int a_score) { mGameScoreHuman = a_score; };

    /*Getter and Setter for computer score*/
    public int GetComputerScore(){return mGameScoreComputer;}
    public void SetComputerGameScore(int a_score) { mGameScoreComputer = a_score; };



    /*** Private member variables***/

    //round #
    private int mGameRound;

    //game scores
    private int mGameScoreHuman;
    private int mGameScoreComputer;

    //first player
    private int mPlayer;


}
