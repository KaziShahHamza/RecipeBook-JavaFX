/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package recipe.book;

/**
 *
 * @author LAPTOPBD
 */

public class Validator {

    // Email Validation (standard format)
    public static boolean isEmailValid(String email) {
        return email != null && email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }

    // Password must contain uppercase, lowercase, number, symbol, and 8-20 characters
    public static boolean isPasswordValid(String password) {
        if (password == null) return false;
        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,20}$");
    }

    // Username: only lowercase letters and numbers, 3-15 characters, no space or symbols
    public static boolean isUsernameValid(String username) {
        if (username == null) return false;
        return username.matches("^[a-z0-9]{6,20}$");
    }

    // Recipe Name: 8–100 characters
    public static boolean isRecipeNameValid(String name) {
        return name != null && name.length() >= 6 && name.length() <= 30;
    }

    // Ingredients: 8–100 characters
    public static boolean isIngredientsValid(String ingredients) {
        return ingredients != null && ingredients.length() >= 8 && ingredients.length() <= 100;
    }

    // Description: 20–350 characters
    public static boolean isDescriptionValid(String description) {
        return description != null && description.length() >= 20 && description.length() <= 350;
    }

    // Cooking Time: 5–240 minutes
    public static boolean isCookingTimeValid(int input) {
        try {
            int time = input;
            return time >= 5 && time <= 240;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}

