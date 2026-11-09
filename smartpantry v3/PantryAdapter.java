package com.example.smartpantry;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

public class PantryAdapter extends BaseAdapter {
    private final Activity activity;
    private final PantryDatabase database;
    private final List<Ingredient> ingredients;
    private final Runnable refreshList;

    public PantryAdapter(Activity activity,
                         PantryDatabase database,
                         List<Ingredient> ingredients,
                         Runnable refreshList) {
        this.activity = activity;
        this.database = database;
        this.ingredients = ingredients;
        this.refreshList = refreshList;
    }

    @Override
    public int getCount() {
        return ingredients.size();
    }

    @Override
    public Object getItem(int position) {
        return ingredients.get(position);
    }

    @Override
    public long getItemId(int position) {
        return ingredients.get(position).id;
    }

    @Override
    public View getView(int position, View recycledView, ViewGroup parent) {
        Ingredient item = ingredients.get(position);

        LinearLayout row = new LinearLayout(activity);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(4, 8, 4, 8);
        row.setBackgroundColor(Color.rgb(245, 248, 245));

        TextView details = new TextView(activity);
        details.setText(String.format(
                Locale.getDefault(), "%s — %s %s",
                item.name, item.quantity, item.unit));
        details.setTextSize(16);

        row.addView(details, new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        Button minusButton = new Button(activity);
        minusButton.setText("−1");
        minusButton.setOnClickListener(v -> changeQuantity(item, -1));
        row.addView(minusButton);

        Button plusButton = new Button(activity);
        plusButton.setText("+1");
        plusButton.setOnClickListener(v -> changeQuantity(item, 1));
        row.addView(plusButton);

        Button deleteButton = new Button(activity);
        deleteButton.setText("Delete");
        deleteButton.setOnClickListener(v -> {
            database.deleteIngredient(item.id);
            refreshList.run();
        });
        row.addView(deleteButton);

        return row;
    }

    private void changeQuantity(Ingredient item, double change) {
        double newQuantity = item.quantity + change;

        if (newQuantity <= 0) {
            database.deleteIngredient(item.id);
        } else {
            database.updateIngredient(
                    item.id, item.name, newQuantity, item.unit);
        }

        refreshList.run();
    }
}

