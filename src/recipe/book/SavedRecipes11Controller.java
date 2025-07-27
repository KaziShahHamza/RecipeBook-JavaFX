/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package recipe.book;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SavedRecipes11Controller {

    @FXML private FlowPane cardContainer;
    @FXML private Label statusLabel;

    public void initialize() {
        loadSavedRecipes();
    }

    private void loadSavedRecipes() {
        List<Recipe> savedRecipes = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/recipedb", "root", "password");) {
            String sql = """
                SELECT r.* FROM recipes r
                JOIN saved_recipes s ON r.id = s.recipe_id
                WHERE s.user_id = ?
            """;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, Session.loggedInUserId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Recipe recipe = new Recipe(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("ingredients"),
                    rs.getString("description"),
                    rs.getString("category"),
                    rs.getString("budget"),
                    rs.getInt("cooking_time"),
                    rs.getString("difficulty"),
                    rs.getString("image_file")
                );
                savedRecipes.add(recipe);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText("Failed to load saved recipes.");
        }

        cardContainer.getChildren().clear();
        for (Recipe recipe : savedRecipes) {
            cardContainer.getChildren().add(createCard(recipe));
        }
    }

    private VBox createCard(Recipe recipe) {
        VBox card = new VBox();
        card.setStyle("-fx-padding: 10; -fx-background-color: #fff8f0; -fx-border-radius: 8; -fx-border-color: #e67e22;");
        card.setSpacing(5);

        Label nameLabel = new Label(recipe.getName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");

        Label shortDesc = new Label(recipe.getDescription().length() > 60 ? recipe.getDescription().substring(0, 60) + "..." : recipe.getDescription());
        shortDesc.setWrapText(true);

        Label unsaveBtn = new Label("Unsave");
        unsaveBtn.setStyle("-fx-text-fill: red; -fx-cursor: hand;");
        unsaveBtn.setOnMouseClicked(e -> {
            unsaveRecipe(recipe.getId());
            loadSavedRecipes(); // refresh
        });

        card.getChildren().addAll(nameLabel, shortDesc, unsaveBtn);
        card.setOnMouseClicked(event -> {
            if (!(event.getTarget() instanceof Label)) {
                showRecipeDetails(recipe, event);
            }
        });

        return card;
    }

    private void showRecipeDetails(Recipe recipe, MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("RecipeDetails.fxml"));
            Parent detailRoot = loader.load();
            RecipeDetailsController controller = loader.getController();
            controller.setRecipe(recipe, "saved");

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(detailRoot));
            stage.setTitle("Recipe Details");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unsaveRecipe(int recipeId) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/recipedb", "root", "password");) {
            String sql = "DELETE FROM saved_recipes WHERE user_id = ? AND recipe_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, Session.loggedInUserId);
            stmt.setInt(2, recipeId);
            stmt.executeUpdate();
            statusLabel.setText("Recipe unsaved.");
        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText("Failed to unsave recipe.");
        }
    }
}
