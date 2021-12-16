package edu.ramapo.ssilwal.mexicantrain.model;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Collections;
import java.util.Random;
import java.util.Vector;

public class Deck {

    /*
    Deck class constructor that created the 55 unique tiles for each round
    * */
    public Deck() {
        for (int i = 0; i <= 9; i++) {
            for (int j = i; j <= 9; j++) {
                mDeck.add(new Tile(i, j));
            }
        }
    }


    /**
     Function to shuffle the deck of tiles before dealing them so that in each
     round players get different set of tiles
     */
    void ShuffleDeck() {

        Random rand = new Random();

        int swapIndex1, swapIndex2;
        for (int i = 0; i < 27; i++) {
            swapIndex1 = rand.nextInt(54);
            swapIndex2 = rand.nextInt(54);
            if (swapIndex1 != swapIndex2) {
                Collections.swap(mDeck, swapIndex1, swapIndex2);
            }
        }
    }

    /**
     Removes engine tile from the deck before dealing the tile
     @param a_engine :engine tile to be removed from the deck
     */
    public void RemoveEngine(Tile a_engine){
       for(int i=0; i<mDeck.size(); i++){
            if((mDeck.get(i).GetTileLeft()==a_engine.GetTileLeft()) && (mDeck.get(i).GetTileRight()==a_engine.GetTileRight())) {
                mDeck.removeElementAt(i);
            }
       }
    }

    /**
     Function to deal the tiles to players and place remaining in the boneyard
     @return vector of players hands and boneyard tiles
     */
    public Vector<Vector<Tile>> DealDeck() {

        //temporary vector to hold the players tiles
        Vector<Tile> a_HumanHand = new Vector<Tile>(16);  Vector<Tile> a_ComputerHand = new Vector<Tile>(16);
        Vector<Tile> a_Boneyard = new Vector<Tile>(22);

        //sum of the tiles to be dealt to both players
        int playersHand = 32;

        //boneyard size
        int boneYard = mDeck.size() - playersHand;

        for (int i = 0; i < playersHand; i+=2) {
            a_HumanHand.add(mDeck.get(i));
            a_ComputerHand.add(mDeck.get(i+1));
        }
        for (int i = playersHand; i < (boneYard+playersHand) ; i++) {
            a_Boneyard.add(mDeck.get(i));
        }


        //deleting all the tiles from the deck
        mDeck.clear();

        //return vector that has players hands and boneyard tiles
        Vector<Vector<Tile>> returnVector = new Vector<>(3);
        returnVector.add(a_HumanHand); returnVector.add(a_ComputerHand); returnVector.add(a_Boneyard);

        return returnVector;
    }

    /**

     @param
     @return

    public void printDeck(){
        for(int i=0; i<mDeck.size(); i++){
            System.out.println(mDeck.get(i).GetTileString());
        }
    }

    /**

     @param
     @return

    public static void printDeck2(Vector<Tile> aDeck){
        System.out.println("size: "+ aDeck.size());
        for(int i=0; i<aDeck.size(); i++){
            System.out.print(i + " ->"+ aDeck.get(i).GetTileString() + " ");
        }
        System.out.println(".....................");
    }*/

    // deck of 55 tiles generated in the constructor
    private Vector<Tile> mDeck = new Vector<Tile>(55);
}
