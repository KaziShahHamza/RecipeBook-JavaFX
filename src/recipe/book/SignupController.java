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
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.sql.*;


public class SignupController {

    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    @FXML
    private void handleSignup(ActionEvent event) {
    String username = usernameField.getText();
    String email = emailField.getText();
    String password = passwordField.getText();
    
    if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
        statusLabel.setText("All fields are required.");
        return;
    }
    
    if (!Validator.isEmailValid(email)) {
        statusLabel.setText("Invalid email format.");
        return;
    }
    
    if (!Validator.isUsernameValid(username)) {
        statusLabel.setText("Username must be lowercase letters or numbers only.");
        return;
    }

    if (!Validator.isPasswordValid(password)) {
        statusLabel.setText("Password must be min 8 characters, contain uppercase, lowercase, number, and symbol.");
        return;
    }
    
    if (isUsernameOrEmailTaken(username, email)) {
        statusLabel.setText("Username or Email already exists.");
        return;
    }

    

    String hashedPassword = HashUtil.hashPassword(password);

    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/recipedb", "root", "password")) {
        String sql = "INSERT INTO users (username, email, password_hash) VALUES (?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, username);
        stmt.setString(2, email);
        stmt.setString(3, hashedPassword);
        stmt.executeUpdate();

        statusLabel.setText("✅ Signup successful!");
    } catch (SQLException e) {
        e.printStackTrace();
        statusLabel.setText("❌ Signup failed. Username may exist.");
    }
}

    @FXML
    private void handleBackToLogin(ActionEvent event) {
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
    
    
    public boolean isUsernameOrEmailTaken(String username, String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ? OR email = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/recipedb", "root", "password");
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, email);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return true; // fail safe: assume taken if error
    }
}