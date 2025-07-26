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
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;


public class DashboardController {
    @FXML private Label welcomeLabel;
    @FXML private TextField recipeNameField;
    @FXML private TextArea ingredientsField;
    @FXML private Label statusLabel;
    
    @FXML private int userId;
    @FXML private String username;
    
    @FXML private TextArea descriptionField;
    @FXML private RadioButton breakfastRadio, lunchRadio, dinnerRadio;
    @FXML private RadioButton budget100, budget250, budget500;
    @FXML private RadioButton easyRadio, mediumRadio, hardRadio;
    @FXML private TextField cookingTimeField;

    @FXML private TableView<Recipe> recipeTable;
    @FXML private TableColumn<Recipe, String> colName, colIngredients, colDescription,
                                              colCategory, colBudget, colDifficulty;
    @FXML private TableColumn<Recipe, Integer> colTime;

    
    @FXML private Recipe selectedRecipe = null;

    private ObservableList<Recipe> recipeList = FXCollections.observableArrayList();
    
    @FXML
    private FlowPane cardContainer2;


    public void setUser(int id, String name) {
        this.userId = id;
        this.username = name;
        welcomeLabel.setText("Welcome, " + username + "!");
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
        
        if (name.isEmpty() || ingredients.isEmpty() || category == null || budget == null || difficulty == null) {
            statusLabel.setText("Please fill all required fields.");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }
        
        if (!Validator.isRecipeNameValid(name)) {
            statusLabel.setText("Recipe name must be min 6 characters.");
            return;
        }
        
        if (!Validator.isIngredientsValid(ingredients)) {
            statusLabel.setText("Ingredients must be min 8 characters.");
            return;
        }
        
        if (!Validator.isDescriptionValid(description)) {
            statusLabel.setText("Description must be min 20 characters.");
            return;
        }
        
        try {
            cookingTime = Integer.parseInt(cookingTimeField.getText());
        } catch (NumberFormatException e) {
            statusLabel.setText("Cooking time must be a number.");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }
        
        if (!Validator.isCookingTimeValid(cookingTime)) {
            statusLabel.setText("Cooking time must be between 5 to 240 mins.");
            return;
        }
        
        try (Connection conn = getConnection()) {

            if (selectedRecipe == null) {
                String sql = "INSERT INTO recipes (name, ingredients, "
                        + "description, category, budget, cooking_time, difficulty, user_id) " +
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
                String sql = "UPDATE recipes SET name = ?, ingredients = ?, "
                        + "description = ?, category = ?, budget = ?, "
                        + "cooking_time = ?, difficulty = ? WHERE id = ?";
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
        System.out.println("Entered loadRecipesFromDatabase.");

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
                rs.getString("difficulty"),
                rs.getString("image_file")
            ));
        }

        recipeTable.setItems(recipeList);

//            while (rs.next()) {
//                String name = rs.getString("name");
//                String description = rs.getString("description");
//                String ingredients = rs.getString("ingredients");
//                String category = rs.getString("category");
//                String budget = rs.getString("budget");
//                int time = rs.getInt("cooking_time");
//                String difficulty = rs.getString("difficulty");
//
//                VBox card = createRecipeCard(name, description, category, budget, time, difficulty, ingredients);
//
//                cardContainer2.getChildren().add(card);
//            }

        } catch (SQLException e) {
            System.out.println("Error from loadRecipesFromDatabase.");
            e.printStackTrace();
        }
    }
    
        private VBox createRecipeCard(String name, String description, String category, String budget, int time, String difficulty, String ingredients) {
        VBox card = new VBox(6);
        card.setPadding(new javafx.geometry.Insets(10, 10, 10, 10));
        card.setPrefSize(200, 180);
        card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-radius: 8; -fx-background-radius: 8;");

        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        Label ingredientsLabel = new Label(ingredients);

        Label descLabel = new Label(shorten(description, 80));
        descLabel.setWrapText(true);

        Label categoryLabel = new Label("Category: " + (category != null ? category : "N/A"));
        Label budgetLabel = new Label("Budget: " + (budget != null ? budget : "N/A"));
        Label timeLabel = new Label("Time: " + time + " min");
        Label difficultyLabel = new Label("Difficulty: " + (difficulty != null ? difficulty : "N/A"));

        card.getChildren().addAll(nameLabel, descLabel, categoryLabel,
                budgetLabel, timeLabel, difficultyLabel, ingredientsLabel);

//        card.setOnMouseClicked(event -> {
//            try {
//                FXMLLoader loader = new FXMLLoader(getClass().getResource("RecipeDetails.fxml"));
//                Parent detailRoot = loader.load();
//
//                // Get controller and set the selected recipe
//                RecipeDetailsController controller = loader.getController();
//                controller.setRecipe(new Recipe(0, name, ingredients, description, category, budget, time, difficulty));
//
//                // Replace current scene with detail scene
//                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
//                stage.setScene(new Scene(detailRoot));
//                stage.setTitle("Recipe Details - " + name);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
        return card;
    }

    private String shorten(String text, int maxLength) {
        if (text == null) return "No description.";
        return text.length() <= maxLength ? text : text.substring(0, maxLength) + "...";
    }


    
    public Connection getConnection() {
        try {
            System.out.println("DB Connected");
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/recipedb",
                    "root", "password");
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

