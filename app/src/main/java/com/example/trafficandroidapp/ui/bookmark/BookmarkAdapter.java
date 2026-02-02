package com.example.trafficandroidapp.ui.bookmark;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trafficandroidapp.R;
import com.example.trafficandroidapp.models.Camera;
import com.example.trafficandroidapp.repository.BookmarkRepository;

import java.util.ArrayList;
import java.util.List;

public class BookmarkAdapter
        extends RecyclerView.Adapter<BookmarkViewHolder> {

    private final List<Camera> items = new ArrayList<>();
    private BookmarkRepository repository;
    private Runnable onUnbookmarked;

    public void configure(
            BookmarkRepository repo,
            Runnable onUnbookmarked
    ) {
        this.repository = repo;
        this.onUnbookmarked = onUnbookmarked;
    }

    @NonNull
    @Override
    public BookmarkViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bookmark, parent, false);
        return new BookmarkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull BookmarkViewHolder holder, int position) {

        holder.bind(
                items.get(position),
                repository,
                onUnbookmarked
        );
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<Camera> cameras) {
        items.clear();
        if (cameras != null) {
            items.addAll(cameras);
        }
        notifyDataSetChanged();
    }
}
