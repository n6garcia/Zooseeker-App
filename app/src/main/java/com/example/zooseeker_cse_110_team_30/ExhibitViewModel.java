package com.example.zooseeker_cse_110_team_30;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.Collections;
import java.util.List;

public class ExhibitViewModel extends AndroidViewModel {
    private LiveData<List<Exhibit>> exhibits;
    private final ExhibitDao exhibitDao;
    private List<Exhibit> visitList;

    public ExhibitViewModel(@NonNull Application application){
        super(application);
        Context context = getApplication().getApplicationContext();
        ExhibitDatabase db = ExhibitDatabase.getSingleton(context);
        exhibitDao = db.exhibitDao();
        visitList = Collections.emptyList();
    }

    public LiveData<List<Exhibit>> getExhibits() {
        if(exhibits == null) {
            loadUsers();
        }
        return exhibits;
    }

    private void loadUsers() {
        exhibits = exhibitDao.getAllLive();
    }

    public Exhibit query(String search) {
        return exhibitDao.getSearch(search);
    }

    public Exhibit tagQuery(String name) {
        return exhibitDao.getTag(name);
    }

    public List<Exhibit> allQuery() {
        return exhibitDao.getAll();
    }

    /**
     * Handles exhibit selection toggle, updates exhibit in DAO and list of selected exhibits
     *
     * @param exhibit the
     */
    public void toggleSelected(Exhibit exhibit) {
        exhibit.selected = !exhibit.selected; //toggle selection
        exhibitDao.update(exhibit); //update DAO

        //update visit list
        if(!exhibit.selected) { //after toggle, unselected
            visitList.remove(exhibit); //remove if not selected anymore
            System.out.println("removed " + exhibit.name + ", size " +visitList.size());
        }
        else { //after toggle, selected
            visitList.add(exhibit); //add if now selected
            System.out.println("added " + exhibit.name + ", size " +visitList.size());
        }

        //asdasdas.setText(visitList.size()) for US6
    }

    /**
     * Getter for visitList.
     * @return the visitList field in this Object.
     */
    public List<Exhibit> getVisitList() {
        return visitList;
    }
/**
    public LiveData<List<Exhibit>> getTodoListItems(){
        if(exhibits == null){
            loadUsers();
        }
        return exhibits;
    }

    private void loadUsers(){
        exhibits = todoListItemDao.getAllLive();
    }

    public void toggleCompleted(Exhibit todoListItem){
        todoListItem.selected = !todoListItem.selected;
        todoListItemDao.update(todoListItem);
    }
*/
}

