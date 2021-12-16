package edu.ramapo.ssilwal.mexicantrain.controller;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.SystemClock;

public class DialogMessage {

    // DialogMessage empty constructor
    public DialogMessage(){}

    /**
     Displays the dismissable dialog pop up with title and message
     @param a_Title title for the dialog box
     @param  a_Message message for the dialog box
     @param  a_context context of the calling activity
     */
    public static void DialogBox(String a_Title, String a_Message, Context a_context) {

        // Create the object of AlertDialog Builder class
        AlertDialog.Builder builder = new AlertDialog.Builder(a_context);

        // Set dialog box title
        builder.setTitle(a_Title);

        // Set the message shown in the dialog box
        builder.setMessage(a_Message);


        // Set Cancelable false for not dismissing the dialog box if the user clicks somewhere outside the dialog box
        builder.setCancelable(false);

        // Set the positive button to dismiss the dialog box
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // If user clicks on ok then dialog box is dismissed.
                dialog.dismiss();
            }
        });

        // Create the Alert dialog
        AlertDialog alertDialog = builder.create();

        // Show the Alert Dialog box
        alertDialog.show();
    }
}
