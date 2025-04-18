package sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.dao;

import java.sql.Connection;
import java.sql.DriverManager;

public class DB {
    private final String url = "jdbc:postgresql://localhost:5432/db_jpa_gestion_scolaire";
    private final String username = "postgres";
    private final String password = "passer";
    private Connection conn;

    public Connection getConnection(){
        try {
            Class.forName("org.postgresql.Driver");
            conn= DriverManager.getConnection(url, username, password);
            System.out.println("Connecté");
        } catch (Exception ex) {
            conn = null;
            System.out.print("Erreur");
        }
        return conn;
    }
}

