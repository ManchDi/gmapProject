package com.capstone.gmapproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoryViewer extends RecyclerView.Adapter<HistoryViewer.HistoryViewHolder> {
    private List<HistoryEntry> historyList;
    private OnHistoryClickListener listener;
    // Constructor
    public HistoryViewer(List<HistoryEntry> history, OnHistoryClickListener listener) {
        this.historyList = history;
        this.listener = listener;
    }

    //Interface for item click listener
    public interface OnHistoryClickListener {
        void onHistoryClick(HistoryEntry history);
    }
    class HistoryViewHolder extends RecyclerView.ViewHolder {
        private TextView txtName;
        private TextView txtStreet;

        //actual placeholder for charger object
        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtStreet = itemView.findViewById(R.id.txtStreet);

            //click listener for history block
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onHistoryClick(historyList.get(position));
                }
            });
        }

        //translating charger params into view textfields.
        public void bind(HistoryEntry history) {
            txtName.setText(String.format("%s", history.getName()));
            txtStreet.setText(String.format("Connection: %s", history.getAddress()));
        }
    }
    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        HistoryEntry history = historyList.get(position);
        holder.bind(history);
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }
}
