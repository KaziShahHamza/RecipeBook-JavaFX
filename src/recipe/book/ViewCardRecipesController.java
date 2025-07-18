package recipe.book;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.Node;

import java.sql.*;
import java.util.List;

public class ViewCardRecipesController {

    @FXML
    private VBox recipeContainer;

    @FXML
    private void initialize() {
        loadRecipeCards();
    }

    private void loadRecipeCards() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/recipedb", "root", "password");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM recipes")) {

            while (rs.next()) {
                String name = rs.getString("name");
                String category = rs.getString("category");
                String description = rs.getString("description");

                VBox card = new VBox(5);
                card.setPrefWidth(550);
                card.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #ccc; -fx-padding: 10; -fx-background-radius: 10; -fx-border-radius: 10;");

                Label nameLabel = new Label("🍽 " + name);
                nameLabel.setFont(new Font("Arial", 16));
                nameLabel.setStyle("-fx-font-weight: bold;");

                Label categoryLabel = new Label("Category: " + category);
                categoryLabel.setTextFill(Color.GRAY);

                Label descLabel = new Label(shorten(description, 100));
                descLabel.setWrapText(true);

                card.getChildren().addAll(nameLabel, categoryLabel, descLabel);
                recipeContainer.getChildren().add(card);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String shorten(String text, int length) {
        if (text == null) return "";
        return text.length() > length ? text.substring(0, length) + "..." : text;
    }
}
