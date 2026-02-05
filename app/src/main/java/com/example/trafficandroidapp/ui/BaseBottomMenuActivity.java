package com.example.trafficandroidapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trafficandroidapp.R;
import com.example.trafficandroidapp.ui.bookmark.BookmarkActivity;

/**
 * Base activity that handles the bottom navigation menu logic.
 * All activities with a bottom menu should extend this class.
 */
public abstract class BaseBottomMenuActivity extends AppCompatActivity {

    private View btnExplore;
    private View btnBookmark;
    private View btnProfile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Should be called after setContentView in child activities.
     * Sets up the bottom menu and highlights the active tab.
     * 
     * @param activeTab The currently active tab: "explore", "bookmark", or
     *                  "profile"
     */
    protected void setupBottomMenu(String activeTab) {
        btnExplore = findViewById(R.id.menuExplore);
        btnBookmark = findViewById(R.id.menuBookmark);
        btnProfile = findViewById(R.id.menuProfile);

        // Set the active tab
        if ("explore".equals(activeTab)) {
            setMenuSelected(btnExplore, true);
        } else if ("bookmark".equals(activeTab)) {
            setMenuSelected(btnBookmark, true);
        } else if ("profile".equals(activeTab)) {
            setMenuSelected(btnProfile, true);
        }

        // Set up click listeners
        btnExplore.setOnClickListener(v -> navigateToExplore());
        btnBookmark.setOnClickListener(v -> navigateToBookmark());
        btnProfile.setOnClickListener(v -> navigateToProfile());
    }

    /**
     * Navigate to the Explore (Maps) activity.
     * Override this method if you need custom navigation behavior.
     */
    protected void navigateToExplore() {
        if (!(this instanceof MapsActivity)) {
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            // Only finish if we're not already in Maps
            if (this instanceof BookmarkActivity) {
                finish();
            }
        }
    }

    /**
     * Navigate to the Bookmark activity.
     * Override this method if you need custom navigation behavior.
     */
    protected void navigateToBookmark() {
        if (!(this instanceof BookmarkActivity)) {
            Intent intent = new Intent(this, BookmarkActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        }
    }

    /**
     * Navigate to the Profile activity.
     * Override this method if you need custom navigation behavior.
     */
    protected void navigateToProfile() {
        if (!(this instanceof ProfileActivity)) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            // Only finish if coming from Maps
            if (this instanceof MapsActivity) {
                finish();
            }
        }
    }

    /**
     * Sets the selected state for a menu item and all its children.
     * 
     * @param container The menu item container view
     * @param selected  Whether the item should be selected
     */
    protected void setMenuSelected(View container, boolean selected) {
        container.setSelected(selected);
        if (container instanceof android.view.ViewGroup) {
            android.view.ViewGroup vg = (android.view.ViewGroup) container;
            for (int i = 0; i < vg.getChildCount(); i++) {
                vg.getChildAt(i).setSelected(selected);
            }
        }
    }
}
