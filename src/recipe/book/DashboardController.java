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
    
    @FXML private TableView<Recipe> recipeTable;
    @FXML private TableColumn<Recipe, String> colName;
    @FXML private TableColumn<Recipe, String> colIngredients;
    
    private Recipe selectedRecipe = null;

    private ObservableList<Recipe> recipeList = FXCollections.observableArrayList();


    public void setUsername(String username) {
        welcomeLabel.setText("Welcome, " + username + "!");
        loadRecipesFromDatabase();
    }
    
    @FXML
    private void initialize() {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colIngredients.setCellValueFactory(new PropertyValueFactory<>("ingredients"));
    }
    
    @FXML
    private void handleSaveRecipe(ActionEvent event) {
        String recipeName = recipeNameField.getText();
        String ingredients = ingredientsField.getText();

        if (recipeName.isEmpty() || ingredients.isEmpty()) {
            statusLabel.setText("Please enter both recipe name and ingredients.");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }
        
        try (Connection conn = getConnection();) {

            if (selectedRecipe == null) {
            String sql = "INSERT INTO recipes (name, ingredients) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, recipeName);
            stmt.setString(2, ingredients);
            stmt.executeUpdate();
            statusLabel.setText("Recipe added!");
        } else {
            String sql = "UPDATE recipes SET name = ?, ingredients = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, recipeName);
            stmt.setString(2, ingredients);
            stmt.setInt(3, selectedRecipe.getId());
            stmt.executeUpdate();
            statusLabel.setText("Recipe updated!");
        }

        statusLabel.setStyle("-fx-text-fill: green;");
        recipeNameField.clear();
        ingredientsField.clear();
        selectedRecipe = null;
        loadRecipesFromDatabase();

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("❌ Failed to save recipe.");
            statusLabel.setStyle("-fx-text-fill: red;");
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

    
    private void loadRecipesFromDatabase() {
        recipeList.clear();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM recipes")) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String ingredients = rs.getString("ingredients");
                recipeList.add(new Recipe(id, name, ingredients));
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

