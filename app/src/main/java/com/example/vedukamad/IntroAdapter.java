// IntroAdapter.java
package com.example.vedukamad;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class IntroAdapter extends RecyclerView.Adapter<IntroAdapter.IntroViewHolder> {

    private List<IntroItem> introItemList;

    public IntroAdapter(List<IntroItem> introItemList) {
        this.introItemList = introItemList;
    }

    @NonNull
    @Override
    public IntroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_intro, parent, false);
        return new IntroViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IntroViewHolder holder, int position) {
        holder.bind(introItemList.get(position));
    }

    @Override
    public int getItemCount() {
        return introItemList.size();
    }

    public static class IntroViewHolder extends RecyclerView.ViewHolder {

        private TextView title, description;
        private ImageView image;

        public IntroViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.intro_title);
            description = itemView.findViewById(R.id.intro_description);
            image = itemView.findViewById(R.id.intro_image);
        }

        void bind(IntroItem item) {
            title.setText(item.getTitle());
            description.setText(item.getDescription());
            image.setImageResource(item.getImageRes());
        }
    }
}
