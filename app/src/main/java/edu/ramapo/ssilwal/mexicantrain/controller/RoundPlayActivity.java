package edu.ramapo.ssilwal.mexicantrain.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Vector;

import com.google.android.flexbox.*;

import edu.ramapo.ssilwal.mexicantrain.model.Computer;
import edu.ramapo.ssilwal.mexicantrain.model.Deck;
import edu.ramapo.ssilwal.mexicantrain.model.Game;
import edu.ramapo.ssilwal.mexicantrain.model.Round;
import edu.ramapo.ssilwal.mexicantrain.model.Tile;
import edu.ramapo.ssilwal.mexicantrain.model.Train;

// this is the activity class where a round starts and ends
public class RoundPlayActivity extends AppCompatActivity {

    //UI variables
    private Button mEngineTile, mBoneyardTopTile;
    private static ConstraintLayout mHumanMenu, mHumanSubMenu, mHuamnPickTrainMenu;
    private Button mMtrainPickBtn, mHtrainPickBtn, mCtrainPickBtn, mCtrainMarker, mHtrainMarker, mLogBtn;
    private TextView mRoundScore, mGameScore, mRoundNumber;

    //Computer menu view components
    private static ConstraintLayout mComputerMenu;
    private Button mSaveGameCBtn, mQuitGameCBtn, mContinueGameCBtn;

    private Round mRoundObj = new Round();
    private RecyclerView mHumanHand, mComputerHand, mHTrain, mCTrain, mMTrain;
    private TilesListAdapter mHumanTileAdapter, mComputerTileAdapter, mMTrainAdapter, mHTrainAdapter, mCTrainAdapter;

    // logic variables
    private int mRoundNum, mNextPlayer;
    private String mGameType, mFilePath;
    private boolean mGameLoad = false;
    //drawn tile if any
    private Tile mDrawnTile, mEngine;

    //Current Game class object passed from MainActivity
    private Game mGame ;

    private String mGameLog = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_round_play);

        InitializeView();

        //grid layout for player's hands
        mHumanHand.setLayoutManager(new GridLayoutManager(this, 7));
        mComputerHand.setLayoutManager(new GridLayoutManager(this, 7));

        //flex layouts for three trains

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
        layoutManager.setFlexDirection(FlexDirection.COLUMN);
        layoutManager.setJustifyContent(JustifyContent.CENTER);
        layoutManager.setAlignItems(AlignItems.CENTER);
        mMTrain.setLayoutManager(layoutManager);
        FlexboxLayoutManager layoutManager2 = new FlexboxLayoutManager(this);

        layoutManager2.setFlexDirection(FlexDirection.COLUMN);
        layoutManager2.setJustifyContent(JustifyContent.CENTER);
        layoutManager2.setAlignItems(AlignItems.CENTER);
        mHTrain.setLayoutManager(layoutManager2);

        FlexboxLayoutManager layoutManager3 = new FlexboxLayoutManager(this);
        layoutManager3.setFlexDirection(FlexDirection.COLUMN);
        layoutManager3.setJustifyContent(JustifyContent.CENTER);
        layoutManager3.setAlignItems(AlignItems.CENTER);
        mCTrain.setLayoutManager(layoutManager3);

        //initializes the round depending upon if its a fresh round or load from file
        if(mGameLoad) {
            mRoundObj.loadGame(mGame, mFilePath);
            mNextPlayer = mRoundObj.GetCurrentPlayer();
            mRoundNum = mRoundObj.GetRoundNum();
        }else{
            mRoundObj.InitializeRound();
        }

        //setting the engine tile for the round
        Tile mEngine = mRoundObj.GetEngine();
        mEngineTile.setText(mEngine.GetTilePrint());

        // sets the game board for the round
        SetUpGameboard();

        //displays the next player / the first player to play
        DisplayMenu(mNextPlayer);
    }

    /**
     Sets the game board when a round is loaded or started
     */
    private void SetUpGameboard(){
        //player hands and boneyard set up
        mHumanTileAdapter = new TilesListAdapter(mRoundObj.GetPlayers()[0].GetPlayerHand(), mRoundObj, RoundPlayActivity.this);
        mComputerTileAdapter = new TilesListAdapter(mRoundObj.GetPlayers()[1].GetPlayerHand(), mRoundObj, RoundPlayActivity.this);
        mComputerTileAdapter.isClickable = false; // computer tiles can always be NOT clickable
        mBoneyardTopTile.setText(mRoundObj.GetBoneYard().get(0).GetTileString());
        mHumanTileAdapter.recViewName = "hhand";
        mComputerTileAdapter.recViewName = "chand";
        mHumanHand.setAdapter(mHumanTileAdapter);
        mComputerHand.setAdapter(mComputerTileAdapter);

        //trains setup
        mMTrainAdapter = new TilesListAdapter(mRoundObj.GetTrains().get(0).GetTrain(), mRoundObj, RoundPlayActivity.this);
        mHTrainAdapter = new TilesListAdapter(mRoundObj.GetTrains().get(1).GetTrain(), mRoundObj, RoundPlayActivity.this);
        mCTrainAdapter = new TilesListAdapter(mRoundObj.GetTrains().get(2).GetTrain(), mRoundObj, RoundPlayActivity.this);

        //make all the trains not clickable
        mMTrainAdapter.isClickable = false;
        mHTrainAdapter.isClickable = false;
        mCTrainAdapter.isClickable = false;

        //naming all the train recycler views
        mMTrainAdapter.recViewName = "mtrain";
        mHTrainAdapter.recViewName = "htrain";
        mCTrainAdapter.recViewName = "ctrain";

        //setting adapter for the trains
        mMTrain.setAdapter(mMTrainAdapter);
        mHTrain.setAdapter(mHTrainAdapter);
        mCTrain.setAdapter(mCTrainAdapter);

        //set markers if any
        UpdateMarkerUI();

        //Set Round #, Game Score and Round Score
        mRoundNumber.setText(mRoundNum+"");

        String gameScore = "H->" + mGame.GetHumanScore() + ", C->" + mGame.GetComputerScore();
        mGameScore.setText(gameScore);

        String roundScore = "H->" + mRoundObj.GetPlayers()[0].GetPlayerScore() + ", C->" + mRoundObj.GetPlayers()[1].GetPlayerScore();
        mRoundScore.setText(roundScore);
    }

    /* *********************************************
    Source Code to ask the human player for input
    ********************************************* */

    /**
     Lets the computer play its turn after human clicks on the Continue Button for Computer's turn
     @param view being clicked which is the Continue button on Computer's Menu
     */
    public void ContinueGame(View view){
        // computer strategy is returned by the Makemove function
        String computerPlayed = mRoundObj.MakeMove(mRoundObj.GetPlayers()[1]);

        // find's the next player after computer makes its move
        int mNextPlayer = mRoundObj.FindNextPlayer();

        //displays the strategy and notifies about the next player to play in a dialogue box.
        if(mNextPlayer == 0)
            DialogMessage.DialogBox("Computer's Game", computerPlayed+"\n\nIt is Human's turn.", RoundPlayActivity.this);
        else
            DialogMessage.DialogBox("Computer's Game", computerPlayed+"\n\nComputer played a double. Therefore, it is computer's turn again.", RoundPlayActivity.this);

        //adds the computer's strategy to the log
        mGameLog = mGameLog + "\n\n" + computerPlayed;

        // shows "xx" for boneyard top tile, if the boneyard is empty
        if(mRoundObj.GetBoneYard().isEmpty()) mBoneyardTopTile.setText("XX");
        else mBoneyardTopTile.setText(mRoundObj.GetBoneYard().get(0).GetTileString());

        // notifies the computer hand adapter about the recent tile played, this helps to update the recycler view if a tile was removed
        mComputerTileAdapter.notifyItemRemoved(mRoundObj.GetPickedTileIndex());
        mComputerTileAdapter.notifyItemRangeChanged(mRoundObj.GetPickedTileIndex(), mRoundObj.GetPlayers()[1].GetPlayerHand().size());

        //Notifies the train adapters, that something was added to the train, which updates the train
        NotifyTrainAdapter();

        //displays the menu for next player
        DisplayMenu(mNextPlayer);
    }


    /**
     Continue Human's game without providing any help
     @param view: Continue Button on human's menu
     */
    //if human turn and human wants to continue the game, call this
    public void ContinueGameHuman(View view){
        mHumanSubMenu.setVisibility(View.GONE);

        boolean hasPlayableTile = mRoundObj.GetPlayers()[0].CheckPlayableTile(
                mRoundObj.GetPlayers()[0].FindEligibleTrains(mRoundObj.GetTrains().get(0).TrainHasMarker(), mRoundObj.GetTrains().get(1).TrainHasMarker(), mRoundObj.GetTrains()),
                mEngine );
        //check if player has tiles to play
        if(hasPlayableTile == false){
            DialogMessage.DialogBox("No Playable Tile", "You do not have a playable tile, please pick from boneyard by CLICKING on the boneyard tile", RoundPlayActivity.this);
        }
        else{
            mHuamnPickTrainMenu.setVisibility(View.VISIBLE);
        }
    }

    /**
     Provides help to human
     @param view: Get Help button on Human's menu
     */
    public void GetHelpHuman(View view){
        mHumanSubMenu.setVisibility(View.GONE);

        boolean hasPlayableTile = mRoundObj.GetPlayers()[0].CheckPlayableTile(
                mRoundObj.GetPlayers()[0].FindEligibleTrains(mRoundObj.GetTrains().get(0).TrainHasMarker(), mRoundObj.GetTrains().get(1).TrainHasMarker(), mRoundObj.GetTrains()),
                mEngine );

        //check if player has tiles to play
        if(hasPlayableTile == false){
            DialogMessage.DialogBox("No Playable Tile", "You do not have a playable tile, please pick from boneyard", RoundPlayActivity.this);
            mGameLog = mGameLog + "\n\nHuman does not have a playable tile and thus picked from boneyard.";
        }
        else{
            mRoundObj.GetPlayers()[0].HelpHuman(mRoundObj.GetTrains(), mRoundObj, mRoundObj.GetEngine());
            String humanHelp = mRoundObj.GetPlayers()[0].GetHumanHelp();
            DialogMessage.DialogBox("Help", humanHelp,RoundPlayActivity.this);

            //add help to the log
            mGameLog = mGameLog + "\n\nHelp provided to human player: " + humanHelp;
            mHuamnPickTrainMenu.setVisibility(View.VISIBLE);
        }
    }

    /**
     Lets human pick a train, notifies the player if they picked an eligible train, ineligible train, or train with no matching tiles
     @param view trains button, the train's button which human player clicked on
     */
    public void PickATrainUI(View view){
        String pickedTrain="";
        switch (view.getId()) {
            case R.id.b_Mtrain:
                pickedTrain = "M-Train";
                break;
            case R.id.b_Htrain:
                pickedTrain="H-Train";
                break;
            case R.id.b_Ctrain:
                pickedTrain="C-Train";
                break;
        }

        // validates the picked train
        int goodTrain = mRoundObj.ValidateTrain(pickedTrain,mRoundObj.GetPlayers()[0]);


        if(goodTrain > -1){
            DialogMessage.DialogBox("Eligible Train Picked",
                    "You picked "+ pickedTrain +"\nPlease pick a tile by clicking a valid tile from tiles listed above.",
                    RoundPlayActivity.this);
            mHumanTileAdapter.isClickable = true;
        }
        else if(goodTrain == -1){
            if(mRoundObj.GetPlayers()[0].PlayerDrewTile()){
                DrawMoveHuman(pickedTrain);
            }else{
                DialogMessage.DialogBox("No Matching Tile",
                        pickedTrain+" is valid, but you do not have any matching tile for the train.",
                        RoundPlayActivity.this);
            }
        }
        else{
            DialogMessage.DialogBox("Ineligible Train Picked.",
                    pickedTrain+" is not eligible in this turn. Please pick another train.",
                    RoundPlayActivity.this);
        }
    }

    /**
     Function called on clicking on Boneyard top tile, draws boneyard tile for human player, and validates it
     @param view: Button view i.e. the boneyard's top tile button
     */
    public void DrawTile(View view){
        mRoundObj.GetPlayers()[0].SetPlayerDrewTile(true);
        mDrawnTile = mRoundObj.GetBoneYard().get(0);
        mRoundObj.GetBoneYard().removeElementAt(0);
        mBoneyardTopTile.setText(mRoundObj.GetBoneYard().get(0).GetTileString());

        // If drawn tile not playable, notify user, and pass turn to computer
        if(mRoundObj.ValidateDrawnTile(mDrawnTile, mRoundObj.GetPlayers()[0])){
            DialogMessage.DialogBox("Drawn Tile Not Playable", "The tile drawn: " +  mDrawnTile.GetTileString() + " was not playable therefore, turn was passed to Computer.", RoundPlayActivity.this);
            mRoundObj.GetPlayers()[0].SetPlayerDrewTile(false); // update drawn tile status before passing turn
            mRoundObj.SetPlayer(1);
            mHumanTileAdapter.notifyDataSetChanged();
            mHtrainMarker.setVisibility(View.VISIBLE);
            DisplayMenu(1); // next player is computer
            mGameLog = mGameLog + " The tile drawn: " +  mDrawnTile.GetTileString() + " was not playable therefore, turn was passed to Computer.";
        }
        // drawn tile was valid, ask user to pick a train to place the drawn tile on
        else{
            String message = "Drawn Tile: " + mDrawnTile.GetTileString()+"\nThe tile drawn is playable. Please pick a train.";
            DialogMessage.DialogBox("Drawn Tile Valid", message,RoundPlayActivity.this);
            mGameLog = mGameLog + "Drawn Tile: " + mDrawnTile.GetTileString()+" The tile drawn is playable.";
            mHuamnPickTrainMenu.setVisibility(View.VISIBLE);
        }
    }

    /**
     Plays the draw move for human player
     @param pickedTrain: String, picked train name
     */
    private void DrawMoveHuman(String pickedTrain){
        Train PickedTrain;
        Tile lastTile, engine = mRoundObj.GetEngine();

        //get picked train
        if(pickedTrain == "M-Train") PickedTrain = mRoundObj.GetTrains().get(0);
        else if(pickedTrain == "H-Train") PickedTrain = mRoundObj.GetTrains().get(1);
        else PickedTrain = mRoundObj.GetTrains().get(2);

        // get picked train last tile
        lastTile = PickedTrain.GetTrainLastTile(engine);

        //validate the picked tile
        Tile tileValid = mRoundObj.GetPlayers()[0].ValidateTile(mDrawnTile, lastTile);

        // if tile valid, add tile to the train
        if(tileValid.GetTileLeft()!=-1){
            PickedTrain.AddTileOnTrain(tileValid);
            String message = "Drawn "+ mDrawnTile.GetTileString() + " tile placed on "+ pickedTrain;
            int nextPlayer;

            //get next player
            if(mDrawnTile.IsDoubleTile()){
                nextPlayer = 0;
                mRoundObj.GetPlayers()[0].SetHasToPlayOD(false);
                message = message + "\nSince, you played a double tile, it is your turn again.";
                DialogMessage.DialogBox("Good Train", message, RoundPlayActivity.this);
            }else{
                nextPlayer = 1;
                mRoundObj.GetPlayers()[0].SetHasToPlayOD(true);
                DialogMessage.DialogBox("Good Train", message+"\n\nIt is now computer's turn.", RoundPlayActivity.this);
            }

            // update the player's info like player train markers,
            mRoundObj.GetPlayers()[0].UpdatePlayerInfo(PickedTrain, tileValid);
            NotifyTrainAdapter();

            //set human drew tile to false, for next turn for human
            mRoundObj.GetPlayers()[0].SetPlayerDrewTile(false);
            mHTrainAdapter.isClickable = true; //enable clicks for human tiles for next turn

            mRoundObj.SetPlayer(nextPlayer);
            DisplayMenu(nextPlayer);

        }
        // if tile not valid notify player and ask to pick another train
        else{
            DialogMessage.DialogBox("Bad train","Drawn tile does not fit on "+ pickedTrain + ". Please pick train again.",
                    RoundPlayActivity.this);
        }
    }

    /**
     Quits the game and displays the game scores and announces the winner of the game
     @param view - Quit button which is clicked to quit the game
     */
    public void QuitGame(View view) {

        // Getting players round scores
        int humanRoundScore = mRoundObj.GetPlayers()[0].GetPlayerScore();
        int computerRoundScore = mRoundObj.GetPlayers()[1].GetPlayerScore();

        // updates the game score by passing the player's round scores
        mGame.UpdateGameInfo(humanRoundScore, computerRoundScore);

        // gets the updates game score for human and computer
        int mHGameScore = mGame.GetHumanScore();
        int mCGameScore = mGame.GetComputerScore();
        String Scores = "Total Scores >>\nYour Score: " + mHGameScore + "\nComputer's Score: " + mCGameScore + "\n\n";

        // determines the winner
        String title, message;
        if(mHGameScore < mCGameScore){
            title = "Congratulation!!!";
            message = "Congratulation, you won the game!!";
        }else if(mCGameScore < mHGameScore){
            title = "Game Ended!!!";
            message = "You lost the game :( \nComputer Won the game !!";
        }else{
            title = "Draw Game!!!";
            message = "No body won the game. It was a draw.";
        }

        // displays everything in the dialog box
        AlertDialog dialog=new AlertDialog.Builder(RoundPlayActivity.this)
                .setTitle(title)
                .setMessage(Scores+message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startActivity(new Intent(RoundPlayActivity.this, MainActivity.class));
                    }
                })
                .create();
        dialog.show();
    }


    /* *********************************************
    Source Code to update display
    ********************************************* */

    /**
     Notifies train adapter about dataset changed after adding tiles to the train
     */
    public void NotifyTrainAdapter(){
        mMTrainAdapter.notifyDataSetChanged();
        mHTrainAdapter.notifyDataSetChanged();
        mCTrainAdapter.notifyDataSetChanged();
        UpdateMarkerUI();
        String roundScore = "H->" + mRoundObj.GetPlayers()[0].GetPlayerScore() + ", C->" + mRoundObj.GetPlayers()[1].GetPlayerScore();
        mRoundScore.setText(roundScore);
    }

    /**
     Updates markers display on the game board after each player's turn
     */
    private void UpdateMarkerUI(){
        if(mRoundObj.GetTrains().get(1).TrainHasMarker()) {
            mHtrainMarker.setVisibility(View.VISIBLE);
        }else{
            mHtrainMarker.setVisibility(View.GONE);
        }
        if(mRoundObj.GetTrains().get(2).TrainHasMarker()){
            mCtrainMarker.setVisibility(View.VISIBLE);
        }else{
            mCtrainMarker.setVisibility(View.GONE);
        }
    }

    /**
     Displays the next players menu if the round has not yet ended
     @param a_player player index: 0 for Human, 1 for Computer
     */
    public void DisplayMenu(int a_player){

        // checks if the round has ended becouse of previous person's move, if yes, calls the RoundEnd function
        if(mRoundObj.GetTrains().get(1).TrainHasMarker() && mRoundObj.GetTrains().get(2).TrainHasMarker() && mRoundObj.GetBoneYard().isEmpty()){
            RoundEnd("Boneyard is empty and both the players passed their turns. Therefore, game ended!");
            return;
        }
        if(mRoundObj.GetPlayers()[0].GetPlayerHand().isEmpty()){
            RoundEnd("Human's Hand is empty. Therefore, game ended!");
            return;
        }
        if(mRoundObj.GetPlayers()[1].GetPlayerHand().isEmpty()){
            RoundEnd("Computer's Hand is empty. Therefore, game ended!");
            return;
        }

        mHuamnPickTrainMenu.setVisibility(View.GONE);

        // if player is human display human menu
        if(a_player == 0){
            mHumanMenu.setVisibility(View.VISIBLE);//visible
            mHumanSubMenu.setVisibility(View.VISIBLE);
            mComputerMenu.setVisibility(View.GONE);
        }

        //if player is computer display computer menu.
        else{
            mComputerMenu.setVisibility(View.VISIBLE);//VISIBLE
            mHumanMenu.setVisibility(View.GONE);
        }
    }

    /**
     Displays the message if a round ends in a dialog box, starts the GameEnd activity once the dialog is dismissed
     @param aRoundEndMessage: the reason for why the game ended
     */
    private void RoundEnd(String aRoundEndMessage){
        AlertDialog dialog = new AlertDialog.Builder(RoundPlayActivity.this)
            .setTitle("Round over")
            .setMessage(aRoundEndMessage)
            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    int humanRoundScore = mRoundObj.GetPlayers()[0].GetPlayerScore();
                    int computerRoundScore = mRoundObj.GetPlayers()[1].GetPlayerScore();
                    mGame.UpdateGameInfo(humanRoundScore, computerRoundScore);

                    Intent intent = new Intent(RoundPlayActivity.this, GameEnd.class);
                    intent.putExtra("gameObj", mGame);
                    intent.putExtra("roundScoreHuman", humanRoundScore);
                    intent.putExtra("roundScoreComputer", computerRoundScore);
                    startActivity(intent);

                }
            }).create();

        dialog.show();
    }

    /**
     Function that is called if user clicks on Save Game button to save the game in a file
     @param view that is clicked which is the "SAVE GAME" button on any of the player's turn
     */
    public void SaveGame(View view){

        //dialog box to take user input for the file name
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please enter the filename for the game.");

        // Set up the input
        final EditText input = new EditText(this);

        // Specifying the type of input expected
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // on SAVE calls the WriteToFile function from Round class
        builder.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mRoundObj.WriteToFile(mGame, input.getText().toString());
                builder.setMessage("File saved successfully");
                startActivity(new Intent(RoundPlayActivity.this, MainActivity.class));
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    /**
     Shows a dialogue box with the lists of logs (strategies and help) provided so far in the game
     @param view which is clicked to view the log i.e LOG button
     */
    public void ViewLog(View view){
        DialogMessage.DialogBox("Game Log", mGameLog, RoundPlayActivity.this);
    }


    /**
     Initializes the views used in the activity and variables information received from the calling activity
     */
    private void InitializeView(){
        mEngineTile = findViewById(R.id.b_engine);
        mBoneyardTopTile = findViewById(R.id.b_boneyardtoptile);
        mHumanHand = findViewById(R.id.recView_humanHand);
        mComputerHand = findViewById(R.id.recView_computerHand);
        mRoundScore = findViewById(R.id.t_RoundScore);
        mGameScore = findViewById(R.id.t_GameScore);
        mRoundNumber = findViewById(R.id.t_roundNum);
        mLogBtn = findViewById(R.id.b_log);

        //Human menu
        mHumanMenu = findViewById(R.id.humanMenu);
        mHumanSubMenu = findViewById(R.id.humansubmenu);
        mHuamnPickTrainMenu = findViewById(R.id.pickTrainMenu);
        mMtrainPickBtn = findViewById(R.id.b_Mtrain);
        mHtrainPickBtn = findViewById(R.id.b_Htrain);
        mCtrainPickBtn = findViewById(R.id.b_Ctrain);

        //Computer menu view components
        mComputerMenu = findViewById(R.id.computerMenu);
        mSaveGameCBtn = findViewById(R.id.b_saveGameComputer);
        mQuitGameCBtn = findViewById(R.id.b_quitGameComputer);
        mContinueGameCBtn = findViewById(R.id.b_continueGameComputer);

        //trains
        mMTrain = findViewById(R.id.recViewMTrain);
        mHTrain = findViewById(R.id.recViewHTrain);
        mCTrain = findViewById(R.id.recViewCTrain);
        mHtrainMarker = findViewById(R.id.b_HumanMarker);
        mCtrainMarker = findViewById(R.id.b_ComputerMarker);

        //data received from called activity
        mGameType = getIntent().getStringExtra("gameType");
        mGame = (Game)getIntent().getSerializableExtra("gameObj");
        if(mGameType.equals("load")){
            mFilePath = getIntent().getStringExtra("filepath");
            mGameLoad = true;
        }else{
            mRoundNum = Integer.parseInt(getIntent().getStringExtra("roundNum"));
            mNextPlayer = Integer.parseInt(getIntent().getStringExtra("player"));
            mRoundObj.SetRoundNum(mRoundNum);
            mRoundObj.SetPlayer(mNextPlayer);
        }
    }
}