package com.example.zooseeker_cse_110_team_30;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ExhibitViewModel extends AndroidViewModel {
    private LiveData<List<Exhibit>> exhibits;
    private final ExhibitDao exhibitDao;

    public ExhibitViewModel(@NonNull Application application){
        super(application);
        Context context = getApplication().getApplicationContext();
        ExhibitDatabase db = ExhibitDatabase.getSingleton(context);
        exhibitDao = db.exhibitDao();
    }
    public Exhibit query(String name) {
        return exhibitDao.getName(name);
    }

    public Exhibit tagQuery(String name) {
        return exhibitDao.getTag(name);
    }

    public List<Exhibit> allQuery() {
        return exhibitDao.getAll();
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

