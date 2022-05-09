package com.example.zooseeker_cse_110_team_30;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jgrapht.alg.util.Triple;

import java.util.Collections;
import java.util.List;

/**
 * Adapter class from Exhibit-adjacent classes to RecyclerView for the visit activity.
 * @see "https://developer.android.com/reference/androidx/recyclerview/widget/RecyclerView.Adapter"
 */
public class VisitExhibitAdapter extends RecyclerView.Adapter<VisitExhibitAdapter.ViewHolder> {
    //triple format: {Exhibit object, road name, total distance}
    //triples are ordered in the order in which the exhibits are visited.
    private List<Triple<Exhibit, String, Integer>> exhibits = Collections.emptyList();

    /**
     * Replaces the list of Exhibits to display with a completely new list.
     * @param newExhibits The new List of Exhibits to display.
     */
    public void setExhibits(List<Triple<Exhibit, String, Integer>> newExhibits) {
        this.exhibits.clear(); //clear before reassigning for some reason
        this.exhibits = newExhibits;
        notifyDataSetChanged(); //"last resort" because entire dataset changed - mouseover for more
    }

    /**
     * Overridden method to instantiate a new ViewHolder from a layout XML file.
     * @param parent The parent ViewGroup (a special view that can contain other views).
     * @param viewType An unused variable in this implementation.
     * @return A ViewHolder (contains info about a View and its place within RecyclerView).
     * @see "https://developer.android.com/reference/androidx/recyclerview/widget/RecyclerView.ViewHolder"
     * @see RecyclerView.Adapter javadocs
     */
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //create new View from exhibit.xml
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.visit_exhibit, parent, false);
        return new ViewHolder(view); //encapsulate view within ViewHolder
    }

    /**
     * Overridden method to display a ViewHolder at a specified position.
     * @param holder The ViewHolder to display.
     * @param position The index of the Exhibit to display.
     * @see RecyclerView.Adapter javadocs
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setExhibit(exhibits.get(position)); //set Exhibit of our ViewHolder implementation
    }

    /**
     * Overridden getter for number of Exhibits in this Adapter.
     * @return the number of Exhibit objects in this Adapter.
     * @see RecyclerView.Adapter javadocs
     */
    @Override
    public int getItemCount() {
        return exhibits.size();
    }

    /**
     * Overridden getter for a specific Exhibit's ID.
     * @param position the index of the Exhibit object to get the ID of.
     * @return the ID of the Exhibit at the specific index in the exhibit list.
     * @see RecyclerView.Adapter javadocs
     */
    @Override
    public long getItemId(int position) {
        return exhibits.get(position).getFirst().id;
    }

    /**
     * Implementation of RecyclerView.ViewHolder adapted specifically for Exhibits.
     * @see "https://developer.android.com/reference/androidx/recyclerview/widget/RecyclerView.ViewHolder"
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView; //exhibit name
        private final TextView locationTextView; //exhibit location
        private final TextView distanceTextView; //exhibit distance
        private Exhibit exhibit; //The specific Exhibit object in the View

        /**
         * Constructor for the Exhibit ViewHolder object.
         * @param itemView The View containing the required TextView and CheckBox.
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.nameTextView = itemView.findViewById(R.id.exhibit_name_text);
            this.locationTextView = itemView.findViewById(R.id.exhibit_location_text);
            this.distanceTextView = itemView.findViewById(R.id.exhibit_distance_text);
        }

        /**
         * Getter for this ViewHolder's Exhibit field.
         * @return The Exhibit stored within this ViewHolder.
         */
        public Exhibit getExhibit() {
            return exhibit;
        }

        /**
         * Setter for this ViewHolder's Exhibit field. Also updates the UI elements.
         * @param exhibitTriple The new Exhibit to use for this ViewHolder.
         */
        public void setExhibit(Triple<Exhibit, String, Integer> exhibitTriple) {
            this.exhibit = exhibitTriple.getFirst(); //exhibit object
            this.nameTextView.setText(this.exhibit.name); //extract name from object
            this.locationTextView.setText(exhibitTriple.getSecond()); //road name
            this.distanceTextView.setText(exhibitTriple.getThird() + "ft"); //distance
        }
    }
}