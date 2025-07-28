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
    @FXML
    private Label usernameLabel;

    @FXML
    private Button loginButton;

    @FXML
    private Button logoutButton;
    
    @FXML private void initialize() {
        if (Session.isLoggedIn()) {
            backButton.setText("Back to Home");
            usernameLabel.setText("👤 " + Session.loggedInUsername);
            usernameLabel.setVisible(true);
            loginButton.setVisible(false);
            logoutButton.setVisible(true);
        } else {
            usernameLabel.setVisible(false);
            loginButton.setVisible(true);
            logoutButton.setVisible(false);
            backButton.setText("Back to Login");
        }
        
        loadRecipeCards();
    }

    @FXML
    private void loadRecipeCards() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/recipedb", "root", "password");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, name, ingredients, description, category, budget, cooking_time, difficulty, image_file FROM recipes")) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                String ingredients = rs.getString("ingredients");
                String category = rs.getString("category");
                String budget = rs.getString("budget");
                int time = rs.getInt("cooking_time");
                String difficulty = rs.getString("difficulty");
                String imageFile = rs.getString("image_file");

                VBox card = createRecipeCard(id, name, description, category, budget, time, difficulty, ingredients, imageFile);

                cardContainer.getChildren().add(card);
            }
        } catch (Exception e) {
            e.printStackTrace();
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



    // Handle card click (not on button) for detail view
    card.setOnMouseClicked(event -> {
        if (!(event.getTarget() instanceof Button)) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("RecipeDetails.fxml"));
                Parent detailRoot = loader.load();

                RecipeDetailsController controller = loader.getController();
                controller.setRecipe(
                    new Recipe(id, name, ingredients, description, category, budget, time, difficulty, imageFile),
                    "recipescards"
                );

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(detailRoot));
                stage.setTitle("Recipe Details - " + name);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    });

    card.getChildren().addAll(nameLabel, descLabel, categoryLabel, budgetLabel, timeLabel, difficultyLabel, ingredientsLabel);

        // Create Save/Saved button only if user is logged in
    if (Session.loggedInUserId != -1) {
        Button saveBtn = new Button();
        saveBtn.setPrefWidth(100);
        
        if (isRecipeAlreadySaved(id, Session.loggedInUserId)) {
            saveBtn.setText("Saved");
            saveBtn.setDisable(true);
            saveBtn.setStyle("-fx-background-color: #BFC9CA; -fx-text-fill: #2E4053; -fx-font-weight: bold;");
        } else {
            saveBtn.setText("Save");
            saveBtn.setStyle("-fx-background-color: #28B463; -fx-text-fill: white; -fx-font-weight: bold;");
            saveBtn.setOnAction(e -> {
                saveRecipeForUser(id);
                saveBtn.setText("Saved");
                saveBtn.setDisable(true);
                saveBtn.setStyle("-fx-background-color: #BFC9CA; -fx-text-fill: #2E4053; -fx-font-weight: bold;");
            });
        }

        VBox.setMargin(saveBtn, new Insets(5, 0, 0, 0));
        card.getChildren().add(saveBtn);
    }
    
    // Add Love button if user is logged in
if (Session.loggedInUserId != -1) {
    Button loveBtn = new Button();
    loveBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 16px;");

    // Set initial heart icon based on love status
    if (isRecipeLovedByUser(id, Session.loggedInUserId)) {
        loveBtn.setText("❤️"); // Loved
    } else {
        loveBtn.setText("♡"); // Not loved
    }

    loveBtn.setOnAction(e -> {
        toggleLoveForRecipe(id, Session.loggedInUserId, loveBtn);
    });

    HBox actionRow = new HBox(10);
    actionRow.getChildren().addAll( loveBtn);
    card.getChildren().add(actionRow);
}

    
    return card;
}

private boolean isRecipeLovedByUser(int recipeId, int userId) {
    String sql = "SELECT COUNT(*) FROM loved_recipes WHERE user_id = ? AND recipe_id = ?";
    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/recipedb", "root", "password");
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, userId);
        stmt.setInt(2, recipeId);
        ResultSet rs = stmt.executeQuery();

        return rs.next() && rs.getInt(1) > 0;

    } catch (SQLException e) {
        e.printStackTrace();
    }
    return false;
}


private void toggleLoveForRecipe(int recipeId, int userId, Button loveBtn) {
    String checkSql = "SELECT * FROM loved_recipes WHERE user_id = ? AND recipe_id = ?";
    String insertSql = "INSERT INTO loved_recipes (user_id, recipe_id) VALUES (?, ?)";
    String deleteSql = "DELETE FROM loved_recipes WHERE user_id = ? AND recipe_id = ?";

    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/recipedb", "root", "password")) {
        PreparedStatement checkStmt = conn.prepareStatement(checkSql);
        checkStmt.setInt(1, userId);
        checkStmt.setInt(2, recipeId);
        ResultSet rs = checkStmt.executeQuery();

        if (rs.next()) {
            // Already loved → Remove love
            PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
            deleteStmt.setInt(1, userId);
            deleteStmt.setInt(2, recipeId);
            deleteStmt.executeUpdate();
            loveBtn.setText("♡"); // outline heart
        } else {
            // Not yet loved → Add love
            PreparedStatement insertStmt = conn.prepareStatement(insertSql);
            insertStmt.setInt(1, userId);
            insertStmt.setInt(2, recipeId);
            insertStmt.executeUpdate();
            loveBtn.setText("❤️"); // filled heart
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
}


private boolean isRecipeAlreadySaved(int recipeId, int userId) {
    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/recipedb", "root", "password")) {
        String sql = "SELECT COUNT(*) FROM saved_recipes WHERE user_id = ? AND recipe_id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, userId);
        stmt.setInt(2, recipeId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return false;
}


private void saveRecipeForUser(int recipeId) {
    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/recipedb", "root", "password");) {
        String checkSql = "SELECT * FROM saved_recipes WHERE user_id = ? AND recipe_id = ?";
        PreparedStatement checkStmt = conn.prepareStatement(checkSql);
        checkStmt.setInt(1, Session.loggedInUserId);
        checkStmt.setInt(2, recipeId);
        ResultSet rs = checkStmt.executeQuery();

        if (rs.next()) {
            // Already saved
            System.out.println("Already saved.");
        } else {
            String insertSql = "INSERT INTO saved_recipes (user_id, recipe_id) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(insertSql);
            stmt.setInt(1, Session.loggedInUserId);
            stmt.setInt(2, recipeId);
            stmt.executeUpdate();

            System.out.println("Recipe saved.");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
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

    @FXML
    private void handleLogout(ActionEvent event) {
        Session.logout();
        // Refresh the page after logout to update view
        try {
            Parent root = FXMLLoader.load(getClass().getResource("recipescards.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("All Recipes");
            stage.show();
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
            if (Session.isLoggedIn()) {
                // Load dashboard2.fxml and set user
                FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard2.fxml"));
                Parent root = loader.load();

                Dashboard2Controller controller = loader.getController();
                controller.setUser(Session.loggedInUserId, Session.loggedInUsername);

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Dashboard");
                stage.show();

            } else {
                // Load login page if not logged in
                Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Login");
                stage.show();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    @FXML
    private void handleSearch(ActionEvent event) {
        String searchTerm = searchField.getText().trim().toLowerCase();

        cardContainer.getChildren().clear();

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/recipedb", "root", "password");
             PreparedStatement stmt = conn.prepareStatement("SELECT id, name, ingredients, description, category, budget, cooking_time, difficulty, image_file FROM recipes WHERE LOWER(name) LIKE ?")) {

            stmt.setString(1, "%" + searchTerm + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                String ingredients = rs.getString("ingredients");
                String category = rs.getString("category");
                String budget = rs.getString("budget");
                int time = rs.getInt("cooking_time");
                String difficulty = rs.getString("difficulty");
                String imageFile = rs.getString("image_file");

                VBox card = createRecipeCard(id, name, description, category, budget, time, difficulty, ingredients, imageFile);

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
        StringBuilder query = new StringBuilder("SELECT id, name, ingredients, description, category, budget, cooking_time, difficulty, image_file FROM recipes WHERE 1=1");

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
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String description = rs.getString("description"); 
                String ingredients = rs.getString("ingredients"); 
                String category = rs.getString("category");
                String budget = rs.getString("budget");
                int time = rs.getInt("cooking_time");
                String difficulty = rs.getString("difficulty");
                String imageFile = rs.getString("image_file");


                VBox card = createRecipeCard(id, name, description, category, budget, time, difficulty, ingredients, imageFile);

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
