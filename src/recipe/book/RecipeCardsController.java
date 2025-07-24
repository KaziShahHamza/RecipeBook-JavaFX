package recipe.book;

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

    @FXML
    private FlowPane cardContainer;
    @FXML private TextField searchField;
    @FXML private CheckBox catBreakfast, catLunch, catDinner;
@FXML private CheckBox diffEasy, diffMedium, diffHard;


    @FXML
    private void initialize() {
        loadRecipeCards();
    }

private void loadRecipeCards() {
    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/recipedb", "root", "password");
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT name, description, category, budget, cooking_time, difficulty FROM recipes")) {

        while (rs.next()) {
            String name = rs.getString("name");
            String description = rs.getString("description");
            String category = rs.getString("category");
            String budget = rs.getString("budget");
            int time = rs.getInt("cooking_time");
            String difficulty = rs.getString("difficulty");

            VBox card = createRecipeCard(name, description, category, budget, time, difficulty);
            cardContainer.getChildren().add(card);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
}


private VBox createRecipeCard(String name, String description, String category, String budget, int time, String difficulty) {
    VBox card = new VBox(6);
    card.setPadding(new javafx.geometry.Insets(10, 10, 10, 10));
    card.setPrefSize(200, 180);
    card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-radius: 8; -fx-background-radius: 8;");

    Label nameLabel = new Label(name);
    nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

    Label descLabel = new Label(shorten(description, 80));
    descLabel.setWrapText(true);

    Label categoryLabel = new Label("Category: " + (category != null ? category : "N/A"));
    Label budgetLabel = new Label("Budget: " + (budget != null ? budget : "N/A"));
    Label timeLabel = new Label("Time: " + time + " min");
    Label difficultyLabel = new Label("Difficulty: " + (difficulty != null ? difficulty : "N/A"));

    card.getChildren().addAll(nameLabel, descLabel, categoryLabel, budgetLabel, timeLabel, difficultyLabel);
    return card;
}


private String shorten(String text, int maxLength) {
    if (text == null) return "No description.";
    return text.length() <= maxLength ? text : text.substring(0, maxLength) + "...";
}



    @FXML
    private void handleBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("dashboard.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Dashboard");
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
         PreparedStatement stmt = conn.prepareStatement("SELECT name, description, category, budget, cooking_time, difficulty FROM recipes WHERE LOWER(name) LIKE ?")) {

        stmt.setString(1, "%" + searchTerm + "%");
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            String name = rs.getString("name");
            String description = rs.getString("description");
            String category = rs.getString("category");
            String budget = rs.getString("budget");
            int time = rs.getInt("cooking_time");
            String difficulty = rs.getString("difficulty");

            VBox card = createRecipeCard(name, description, category, budget, time, difficulty);
            cardContainer.getChildren().add(card);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
}

@FXML
private void handleFilter(ActionEvent event) {
    cardContainer.getChildren().clear();

    List<String> selectedCategories = new ArrayList<>();
    if (catBreakfast.isSelected()) selectedCategories.add("Breakfast");
    if (catLunch.isSelected()) selectedCategories.add("Lunch");
    if (catDinner.isSelected()) selectedCategories.add("Dinner");

    List<String> selectedDifficulties = new ArrayList<>();
    if (diffEasy.isSelected()) selectedDifficulties.add("Easy");
    if (diffMedium.isSelected()) selectedDifficulties.add("Medium");
    if (diffHard.isSelected()) selectedDifficulties.add("Hard");

    StringBuilder query = new StringBuilder("SELECT name, description, category, budget, cooking_time, difficulty FROM recipes WHERE 1=1");

    if (!selectedCategories.isEmpty()) {
        query.append(" AND category IN (");
        query.append("?,".repeat(selectedCategories.size()).replaceAll(",$", ""));
        query.append(")");
    }

    if (!selectedDifficulties.isEmpty()) {
        query.append(" AND difficulty IN (");
        query.append("?,".repeat(selectedDifficulties.size()).replaceAll(",$", ""));
        query.append(")");
    }

    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/recipedb", "root", "password");
         PreparedStatement stmt = conn.prepareStatement(query.toString())) {

        int index = 1;
        for (String cat : selectedCategories) {
            stmt.setString(index++, cat);
        }
        for (String diff : selectedDifficulties) {
            stmt.setString(index++, diff);
        }

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            String name = rs.getString("name");
            String description = rs.getString("description");
            String category = rs.getString("category");
            String budget = rs.getString("budget");
            int time = rs.getInt("cooking_time");
            String difficulty = rs.getString("difficulty");

            VBox card = createRecipeCard(name, description, category, budget, time, difficulty);
            cardContainer.getChildren().add(card);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
}

@FXML
private void handleReset(ActionEvent event) {
    // Uncheck all filters
    catBreakfast.setSelected(false);
    catLunch.setSelected(false);
    catDinner.setSelected(false);
    diffEasy.setSelected(false);
    diffMedium.setSelected(false);
    diffHard.setSelected(false);
    searchField.clear(); // also reset search field if needed

    cardContainer.getChildren().clear();
    loadRecipeCards(); // reload all recipes
}


}
