package edu.ramapo.ssilwal.mexicantrain.model;

import java.util.Vector;

import edu.ramapo.ssilwal.mexicantrain.controller.DialogMessage;
import edu.ramapo.ssilwal.mexicantrain.controller.RoundPlayActivity;

public class Human extends Player{

    /**
     Virtual function, to find Human's eligible trains for the turn
     @param a_HTrainMarker: true if Human train has marker, false otherwise
     @param a_CTrainMarker: true if computer train has marker, false otherwise
     @param a_Trains: vector of the three trains M,H, and C
     @return vector of eligible train for computer
     */
    public Vector<Train> FindEligibleTrains(boolean a_HTrainMarker, boolean a_CTrainMarker, Vector<Train> a_Trains) {

        Vector<Train> eligTrains = new Vector<>();
        if ((GetOrpDblTrain(a_Trains).size()) > 0 && (HasToPlayOD() == true)) {
            eligTrains = GetOrpDblTrain(a_Trains);
        }
        else {
            eligTrains.add(a_Trains.get(0));
            eligTrains.add(a_Trains.get(1));

            //if computer train does not have marker, remove it from the list of eligible trains
            if (a_CTrainMarker) {
                eligTrains.add(a_Trains.get(2));
            }
        }
        return eligTrains;
    }


    /**
     Updates players state after playing a tile in a turn
     @param a_PlayedTrain: train played on by human
     @param a_PlayedTile: tile played by human
     */
    public void UpdatePlayerInfo(Train a_PlayedTrain, Tile a_PlayedTile) {
        if (a_PlayedTrain.GetTrainName() == "H-Train") {
            a_PlayedTrain.UpdateTrainMarker(false);
        }
    }

}
