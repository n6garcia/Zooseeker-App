package com.example.zooseeker_cse_110_team_30;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

/**
 * An extension to AndroidViewModel. Manages data for the UI.
 * @see "https://developer.android.com/reference/android/arch/lifecycle/ViewModel"
 */
public class ExhibitViewModel extends AndroidViewModel {
    private LiveData<List<Exhibit>> exhibits;
    private final ExhibitDao exhibitDao; //DAO containing all Exhibits
    private List<Exhibit> selectedList; //list of selected exhibits

    /**
     * Constructor for ExhibitViewModel.
     * @param application The application, maintains global application state.
     */
    public ExhibitViewModel(@NonNull Application application){
        super(application);
        Context context = getApplication().getApplicationContext(); //get this app's Context
        ExhibitDatabase db = ExhibitDatabase.getSingleton(context); //create a singleton
        exhibitDao = db.exhibitDao(); //get DAO from ExhibitDatabase
        selectedList = new ArrayList<>(); //ArrayList because needs to support remove()
    }

    /**
     * Getter for the list of live Exhibits.
     * @return A LiveData containing a list of Exhibits to monitor for changes.
     */
    public LiveData<List<Exhibit>> getExhibits() {
        if(exhibits == null) {
            loadExhibits(); //load exhibits if the list does not exist yet
        }
        return exhibits;
    }

    /**
     * Loads the List of Exhibits from the DAO if it has not yet been created.
     */
    private void loadExhibits() {
        exhibits = exhibitDao.getAllLive();
    }

    /**
     * Queries the DAO with the search term. To be called from classes without access to the DAO.
     * @param search The String to search the DAO for.
     * @return A list of all exhibits queried from the DAO.
     * @see ExhibitDao
     */
    public List<Exhibit> query(String search) {
        return exhibitDao.getSearch(search);
    }

    /**
     * Queries the DAO for all Exhibits with kind 'exhibit'.
     * @return A list of all exhibits queried from the DAO.
     * @see ExhibitDao
     */
    public List<Exhibit> getAllExhibits() {
        return exhibitDao.getAllExhibits();
    }

    /**
     * Handles exhibit selection toggle, updates exhibit in DAO and list of selected exhibits.
     * @param exhibit The Exhibit whose selection is being toggled.
     */
    public void toggleSelected(Exhibit exhibit) {
        exhibit.selected = !exhibit.selected; //toggle selection
        exhibitDao.update(exhibit); //update DAO

        //update selected list
        if(!exhibit.selected) { //after toggle, unselected
            selectedList.remove(exhibit); //remove if not selected anymore
            //System.out.println("removed "+exhibit.name+", size "+selectedList.size()); //debug
        }
        else { //after toggle, selected
            selectedList.add(exhibit); //add if now selected
            //System.out.println("added "+exhibit.name+", size "+selectedList.size()); //debug
        }

        //asdasdas.setText(selectedList.size()) for US6 //TODO
    }

    /**
     * Getter for the list of selected exhibits.
     * @return the List of all Exhibits selected by the user.
     */
    public List<Exhibit> getSelectedList() {
        return selectedList;
    }
}

