package edu.ramapo.ssilwal.mexicantrain.model;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Vector;

import edu.ramapo.ssilwal.mexicantrain.controller.DialogMessage;
import edu.ramapo.ssilwal.mexicantrain.controller.RoundPlayActivity;

public class Round {

    /**
     Constructor of round class, initializes all the member variables of round class
     */
    public Round() {
        mRoundNumber = 1;
        mHumanScore = 0;
        mComputerScore = 0;
        mPlayers[0] = new Human();
        mPlayers[0].SetPlayerNumber(0);
        mPlayers[1] = new Computer();
        mPlayers[1].SetPlayerNumber(1);
        mBoneYard = new Vector<>();
        mPickedTrainIndex = -1;
        mPickedTile = new Tile(-1, -1);
        mDrawnTile = new Tile(-1, -1);
        mMexicanTrain = new Train();
        mHumanTrain = new Train();
        mComputerTrain = new Train();
        mHumanTrain.SetTrainName("H-Train");
        mMexicanTrain.SetTrainName("M-Train");
        mComputerTrain.SetTrainName("C-Train");
        mTrains.add(mMexicanTrain);
        mTrains.add(mHumanTrain);
        mTrains.add(mComputerTrain);
    }

    /**
     Constructs and returns the engine tile for the round using the round number
     @return engine tile object
     */
    public Tile GetEngine() {
        int mod = mRoundNumber % 10;
        if (mod == 0) {
            return new Tile(0, 0);
        } else {
            Tile engine = new Tile((10 - mod), (10 - mod));
            return engine;
        }
    }

    /**
     Initializes a new round of the game, shuffles and deals deck
     */
    public void InitializeRound() {
        mDeckObj.RemoveEngine(GetEngine());
        mDeckObj.ShuffleDeck();

        Vector<Vector<Tile>> dealtTiles = mDeckObj.DealDeck();

        //setting player hands and boneyard tiles
        mPlayers[0].SetPlayerHand(dealtTiles.get(0));
        mPlayers[1].SetPlayerHand(dealtTiles.get(1));
        mBoneYard = dealtTiles.get(2);

    }


    /**
     Validates if the picked train is eligible and has playable tile for human
     @param a_pickedTrain: train picked by human
     @param a_player : human player object passed from activity class
     @return integer value returned by isGoodTrain function
     */
    public int ValidateTrain(String a_pickedTrain, Player a_player) {
        int goodTrain = a_player.isGoodTrain(a_player.FindEligibleTrains(mHumanTrain.TrainHasMarker(), mComputerTrain.TrainHasMarker(), mTrains), GetEngine(), a_pickedTrain);
        mPickedTrainIndex = goodTrain;
        return goodTrain;
    }

    /**
     Validates picked tile
     @param a_pickedTile: picked tile in string format
     @param a_player: player who picked the tile (human)
     @return true if picked tile valid false otherwise
     */
    public boolean ValidatePickedTile(String a_pickedTile, Player a_player) {

        mPickedTile = a_player.ValidateTile(Tile.MakeTile(a_pickedTile, "player"), mTrains.get(mPickedTrainIndex).GetTrainLastTile(GetEngine()));

        if (mPickedTile.GetTileLeft() == -1) return false;
        else {

            mTrains.get(mPickedTrainIndex).AddTileOnTrain(mPickedTile);
            //update has to play OD
            if (mPickedTile.IsDoubleTile()){
                a_player.SetHasToPlayOD(false);
            }
            else a_player.SetHasToPlayOD(true);
            return true;
        }
    }

    /**
     Checks if the drawn tile is playable for the player
     @param a_drawnTile: tile drawn from boneyard
     @param a_player: player who is playing and drew the tile
     @return true if tile not valid otherwise false
     */
    public boolean ValidateDrawnTile(Tile a_drawnTile, Player a_player) {
        Vector<Train> eligTrains = a_player.FindEligibleTrains(mHumanTrain.TrainHasMarker(), mComputerTrain.TrainHasMarker(), mTrains);
        mDrawnTile = a_drawnTile;
        return a_player.AddMarker(a_drawnTile, eligTrains, GetEngine(), mTrains, a_player.GetPlayerNumber());
    }

    /**
     Function to make a move for a player(computer) in their turn
     @param a_player: current player's object
     @return description of player's (computer) move for the turn
     */
    public String MakeMove(Player a_player) {

        Tile engine = GetEngine();
        // 1. get eligible trains
        Vector<Train> eligTrains = a_player.FindEligibleTrains(mHumanTrain.TrainHasMarker(), mComputerTrain.TrainHasMarker(), mTrains);

        // 2. check if player has tiles or need to draw from boneyard
        boolean hasPlayableTile = a_player.CheckPlayableTile(eligTrains, engine);

        if (!hasPlayableTile) {
            Tile drawnTile = mBoneYard.get(0);
            mBoneYard.removeElementAt(0);
            return DrawMove(drawnTile, eligTrains, engine);
        }

        // 3. If no drawing required
        Train pickedTrain = a_player.PickAtrain(mTrains, eligTrains, engine);

        int pickedTileIndex = a_player.PickATile(pickedTrain, engine);
        mPickedTileIndex = pickedTileIndex;
        Tile pickedTile = a_player.GetPlayerHand().get(pickedTileIndex);

        mPickedTile = pickedTile;
        Tile validTile = a_player.ValidateTile(pickedTile, pickedTrain.GetTrainLastTile(engine));

        //if valid tile is valid, remove tile from player's hand
        if (validTile.GetTileLeft() != -1) {
            a_player.GetPlayerHand().removeElementAt(pickedTileIndex);
            //add tile to train
            pickedTrain.AddTileOnTrain(validTile);
        }

        if (mPickedTile.IsDoubleTile()) a_player.SetHasToPlayOD(false);
        else a_player.SetHasToPlayOD(true);

        System.out.println("Picked Tile: " + pickedTile.GetTileString());

        // update players info like marker
        a_player.UpdatePlayerInfo(pickedTrain, pickedTile);

        return a_player.GetPlayerStrategy();
    }

    /**
     Draw move for computer, function called after the computer draws a tile in its turn
     @param a_DrawnTile: tile drawn from boneyard
     @param a_eligTrains : computer's eligible trains
     @param a_engine: engine tile
     @return description of computer's move for the turn
     */
    public String DrawMove(Tile a_DrawnTile, Vector<Train> a_eligTrains, Tile a_engine) {
        String message = "Computer did not have a playable tile. Therefore, Computer drew a tile from Boneyard.\nDrawn Tile: " + a_DrawnTile.GetTileString();

        //drawn TILE NOT playable
        if (ValidateDrawnTile(a_DrawnTile, mPlayers[1])) {
            mPickedTile = new Tile(-1, -2); // setting a random non-double tile as the picked tile for the turn
            mPlayers[1].SetHasToPlayOD(true);
            return message + "\nDrawn tile was not playable. Therefore, turn passed to human.";
        }
        else {
            // add the drawn tile to computer hand, and let computer pick a train
            mPlayers[1].GetPlayerHand().add(a_DrawnTile);
            Train pickedTrain = mPlayers[1].PickAtrain(mTrains, a_eligTrains, a_engine);
            mPickedTile = a_DrawnTile;
            Tile validTile = mPlayers[1].ValidateTile(mPickedTile, pickedTrain.GetTrainLastTile(a_engine));

            //if valid tile is valid, remove tile from player's hand
            if (validTile.GetTileLeft() != -1) {
                mPlayers[1].GetPlayerHand().removeElementAt(mPlayers[1].GetPlayerHand().size() - 1);

                //add tile to train
                pickedTrain.AddTileOnTrain(validTile);

                //update has to play OD
                if (mPickedTile.IsDoubleTile()) mPlayers[1].SetHasToPlayOD(false);
                else mPlayers[1].SetHasToPlayOD(true);

                mPlayers[1].UpdatePlayerInfo(pickedTrain, a_DrawnTile);
                return message + "\n>> Computer played drawn tile on " + pickedTrain.GetTrainName();
            }
        }

        //default, should not happen
        return message;
    }



    /**
     Find the player for next turn by checking the tile played
     @return 0 if nextplayer is human, 1 if computer
     */
    public int FindNextPlayer() {
        //if player played a double tile return same player
        if (mPickedTile.IsDoubleTile() == true) {
            return mNextPlayer;
        }
        else {
            if (mNextPlayer == 0) {
                mNextPlayer = 1;
                return mNextPlayer;
            } else {
                mNextPlayer = 0;
                return mNextPlayer;
            }
        }
    }

    /**
     Read game data from the file and pass to StartGameFromFile function to parse it
     @param agameObj: current game onject
     @param filepath: filepath to load file from
    */
    public void loadGame(Game agameObj, String filepath) {
        try {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                String path = filepath; //Environment.getExternalStorageDirectory().getAbsolutePath()+"/pinochlesave/"+filename;
                InputStream input = new FileInputStream(path);
                InputStream isCopy = new FileInputStream(path);
                StartGameFromFile(agameObj, isCopy);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     To start an old game from a file, parses the data stored in the file as string
     @param a_gameObject: current game object
     @param aInputStream : inputstream to read file data
     */
    void StartGameFromFile(Game a_gameObject, InputStream aInputStream) throws IOException {

        //vector of string that stores each word of the file
        Vector<String> gameInfo = new Vector<>(60);

        BufferedReader fileReader = new BufferedReader(new InputStreamReader(aInputStream));
        String gameData = fileReader.readLine();

        while (gameData != null) {
            gameData = gameData.trim();
            if(!gameData.isEmpty()){
                for (String oneWord : gameData.split("\\s+")) {
                    gameInfo.add(oneWord);
                }
            }
            gameData = fileReader.readLine();
        }

        mRoundNumber = Integer.parseInt(gameInfo.get(1));
        a_gameObject.SetRoundNum(mRoundNumber);

        a_gameObject.SetComputerGameScore(Integer.parseInt(gameInfo.get(4)));


        // using CreateTilesVector function to create the vector of tiles for player hands, providing the starting index
        Vector<Tile> ComputerHand = CreateTilesVector(6, "Train:", gameInfo, "hand");

        Vector<Tile> ComputerTrain = CreateTilesVector(5 + ComputerHand.size() + 2, "Human:", gameInfo, "C-train");

        Vector<Tile> HumanHand = CreateTilesVector(5 + ComputerHand.size() + 1 + ComputerTrain.size() + 5, "Train:", gameInfo, "hand");

        //computing the start index for h-train since it will be determined by computer's hand, train, and human hands size in the file
        int HTrainStartIndex = 5 + ComputerHand.size() + 1 + ComputerTrain.size() + 4 + HumanHand.size() + 2;

        Vector<Tile> HumanTrain = CreateTilesVector(HTrainStartIndex, "Mexican", gameInfo, "Htrain");

        int MtrainStartIndex = HTrainStartIndex + HumanTrain.size() + 2;

        Vector<Tile> MexicanTrain = CreateTilesVector(MtrainStartIndex, "Boneyard:", gameInfo, "Mtrain");

        int BoneyardStartIndex = MtrainStartIndex + MexicanTrain.size() + 1;

        Vector<Tile> Boneyard = CreateTilesVector(BoneyardStartIndex, "Next", gameInfo, "boneyard");

        //after structuring the data, setting them to the member variables of round class, trains, and players objects

        mBoneYard = Boneyard;
        //SetBoneYard(Boneyard);

        int humanScoreIndex = 5 + ComputerHand.size() + 1 + ComputerTrain.size() + 3;
        a_gameObject.SetHumanGameScore(Integer.parseInt(gameInfo.get(humanScoreIndex)));

        mPlayers[1].SetPlayerHand(ComputerHand);

        //adding trains tiles from file to the actual computer train object

        if(ComputerTrain.get(0).GetTileLeft() == -2){
            ComputerTrain.removeElementAt(0);
            mTrains.get(2).UpdateTrainMarker(true);
        }
        for (int i = ComputerTrain.size() - 2; i >= 0; i--) { //changed -2 to -1
            mTrains.get(2).AddTileOnTrain(ComputerTrain.get(i));
        }

        mPlayers[0].SetPlayerHand(HumanHand);
        if(HumanTrain.get(HumanTrain.size()-1).GetTileLeft() == -2){
            HumanTrain.removeElementAt(HumanTrain.size()-1);
            mTrains.get(1).UpdateTrainMarker(true);
        }
        for (int i = 1; i < HumanTrain.size(); i++) {
            mTrains.get(1).AddTileOnTrain(HumanTrain.get(i));
        }

        // adding engine tile to M-train 0th index
        //mTrains.get(0).AddTileOnTrain(GetEngine());
        for (int i = 0; i < MexicanTrain.size(); i++) {
            mTrains.get(0).AddTileOnTrain(MexicanTrain.get(i));
        }

        //getting the next player from the file
        String nextPlayer = gameInfo.get(gameInfo.size() - 1);
        if (nextPlayer.equals("Human")) {
            mNextPlayer = 0;
        }else{
            mNextPlayer = 1;
        }
    }

    /**
     Writes game states to the given fileName.
     @param a_gameObject: current game object
     @param afileName : filename to store the information in
     */
    public void WriteToFile(Game a_gameObject, String afileName)
    {
        //save all the game information in a string in parts

        String round = "Round: " + mRoundNumber + "\n";
        String computerState = "Computer:\nScore: " + a_gameObject.GetComputerScore() + "\n";
        String computerHand = "Hand:";
        String computerTrain = "Train:";

        //if computer train has marker add "M" to the computerTrain string
        if (mComputerTrain.TrainHasMarker()) computerTrain = "Train: M";

        for (int i = 0; i < mPlayers[1].GetPlayerHand().size(); i++) {
            computerHand = computerHand + " " + mPlayers[1].GetPlayerHand().get(i).GetTileString();
        }

        for (int i = mTrains.get(2).GetTrain().size(); i > 0; i--) {
            computerTrain = computerTrain + " " + mTrains.get(2).GetTrain().get(i-1).PrintTileReverse();
        }

        computerTrain = computerTrain + " " + GetEngine().GetTileString() + "\n";

        computerState = computerState + computerHand + "\n" + computerTrain;

        String humanState = "Human:\nScore: " + a_gameObject.GetHumanScore() + "\n";
        String humanHand = "Hand:";
        String humanTrain = "Train:";

        for (int i = 0; i < mPlayers[0].GetPlayerHand().size(); i++) {
            humanHand = humanHand + " " + mPlayers[0].GetPlayerHand().get(i).GetTileString();
        }

        humanTrain = humanTrain + " " + GetEngine().GetTileString();
        for (int i = 0; i < mTrains.get(1).GetTrain().size(); i++) {
            humanTrain = humanTrain + " " + mTrains.get(1).GetTrain().get(i).GetTileString();
        }
        //if human train has marker add it to the end of human train string
        if(mHumanTrain.TrainHasMarker()) humanTrain = humanTrain + " M\n";
        else humanTrain = humanTrain + "\n";

        humanState = humanState + humanHand + "\n" + humanTrain;

        String mexicanTrain = "Mexican Train:";
        for (int i = 0; i < mTrains.get(0).GetTrain().size(); i++) {
            mexicanTrain = mexicanTrain + " " + mTrains.get(0).GetTrain().get(i).GetTileString();
        }
        mexicanTrain = mexicanTrain + "\n";

        String boneyard = "Boneyard:";
        for (int i = 0; i < mBoneYard.size(); i++) {
            boneyard = boneyard + " " + mBoneYard.get(i).GetTileString();
        }
        boneyard = boneyard + "\n";

        String nextPlayer = "Next Player:";

        //player index 1 is always computer's and 0 is always human's, if 1 next player is computer
        if (mNextPlayer == 1) {
            nextPlayer = nextPlayer + "\n" + "Computer";
        }
        //if 0 next player is human
        else {
            nextPlayer = nextPlayer + "\n" + "Human";
        }

        //append all the information saved in different string variables to one string variable which will be wrote to file
        String gameState = round + computerState + humanState + mexicanTrain + boneyard + nextPlayer;

        // Get the directory for the user's public pictures directory.
        final File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download");

        // Make sure the path directory exists.
        if(!path.exists())
        {
            // Make it, if it doesn't exit
            path.mkdirs();
        }

        final File file = new File(path, afileName);


        try
        {
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(gameState);

            myOutWriter.close();

            fOut.flush();
            fOut.close();
        }
        catch (IOException e)
        {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    /**
     Creates a vector of tile from a given vector of string
     @param a_startIndex: starting index in the string vector to create the tile from
     @param a_endString : string in the string vector at which it needs to stop creating tiles
     @param a_stringVector : vector of string that has the tiles
     @param a_tileFor: whom are we creating the tile vector for (ctrain, htrain, hands)
     @return returns the vector of tiles
     */
    private Vector<Tile> CreateTilesVector(int a_startIndex, String a_endString, Vector<String> a_stringVector, String a_tileFor) {
        Vector<Tile> tileVector = new Vector<>();
        Tile tileobj = new Tile(-1, -1);

        for (int i = a_startIndex; i < a_stringVector.size(); i++) {
            if (a_stringVector.get(i).equals(a_endString)) {
                return tileVector;
            };
            tileVector.add(tileobj.MakeTile(a_stringVector.get(i), a_tileFor));
        }

        return tileVector;
    }

    /**
     Getter for boneyard tiles
     */
    public Vector<Tile> GetBoneYard(){ return mBoneYard; }

    /**
     Getter for players objects
     */
    public Player[] GetPlayers(){ return mPlayers; }

    /**
    Getters for trains of the round
     */
    public Vector<Train> GetTrains(){return mTrains;}

    /**
    Getter for picked train Index of the current turn by any of the two players
     */
    public int GetPickedTrainIndex(){ return mPickedTrainIndex; }

    /**
     Getter for picked tile Index of the current turn by any of the two players
     */
    public int GetPickedTileIndex(){ return mPickedTileIndex; }

    /**
     Setter for round number
     */
    public void SetRoundNum(int a_RoundNum){ mRoundNumber = a_RoundNum; }

    /**
     Getter for round number
     */
    public int GetRoundNum(){ return mRoundNumber; }

    /**
     Setter for next player
     */
    public void SetPlayer(int a_playerIndex) { mNextPlayer = a_playerIndex; }

    /**
     Getter for current player
     */
    public int GetCurrentPlayer(){return mNextPlayer;}



    /*** Private member variables of the round class ***/

    private int mRoundNumber, mNextPlayer, mHumanScore, mComputerScore;

    private Deck mDeckObj = new Deck();

    private Vector<Tile> mHumanHand, mComputerHand, mBoneYard;

    private Train mMexicanTrain, mHumanTrain, mComputerTrain;

    private Vector<Train> mTrains = new Vector<Train>();

   //create player objects
    private Player[] mPlayers = new Player[2];

    private int mPickedTrainIndex, mPickedTileIndex;

    private Tile mPickedTile, mDrawnTile;
}
