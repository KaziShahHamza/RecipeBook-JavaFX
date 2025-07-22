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
import javafx.scene.control.CheckBox;


public class ViewRecipesController {
    @FXML private TableView<Recipe> recipeTable;
    @FXML private TableColumn<Recipe, String> colName, colIngredients, colDescription,
                                              colCategory, colBudget, colDifficulty;
    @FXML private TableColumn<Recipe, Integer> colTime;
    @FXML private TextField searchField;
    @FXML private CheckBox catBreakfast, catLunch, catDinner;
    @FXML private CheckBox budget100, budget250, budget500;
    @FXML private CheckBox diffEasy, diffMedium, diffHard;
    @FXML private TextField minTimeField, maxTimeField;

    private ObservableList<Recipe> recipeList = FXCollections.observableArrayList();
    private FilteredList<Recipe> filteredData;

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
            filteredData = new FilteredList<>(recipeList, b -> true);
            SortedList<Recipe> sorted = new SortedList<>(filteredData);
            sorted.comparatorProperty().bind(recipeTable.comparatorProperty());
            recipeTable.setItems(sorted);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
   
    private void setupSearchFilter() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredData.setPredicate(recipe -> {
                if (newVal == null || newVal.isEmpty()) return true;
                return recipe.getName().toLowerCase().contains(newVal.toLowerCase());
            });
        });
    }
    
    @FXML
    private void handleFilter(ActionEvent event) {
        try {
            filteredData.setPredicate(recipe -> {
                if (recipe == null || recipe.getBudget() == null) return false;

                String budget = recipe.getBudget().trim().replaceAll("\\s+", "");

                if (!budget100.isSelected() && !budget250.isSelected() && !budget500.isSelected()) {
                    return true; // No filter, show all
                }

                return (budget100.isSelected() && budget.contains("100") && budget.contains("250")) ||
                       (budget250.isSelected() && budget.contains("250") && budget.contains("500")) ||
                       (budget500.isSelected() && budget.contains("500+"));
            });

        } catch (Exception e) {
            e.printStackTrace(); // Show real error in console
        }
    }

    @FXML
    private void handleReset(ActionEvent event) {
        searchField.clear();
        catBreakfast.setSelected(false);
        catLunch.setSelected(false);
        catDinner.setSelected(false);

        budget100.setSelected(false);
        budget250.setSelected(false);
        budget500.setSelected(false);

        diffEasy.setSelected(false);
        diffMedium.setSelected(false);
        diffHard.setSelected(false);

        minTimeField.clear();
        maxTimeField.clear();

        filteredData.setPredicate(r -> true);
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
