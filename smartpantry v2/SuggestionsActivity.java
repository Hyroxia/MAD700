package com.example.smartpantry;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class SuggestionsActivity extends Activity {
    PantryDatabase database;
    List<Recipe> matches;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        database = new PantryDatabase(this);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(24, 24, 24, 24);

        NavigationBar.addTo(this, layout);

        TextView title = new TextView(this);
        title.setText("Recipes you can make now");
        title.setTextSize(22);
        layout.addView(title);

        ListView listView = new ListView(this);
        layout.addView(listView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));

        matches = RecipeMatcher.findMatches(
                database.getRecipes(), database.getIngredients());

        if (matches.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("No recipes match your pantry yet — add more ingredients.");
            layout.addView(empty);
        } else {
            listView.setAdapter(new ArrayAdapter<>(
                    this, android.R.layout.simple_list_item_1, recipeNames(matches)));

            listView.setOnItemClickListener((parent, view, position, id) -> {
                Intent intent = new Intent(this, RecipeDetailActivity.class);
                intent.putExtra("recipeId", matches.get(position).id);
                startActivity(intent);
            });
        }

        setContentView(layout);
    }

    private String[] recipeNames(List<Recipe> recipes) {
        String[] names = new String[recipes.size()];
        for (int i = 0; i < recipes.size(); i++) {
            names[i] = recipes.get(i).name;
        }
        return names;
    }
}
