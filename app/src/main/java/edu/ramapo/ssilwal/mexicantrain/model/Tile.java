package edu.ramapo.ssilwal.mexicantrain.model;

import java.util.Vector;

public class Tile {

    /**
     Constructor of tile class
     @param a_TileLeft : left pips of the tile
     @param a_TileRight : right pips of the tile
     */
    public Tile(int a_TileLeft, int a_TileRight) {
        mTileLeft = a_TileLeft;
        mTileRight = a_TileRight;
    }

    /* Setter and getter for tile left and right pips */
    public int GetTileLeft(){return mTileLeft;}
    void SetTileLeft(int a_left) { mTileLeft = a_left; };

    public int GetTileRight(){return mTileRight;}
    void SetTileRight(int a_right) { mTileRight = a_right; };


    /**
     Funtion to check if a tile is a double tile
     @return true or false
    */
     public boolean IsDoubleTile() {

        if (GetTileLeft() == GetTileRight()) {
            return true;
        }
        return false;
    }


    /**
     Function to create a tile object from a given string
     @param a_TileString : string to generate a tile from
     @param a_TileFor : string identifier for tile being generated for
     @return the tile object generated from the string
     */
    public static Tile MakeTile(String a_TileString, String a_TileFor) {
        System.out.println("Make tile: "+a_TileString);

        if (a_TileString.equals("M")) {
            return new Tile(-2, -2);
        }
        if (a_TileFor.equals("C-train")) {
            // char to int conversion : referred to stackoverflow forum for this conversion technique
            int tileLeft = a_TileString.charAt(2) - '0';
            int tileRight = a_TileString.charAt(0) - '0';
            return new Tile(tileLeft, tileRight);
        }
        // char to int conversion : referred to stackoverflow forum for this conversion technique
        int tileLeft = a_TileString.charAt(0) - '0';
        int tileRight = a_TileString.charAt(2) - '0';
        return new Tile(tileLeft, tileRight);
    }


    /**
     Function to return the tile as string in reverse (right-left)
     @return reversed string for the tile
     */
    public String PrintTileReverse() {
        String tile = mTileRight + "-" +mTileLeft;
        return  tile;
    }

    /**
     Function to return the tile object as a string
     @return string version of the tile
     */
    public String GetTileString(){
        return Integer.toString(mTileLeft) + ":" + Integer.toString(mTileRight);
    }

    /**
     Function to print the tile horizontally if non-double and vertically if double
     */
    public String GetTilePrint(){
        if(IsDoubleTile()) return Integer.toString(mTileLeft) + "\n" + Integer.toString(mTileRight);
        else return Integer.toString(mTileLeft) + ":" + Integer.toString(mTileRight);
    }

    /**
     Function to return the sum of the right and left pips of the tile
     @return sum of tile pips
     */
    public int GetTileSum(){
        return mTileLeft + mTileRight;
    }


    /*** Private member variables ***/

    //LEFT AND RIGHT pips of the tile
    private int mTileLeft;
    private int mTileRight;
}
