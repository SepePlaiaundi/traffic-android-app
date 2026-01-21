package com.example.trafficandroidapp.ui;

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
import java.util.Set;

public class BookmarkAdapter
        extends RecyclerView.Adapter<BookmarkViewHolder> {

    private final List<Camera> items = new ArrayList<>();
    private Set<Long> bookmarkedIds;
    private BookmarkRepository repository;
    private Runnable onUnbookmarked;

    public void configure(Set<Long> ids,
                          BookmarkRepository repo,
                          Runnable onUnbookmarked) {
        this.bookmarkedIds = ids;
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
                bookmarkedIds,
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
