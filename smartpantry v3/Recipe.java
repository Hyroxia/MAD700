package com.example.smartpantry;

import java.util.List;

public class Recipe {
    public int id;
    public String name;
    public String steps;
    public List<RecipeIngredient> ingredients;

    public Recipe(int id, String name, String steps,
                  List<RecipeIngredient> ingredients) {
        this.id = id;
        this.name = name;
        this.steps = steps;
        this.ingredients = ingredients;
    }

    public static class RecipeIngredient {
        public String name;
        public double quantity;
        public String unit;

        public RecipeIngredient(String name, double quantity, String unit) {
            this.name = name;
            this.quantity = quantity;
            this.unit = unit;
        }

        @Override
        public String toString() {
            return name + " — " + quantity + " " + unit;
        }
    }
}
