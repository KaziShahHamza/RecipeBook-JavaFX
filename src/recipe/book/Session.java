/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package recipe.book;

/**
 *
 * @author LAPTOPBD
 */
public class Session {
    public static int loggedInUserId = -1; // -1 means not logged in
    public static String loggedInUsername = null;

    public static boolean isLoggedIn() {
        return loggedInUserId != -1;
    }

    public static void logout() {
        loggedInUserId = -1;
        loggedInUsername = null;
    }
}