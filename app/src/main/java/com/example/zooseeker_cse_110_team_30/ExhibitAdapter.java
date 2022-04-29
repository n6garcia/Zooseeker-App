package com.example.zooseeker_cse_110_team_30;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ExhibitAdapter extends RecyclerView.Adapter<ExhibitAdapter.ViewHolder> {
    private List<Exhibit> exhibits = Collections.emptyList();
    private Consumer<Exhibit> onCheckBoxClicked;

    public void setExhibitItems(List<Exhibit> newTodoItems) {
        this.exhibits.clear();
        this.exhibits = newTodoItems;
        notifyDataSetChanged();
    }

    public void setOnCheckBoxClickedHandler(Consumer<Exhibit>onCheckBoxClicked){
        this.onCheckBoxClicked = onCheckBoxClicked;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.exhibit, parent, false); //TODO: create xml file for cells
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setTodoListItems(exhibits.get(position));
    }

    @Override
    public int getItemCount() {
        return exhibits.size();
    }

    @Override
    public long getItemId(int position) {
        return exhibits.get(position).id;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private Exhibit exhibit;
        private CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.exhibit_text); //TODO: fill in with xml file objects
            this.checkBox = itemView.findViewById(R.id.selected); //TODO: fill in with xml file objects

            this.checkBox.setOnClickListener(view -> {
                if(onCheckBoxClicked == null) return;
                onCheckBoxClicked.accept(exhibit);
            });

        }

        public Exhibit getTodoListItem() {
            return exhibit;
        }

        public void setTodoListItems(Exhibit exhibit) {
            this.exhibit = exhibit;
            this.textView.setText(exhibit.name);
            this.checkBox.setChecked(exhibit.selected);
        }

    }
}
