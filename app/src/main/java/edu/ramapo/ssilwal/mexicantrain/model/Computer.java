package edu.ramapo.ssilwal.mexicantrain.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Computer extends Player{

    /*Constructor for computer class*/
    public Computer(){
        mStrategy = "";
    }

    /**
     Virtual function, to find Computer's eligible trains for the turn
     @param a_HTrainMarker: true if Human train has marker, false otherwise
     @param a_CTrainMarker: true if computer train has marker, false otherwise
     @param a_Trains: vector of the three trains M,H, and C
     @return vector of eligible train for computer
     */
    @Override
    public Vector<Train> FindEligibleTrains(boolean a_HTrainMarker, boolean a_CTrainMarker, Vector<Train> a_Trains) {

        Vector<Train> eligTrains = new Vector<>();
        if ((GetOrpDblTrain(a_Trains).size()) > 0 && (HasToPlayOD() == true)) {
            eligTrains = GetOrpDblTrain(a_Trains);
        }
        else{
            eligTrains.add(a_Trains.get(0));
            //if human train has marker, add it to the list of eligible trains
            if (a_HTrainMarker) {
                eligTrains.add(a_Trains.get(1));
            }
            eligTrains.add(a_Trains.get(2));
        }
        return eligTrains;
    }

    /**
     Virtual function to allow make computer pick a train to place tile on
     from it's eligigble trains
     @param a_trains: vector of all three trains
     @param a_EligibleTrains: vector of eligible trians
     @param a_Engine: engine tile
     @return train picked by the computer
     */
    @Override
    public Train PickAtrain(Vector<Train> a_trains, Vector<Train> a_EligibleTrains, Tile a_Engine) {
        System.out.println("Pick a train computer ran");
        int hTrainIndex = -1; int mTrainIndex = -1; int cTrainIndex = -1;
        boolean canPlayHTrain = false; boolean canPlayMTrain = false; boolean canPlayCTrain = false;
        int playableTrainCount = 0;

        for (int i = 0; i < a_EligibleTrains.size(); i++) {
            if (a_EligibleTrains.get(i).GetTrainName() == "H-Train") {
                hTrainIndex = i;
                if (HasPlayableTile(a_EligibleTrains.get(hTrainIndex), GetPlayerHand(), a_Engine)) {
                    canPlayHTrain = true;
                    playableTrainCount++;
                }
            }
            if (a_EligibleTrains.get(i).GetTrainName() == "M-Train") {
                mTrainIndex = i;
                if (HasPlayableTile(a_EligibleTrains.get(mTrainIndex), GetPlayerHand(), a_Engine)) {
                    canPlayMTrain = true;
                    playableTrainCount++;
                }
            }
            if (a_EligibleTrains.get(i).GetTrainName() == "C-Train") {
                cTrainIndex = i;
                if (HasPlayableTile(a_EligibleTrains.get(cTrainIndex), GetPlayerHand(), a_Engine)) {
                    canPlayCTrain = true;
                    playableTrainCount++;
                }
            }
        }

        //First priority: Pick a train that can make an orphan train if possible
        Map<String, String> makeOrphan = MakeOrphanTrain(a_EligibleTrains, a_Engine);
        if (makeOrphan.get("possible") == "yes") {
            mStrategy = " was the double tile on hand which can make an orphan train and force opponent to play on it.";
            for (int i = 0; i < a_EligibleTrains.size(); i++) {
                if (a_EligibleTrains.get(i).GetTrainName() == makeOrphan.get("trainName")) return a_EligibleTrains.get(i);
            }
        }

        //if more than 1 orphan double trains, break an orphan double train so that there is only 1 OD train.
        if((GetOrpDblTrain(a_trains).size()>1) && (playableTrainCount > 1)){
            mStrategy = " was the largest tile that could be played breaking one of the two orphan trains.";
            return a_trains.get(FindLargestTrain(a_trains,
                                (canPlayMTrain && a_EligibleTrains.get(mTrainIndex).IsOrphDoubleTrain()),
                                (canPlayHTrain && a_EligibleTrains.get(hTrainIndex).IsOrphDoubleTrain()),
                                (canPlayCTrain && a_EligibleTrains.get(cTrainIndex).IsOrphDoubleTrain()), a_Engine));
        }

        //if there are more than 1 playable trains
        if(playableTrainCount > 1){
            //if m-train is an orphan train, pick another train
            if(canPlayMTrain && a_EligibleTrains.get(mTrainIndex).IsOrphDoubleTrain()){
                mStrategy = " was the largest tile that could be played leaving Mexican Train as an orphan train.";
                return a_trains.get(FindLargestTrain(a_trains, false, canPlayHTrain, canPlayCTrain, a_Engine));
            }
            //if h-train is an orphan train, pick another train
            if(canPlayHTrain && a_EligibleTrains.get(hTrainIndex).IsOrphDoubleTrain()){
                mStrategy = " was the largest tile that could be played leaving the orphan Human Train created by computer.";
                return a_trains.get(FindLargestTrain(a_trains, canPlayMTrain, false, canPlayCTrain, a_Engine));
            }
            //if c-train is an orphan train, pick another train
            if(canPlayCTrain && a_EligibleTrains.get(cTrainIndex).IsOrphDoubleTrain()){
                mStrategy = " was the largest tile that could be played leaving Computer Train as an orphan train.";
                return a_trains.get(FindLargestTrain(a_trains, canPlayMTrain, canPlayHTrain, false, a_Engine));
            }
        }

        //if m-train is not yet started
        if(canPlayMTrain && a_trains.get(0).GetTrain().size()==0){
            mStrategy = " was the largest tile on hand and Mexican train was not started yet. ";
            return a_trains.get(0);
        }

        //if none of the trains are orphan, return the train that can play the largest tile
        int pickedTrain = FindLargestTrain(a_trains, canPlayMTrain, canPlayHTrain, canPlayCTrain, a_Engine);
        if(pickedTrain == 1){
            if(a_EligibleTrains.size()==1) mStrategy = " H-train was the must play Orphan Train.";
            else mStrategy = " had a marker.";
        }else{
            mStrategy = " was the largest tile that could be played in this turn among all the eligible trains.";
        }

        return a_trains.get(pickedTrain);

    }


    /**
     Virtual function to make computer pick a tile to place on the
     picked train
     @param a_PickedTrain: train picked to place the tile on
     @param a_Engine: engine tile
     @return index of the picked tile from computer's hand
     */
    @Override
    public int PickATile(Train a_PickedTrain, Tile a_Engine) {

        System.out.println("pick a tile computer ran");
        int endTilePips = a_PickedTrain.GetTrainLastTile(a_Engine).GetTileRight();

        int tileIndex;

        int doubleTileIndex = FindDoubelTile(a_PickedTrain, a_Engine);

        //if playable double tile is found, play double tile, else play the largest pips tile
        if (doubleTileIndex != -1) {
            tileIndex = doubleTileIndex;

            if(mStrategy==" was the largest tile that could be played in this turn among all the eligible trains."){
                mStrategy = "Computer picked " + a_PickedTrain.GetTrainName() + " and " + GetPlayerHand().get(tileIndex).GetTileString()
                        + " because it was the double tile that could be played in this turn.";
            }else{
                mStrategy = "Computer picked " + a_PickedTrain.GetTrainName() + " and " + GetPlayerHand().get(tileIndex).GetTileString()
                        +  " because it" + mStrategy;
            }

        }
        else {
            tileIndex = FindLargestTileIndex(endTilePips);
            mStrategy = "Computer picked "+ a_PickedTrain.GetTrainName() + " and "+
                    GetPlayerHand().get(tileIndex).GetTileString() + " because it" + mStrategy;
        }

        return tileIndex;
    }

    /**
     Updates players state after playing a tile in a turn
     @param a_PlayedTrain: train played on by the computer
     @param a_PlayedTile: tile played by the computer
     */
    @Override
    public void UpdatePlayerInfo(Train a_PlayedTrain, Tile a_PlayedTile) {
        if (a_PlayedTrain.GetTrainName() == "C-Train") {
            a_PlayedTrain.UpdateTrainMarker(false);
        }
    }

    /* Return computer's strategy as a string*/
    public String GetPlayerStrategy(){
        return  mStrategy;
    }

    //stores computer strategy
    private String mStrategy;
}
