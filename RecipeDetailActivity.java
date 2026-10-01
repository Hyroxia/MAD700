package com.example.smartpantry;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RecipeDetailActivity extends Activity {
    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);

        PantryDatabase database = new PantryDatabase(this);
        int recipeId = getIntent().getIntExtra("recipeId", -1);
        Recipe selected = null;

        for (Recipe recipe : database.getRecipes()) {
            if (recipe.id == recipeId) {
                selected = recipe;
                break;
            }
        }

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(24, 24, 24, 24);

        if (selected == null) {
            TextView error = new TextView(this);
            error.setText("Recipe not found.");
            layout.addView(error);
        } else {
            addText(layout, selected.name, 24);
            addText(layout, "Ingredients", 20);

            for (Recipe.RecipeIngredient ingredient : selected.ingredients) {
                addText(layout, "• " + ingredient, 16);
            }

            addText(layout, "Method", 20);
            addText(layout, selected.steps, 16);
        }

        setContentView(layout);
    }

    private void addText(LinearLayout layout, String text, int size) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextSize(size);
        view.setPadding(0, 8, 0, 8);
        layout.addView(view);
    }
}
