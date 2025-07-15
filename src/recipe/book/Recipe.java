/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package recipe.book;

/**
 *
 * @author LAPTOPBD
 */
public class Recipe {
    private int id;
    private String name;
    private String ingredients;
    private String description;
    private String category;
    private String budget;
    private int cookingTime;
    private String difficulty;

    public Recipe(int id, String name, String ingredients, String description,
                  String category, String budget, int cookingTime, String difficulty) {
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
        this.description = description;
        this.category = category;
        this.budget = budget;
        this.cookingTime = cookingTime;
        this.difficulty = difficulty;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getIngredients() { return ingredients; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public String getBudget() { return budget; }
    public int getCookingTime() { return cookingTime; }
    public String getDifficulty() { return difficulty; }
}
