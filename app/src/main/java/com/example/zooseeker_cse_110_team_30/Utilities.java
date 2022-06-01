package com.example.zooseeker_cse_110_team_30;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import java.util.List;

/**
 * Utility class for all other classes to use
 * @author CSE 110 instructors
 */
public class Utilities {
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

    /**
     * Returns a replan alert with yes or no choices. The alert handles all replan logic.
     * @param activity The activity in which to display the alert.
     */
    public static AlertDialog getReplanAlert(DirectionsActivity activity) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
        alertBuilder
                .setTitle("Alert!") //title, hardcoded
                .setMessage("Off track, replan?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        activity.replan();
                        activity.updateDirections();
                        dialogInterface.cancel();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .setCancelable(true);

        AlertDialog alertDialog = alertBuilder.create();
        return alertDialog;
    }

    /**
     * Returns a clear selected alert with yes or no choices. The alert handles all clearing logic.
     * @param activity The activity in which to display the alert.
     */
    public static AlertDialog getClearSelectedAlert(MainActivity activity) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
        alertBuilder
                .setTitle("Are you sure you want to clear all selected exhibits?") //title, hardcoded
                .setMessage("This action cannot be undone.")
                .setPositiveButton("Yes, Clear", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        List<Exhibit> selectedList = activity.viewModel.getSelectedExhibits();
                        for(Exhibit exhibit : selectedList) {
                            //loops through all selected exhibits and toggle selection
                            activity.toggleSelected(exhibit);
                        }
                        activity.refreshExhibitDisplay(); //update UI
                        dialogInterface.cancel(); //close this dialog
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel(); //do nothing and close
                    }
                })
                .setCancelable(true);

        AlertDialog alertDialog = alertBuilder.create();
        return alertDialog;
    }
}
