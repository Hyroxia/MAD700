package com.example.smartpantry;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends Activity {
    private PantryDatabase database;
    private ListView pantryList;
    private TextView emptyMessage;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);

        database = new PantryDatabase(this);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(16, 16, 16, 16);

        NavigationBar.addTo(this, layout);

        TextView title = new TextView(this);
        title.setText("My Pantry");
        title.setTextSize(26);
        title.setPadding(4, 16, 4, 8);
        layout.addView(title);

        TextView hint = new TextView(this);
        hint.setText("Tap an ingredient to edit it. Use −1 or Delete to update your pantry.");
        layout.addView(hint);

        Button addButton = new Button(this);
        addButton.setText("＋ Add ingredient");
        addButton.setOnClickListener(v ->
                startActivity(new Intent(this, AddIngredientActivity.class)));
        layout.addView(addButton);

        emptyMessage = new TextView(this);
        emptyMessage.setText("Your pantry is empty. Add an ingredient to get started.");
        emptyMessage.setTextSize(16);
        emptyMessage.setPadding(4, 16, 4, 16);
        layout.addView(emptyMessage);

        pantryList = new ListView(this);
        layout.addView(pantryList, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));

        setContentView(layout);

        pantryList.setOnItemClickListener((parent, view, position, id) -> {
            List<Ingredient> items = database.getIngredients();
            if (position < items.size()) {
                Ingredient item = items.get(position);

                Intent intent = new Intent(this, AddIngredientActivity.class);
                intent.putExtra("id", item.id);
                intent.putExtra("name", item.name);
                intent.putExtra("quantity", item.quantity);
                intent.putExtra("unit", item.unit);
                startActivity(intent);
            }
        });

        refreshList();
    }

    private void refreshList() {
        List<Ingredient> ingredients = database.getIngredients();

        emptyMessage.setVisibility(ingredients.isEmpty()
                ? TextView.VISIBLE : TextView.GONE);

        PantryAdapter adapter = new PantryAdapter(
                this,
                database,
                ingredients,
                this::refreshList
        );

        pantryList.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (database != null && pantryList != null) {
            refreshList();
        }
    }
}
