/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package recipe.book;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import java.sql.*;
import java.util.UUID;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.*;

/**
 * FXML Controller class
 *
 * @author LAPTOPBD
 */
public class Dashboard2Controller  {
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
    
    @FXML private Label imagePathLabel;
    @FXML private ImageView recipeImageView;

    private String savedImageFileName = null;
    
    @FXML private Recipe selectedRecipe = null;

    @FXML private FlowPane cardContainer;
    
    public void setUser(int id, String name) {
        this.userId = id;
        this.username = name;
        welcomeLabel.setText("Welcome, " + username + "!");
        loadRecipeCards();

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
        
        loadRecipeCards();
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
                    // INSERT new
                    String sql = "INSERT INTO recipes (user_id, name, ingredients, description, category, budget, cooking_time, difficulty, image_file) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, userId);
                    stmt.setString(2, name);
                    stmt.setString(3, ingredients);
                    stmt.setString(4, description);
                    stmt.setString(5, category);
                    stmt.setString(6, budget);
                    stmt.setInt(7, cookingTime);
                    stmt.setString(8, difficulty);
                    stmt.setString(9, savedImageFileName); 
                    stmt.executeUpdate();
                    statusLabel.setText("Recipe saved successfully.");
                    statusLabel.setStyle("-fx-text-fill: green;");
                } else {
                    // UPDATE existing
                    String sql = "UPDATE recipes SET name = ?, ingredients = ?, description = ?, category = ?, budget = ?, cooking_time = ?, difficulty = ? WHERE id = ?";
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
                    statusLabel.setText("Recipe updated: " + name);
                    statusLabel.setStyle("-fx-text-fill: green;");
                }

            // Clear form and refresh
            clearForm();
            loadRecipeCards();

        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText("Error saving recipe.");
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
        selectedRecipe = null;
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
            Session.logout();
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
            Parent root = FXMLLoader.load(getClass().getResource("recipescards.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("View Recipes");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    @FXML
    private void loadRecipeCards() {
        cardContainer.getChildren().clear(); // clear old cards

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM recipes WHERE user_id = ?")) {

            stmt.setInt(1, userId);
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

                VBox card = createRecipeCard(recipe);
                cardContainer.getChildren().add(card);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    
    private VBox createRecipeCard(Recipe recipe) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(10));
        card.setPrefSize(200, 200);
        card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-radius: 8; -fx-background-radius: 8;");

        Label nameLabel = new Label(recipe.getName());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        Label descLabel = new Label(shorten(recipe.getDescription(), 80));
        descLabel.setWrapText(true);
        Label ingredientsLabel = new Label("Ingredients: " + recipe.getIngredients());
        Label categoryLabel = new Label("Category: " + recipe.getCategory());
        Label budgetLabel = new Label("Budget: " + recipe.getBudget());
        Label timeLabel = new Label("Time: " + recipe.getCookingTime() + " min");
        Label difficultyLabel = new Label("Difficulty: " + recipe.getDifficulty());

        Button editBtn = new Button("Edit");
        editBtn.setStyle("-fx-background-color: #f0ad4e; -fx-text-fill: white;");
        editBtn.setOnAction(e -> loadRecipeIntoForm(recipe));

        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white;");
        deleteBtn.setOnAction(e -> deleteRecipe(recipe));

        HBox buttonBox = new HBox(10, editBtn, deleteBtn);

        card.getChildren().addAll(nameLabel, descLabel, categoryLabel, budgetLabel, timeLabel, difficultyLabel, ingredientsLabel, buttonBox);

        card.setOnMouseClicked(event -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("RecipeDetails.fxml"));
                    Parent detailRoot = loader.load();

                    // Get controller and set the selected recipe
                    RecipeDetailsController controller = loader.getController();
                    controller.setRecipe(new Recipe(0, recipe.getName(), recipe.getIngredients(), 
                            recipe.getDescription(), recipe.getCategory(), 
                            recipe.getBudget(), recipe.getCookingTime(), recipe.getDifficulty(), recipe.getImageFile()), "dashboard2");

                    // Replace current scene with detail scene
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.setScene(new Scene(detailRoot));
                    stage.setTitle("Recipe Details - " + recipe.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        return card;
    }


    private void loadRecipeIntoForm(Recipe recipe) {
        selectedRecipe = recipe;

        recipeNameField.setText(recipe.getName());
        ingredientsField.setText(recipe.getIngredients());
        descriptionField.setText(recipe.getDescription());
        cookingTimeField.setText(String.valueOf(recipe.getCookingTime()));

        setRadioSelection(recipe.getCategory(), breakfastRadio, lunchRadio, dinnerRadio);
        setRadioSelection(recipe.getBudget(), budget100, budget250, budget500);
        setRadioSelection(recipe.getDifficulty(), easyRadio, mediumRadio, hardRadio);

        statusLabel.setText("Editing recipe: " + recipe.getName());
        statusLabel.setStyle("-fx-text-fill: orange;");
    }


    private void deleteRecipe(Recipe recipe) {
        try (Connection conn = getConnection()) {
            String sql = "DELETE FROM recipes WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, recipe.getId());
            stmt.executeUpdate();

            statusLabel.setText("Recipe deleted: " + recipe.getName());
            statusLabel.setStyle("-fx-text-fill: green;");

            loadRecipeCards();
        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText("Error deleting recipe.");
            statusLabel.setStyle("-fx-text-fill: red;");
        }
    }

    
    
    private String shorten(String text, int maxLength) {
        if (text == null) return "No description.";
        return text.length() <= maxLength ? text : text.substring(0, maxLength) + "...";
    } 
    
    @FXML
    private void handleChooseImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Recipe Image");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                // Generate unique filename
                String fileName = UUID.randomUUID().toString() + "_" + selectedFile.getName();
                File destDir = new File("src/main/resources/images");
                if (!destDir.exists()) destDir.mkdirs();

                File destFile = new File(destDir, fileName);
                Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                imagePathLabel.setText("Selected: " + fileName);
                recipeImageView.setImage(new Image(destFile.toURI().toString()));
                savedImageFileName = fileName; // Save to DB later

            } catch (IOException e) {
                e.printStackTrace();
                imagePathLabel.setText("Image upload failed.");
            }
        }
    }

    
    
}
