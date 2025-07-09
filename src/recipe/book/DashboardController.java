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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.net.URL;

public class DashboardController {

    @FXML private Label welcomeLabel;
    @FXML private TextField recipeNameField;
    @FXML private TextArea ingredientsField;
    @FXML private Label statusLabel;
    @FXML
    private Label welcomeLabel1;

    public void setUsername(String username) {
        welcomeLabel.setText("Welcome, " + username + "!");
    }

    @FXML
    private void handleSaveRecipe(ActionEvent event) {
        String recipeName = recipeNameField.getText();
        String ingredients = ingredientsField.getText();

        if (recipeName.isEmpty() || ingredients.isEmpty()) {
            statusLabel.setText("Please enter both recipe name and ingredients.");
            statusLabel.setStyle("-fx-text-fill: red;");
        } else {
            // Placeholder: Save to MySQL or List later
            statusLabel.setText("Recipe saved: " + recipeName);
            statusLabel.setStyle("-fx-text-fill: green;");
            recipeNameField.clear();
            ingredientsField.clear();
        }
        
//         String sql = "INSERT INTO recipes (name, ingredients) VALUES (?, ?)";
//
//        try (Connection conn = getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//
//            stmt.setString(1, recipeName);
//            stmt.setString(2, ingredients);
//
//            stmt.executeUpdate();
//            statusLabel.setText("✅ Recipe saved to database!");
//            statusLabel.setStyle("-fx-text-fill: green;");
//            recipeNameField.clear();
//            ingredientsField.clear();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            statusLabel.setText("❌ Failed to save recipe.");
//            statusLabel.setStyle("-fx-text-fill: red;");
//        }   
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
}

