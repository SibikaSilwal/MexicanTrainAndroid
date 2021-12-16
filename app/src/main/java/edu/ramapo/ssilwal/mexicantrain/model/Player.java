package edu.ramapo.ssilwal.mexicantrain.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import edu.ramapo.ssilwal.mexicantrain.controller.DialogMessage;
import edu.ramapo.ssilwal.mexicantrain.controller.RoundPlayActivity;

public class Player {

    /*
    Constructor for player class
    */
    public Player(){
        mHasToPlayOD = true;
        mPlayedDblCount = 0;
        mPlayerNumber = -1; // means not a valid player, this will be set from round class
        mPlayerDrewTile = false;
        mHumanHelp = "";
    }


    /*
    Getter and Setter for player's hand
    */
    public void SetPlayerHand(Vector<Tile> aPlayerHand){ mPlayerHand = aPlayerHand;}
    public Vector<Tile> GetPlayerHand(){ return mPlayerHand; }

    /*
    Getter and Setter for player number
    */
    public void SetPlayerNumber(int a_num){ mPlayerNumber = a_num;}
    public int GetPlayerNumber(){ return mPlayerNumber; }

    /*
    Getter and Setter for player drew tile boolean variable
    */
    public boolean PlayerDrewTile() { return mPlayerDrewTile; }
    public void SetPlayerDrewTile(boolean a_PlayerDrewTile) { mPlayerDrewTile = a_PlayerDrewTile; }

    /*
    Getter and Setter for player has to play orphan double train or not
    */
    public boolean HasToPlayOD(){ return mHasToPlayOD;}
    public void SetHasToPlayOD(boolean a_hastoPlay) { mHasToPlayOD = a_hastoPlay;}


    /**
     Function to Return a list of orphan double trains
     @param a_trains: vector of all three trains
     @return vector of orphan double trains
     */
    public Vector<Train> GetOrpDblTrain(Vector<Train> a_trains) {
        Vector<Train> OrpDblTrains = new Vector<>();

        for (int i = 0; i < a_trains.size(); i++) {
            if (a_trains.get(i).IsOrphDoubleTrain()) {
                OrpDblTrains.add(a_trains.get(i));
            }
        }
        return OrpDblTrains;
    }


    //virtual function to make the players pick a train, does not do anything from player class
    public Train PickAtrain(Vector<Train> a_trains, Vector<Train> a_EligibleTrains, Tile a_Engine) {
        return a_EligibleTrains.get(0);
    };


    //vritual function to make players pick a tile, does not do anything from players class
    public int PickATile(Train a_PickedTrain, Tile a_Engine) {
        return 0;
    }


    //virtual function to find eligible trains for each player
    public Vector<Train> FindEligibleTrains(boolean a_HTrainMarker, boolean a_CTrainMarker, Vector<Train> a_Trains){
        return new Vector<>();
    }

    /**
     Function to check if player has playable tile or need to draw from boneyard
     @param a_EligibleTrains: vector of eligible trains for the player
     @param a_Engine : engine tile
     @return true if no need to draw false otherwise
     */
    public boolean CheckPlayableTile(Vector<Train> a_EligibleTrains, Tile a_Engine){
        boolean hasPlayableTile = false;
        for (int i = 0; i < a_EligibleTrains.size(); i++) {
            for (int j = 0; j < mPlayerHand.size(); j++) {

                Tile playableTile = ValidateTile(mPlayerHand.get(j), a_EligibleTrains.get(i).GetTrainLastTile(a_Engine));

                //if the computer player has atleast one tile on hand to play on one of the eligible trains, return true
                if (playableTile.GetTileLeft() != -1)return true;
            }
        }
        return hasPlayableTile;
    }


    /**
     Function to validate the picked tile by user before adding it to the train
     @param a_PlayingTile: tile picked by the player to place on the train
     @param a_LastTile : last tile of the picked train
     @return the picked tile if tile valid, otherwise Tile(-1,-1) denoting invalid tile
     */
    public Tile ValidateTile(Tile a_PlayingTile, Tile a_LastTile) {
        //Tile defaultTile(-1,-1);
        int lastTilePips = a_LastTile.GetTileRight();

        if (a_PlayingTile.GetTileLeft() == lastTilePips)
        {
            return a_PlayingTile;
        }
        else if (a_PlayingTile.GetTileRight() == lastTilePips) {

            int tileLeft = a_PlayingTile.GetTileLeft();
            int tileRight = a_PlayingTile.GetTileRight();
            a_PlayingTile.SetTileLeft(tileRight);
            a_PlayingTile.SetTileRight(tileLeft);

            return a_PlayingTile;
        }
        else {
            return new Tile(-1,-1);
        }
    }


    /**
     Function to check if the player has a playable tile for the picked train
     @param a_pickedTrain : picked train
     @param a_PlayerHand: player's hand
     @param a_Engine : engine tile
     @return true or false
     */
    boolean HasPlayableTile(Train a_pickedTrain, Vector<Tile> a_PlayerHand, Tile a_Engine) {
        for (int j = 0; j < a_PlayerHand.size(); j++) {

            Tile playableTile = ValidateTile(a_PlayerHand.get(j), a_pickedTrain.GetTrainLastTile(a_Engine));

            //if the player has atleast one tile on hand to play on the eligible trains, return true
            if (playableTile.GetTileLeft() != -1)return true;
        }
        return false;
    }

    /**
     Function to check if the given train is eligible in the turn and if the player has playable tile for the train
     @param a_EligibleTrains: player's eligible trains
     @param a_Engine : engine tile
     @param a_TrainName : name of the picked train
     @return
     */
    public int isGoodTrain(Vector<Train> a_EligibleTrains, Tile a_Engine, String a_TrainName) {
        for (int i = 0; i < a_EligibleTrains.size(); i++) {
            if (a_EligibleTrains.get(i).GetTrainName() == a_TrainName) {
                if (HasPlayableTile(a_EligibleTrains.get(i), GetPlayerHand(), a_Engine)) {
                    return GetTrainIndex(a_TrainName);
                }
                else {
                    return -1; //user has no playable tile for train
                }
            }
        }
        return -2; // the picked train was not eligible
    }


    /**
     Function to add marker to player's train if the drawn tile could not be played
     @param a_drawnTile: tile drawn by the player from boneyard
     @param a_EligibleTrains : player's eligible trains
     @param a_Engine : engine tile
     @param a_Trains : vector of all the trains
     @param a_player: current player index : 0- human, 1- computer
     @return true if marker added, false otherwise
     */
    public boolean AddMarker(Tile a_drawnTile, Vector<Train> a_EligibleTrains, Tile a_Engine, Vector<Train> a_Trains, int a_player) {

        for (int i = 0; i < a_EligibleTrains.size(); i++) {

            Tile isValidTile = ValidateTile(a_drawnTile, a_EligibleTrains.get(i).GetTrainLastTile(a_Engine));

            //if the tile fits on any of the eligible train return false immediately
            if (isValidTile.GetTileLeft() != -1) {
                return false;
            }
        }
        //if player could not play a tile after playing an orphan tile, sets the hasToPlayOD variable to true so that player
        //is forced to play on the OD train in next turn
        mHasToPlayOD = true;
        //add tile to players hand
        mPlayerHand.add(a_drawnTile);
        //if player is human, update human train marker
        if(a_player == 0) a_Trains.get(1).UpdateTrainMarker(true);
        //otherwise update computer train marker
        else a_Trains.get(2).UpdateTrainMarker(true);


        mPlayedDblCount = 0; // making sure, its not the same player's turn again, if it was
        return true;
    }


    /**
     Calculates the players score which is a sum of the tile pips in players hand
     @return sum of the tile pips which is an integer
     */
    public int GetPlayerScore() {
        int score = 0;
        for (int i = 0; i < mPlayerHand.size(); i++) {
            score = score + mPlayerHand.get(i).GetTileLeft() + mPlayerHand.get(i).GetTileRight();
        }
        return score;
    }

    /** Winning Strategies **/

    /**
     Function to find the largest tile that can be played in the turn
     @param a_endTilePips of the picked train
     @return largest tile index from player's hand
     */
    int FindLargestTileIndex(int a_endTilePips) {
        int largestpip=-1;
        int largestTileIndex=0;

        Tile lastTile = new Tile(10, a_endTilePips);
        Tile ValidTile = new Tile(-1, -1);

        for (int i = 0; i < mPlayerHand.size(); i++) {
            ValidTile = ValidateTile(mPlayerHand.get(i), lastTile);
            if ( ValidTile.GetTileLeft() != -1) {
                if (ValidTile.GetTileRight() > largestpip) {
                    largestpip = ValidTile.GetTileRight();
                    largestTileIndex = i;
                }
            }
        }

        //default, should not happen
        return largestTileIndex;
    }


    /**
     Function to Return the index of the train that can play the largest tile
     @param a_Trains: vectors of all the trains
     @param aCanPlayM : boolean determining if player can play Mtrain
     @param aCanPlayH : boolean determining if player can play Htrain
     @param aCanPlayC : boolean determining if player can play Ctrain
     @param a_Engine : engine tile
     @return index of the train that can play the largest tile pip
     */
    public int FindLargestTrain(Vector<Train> a_Trains, boolean aCanPlayM, boolean aCanPlayH, boolean aCanPlayC, Tile a_Engine){

        //1) for all the playable trains, compares the sum of the largest tiles in each train
        //2) uses getMax function to do so which returns the index of the max number in a list
        //3) returns the index returned by get max as a train that can play the lasrgest tile

        int mtrainLargestTile = mPlayerHand.get((FindLargestTileIndex(a_Trains.get(0).GetTrainLastTile(a_Engine).GetTileRight()))).GetTileSum();
        int htrainLargestTile = mPlayerHand.get((FindLargestTileIndex(a_Trains.get(1).GetTrainLastTile(a_Engine).GetTileRight()))).GetTileSum();
        int ctrainLargestTile = mPlayerHand.get((FindLargestTileIndex(a_Trains.get(2).GetTrainLastTile(a_Engine).GetTileRight()))).GetTileSum();

        if(aCanPlayM && aCanPlayH && aCanPlayC){
            int[] sums = {mtrainLargestTile, htrainLargestTile, ctrainLargestTile};
            return GetMaxIndex(sums);
        }
        else if(aCanPlayM && aCanPlayH){
            int[] sums = {mtrainLargestTile, htrainLargestTile, -10};
            return GetMaxIndex(sums);
        }
        else if(aCanPlayH && aCanPlayC){
            int[] sums = {-10, htrainLargestTile, ctrainLargestTile};
            return GetMaxIndex(sums);
        }
        else if(aCanPlayM && aCanPlayC){
            int[] sums = {mtrainLargestTile, -10, ctrainLargestTile};
            return GetMaxIndex(sums);
        }
        else if(aCanPlayM) return 0;
        else if(aCanPlayH) return 1;
        else return 2;
    }

    /**
     Function to return the index of the highest number from an array of ints
     @param aSums: an array of integer
     @return index of the largest number in the array
     */
    private int GetMaxIndex(int[] aSums){
        int largeValue = aSums[0], largeIndex = 0;
        for(int i=1; i<aSums.length; i++){
            if(aSums[i] > largeValue){
                largeValue = aSums[i];
                largeIndex = i;
            }
        }
        return largeIndex;
    }


    /**
     Function to find a double tile that can be played in the turn
     @param a_eligTrain: player's eligible trains
     @param a_Engine : engine tile
     @return index of the double tile playable from player's hand
     */
    public int FindDoubelTile(Train a_eligTrain, Tile a_Engine) {
        //map<int, Tile> TileIndexMap;
        Tile ValidTile = new Tile(-1, -1);
        //int doubleTileIndex = -1;
        for (int i = 0; i < mPlayerHand.size(); i++) {
            ValidTile = ValidateTile(mPlayerHand.get(i), a_eligTrain.GetTrainLastTile(a_Engine));
            if ((ValidTile.GetTileLeft() != -1) && ValidTile.IsDoubleTile()) {
                return i;
            }
        }
        return -1;
    }

    /**
     Function to check if the player can create an orphan train in its turn
     @param a_EligibleTrains: player's eligible trains
     @param a_Engine : engine tile
     @return map of strings with the information about the possibility of making an orphan train
     */
    public Map<String, String> MakeOrphanTrain(Vector<Train> a_EligibleTrains, Tile a_Engine) {
        Map<String, String> MakeOrphan = new HashMap<>();

        //creating a copy of player hand
        Vector<Tile> playerHandCopy = (Vector)mPlayerHand.clone();

        //not possible to make an orphan train if the # of eligible trains are only one
        if (a_EligibleTrains.size() <= 1) return MakeOrphan;

        for (int i = 0; i < a_EligibleTrains.size(); i++) {
            int doubleTileIndex = FindDoubelTile(a_EligibleTrains.get(i), a_Engine);
            if (doubleTileIndex != -1) {
                playerHandCopy.removeElementAt(doubleTileIndex);
                for (int j = 0; j < a_EligibleTrains.size(); j++) {
                    if (j != i) {
                        if (HasPlayableTile(a_EligibleTrains.get(j), playerHandCopy, a_Engine)) {
                            MakeOrphan.put("possible", "yes");
                            MakeOrphan.put("trainName", a_EligibleTrains.get(i).GetTrainName());
                            MakeOrphan.put("tile", mPlayerHand.get(doubleTileIndex).GetTileString());
                            MakeOrphan.put("tileIndex", Integer.toString(doubleTileIndex));
                            MakeOrphan.put("secondTrain", a_EligibleTrains.get(j).GetTrainName());
                            return MakeOrphan;
                        }
                    }
                }
            }
        }
        MakeOrphan.put("possible", "no");
        return MakeOrphan; // if here, can't make orphan
    }


    /**
     Function to help human to make winning moves in its turn
     @param a_trains : vector of all trains
     @param a_roundObj : round object of the current round
     @param a_Engine : engine tile
     */
    public void HelpHuman(Vector<Train> a_trains, Round a_roundObj, Tile a_Engine) {
        Vector<Train> a_EligibleTrains = FindEligibleTrains(a_roundObj.GetTrains().get(1).TrainHasMarker(),a_roundObj.GetTrains().get(2).TrainHasMarker(), a_trains);
        int hTrainIndex = -1; int mTrainIndex = -1; int cTrainIndex = -1;
        boolean canPlayHTrain = false; boolean canPlayMTrain = false; boolean canPlayCTrain = false;
        int playableTrainCount = 0;

        for (int i = 0; i < a_EligibleTrains.size(); i++) {
            if (a_EligibleTrains.get(i).GetTrainName() == "H-Train") {
                hTrainIndex = i;
                if (HasPlayableTile(a_EligibleTrains.get(hTrainIndex), mPlayerHand, a_Engine)) {
                    canPlayHTrain = true;
                    playableTrainCount++;
                }
            }
            if (a_EligibleTrains.get(i).GetTrainName() == "M-Train") {
                mTrainIndex = i;
                if (HasPlayableTile(a_EligibleTrains.get(mTrainIndex), mPlayerHand, a_Engine)) {
                    canPlayMTrain = true;
                    playableTrainCount++;
                }
            }
            if (a_EligibleTrains.get(i).GetTrainName() == "C-Train") {
                cTrainIndex = i;
                if (HasPlayableTile(a_EligibleTrains.get(cTrainIndex), mPlayerHand, a_Engine)) {
                    canPlayCTrain = true;
                    playableTrainCount++;
                }
            }
        }

        //Pick a train that can make an orphan train if possible
        Map<String, String> makeOrphan = MakeOrphanTrain(a_EligibleTrains, a_Engine);

        if (makeOrphan.get("possible") == "yes") {
            SetHumanHelp(makeOrphan.get("tile"), makeOrphan.get("trainName"),
                    " it is a double tile that can make an orphan train.");
            return;
        }

        //if more than 1 orphan double trains, break an orphan double train so that there is only 1 OD train.
        if((GetOrpDblTrain(a_trains).size()>1) && (playableTrainCount > 1)){
            int largestTrainIndex = FindLargestTrain(a_trains,
                                                    (canPlayMTrain && a_EligibleTrains.get(mTrainIndex).IsOrphDoubleTrain()),
                                                    (canPlayHTrain && a_EligibleTrains.get(hTrainIndex).IsOrphDoubleTrain()),
                                                    (canPlayCTrain && a_EligibleTrains.get(cTrainIndex).IsOrphDoubleTrain()), a_Engine);
            Tile largestTile = mPlayerHand.get(FindLargestTileIndex(a_trains.get(largestTrainIndex).GetTrainLastTile(a_Engine).GetTileRight()));
            SetHumanHelp(largestTile.GetTileString(), GetTrainName(largestTrainIndex), " it is the largest tile that can be played, breaking one of the two orphan trains.");
            return;
        }

        //if there are more than 1 playable trains and a train is an orphan train created by computer
        if(playableTrainCount > 1){
            //if m-train is an orphan train, pick another train
            if(canPlayMTrain && a_EligibleTrains.get(mTrainIndex).IsOrphDoubleTrain()){
                int largestTrainIndex = FindLargestTrain(a_trains, false, canPlayHTrain, canPlayCTrain, a_Engine);
                Tile largestTile = mPlayerHand.get(FindLargestTileIndex(a_trains.get(largestTrainIndex).GetTrainLastTile(a_Engine).GetTileRight()));
                SetHumanHelp(largestTile.GetTileString(), GetTrainName(largestTrainIndex), " it is the largest tile that can be played leaving Mexican Train as an orphan train.");
                return;
            }
            //if h-train is an orphan train, pick another train
            if(canPlayHTrain && a_EligibleTrains.get(hTrainIndex).IsOrphDoubleTrain()){
                int largestTrainIndex = FindLargestTrain(a_trains, canPlayMTrain, false, canPlayCTrain, a_Engine);
                Tile largestTile = mPlayerHand.get(FindLargestTileIndex(a_trains.get(largestTrainIndex).GetTrainLastTile(a_Engine).GetTileRight()));
                SetHumanHelp(largestTile.GetTileString(), GetTrainName(largestTrainIndex)," it is the largest tile that can be played leaving Human Train as an orphan train.");
                return;
            }
            //if c-train is an orphan train, pick another train
            if(canPlayCTrain && a_EligibleTrains.get(cTrainIndex).IsOrphDoubleTrain()){
                int largestTrainIndex = FindLargestTrain(a_trains, canPlayMTrain, canPlayHTrain, false, a_Engine);
                Tile largestTile = mPlayerHand.get(FindLargestTileIndex(a_trains.get(largestTrainIndex).GetTrainLastTile(a_Engine).GetTileRight()));
                SetHumanHelp(largestTile.GetTileString(), GetTrainName(largestTrainIndex)," it is the largest tile that can be played leaving Computer Train as an orphan train.");
                return;
            }
        }

        //if m-train is not yet started
        if(canPlayMTrain && a_trains.get(0).GetTrain().size() == 0){
            Tile largestTile = mPlayerHand.get(FindLargestTileIndex(a_trains.get(0).GetTrainLastTile(a_Engine).GetTileRight()));
            SetHumanHelp(largestTile.GetTileString(), "M-train", " can start Mexican train and starting Mexican train can be beneficial.");
            return;
        }

        //if none of the trains are orphan, return the train that can play the largest tile
        int largestTrain = FindLargestTrain(a_trains, canPlayMTrain, canPlayHTrain, canPlayCTrain, a_Engine);
        Tile largestTile = mPlayerHand.get(FindLargestTileIndex(a_trains.get(largestTrain).GetTrainLastTile(a_Engine).GetTileRight()));
        if(largestTrain == 2){
            if(playableTrainCount==1) SetHumanHelp(largestTile.GetTileString(), "C-train", " C-train is the must play Orphan Train.");
            else SetHumanHelp(largestTile.GetTileString(), "C-train", " C-train has a marker.");
        }else{
            SetHumanHelp(largestTile.GetTileString(), GetTrainName(largestTrain), " it is the largest tile that can be played in this turn among all the eligible trains.");
        }
    }

    /*
    Getter and setter for played double tile count in a turn for the player
    */
    public int GetPlayedDbtCount(){ return mPlayedDblCount; }
    public void SetDblCount(int a_dblCount){ mPlayedDblCount = a_dblCount; }

    /**
     Function to return the correct train index by checking the train name passed
     @param a_TrainName: train name as string
     @return index of the given train name
     */
    // returns the correct train index (as ordered in the mtrains vector), by checking the train name passed
    private int GetTrainIndex(String a_TrainName){
        if(a_TrainName.equals("M-Train")) return 0;
        else if(a_TrainName.equals("H-Train")) return 1;
        else return 2;
    }

    /**
     Function to return the correct train name by checking the train index passed
     @param aIndex index of the train
     @return name of the given train index
     */
    private String GetTrainName(int aIndex){
        if(aIndex == 0) return "M-train";
        if(aIndex == 1) return "H-train";
        return "C-train";
    }

    /*
    Function to return computer player's strategy, overridden in computer class
    */
    public String GetPlayerStrategy(){
        return  "";
    }


    /*
    Setter and Getter for human help
    */
    public String GetHumanHelp(){
        return  mHumanHelp;
    }
    void SetHumanHelp(String a_tile, String a_trainName, String a_message) {
        mHumanHelp = "I suggest you to play " + a_tile + " on " + a_trainName + " because " + a_message;
    }


    //virtual function, to update players info like marker after player plays a tile
    public void UpdatePlayerInfo(Train a_PlayedTrain, Tile a_PlayedTile) {}

    /*Private members of the class*/
    private Vector<Tile> mPlayerHand;
    private int mPlayerNumber, mPlayedDblCount;
    private boolean mHasToPlayOD, mPlayerDrewTile;
    private String mHumanHelp;
}
