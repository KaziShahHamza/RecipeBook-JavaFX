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

