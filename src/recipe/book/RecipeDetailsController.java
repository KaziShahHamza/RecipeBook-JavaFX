package recipe.book;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class RecipeDetailsController {
    @FXML private Label nameLabel, categoryLabel, budgetLabel, timeLabel, difficultyLabel;
    @FXML private TextArea ingredientsArea, descriptionArea;

    private Recipe recipe;

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
        nameLabel.setText(recipe.getName());
        categoryLabel.setText("Category: " + recipe.getCategory());
        budgetLabel.setText("Budget: " + recipe.getBudget());
        timeLabel.setText("Cooking Time: " + recipe.getCookingTime() + " min");
        difficultyLabel.setText("Difficulty: " + recipe.getDifficulty());
        ingredientsArea.setText(recipe.getIngredients());
        descriptionArea.setText(recipe.getDescription());
    }

    @FXML
    private void handleBack() {
        // Close the detail window
        Stage stage = (Stage) nameLabel.getScene().getWindow();
        stage.close();
    }
}
