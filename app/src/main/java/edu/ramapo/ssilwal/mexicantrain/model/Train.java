package edu.ramapo.ssilwal.mexicantrain.model;

import java.util.Vector;

public class Train {

    /*Constructor for Train class*/
    public Train(){
        mTrain = new Vector<Tile>();
    }

    //sets train's name eg: "H-train", "M-train"
    public void SetTrainName(String a_name) { mTrainName = a_name; };
    //gets train name
    public String GetTrainName() { return mTrainName; };

    /**
     Adds tile to the end of the train (vector of tiles)
     @param a_tile tile to add to the end of the train
    */
    public void AddTileOnTrain(Tile a_tile){
        mTrain.add(a_tile);
    }

    //returns the train i.e. the vector of tiles for the train
    public Vector<Tile> GetTrain() { return mTrain; };

    /**
     Returns last tile of a train
     @param a_Engine engine tile
     @return last tile of the train if train is empty returns engine
     */
    public Tile GetTrainLastTile(Tile a_Engine){
        if(mTrain.size()>0)
            return mTrain.get(mTrain.size() - 1);
        return a_Engine;
    }

    /**
     Updates the train marker to true or false
     @param a_marker true or false to set train marker
    */
    public void UpdateTrainMarker(boolean a_marker) { mTrainMarker = a_marker; }

    /**
     Getter for train marker
     @return true or false
     */
    public boolean TrainHasMarker() { return mTrainMarker; };

    /**
    Check if the train is an orphan double train
     @return true if orphan double, false otherwise
     */
    public boolean IsOrphDoubleTrain() {
        int lastIndex = GetTrain().size() - 1;

        if (lastIndex < 0) {
            return false;
        }
        if (GetTrain().get(lastIndex).IsDoubleTile()) {
            return true;
        }

        return false;
    }

    /*** Private member variables ***/

    //train's name
    private String mTrainName;

    //the actual train which is a vector of tiles
    private Vector<Tile> mTrain;

    //train's marker
    private boolean mTrainMarker;
}
