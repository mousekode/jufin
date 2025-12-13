/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oop_class.jufin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This shi do connection to DB. Yes
 * @author Akbar
 */
public class DBConnect {
    private static Connection connect;
    
    public static Connection getConnect() {
        // Chek jika koneksi == null
        if (connect == null) {
            try {
                String url = "jdbc:mysql://localhost:3306/db_jufin";
                String user = "root";
                String password = "";

                DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());

                connect = DriverManager.getConnection(url, user, password);
            }
            
            catch (SQLException e) {
                System.err.println("Koneksi error!");
            }
        }
        return connect;
    }
}
