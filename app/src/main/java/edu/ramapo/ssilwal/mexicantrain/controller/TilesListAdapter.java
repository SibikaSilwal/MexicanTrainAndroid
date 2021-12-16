package edu.ramapo.ssilwal.mexicantrain.controller;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.AlignSelf;
import com.google.android.flexbox.FlexboxLayoutManager;

import java.util.Vector;

import edu.ramapo.ssilwal.mexicantrain.model.Deck;
import edu.ramapo.ssilwal.mexicantrain.model.Round;
import edu.ramapo.ssilwal.mexicantrain.model.Tile;

public class TilesListAdapter extends RecyclerView.Adapter<TilesListAdapter.Holder> {

    private Vector<Tile> mTiles = new Vector<>();
    private Round mRoundObj;
    private Context mcontext;
    public boolean isClickable = true;
    public String recViewName = "";

    /**
     TilesListAdapter constructor
     @param mTiles: vector of tiles to be displayed in the recycler view
     @param a_roundObj: current round object passed from calling class
     @param a_context: context of the calling class
    */
    public TilesListAdapter(Vector<Tile> mTiles, Round a_roundObj, Context a_context) {
        this.mTiles = mTiles;
        mRoundObj = a_roundObj;
        mcontext = a_context;
    }

    /**
     Links the layout file designed for recycler view's single item
     @param parent:ViewGroup
     @param viewType: int
     @return returns the holder object after inflating the view with the layout file created for recycler vire
     */
    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.singletile, parent, false);
        return new Holder(view);
    }

    /**
     Binds the data with the view holder object for each item
     @param holder:an object of the Holder class
     @param position: the position of the binding item
     */
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

        // if the adapter is being used for player's hand tiles, user color: CYAN
        if(recViewName == "hhand" || recViewName == "chand"){
            holder.mTileBtn.setBackgroundColor(Color.CYAN);
            holder.mTileBtn.setText(mTiles.get(position).GetTileString());
        }

        // if the adapter is being used for C-trains reverse each tile, otherwise don't reverse tile
        if(recViewName == "mtrain" || recViewName == "htrain" || recViewName == "ctrain") {
            if(recViewName == "ctrain"){
                //for ctrain reverse each tile since it is going from right to left
                Tile tile = new Tile(mTiles.get(position).GetTileRight(), mTiles.get(position).GetTileLeft());
                holder.mTileBtn.setText(tile.GetTilePrint());
            }
            if(recViewName == "mtrain" || recViewName == "htrain") holder.mTileBtn.setText(mTiles.get(position).GetTilePrint());
        }

        // if the adapter is being used for trains use different color for each train
        if(recViewName == "mtrain") holder.mTileBtn.setBackgroundColor(Color.parseColor("#F4A460"));
        if(recViewName == "htrain") holder.mTileBtn.setBackgroundColor(Color.RED);

        holder.mTileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //check if tiles are clickable: Trains tile and computer tiles would not be clickable
                if(isClickable){
                    // picks the tile that human clicks and validates the tile for the picked train for the turn
                    if(mRoundObj.ValidatePickedTile(holder.mTileBtn.getText().toString(), mRoundObj.GetPlayers()[0])){
                        Tile pickedTile = mRoundObj.GetPlayers()[0].GetPlayerHand().get(position);

                        mRoundObj.GetPlayers()[0].GetPlayerHand().removeElementAt(position);

                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, mTiles.size());

                        String humansGame = "You selected a valid tile." + holder.mTileBtn.getText().toString();

                        // updates player's info like train marker
                        mRoundObj.GetPlayers()[0].UpdatePlayerInfo(mRoundObj.GetTrains().get(mRoundObj.GetPickedTrainIndex()), pickedTile);

                        ((RoundPlayActivity)mcontext).NotifyTrainAdapter();

                        //find next player
                        int nextPlayer = mRoundObj.FindNextPlayer();

                        // displays the message to human about tile picked and the next player
                        if(nextPlayer == 0)DialogMessage.DialogBox("Human's Game", humansGame+"\nSince, you played a double tile, it is your turn again.", mcontext);
                        else DialogMessage.DialogBox("Human's Game", humansGame+"\nIt is now computer's turn.", mcontext);


                        // Display menu for next player
                        ((RoundPlayActivity)mcontext).DisplayMenu(nextPlayer);

                    }
                    else {
                        // if invalid tile is picked, notifies human
                        DialogMessage.DialogBox("InValid !",
                                "You selected an invalid tile." + holder.mTileBtn.getText().toString() + " Tile pips do not match the train pips" +
                                        " Please select tile again.", mcontext);
                    }
                }
            }
        });
    }

    /**
     Returns the items counts for the recycler view
     @return item's counts
     */
    @Override
    public int getItemCount() {
        return mTiles.size();
    }

    /**
     View Holder class for recycler view adapter
     */
    class Holder extends RecyclerView.ViewHolder{
        Button mTileBtn;
        CardView mTile;
        public Holder(@NonNull View itemView) {
            super(itemView);
            mTileBtn = (Button)itemView.findViewById(R.id.b_singleTile);
            mTile = (CardView)itemView.findViewById(R.id.cardViewTile);
        }
    }
}
