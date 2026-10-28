package com.example.smartpantry;

public class Ingredient {
    public int id;
    public String name;
    public double quantity;
    public String unit;

    public Ingredient(int id, String name, double quantity, String unit) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    @Override
    public String toString() {
        return name + " — " + quantity + " " + unit;
    }
}
