package com.example.vedukamad;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> implements Filterable {
    private List<Event> events;
    private List<Event> eventsFull; // backup list
    private Context context;

    public EventAdapter(Context context, List<Event> events) {
        this.context = context;
        this.events = events;
        this.eventsFull = new ArrayList<>(events); // copy for filtering
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.event_card, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        Event event = events.get(position);
        holder.nameTextView.setText(event.getName());
        Picasso.get().load(event.getImageUrl()).into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EventDetailActivity.class);
            intent.putExtra("event_type", event.getName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public class EventViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        ImageView imageView;

        public EventViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.eventName);
            imageView = itemView.findViewById(R.id.eventImage);
        }
    }

    @Override
    public Filter getFilter() {
        return eventFilter;
    }

    private final Filter eventFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Event> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(eventsFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Event event : eventsFull) {
                    if (event.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(event);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            events.clear();
            events.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}
