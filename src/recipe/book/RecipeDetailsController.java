package recipe.book;

import java.io.File;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class RecipeDetailsController {

    @FXML private Label nameLabel, categoryLabel, budgetLabel, timeLabel, difficultyLabel;
    @FXML private Label ingredientsLabel, descriptionLabel;

    private Recipe recipe;
    private String previousPage;
    
    @FXML private ImageView recipeImageView;

    public void setRecipe(Recipe recipe, String previousPage) {
        this.recipe = recipe;
        this.previousPage = previousPage;
        
        nameLabel.setText(recipe.getName());
        categoryLabel.setText("Category: " + recipe.getCategory());
        budgetLabel.setText("Budget: " + recipe.getBudget());
        timeLabel.setText("Cooking Time: " + recipe.getCookingTime() + " min");
        difficultyLabel.setText("Difficulty: " + recipe.getDifficulty());
        ingredientsLabel.setText(recipe.getIngredients());
        descriptionLabel.setText(recipe.getDescription());
        
        if (recipe.getImageFile() != null) {
            File imageFile = new File("src/main/resources/images/" + recipe.getImageFile());
            if (imageFile.exists()) {
                recipeImageView.setImage(new Image(imageFile.toURI().toString()));
            }
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            String fxmlToLoad = previousPage.equals("dashboard2") ? "dashboard2.fxml" : "recipescards.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlToLoad));
            Parent root = loader.load();
            

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Back");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
