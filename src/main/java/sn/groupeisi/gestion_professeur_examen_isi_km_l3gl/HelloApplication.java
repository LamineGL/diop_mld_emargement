package sn.groupeisi.gestion_professeur_examen_isi_km_l3gl;

import jakarta.persistence.EntityManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.utils.JPAUtil;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/auth/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        try {
            EntityManager em = JPAUtil.getEntityManager();
            if (em != null) {
                System.out.println(" Connexion réussie !");
                em.close();
            } else {
                System.out.println("Erreur de connexion !");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Lancer l'application JavaFX
        launch();
    }

}