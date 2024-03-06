package com.capstone.gmapproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ListViewer extends RecyclerView.Adapter<ListViewer.ChargerViewHolder> {
    private List<Charger> chargerList;
    private OnItemClickListener listener;
    // Constructor
    public ListViewer(List<Charger> chargerList, OnItemClickListener listener) {
        this.chargerList = chargerList;
        this.listener = listener;
    }

    //Interface for item click listener
    public interface OnItemClickListener {
        void onItemClick(Charger charger);
    }
    class ChargerViewHolder extends RecyclerView.ViewHolder {
        private TextView chargerTypeTextView;
        private TextView connectionTypeTextView;
        private TextView wattageTextView;

        //actual placeholder for charger object
        public ChargerViewHolder(@NonNull View itemView) {
            super(itemView);
            chargerTypeTextView = itemView.findViewById(R.id.txtShowChargerType);
            connectionTypeTextView = itemView.findViewById(R.id.txtShowConnectionType);
            wattageTextView = itemView.findViewById(R.id.txtShowWattage);

            //click listener for charger block
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(chargerList.get(position));
                }
            });
        }

        //translating charger params into view textfields.
        public void bind(Charger charger) {
            chargerTypeTextView.setText(String.format("%s", charger.getChargerType()));
            connectionTypeTextView.setText(String.format("Connection: %s", charger.getConnectionType()));
            wattageTextView.setText(String.format("Wattage: %s", charger.getWattage()));
        }
    }
    @NonNull
    @Override
    public ChargerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.charger, parent, false);
        return new ChargerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChargerViewHolder holder, int position) {
        Charger charger = chargerList.get(position);
        holder.bind(charger);
    }

    @Override
    public int getItemCount() {
        return chargerList.size();
    }
}
