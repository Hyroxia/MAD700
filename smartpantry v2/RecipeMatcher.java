package com.example.smartpantry;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecipeMatcher {
    public static List<Recipe> findMatches(
            List<Recipe> recipes, List<Ingredient> pantry) {
        List<Recipe> matches = new ArrayList<>();

        for (Recipe recipe : recipes) {
            boolean canMake = true;

            for (Recipe.RecipeIngredient required : recipe.ingredients) {
                Ingredient found = null;

                for (Ingredient item : pantry) {
                    if (normalize(item.name).equals(normalize(required.name))) {
                        found = item;
                        break;
                    }
                }

                if (found == null
                        || !found.unit.equalsIgnoreCase(required.unit)
                        || found.quantity < required.quantity) {
                    canMake = false;
                    break;
                }
            }

            if (canMake) {
                matches.add(recipe);
            }
        }

        return matches;
    }

    private static String normalize(String name) {
        String value = name.trim().toLowerCase(Locale.ROOT);

        // Simple handling for common plurals, such as tomato/tomatoes.
        if (value.endsWith("ies") && value.length() > 3) {
            return value.substring(0, value.length() - 3) + "y";
        }
        if (value.endsWith("oes") && value.length() > 3) {
            return value.substring(0, value.length() - 2);
        }
        if (value.endsWith("s") && !value.endsWith("ss") && value.length() > 1) {
            return value.substring(0, value.length() - 1);
        }

        return value;
    }
}
