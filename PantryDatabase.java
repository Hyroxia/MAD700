package com.example.smartpantry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PantryDatabase extends SQLiteOpenHelper {
    public PantryDatabase(Context context) {
        super(context, "pantry.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE pantry (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, quantity REAL NOT NULL, unit TEXT NOT NULL)");

        db.execSQL("CREATE TABLE recipes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, steps TEXT NOT NULL)");

        db.execSQL("CREATE TABLE recipe_ingredients (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, recipe_id INTEGER, " +
                "name TEXT, quantity REAL, unit TEXT)");

        seedRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS recipe_ingredients");
        db.execSQL("DROP TABLE IF EXISTS recipes");
        db.execSQL("DROP TABLE IF EXISTS pantry");
        onCreate(db);
    }

    private void seedRecipes(SQLiteDatabase db) {
        addRecipe(db, "Tomato Pasta", "Boil pasta. Cook tomatoes in a pan. Mix together.",
                new String[]{"pasta", "tomato", "olive oil"},
                new double[]{200, 2, 1}, new String[]{"g", "each", "tbsp"});

        addRecipe(db, "Cheese Omelette", "Beat eggs. Cook in a pan and add cheese.",
                new String[]{"egg", "cheese", "butter"},
                new double[]{2, 30, 1}, new String[]{"each", "g", "tsp"});

        addRecipe(db, "Banana Oatmeal", "Cook oats with milk. Add sliced banana.",
                new String[]{"oats", "milk", "banana"},
                new double[]{50, 200, 1}, new String[]{"g", "ml", "each"});

        addRecipe(db, "Garlic Rice", "Cook rice. Fry with garlic and oil.",
                new String[]{"rice", "garlic", "olive oil"},
                new double[]{150, 1, 1}, new String[]{"g", "clove", "tbsp"});

        addRecipe(db, "Peanut Butter Toast", "Toast bread and spread peanut butter.",
                new String[]{"bread", "peanut butter"},
                new double[]{2, 2}, new String[]{"slice", "tbsp"});

        addRecipe(db, "Apple Yogurt Bowl", "Chop the apple and serve with yogurt.",
                new String[]{"apple", "yogurt"},
                new double[]{1, 150}, new String[]{"each", "g"});

        addRecipe(db, "Cheese Sandwich", "Put cheese between bread slices.",
                new String[]{"bread", "cheese"},
                new double[]{2, 40}, new String[]{"slice", "g"});

        addRecipe(db, "Tomato Egg Scramble", "Cook chopped tomatoes and eggs together.",
                new String[]{"tomato", "egg", "olive oil"},
                new double[]{1, 2, 1}, new String[]{"each", "each", "tsp"});

        addRecipe(db, "Garlic Bread", "Spread garlic butter on bread and toast.",
                new String[]{"bread", "garlic", "butter"},
                new double[]{2, 1, 1}, new String[]{"slice", "clove", "tbsp"});

        addRecipe(db, "Simple Pancakes", "Mix ingredients and cook small pancakes.",
                new String[]{"flour", "milk", "egg"},
                new double[]{100, 150, 1}, new String[]{"g", "ml", "each"});

        addRecipe(db, "Yogurt Banana Bowl", "Slice banana and mix with yogurt.",
                new String[]{"banana", "yogurt"},
                new double[]{1, 150}, new String[]{"each", "g"});

        addRecipe(db, "Rice and Egg", "Cook rice and top with a fried egg.",
                new String[]{"rice", "egg", "olive oil"},
                new double[]{150, 1, 1}, new String[]{"g", "each", "tsp"});

        addRecipe(db, "Apple Oatmeal", "Cook oats with milk. Add chopped apple.",
                new String[]{"oats", "milk", "apple"},
                new double[]{50, 200, 1}, new String[]{"g", "ml", "each"});

        addRecipe(db, "Tomato Cheese Toast", "Top bread with tomato and cheese. Toast.",
                new String[]{"bread", "tomato", "cheese"},
                new double[]{2, 1, 30}, new String[]{"slice", "each", "g"});

        addRecipe(db, "Peanut Banana Toast", "Spread peanut butter on toast and add banana.",
                new String[]{"bread", "peanut butter", "banana"},
                new double[]{2, 1, 1}, new String[]{"slice", "tbsp", "each"});
    }

    private void addRecipe(SQLiteDatabase db, String name, String steps,
                           String[] ingredients, double[] quantities, String[] units) {
        ContentValues recipeValues = new ContentValues();
        recipeValues.put("name", name);
        recipeValues.put("steps", steps);
        long recipeId = db.insert("recipes", null, recipeValues);

        for (int i = 0; i < ingredients.length; i++) {
            ContentValues item = new ContentValues();
            item.put("recipe_id", recipeId);
            item.put("name", ingredients[i]);
            item.put("quantity", quantities[i]);
            item.put("unit", units[i]);
            db.insert("recipe_ingredients", null, item);
        }
    }

    public long addIngredient(String name, double quantity, String unit) {
        ContentValues values = new ContentValues();
        values.put("name", name.trim());
        values.put("quantity", quantity);
        values.put("unit", unit.trim().toLowerCase());
        return getWritableDatabase().insert("pantry", null, values);
    }

    public int updateIngredient(int id, String name, double quantity, String unit) {
        ContentValues values = new ContentValues();
        values.put("name", name.trim());
        values.put("quantity", quantity);
        values.put("unit", unit.trim().toLowerCase());
        return getWritableDatabase().update("pantry", values, "id=?",
                new String[]{String.valueOf(id)});
    }

    public int deleteIngredient(int id) {
        return getWritableDatabase().delete("pantry", "id=?",
                new String[]{String.valueOf(id)});
    }

    public List<Ingredient> getIngredients() {
        List<Ingredient> result = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT id, name, quantity, unit FROM pantry ORDER BY name", null);

        try {
            while (c.moveToNext()) {
                result.add(new Ingredient(c.getInt(0), c.getString(1),
                        c.getDouble(2), c.getString(3)));
            }
        } finally {
            c.close();
        }
        return result;
    }

    public List<Recipe> getRecipes() {
        List<Recipe> result = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor recipes = db.rawQuery("SELECT id, name, steps FROM recipes", null);

        try {
            while (recipes.moveToNext()) {
                int id = recipes.getInt(0);
                List<Recipe.RecipeIngredient> items = new ArrayList<>();
                Cursor ingredients = db.rawQuery(
                        "SELECT name, quantity, unit FROM recipe_ingredients WHERE recipe_id=?",
                        new String[]{String.valueOf(id)});

                try {
                    while (ingredients.moveToNext()) {
                        items.add(new Recipe.RecipeIngredient(
                                ingredients.getString(0),
                                ingredients.getDouble(1),
                                ingredients.getString(2)));
                    }
                } finally {
                    ingredients.close();
                }

                result.add(new Recipe(id, recipes.getString(1),
                        recipes.getString(2), items));
            }
        } finally {
            recipes.close();
        }
        return result;
    }
}
