package com.example.smartpantry;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class AddIngredientActivity extends Activity {
    PantryDatabase database;
    EditText nameInput, quantityInput, unitInput;
    int ingredientId = -1;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        database = new PantryDatabase(this);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(24, 24, 24, 24);

        nameInput = input(layout, "Ingredient name");
        quantityInput = input(layout, "Quantity (number)");
        unitInput = input(layout, "Unit (g, ml, each, etc.)");

        ingredientId = getIntent().getIntExtra("id", -1);
        if (ingredientId != -1) {
            nameInput.setText(getIntent().getStringExtra("name"));
            quantityInput.setText(String.valueOf(
                    getIntent().getDoubleExtra("quantity", 0)));
            unitInput.setText(getIntent().getStringExtra("unit"));
        }

        Button save = new Button(this);
        save.setText("Save");
        save.setOnClickListener(v -> saveIngredient());
        layout.addView(save);

        setContentView(layout);
    }

    private EditText input(LinearLayout layout, String hint) {
        EditText field = new EditText(this);
        field.setHint(hint);
        layout.addView(field);
        return field;
    }

    private void saveIngredient() {
        String name = nameInput.getText().toString().trim();
        String unit = unitInput.getText().toString().trim();
        String quantityText = quantityInput.getText().toString().trim();

        if (name.isEmpty() || unit.isEmpty() || quantityText.isEmpty()) {
            Toast.makeText(this, "Complete all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double quantity = Double.parseDouble(quantityText);
            if (quantity <= 0) {
                Toast.makeText(this, "Quantity must be greater than zero.",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (ingredientId == -1) {
                database.addIngredient(name, quantity, unit);
            } else {
                database.updateIngredient(ingredientId, name, quantity, unit);
            }
            finish();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Enter a valid quantity.", Toast.LENGTH_SHORT).show();
        }
    }
}
