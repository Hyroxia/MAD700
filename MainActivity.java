package com.example.smartpantry;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends Activity {
    PantryDatabase database;
    ListView listView;
    List<Ingredient> ingredients;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        database = new PantryDatabase(this);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(24, 24, 24, 24);

        NavigationBar.addTo(this, layout);

        TextView title = new TextView(this);
        title.setText("Smart Pantry — My Pantry");
        title.setTextSize(24);
        layout.addView(title);

        addButton(layout, "Add ingredient", AddIngredientActivity.class);
        addButton(layout, "Suggested recipes", SuggestionsActivity.class);
        addButton(layout, "Settings", SettingsActivity.class);

        listView = new ListView(this);
        layout.addView(listView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));

        setContentView(layout);
        refreshList();
    }

    private void addButton(LinearLayout layout, String label, Class<?> screen) {
        Button button = new Button(this);
        button.setText(label);
        button.setOnClickListener(v -> startActivity(new Intent(this, screen)));
        layout.addView(button);
    }

    private void refreshList() {
        ingredients = database.getIngredients();
        listView.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, ingredients));

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Ingredient item = ingredients.get(position);
            Intent intent = new Intent(this, AddIngredientActivity.class);
            intent.putExtra("id", item.id);
            intent.putExtra("name", item.name);
            intent.putExtra("quantity", item.quantity);
            intent.putExtra("unit", item.unit);
            startActivity(intent);
        });

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            database.deleteIngredient(ingredients.get(position).id);
            refreshList();
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (database != null && listView != null) refreshList();
    }
}
