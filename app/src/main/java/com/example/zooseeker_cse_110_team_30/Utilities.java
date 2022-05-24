package com.example.zooseeker_cse_110_team_30;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

/**
 * Utility class for all other classes to use
 * @author CSE 110 instructors
 */
public class Utilities {
    static boolean reprompt;
    /**
     * Displays an alert to the screen. The only option is to cancel the alert.
     * @param activity The activity in which to display the alert.
     * @param message The message of the alert.
     */
    public static void showAlert(Activity activity, String message) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);

        alertBuilder
                .setTitle("Alert!") //title, hardcoded
                .setMessage(message)
                .setPositiveButton("Ok", (dialog, id) -> dialog.cancel())
                .setCancelable(true);

        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show(); ///show dialog
    }

    public static boolean showReplanAlert(Activity activity) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
        alertBuilder
                .setTitle("Alert!") //title, hardcoded
                .setMessage("Off track, replan?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        reprompt = true;
                        dialogInterface.cancel();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        reprompt = false;
                        dialogInterface.cancel();
                    }
                })
                .setCancelable(true);

        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
        return reprompt;
    }
}
