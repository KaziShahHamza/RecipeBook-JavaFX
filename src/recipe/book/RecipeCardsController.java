package recipe.book;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Button;
import javafx.geometry.Insets;
import javafx.scene.control.*;



public class RecipeCardsController {
    @FXML private FlowPane cardContainer;
    @FXML private TextField searchField;
    @FXML private CheckBox catBreakfast, catLunch, catDinner;
    @FXML private CheckBox diffEasy, diffMedium, diffHard;
    @FXML private CheckBox budget100, budget250, budget500;
    @FXML private TextField minTimeField, maxTimeField;
    @FXML private Button backButton;

    @FXML private void initialize() {
        if (Session.isLoggedIn()) {
            backButton.setText("Back to Home");
        } else {
            backButton.setText("Back to Login");
        }
        
        loadRecipeCards();
    }

    @FXML
    private void loadRecipeCards() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/recipedb", "root", "password");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name, ingredients, description, category, budget, cooking_time, difficulty FROM recipes")) {

            while (rs.next()) {
                String name = rs.getString("name");
                String description = rs.getString("description");
                String ingredients = rs.getString("ingredients");
                String category = rs.getString("category");
                String budget = rs.getString("budget");
                int time = rs.getInt("cooking_time");
                String difficulty = rs.getString("difficulty");

                VBox card = createRecipeCard(name, description, category, budget, time, difficulty, ingredients);

                cardContainer.getChildren().add(card);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private VBox createRecipeCard(String name, String description, String category, String budget, int time, String difficulty, String ingredients) {
        VBox card = new VBox(6);
        card.setPadding(new javafx.geometry.Insets(10, 10, 10, 10));
        card.setPrefSize(200, 180);
        card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-radius: 8; -fx-background-radius: 8;");

        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        Label ingredientsLabel = new Label(ingredients);

        Label descLabel = new Label(shorten(description, 80));
        descLabel.setWrapText(true);

        Label categoryLabel = new Label("Category: " + (category != null ? category : "N/A"));
        Label budgetLabel = new Label("Budget: " + (budget != null ? budget : "N/A"));
        Label timeLabel = new Label("Time: " + time + " min");
        Label difficultyLabel = new Label("Difficulty: " + (difficulty != null ? difficulty : "N/A"));

        card.getChildren().addAll(nameLabel, descLabel, categoryLabel, budgetLabel, timeLabel, difficultyLabel, ingredientsLabel);

        card.setOnMouseClicked(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("RecipeDetails.fxml"));
                Parent detailRoot = loader.load();

                // Get controller and set the selected recipe
                RecipeDetailsController controller = loader.getController();
                controller.setRecipe(new Recipe(0, name, ingredients, description, category, budget, time, difficulty), "recipescards");

                // Replace current scene with detail scene
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(detailRoot));
                stage.setTitle("Recipe Details 1 - " + name);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return card;
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String shorten(String text, int maxLength) {
        if (text == null) return "No description.";
        return text.length() <= maxLength ? text : text.substring(0, maxLength) + "...";
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            String fxmlToLoad = Session.isLoggedIn() ? "dashboard2.fxml" : "login.fxml";
            Parent root = FXMLLoader.load(getClass().getResource(fxmlToLoad));
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(Session.isLoggedIn() ? "Dashboard" : "Login");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    @FXML
    private void handleSearch(ActionEvent event) {
        String searchTerm = searchField.getText().trim().toLowerCase();

        cardContainer.getChildren().clear();

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/recipedb", "root", "password");
             PreparedStatement stmt = conn.prepareStatement("SELECT name, ingredients, description, category, budget, cooking_time, difficulty FROM recipes WHERE LOWER(name) LIKE ?")) {

            stmt.setString(1, "%" + searchTerm + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String name = rs.getString("name");
                String description = rs.getString("description");
                String ingredients = rs.getString("ingredients");
                String category = rs.getString("category");
                String budget = rs.getString("budget");
                int time = rs.getInt("cooking_time");
                String difficulty = rs.getString("difficulty");

                VBox card = createRecipeCard(name, description, category, budget, time, difficulty, ingredients);

                cardContainer.getChildren().add(card);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleFilter(ActionEvent event) {
        cardContainer.getChildren().clear();

        // Collect inputs
        String searchTerm = searchField.getText().trim().toLowerCase();

        List<String> selectedCategories = new ArrayList<>();
        if (catBreakfast.isSelected()) selectedCategories.add("Breakfast");
        if (catLunch.isSelected()) selectedCategories.add("Lunch");
        if (catDinner.isSelected()) selectedCategories.add("Dinner");

        List<String> selectedDifficulties = new ArrayList<>();
        if (diffEasy.isSelected()) selectedDifficulties.add("Easy");
        if (diffMedium.isSelected()) selectedDifficulties.add("Medium");
        if (diffHard.isSelected()) selectedDifficulties.add("Hard");

        List<String> selectedBudgets = new ArrayList<>();
        if (budget100.isSelected()) selectedBudgets.add("100–250");
        if (budget250.isSelected()) selectedBudgets.add("250–500");
        if (budget500.isSelected()) selectedBudgets.add("500+");

        String minTimeStr = minTimeField.getText().trim();
        String maxTimeStr = maxTimeField.getText().trim();

        // Build query
        StringBuilder query = new StringBuilder("SELECT name, ingredients, description, category, budget, cooking_time, difficulty FROM recipes WHERE 1=1");

        if (!searchTerm.isEmpty()) {
            query.append(" AND LOWER(name) LIKE ?");
        }

        if (!selectedCategories.isEmpty()) {
            query.append(" AND category IN (").append("?,".repeat(selectedCategories.size()).replaceAll(",$", "")).append(")");
        }

        if (!selectedDifficulties.isEmpty()) {
            query.append(" AND difficulty IN (").append("?,".repeat(selectedDifficulties.size()).replaceAll(",$", "")).append(")");
        }

        if (!selectedBudgets.isEmpty()) {
            query.append(" AND budget IN (").append("?,".repeat(selectedBudgets.size()).replaceAll(",$", "")).append(")");
        }

        if (!minTimeStr.isEmpty()) {
            query.append(" AND cooking_time >= ?");
        }

        if (!maxTimeStr.isEmpty()) {
            query.append(" AND cooking_time <= ?");
        }

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/recipedb", "root", "password");
             PreparedStatement stmt = conn.prepareStatement(query.toString())) {

            int index = 1;

            if (!searchTerm.isEmpty()) stmt.setString(index++, "%" + searchTerm + "%");
            for (String cat : selectedCategories) stmt.setString(index++, cat);
            for (String diff : selectedDifficulties) stmt.setString(index++, diff);
            for (String bud : selectedBudgets) stmt.setString(index++, bud);
            if (!minTimeStr.isEmpty()) stmt.setInt(index++, Integer.parseInt(minTimeStr));
            if (!maxTimeStr.isEmpty()) stmt.setInt(index++, Integer.parseInt(maxTimeStr));

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String name = rs.getString("name");
                String description = rs.getString("description"); 
                String ingredients = rs.getString("ingredients"); 
                String category = rs.getString("category");
                String budget = rs.getString("budget");
                int time = rs.getInt("cooking_time");
                String difficulty = rs.getString("difficulty");

                VBox card = createRecipeCard(name, description, category, budget, time, difficulty, ingredients);

                cardContainer.getChildren().add(card);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleReset(ActionEvent event) {
        // Clear all checkboxes and inputs
        catBreakfast.setSelected(false);
        catLunch.setSelected(false);
        catDinner.setSelected(false);
        diffEasy.setSelected(false);
        diffMedium.setSelected(false);
        diffHard.setSelected(false);
        budget100.setSelected(false);
        budget250.setSelected(false);
        budget500.setSelected(false);

        searchField.clear();
        minTimeField.clear();
        maxTimeField.clear();

        cardContainer.getChildren().clear();
        loadRecipeCards(); // Reload all recipes
    }


}
