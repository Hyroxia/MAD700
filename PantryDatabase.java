package com.example.smartpantry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PantryDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "pantry.db";
    private static final int DATABASE_VERSION = 3;

    public PantryDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
        seedDemoData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Create any missing tables without deleting existing user data.
        createTables(db);
        seedDemoData(db);
    }

    private void createTables(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS pantry (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL, " +
                        "quantity REAL NOT NULL, " +
                        "unit TEXT NOT NULL)"
        );

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS recipes (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL, " +
                        "steps TEXT NOT NULL)"
        );

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS recipe_ingredients (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "recipe_id INTEGER NOT NULL, " +
                        "name TEXT NOT NULL, " +
                        "quantity REAL NOT NULL, " +
                        "unit TEXT NOT NULL)"
        );
    }

    private void seedDemoData(SQLiteDatabase db) {
        Cursor countCursor = db.rawQuery("SELECT COUNT(*) FROM pantry", null);
        boolean pantryIsEmpty;

        try {
            pantryIsEmpty = countCursor.moveToFirst() && countCursor.getInt(0) == 0;
        } finally {
            countCursor.close();
        }

        if (pantryIsEmpty) {
            insertDemoPantryItem(db, "tomato", 2, "each");
            insertDemoPantryItem(db, "pasta", 200, "g");
            insertDemoPantryItem(db, "olive oil", 1, "tbsp");
            insertDemoPantryItem(db, "cheese", 50, "g");
        }

        addDemoRecipeIfMissing(
                db,
                "One-Ingredient Tomato",
                "Slice and eat the tomato, or warm it in a pan.",
                new String[]{"tomato"},
                new double[]{1},
                new String[]{"each"}
        );

        addDemoRecipeIfMissing(
                db,
                "Tomato Pasta",
                "Boil the pasta. Warm the tomato and olive oil in a pan, then combine.",
                new String[]{"pasta", "tomato", "olive oil"},
                new double[]{200, 2, 1},
                new String[]{"g", "each", "tbsp"}
        );

        addDemoRecipeIfMissing(
                db,
                "Cheese Sandwich",
                "Put the cheese between two slices of bread.",
                new String[]{"bread", "cheese"},
                new double[]{2, 30},
                new String[]{"slice", "g"}
        );
    }

    private void insertDemoPantryItem(
            SQLiteDatabase db, String name, double quantity, String unit) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("quantity", quantity);
        values.put("unit", unit);
        db.insert("pantry", null, values);
    }

    private void addDemoRecipeIfMissing(
            SQLiteDatabase db,
            String name,
            String steps,
            String[] ingredients,
            double[] quantities,
            String[] units) {

        Cursor cursor = db.rawQuery(
                "SELECT id FROM recipes WHERE name=?",
                new String[]{name}
        );

        boolean exists;
        try {
            exists = cursor.moveToFirst();
        } finally {
            cursor.close();
        }

        if (!exists) {
            insertRecipe(db, name, steps, ingredients, quantities, units);
        }
    }

    private long insertRecipe(
            SQLiteDatabase db,
            String name,
            String steps,
            String[] ingredients,
            double[] quantities,
            String[] units) {

        if (ingredients.length != quantities.length
                || ingredients.length != units.length) {
            return -1;
        }

        db.beginTransaction();
        try {
            ContentValues recipeValues = new ContentValues();
            recipeValues.put("name", name);
            recipeValues.put("steps", steps);

            long recipeId = db.insert("recipes", null, recipeValues);
            if (recipeId == -1) {
                return -1;
            }

            for (int i = 0; i < ingredients.length; i++) {
                ContentValues item = new ContentValues();
                item.put("recipe_id", recipeId);
                item.put("name", ingredients[i]);
                item.put("quantity", quantities[i]);
                item.put("unit", units[i]);

                if (db.insert("recipe_ingredients", null, item) == -1) {
                    return -1;
                }
            }

            db.setTransactionSuccessful();
            return recipeId;
        } finally {
            db.endTransaction();
        }
    }

    public long addIngredient(String name, double quantity, String unit) {
        if (name == null || name.trim().isEmpty()) {
            return -1;
        }
        if (unit == null || unit.trim().isEmpty()) {
            return -1;
        }
        if (quantity < 0 || Double.isNaN(quantity) || Double.isInfinite(quantity)) {
            return -1;
        }

        ContentValues values = new ContentValues();
        values.put("name", name.trim());
        values.put("quantity", quantity);
        values.put("unit", unit.trim().toLowerCase(Locale.ROOT));

        return getWritableDatabase().insert("pantry", null, values);
    }

    public int updateIngredient(int id, String name, double quantity, String unit) {
        if (id <= 0 || name == null || name.trim().isEmpty()
                || unit == null || unit.trim().isEmpty()
                || quantity < 0 || Double.isNaN(quantity) || Double.isInfinite(quantity)) {
            return 0;
        }

        ContentValues values = new ContentValues();
        values.put("name", name.trim());
        values.put("quantity", quantity);
        values.put("unit", unit.trim().toLowerCase(Locale.ROOT));

        return getWritableDatabase().update(
                "pantry",
                values,
                "id=?",
                new String[]{String.valueOf(id)}
        );
    }

    public int deleteIngredient(int id) {
        return getWritableDatabase().delete(
                "pantry",
                "id=?",
                new String[]{String.valueOf(id)}
        );
    }

    public List<Ingredient> getIngredients() {
        List<Ingredient> result = new ArrayList<>();

        try (Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT id, name, quantity, unit FROM pantry ORDER BY name",
                null
        )) {
            while (cursor.moveToNext()) {
                result.add(new Ingredient(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getDouble(2),
                        cursor.getString(3)
                ));
            }
        }

        return result;
    }

    public List<Recipe> getRecipes() {
        List<Recipe> result = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        try (Cursor recipes = db.rawQuery(
                "SELECT id, name, steps FROM recipes ORDER BY name",
                null
        )) {
            while (recipes.moveToNext()) {
                int recipeId = recipes.getInt(0);
                List<Recipe.RecipeIngredient> items = new ArrayList<>();

                try (Cursor ingredients = db.rawQuery(
                        "SELECT name, quantity, unit " +
                                "FROM recipe_ingredients WHERE recipe_id=?",
                        new String[]{String.valueOf(recipeId)}
                )) {
                    while (ingredients.moveToNext()) {
                        items.add(new Recipe.RecipeIngredient(
                                ingredients.getString(0),
                                ingredients.getDouble(1),
                                ingredients.getString(2)
                        ));
                    }
                }

                result.add(new Recipe(
                        recipeId,
                        recipes.getString(1),
                        recipes.getString(2),
                        items
                ));
            }
        }

        return result;
    }
}
