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
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.fxml.FXMLLoader;
import java.sql.*;
import javafx.scene.control.TextField;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;


public class ViewRecipesController {
    @FXML private TableView<Recipe> recipeTable;
    @FXML private TableColumn<Recipe, String> colName, colIngredients, colDescription,
                                              colCategory, colBudget, colDifficulty;
    @FXML private TableColumn<Recipe, Integer> colTime;
    @FXML private TextField searchField;

    private ObservableList<Recipe> recipeList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colIngredients.setCellValueFactory(new PropertyValueFactory<>("ingredients"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colBudget.setCellValueFactory(new PropertyValueFactory<>("budget"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("cookingTime"));
        colDifficulty.setCellValueFactory(new PropertyValueFactory<>("difficulty"));

        loadRecipes();
        setupSearchFilter();
    }

    private void loadRecipes() {
        recipeList.clear();
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/recipedb", "root", "password");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM recipes")) {

            while (rs.next()) {
                recipeList.add(new Recipe(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("ingredients"),
                    rs.getString("description"),
                    rs.getString("category"),
                    rs.getString("budget"),
                    rs.getInt("cooking_time"),
                    rs.getString("difficulty")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setupSearchFilter() {
        FilteredList<Recipe> filteredData = new FilteredList<>(recipeList, b -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(recipe -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();
                return recipe.getName().toLowerCase().contains(lowerCaseFilter);
            });
        });

        SortedList<Recipe> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(recipeTable.comparatorProperty());
        recipeTable.setItems(sortedData);
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
