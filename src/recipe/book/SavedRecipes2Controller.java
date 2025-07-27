package recipe.book;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import javafx.geometry.Insets;

public class SavedRecipes2Controller {

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private FlowPane cardContainer;
    @FXML
    private Label statusLabel;

    public void initialize() {
        loadSavedRecipeCards();
    }

    private void loadSavedRecipeCards() {
        List<Recipe> savedRecipes = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/recipedb", "root", "password")) {
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
            VBox card = createRecipeCard(
                recipe.getId(),
                recipe.getName(),
                recipe.getDescription(),
                recipe.getCategory(),
                recipe.getBudget(),
                recipe.getCookingTime(),
                recipe.getDifficulty(),
                recipe.getIngredients(),
                recipe.getImageFile()
            );
            cardContainer.getChildren().add(card);
        }
    }

    private VBox createRecipeCard(int id, String name, String description, String category, String budget, int time, String difficulty, String ingredients, String imageFile) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(10));
        card.setPrefSize(200, 200);
        card.setStyle("""
            -fx-background-color: #ffffff;
            -fx-border-color: #D35400;
            -fx-border-radius: 10;
            -fx-background-radius: 10;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 2, 2);
        """);

        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #A04000;");

        Label descLabel = new Label(shorten(description, 80));
        descLabel.setWrapText(true);
        descLabel.setStyle("-fx-text-fill: #4E342E;");

        Label categoryLabel = new Label("Category: " + (category != null ? category : "N/A"));
        Label budgetLabel = new Label("Budget: " + (budget != null ? budget : "N/A"));
        Label timeLabel = new Label("Time: " + time + " min");
        Label difficultyLabel = new Label("Difficulty: " + (difficulty != null ? difficulty : "N/A"));
        Label ingredientsLabel = new Label(ingredients);

        for (Label lbl : List.of(categoryLabel, budgetLabel, timeLabel, difficultyLabel, ingredientsLabel)) {
            lbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #4E342E;");
        }

        Button unsaveBtn = new Button("Unsave");
        unsaveBtn.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-weight: bold;");
        unsaveBtn.setOnAction(e -> {
            unsaveRecipe(id);
            loadSavedRecipeCards(); // Refresh cards
        });

        VBox.setMargin(unsaveBtn, new Insets(5, 0, 0, 0));

        card.getChildren().addAll(
            nameLabel, descLabel, categoryLabel, budgetLabel, timeLabel, difficultyLabel, ingredientsLabel, unsaveBtn
        );

        card.setOnMouseClicked(event -> {
            if (!event.getTarget().equals(unsaveBtn)) {
                showRecipeDetails(new Recipe(id, name, ingredients, description, category, budget, time, difficulty, imageFile), event);
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
            stage.setTitle("Recipe Details - " + recipe.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unsaveRecipe(int recipeId) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/recipedb", "root", "password")) {
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

    private String shorten(String text, int maxLength) {
        if (text == null) return "";
        return text.length() <= maxLength ? text : text.substring(0, maxLength) + "...";
    }
}
