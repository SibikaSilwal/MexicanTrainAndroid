package edu.ramapo.ssilwal.mexicantrain.controller;


import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import edu.ramapo.ssilwal.mexicantrain.model.Game;

        /************************************************************
        * Name:  Sibika Silwal                                     *
        * Project:  Mexican Train JAVA, #3                         *
        * Class:  MainActivity Controller Class                    *
        * Date:  11/17/2021                                        *
        ************************************************************/

// launching activity that asks user if they want to start a new game or load an old game
public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_STORAGE = 200;
    private Button mLoadGameBtn, mStartGameBtn, mHeadBtn, mTailBtn;
    private ImageView mImgHead, mImgTail;
    private EditText mFileNameInput;
    private ConstraintLayout mLoadGame, mTossCoin;

    private Game mGameObj = new Game();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //mContext = getApplicationContext();

        InitializeView();

        //setting permissions to read file from user's external storage
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_STORAGE);
        }

        mStartGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTossCoin.setVisibility(View.VISIBLE);
                mLoadGame.setVisibility(View.GONE);
            }
        });

    }

    /**
     Determines the first player based on coin toss
     @param view: head or tail image view clicked by the user
     */
    public void DetermineFirstPlayer(View view) {
        String humanChoice="";
        switch (view.getId()) {
            case R.id.i_head:
                humanChoice = "H";
            case R.id.i_tail:
                humanChoice="T";
        }
        Intent intent = new Intent(MainActivity.this, RoundPlayActivity.class);
        intent.putExtra("gameType", "newGame");
        intent.putExtra("gameObj", mGameObj);
        intent.putExtra("roundNum", Integer.toString(mGameObj.GetRoundNum()));
        if(mGameObj.TossACoin(humanChoice)){
            DialogMessage.DialogBox("Toss Result", "Human won the toss. Therefore, human plays first", MainActivity.this);
            intent.putExtra("player", Integer.toString(mGameObj.GetPlayer()));
            startActivity(intent);
            //startActivity(new Intent(getApplicationContext(), RoundPlayActivity.class));
        }else{
            DialogMessage.DialogBox("Toss Result", "Human lost the toss. Therefore, computer plays first", MainActivity.this);
            intent.putExtra("player", Integer.toString(mGameObj.GetPlayer()));
            startActivity(intent);
        }
    }

    /**
     Displays the files list in a dialog box to load game from a file
     @param view: Load Game button
     */
    public void loadGame(View view) {

        AlertDialog.Builder load_file_dialog = new AlertDialog.Builder(MainActivity.this);
        load_file_dialog.setTitle("Please select a game file");

        //Download is where I saved the files on device.
        String file_directory = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Download";
        File directory = new File(file_directory);
        final File[] files_list = directory.listFiles();

        Vector<String> files_name = new Vector<>(files_list.length);

        //getting the names of the file and add them to the list of files_name if the file's extension is .txt
        for (File file : files_list) {
            String file_name = file.getName();
            if (file_name.endsWith(".txt")){
                files_name.add(file_name);
            }
        }

        String[] File_Names = new String[files_name.size()];
        File_Names = files_name.toArray(File_Names);

        // displays the list of files and starts another activity when a file is chosen.
        load_file_dialog.setItems(File_Names, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int these) {
                ListView select = ((AlertDialog) dialog).getListView();
                String fileName = (String) select.getAdapter().getItem(these);
                Intent intent = new Intent(MainActivity.this, RoundPlayActivity.class);
                intent.putExtra("filepath", file_directory + "/" + fileName);
                intent.putExtra("gameType", "load");
                intent.putExtra("gameObj", mGameObj);
                startActivity(intent);
            }
        });
        load_file_dialog.show();
    }

    /**
     Initializes the views used in the activity and variables information received from the calling activity
     */
    private void InitializeView(){
        mLoadGameBtn = findViewById(R.id.b_loadGame);
        mStartGameBtn = findViewById(R.id.b_newGame);
        mHeadBtn = findViewById(R.id.b_head);
        mTailBtn = findViewById(R.id.b_tail);
        mLoadGame = findViewById(R.id.l_getLoadGame);
        mTossCoin = findViewById(R.id.l_gettossCoin);
        mImgHead = findViewById(R.id.i_head);
        mImgTail = findViewById(R.id.i_tail);
    }
}