package com.example.smartpantry;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;

public class NavigationBar {
    public static void addTo(Activity activity, LinearLayout screen) {
        LinearLayout bar = new LinearLayout(activity);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        bar.setGravity(Gravity.CENTER);
        bar.setBackgroundColor(Color.rgb(44, 91, 68));
        bar.setPadding(4, 4, 4, 4);

        addButton(activity, bar, "Pantry", MainActivity.class);
        addButton(activity, bar, "Recipes", SuggestionsActivity.class);
        addButton(activity, bar, "Settings", SettingsActivity.class);

        screen.addView(bar, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
    }

    private static void addButton(Activity activity, LinearLayout bar,
                                  String label, Class<?> destination) {
        Button button = new Button(activity);
        button.setText(label);
        button.setTextSize(12);
        button.setTextColor(Color.WHITE);
        button.setBackgroundColor(Color.TRANSPARENT);
        button.setOnClickListener(v -> {
            if (!activity.getClass().equals(destination)) {
                activity.startActivity(new Intent(activity, destination));
            }
        });

        bar.addView(button, new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
    }
}

