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

/**
 * Adapter class from Exhibit-adjacent classes to RecyclerView.
 */
public class ExhibitAdapter extends RecyclerView.Adapter<ExhibitAdapter.ViewHolder> {
    private List<Exhibit> exhibits = Collections.emptyList(); //list of exhibits to display
    private Consumer<Exhibit> onCheckBoxClicked; //exhibit selection handler

    /**
     * Replaces the list of Exhibits to display with a completely new list.
     * @param newExhibits The new List of Exhibits to display.
     */
    public void setExhibitItems(List<Exhibit> newExhibits) {
        this.exhibits.clear(); //clear exhibits before reassigning for some reason //TODO
        this.exhibits = newExhibits;
        notifyDataSetChanged(); //"last resort" because entire dataset changed - mouseover for more
    }

    /**
     * Setter for CheckBox click handler (Exhibit selection).
     * @param onCheckBoxClicked The Consumer that handles the exhibit.xml/selected CheckBox.
     */
    public void setOnCheckBoxClickedHandler(Consumer<Exhibit> onCheckBoxClicked){
        this.onCheckBoxClicked = onCheckBoxClicked;
    }

    /**
     * Overridden method to instantiate a new ViewHolder from a layout XML file.
     * @param parent The parent ViewGroup (a special view that can contain other views).
     * @param viewType An unused variable in this implementation/
     * @return A ViewHolder (contains info about a View and its place within RecyclerView)/
     */
    @Override @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //create new View from exhibit.xml
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.exhibit, parent, false);
        return new ViewHolder(view); //encapsulate view within ViewHolder
    }

    /**
     * Overridden method to display a ViewHolder at a specified position.
     * @param holder The ViewHolder to display/
     * @param position The index of the Exhibit to display.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setExhibit(exhibits.get(position)); //set Exhibit of our ViewHolder implementation
    }

    /**
     * Overridden getter for number of Exhibits in this Adapter.
     * @return the number of Exhibit objects in this Adapter.
     */
    @Override
    public int getItemCount() {
        return exhibits.size();
    }

    /**
     * Overridden getter for a specific Exhibit's ID.
     * @param position the index of the Exhibit object to get the ID of.
     * @return the ID of the Exhibit at the specific index in the exhibit list.
     */
    @Override
    public long getItemId(int position) {
        return exhibits.get(position).id;
    }

    /**
     *
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final CheckBox checkBox;
        private Exhibit exhibit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.exhibit_text); //TODO: fill in with xml file objects
            this.checkBox = itemView.findViewById(R.id.selected); //TODO: fill in with xml file objects

            this.checkBox.setOnClickListener(view -> {
                if(onCheckBoxClicked == null) return;
                onCheckBoxClicked.accept(exhibit);
            });

        }

        public Exhibit getExhibit() {
            return exhibit;
        }

        public void setExhibit(Exhibit exhibit) {
            this.exhibit = exhibit;
            this.textView.setText(exhibit.name);
            this.checkBox.setChecked(exhibit.selected);
        }

    }
}
