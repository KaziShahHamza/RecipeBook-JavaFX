package recipe.book;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class Recipedetails2Controller {
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
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard2.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
