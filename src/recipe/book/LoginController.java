/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package recipe.book;

/**
 *
 * @author LAPTOPBD
 */

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.sql.*;


public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;

    @FXML
    private void handleLogin(ActionEvent event) {
    String username = usernameField.getText();
    String password = passwordField.getText();

    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/recipedb", "root", "password")) {

        // Step 1: Check if username exists
        String checkUserSql = "SELECT password_hash, id FROM users WHERE username = ?";
        PreparedStatement checkStmt = conn.prepareStatement(checkUserSql);
        checkStmt.setString(1, username);
        ResultSet rs = checkStmt.executeQuery();

        if (!rs.next()) {
            errorLabel.setText("User not found");
            return;
        }

        // Step 2: Validate password
        String storedHashedPassword = rs.getString("password_hash");
        int userId = rs.getInt("id");

        if (!HashUtil.checkPassword(password, storedHashedPassword)) {
            errorLabel.setText("Incorrect password.");
            return;
        }

        // Step 3: Load dashboard
        FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
        Parent root = loader.load();
        DashboardController dashboardController = loader.getController();
        dashboardController.setUser(userId, username);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Dashboard - " + username);
        stage.show();

    } catch (Exception e) {
        errorLabel.setText("An error occurred. Please try again.");
        e.printStackTrace();
    }
}


    
    @FXML
    private void handleSignupLink(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("signup.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Sign Up");
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
    
    @FXML
    private void handleViewCardRecipes(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("viewcardrecipes.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("All Recipes - Card View");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
