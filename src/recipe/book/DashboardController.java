/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package recipe.book;

/**
 *
 * @author LAPTOPBD
 */

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.sql.*;

public class DashboardController {
    @FXML private Label welcomeLabel;
    @FXML private TextField recipeNameField;
    @FXML private TextArea ingredientsField;
    @FXML private Label statusLabel;
    
    private int userId;
    private String username;
    
    @FXML private TextArea descriptionField;
    @FXML private RadioButton breakfastRadio, lunchRadio, dinnerRadio;
    @FXML private RadioButton budget100, budget250, budget500;
    @FXML private RadioButton easyRadio, mediumRadio, hardRadio;
    @FXML private TextField cookingTimeField;

    @FXML private TableView<Recipe> recipeTable;
    @FXML private TableColumn<Recipe, String> colName, colIngredients, colDescription,
                                              colCategory, colBudget, colDifficulty;
    @FXML private TableColumn<Recipe, Integer> colTime;

    
    private Recipe selectedRecipe = null;

    private ObservableList<Recipe> recipeList = FXCollections.observableArrayList();


    public void setUser(int id, String name) {
        this.userId = id;
        this.username = name;
        welcomeLabel.setText("Welcome, " + username + "!" + "  userId: " + userId);
        loadRecipesFromDatabase();

    }
    
    @FXML
    private void initialize() {
        ToggleGroup categoryGroup = new ToggleGroup();
        breakfastRadio.setToggleGroup(categoryGroup);
        lunchRadio.setToggleGroup(categoryGroup);
        dinnerRadio.setToggleGroup(categoryGroup);

        ToggleGroup budgetGroup = new ToggleGroup();
        budget100.setToggleGroup(budgetGroup);
        budget250.setToggleGroup(budgetGroup);
        budget500.setToggleGroup(budgetGroup);

        ToggleGroup difficultyGroup = new ToggleGroup();
        easyRadio.setToggleGroup(difficultyGroup);
        mediumRadio.setToggleGroup(difficultyGroup);
        hardRadio.setToggleGroup(difficultyGroup);

        // TableView columns
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colIngredients.setCellValueFactory(new PropertyValueFactory<>("ingredients"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colBudget.setCellValueFactory(new PropertyValueFactory<>("budget"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("cookingTime"));
        colDifficulty.setCellValueFactory(new PropertyValueFactory<>("difficulty"));

        loadRecipesFromDatabase();
    }

    
    private String getSelectedRadioText(RadioButton... buttons) {
        for (RadioButton b : buttons) {
            if (b.isSelected()) return b.getText();
        }
        return null;
    }

    
    @FXML
    private void handleSaveRecipe(ActionEvent event) {
        String name = recipeNameField.getText();
        String ingredients = ingredientsField.getText();
        String description = descriptionField.getText();
        String category = getSelectedRadioText(breakfastRadio, lunchRadio, dinnerRadio);
        String budget = getSelectedRadioText(budget100, budget250, budget500);
        String difficulty = getSelectedRadioText(easyRadio, mediumRadio, hardRadio);
        int cookingTime = 0;

        try {
            cookingTime = Integer.parseInt(cookingTimeField.getText());
        } catch (NumberFormatException e) {
            statusLabel.setText("Cooking time must be a number.");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        if (name.isEmpty() || ingredients.isEmpty() || category == null || budget == null || difficulty == null) {
            statusLabel.setText("Please fill all required fields.");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        try (Connection conn = getConnection()) {

            if (selectedRecipe == null) {
                String sql = "INSERT INTO recipes (name, ingredients, description, category, budget, cooking_time, difficulty, user_id) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, name);
                stmt.setString(2, ingredients);
                stmt.setString(3, description);
                stmt.setString(4, category);
                stmt.setString(5, budget);
                stmt.setInt(6, cookingTime);
                stmt.setString(7, difficulty);
                stmt.setInt(8, userId);
                stmt.executeUpdate();

                statusLabel.setText("✅ Recipe added successfully!");
            } else {
                String sql = "UPDATE recipes SET name = ?, ingredients = ?, description = ?, category = ?, budget = ?, cooking_time = ?, difficulty = ? WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, name);
                stmt.setString(2, ingredients);
                stmt.setString(3, description);
                stmt.setString(4, category);
                stmt.setString(5, budget);
                stmt.setInt(6, cookingTime);
                stmt.setString(7, difficulty);
                stmt.setInt(8, selectedRecipe.getId());
                stmt.executeUpdate();

                statusLabel.setText("✅ Recipe updated!");
            }

            statusLabel.setStyle("-fx-text-fill: green;");
            clearForm();
            loadRecipesFromDatabase();
            selectedRecipe = null; 

        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText("❌ Database error.");
            statusLabel.setStyle("-fx-text-fill: red;");
        }
    }

    private void setRadioSelection(String value, RadioButton... buttons) {
        for (RadioButton b : buttons) {
            if (b.getText().equalsIgnoreCase(value)) {
                b.setSelected(true);
                return;
            }
        }
    }

    
    @FXML
    private void handleEditRecipe(ActionEvent event) {
        selectedRecipe = recipeTable.getSelectionModel().getSelectedItem();

        if (selectedRecipe == null) {
            statusLabel.setText("Please select a recipe to edit.");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        recipeNameField.setText(selectedRecipe.getName());
        ingredientsField.setText(selectedRecipe.getIngredients());
        descriptionField.setText(selectedRecipe.getDescription());
        cookingTimeField.setText(String.valueOf(selectedRecipe.getCookingTime()));

        // Set selected radio buttons
        setRadioSelection(selectedRecipe.getCategory(), breakfastRadio, lunchRadio, dinnerRadio);
        setRadioSelection(selectedRecipe.getBudget(), budget100, budget250, budget500);
        setRadioSelection(selectedRecipe.getDifficulty(), easyRadio, mediumRadio, hardRadio);

        statusLabel.setText("Editing recipe: " + selectedRecipe.getName());
        statusLabel.setStyle("-fx-text-fill: orange;");
    }


    @FXML
    private void handleDeleteRecipe(ActionEvent event) {
        Recipe recipeToDelete = recipeTable.getSelectionModel().getSelectedItem();

        if (recipeToDelete == null) {
            statusLabel.setText("Please select a recipe to delete.");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        try (Connection conn = getConnection();) {

            String sql = "DELETE FROM recipes WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, recipeToDelete.getId());
            stmt.executeUpdate();

            statusLabel.setText("Recipe deleted: " + recipeToDelete.getName());
            statusLabel.setStyle("-fx-text-fill: green;");
            loadRecipesFromDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText("Error deleting recipe.");
            statusLabel.setStyle("-fx-text-fill: red;");
        }
    }

    private void clearForm() {
        recipeNameField.clear();
        ingredientsField.clear();
        descriptionField.clear();
        cookingTimeField.clear();

        breakfastRadio.setSelected(false);
        lunchRadio.setSelected(false);
        dinnerRadio.setSelected(false);
        budget100.setSelected(false);
        budget250.setSelected(false);
        budget500.setSelected(false);
        easyRadio.setSelected(false);
        mediumRadio.setSelected(false);
        hardRadio.setSelected(false);
    }

    
    private void loadRecipesFromDatabase() {
    ObservableList<Recipe> recipeList = FXCollections.observableArrayList();

    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement("SELECT * FROM recipes WHERE user_id = ?")) {

        stmt.setInt(1, userId);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            recipeList.add(new Recipe(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("ingredients"),
                rs.getString("description"),
                rs.getString("category"),
                rs.getString("budget"),
                rs.getInt("cooking_time"),
                rs.getString("difficulty")
            ));
        }

        recipeTable.setItems(recipeList);

    } catch (SQLException e) {
        e.printStackTrace();
    }
}

    
    public Connection getConnection() {
        try {
            System.out.println("DB Connected");
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/recipedb", "root", "password");
        } catch (Exception e) {
            System.out.println( "DB not Connected");
            System.err.println("Error " + e.getMessage());
            return null;
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    @FXML
    private void handleViewRecipes(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("viewrecipes.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("View Recipes");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

