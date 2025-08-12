# 🍲 RecipeBook – JavaFX Desktop App

A personal and social recipe management application built with JavaFX and MySQL. Users can sign up, log in, save their favorite recipes, and explore a variety of dishes with filters, images, and interactive cards.

---

## 🧩 Features

- 👤 User Sign Up / Login with hashed password storage
- 📖 Add, edit, and delete your own recipes
- 🧾 Browse recipes from all users with cards
- ❤️ Save and Love recipes like social media
- 📁 Image support for recipes
- 🔍 Filter by category, budget, difficulty
- 📂 View saved recipes in a separate section

---

## 🛠️ Requirements

- Java 17 or above  
- JavaFX SDK  
- MySQL Server (Workbench/XAMPP)  
- NetBeans or any Java IDE (optional)

---

## 📦 How to Run This App

### 1. Clone or Copy the Project Folder

Make sure the entire `RecipeBook` project folder (with `src/`, `images/`, `FXML/`, and `.java` files) is copied.

---

### 2. Set Up the Database

1. Install **MySQL** on your system (Workbench or XAMPP).
2. Create a database named `recipe_book` (or your preferred name).
3. Import the SQL file:
   - Use MySQL Workbench → File → Open SQL Script → Run.
4. Ensure the table structure includes:
   - `users`, `recipes`, `saved_recipes`, `loves`

### 3. Configure DB in the App

In `DBUtil.java` or wherever the database connection is made, update:

```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/recipe_book";
private static final String DB_USER = "your_username";
private static final String DB_PASSWORD = "your_password";
```

---

### 4. Add Recipe Images

- Place all recipe images inside the `/images` folder.
- Store only relative paths in the database like:

  ```text
  images/biryani.jpg
  ```

- Make sure image loading uses:

  ```java
  new Image(getClass().getResourceAsStream(imageFile));
  ```

---

### 5. Run the App

You can:

- Run using **NetBeans** (`REcipeBook.java`)
- Or build a **JAR file**:
  - Right click project → Build
  - Run using:

    ```bash
    java -jar dist/RecipeBook.jar
    ```

---

## 💡 Tips

- Only logged-in users can **save** or **love** recipes.
- Recipe cards show **Love Count** and **Save/Unsave** toggle.
- Recipes are filterable by category, budget, and difficulty.
- Images and database must exist on the new PC.


---

## 📂 Folder Structure

```
RecipeBook/
├── src/
│   └── recipe/book/
├── images/
│   └── biryani.jpg, burger.jpg, ...

---

## 📧 Contact

Author: Kazi Shah Hamza  
Email: kazishahhamza01@gmail.com

