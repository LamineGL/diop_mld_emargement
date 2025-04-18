package sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.controllers.Dashboard;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.models.Users;
import sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.scene.input.MouseEvent;

public class DashboardController {

    @FXML private Button btnGestionUtilisateurs;
    @FXML private Button btnGestionCours;
    @FXML private Button btnMesCours;
    @FXML private Button btnPresences;
    @FXML private Button btnGestionSalle;
    @FXML private Button btnLogout;
    @FXML private StackPane mainContent;
    @FXML private Button btnGestionRapportDesProf;
    @FXML private Button btnDashboard;



    @FXML private Label Titre;
    @FXML private VBox VboxMenu;


    @FXML private VBox vboxInfoUserConnecter;
    @FXML private StackPane stack;


    @FXML
    public void initialize() {
        Users currentUser = SessionManager.getCurrentUser();

        btnGestionUtilisateurs.setOnMouseEntered(e -> btnGestionUtilisateurs.setStyle("-fx-background-color: white; -fx-text-fill: black;"));
        btnGestionUtilisateurs.setOnMouseExited(e -> btnGestionUtilisateurs.setStyle("-fx-background-color: #34495E; -fx-text-fill: #ECF0F1;"));

        if (currentUser == null) {
            System.out.println("Aucun utilisateur en session !");
        } else {
            String role = currentUser.getRole();

            // Masquer les boutons selon le rôle
            btnGestionSalle.setVisible("Admin".equals(role));
            btnGestionUtilisateurs.setVisible("Admin".equals(role));
            btnGestionCours.setVisible("Gestionnaire".equals(role) || "Admin".equals(role));
            btnDashboard.setVisible("Gestionnaire".equals(role) || "Admin".equals(role));
            btnGestionRapportDesProf.setVisible("Gestionnaire".equals(role ) || "Admin".equals(role));
            btnMesCours.setVisible("Professeur".equals(role));
            btnPresences.setVisible("Gestionnaire".equals(role) || "Admin".equals(role) );

            btnGestionSalle.managedProperty()
                    .bind(btnGestionSalle.visibleProperty());
            btnGestionUtilisateurs.managedProperty()
                    .bind(btnGestionUtilisateurs.visibleProperty());
            btnGestionCours.managedProperty()
                    .bind(btnGestionCours.visibleProperty());
            btnDashboard.managedProperty()
                    .bind(btnDashboard.visibleProperty());
            btnGestionRapportDesProf.managedProperty()
                    .bind(btnGestionRapportDesProf.visibleProperty());
            btnMesCours.managedProperty()
                    .bind(btnMesCours.visibleProperty());
            btnPresences.managedProperty()
                    .bind(btnPresences.visibleProperty());

            System.out.println("✅ Utilisateur connecté : " + currentUser.getEmail() + " - Rôle : " + currentUser.getRole());


            String nomUser = currentUser.getEmail(); // Remplacer par currentUser.getNom() si disponible
            String anneeAcademique = "2025 / 2026";
            String dateDuJour = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String welcomeText = "Welcome " + nomUser + " | " + anneeAcademique + " | " + dateDuJour;

            Label welcomeLabel = new Label(welcomeText);
            welcomeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");
            welcomeLabel.prefWidthProperty().bind(vboxInfoUserConnecter.widthProperty());


            // On vide d'abord le VBox pour s'assurer qu'il n'y a pas d'autres éléments
            vboxInfoUserConnecter.getChildren().clear();
            vboxInfoUserConnecter.getChildren().add(welcomeLabel);
        }
    }

    @FXML
    void toGestionSalle() {
        loadPage("/views/admin/salle-view.fxml");
    }

    @FXML
    private void toGestionUsers() {
        loadPage("/views/admin/utilisateur-view.fxml");
    }

    @FXML
    private void toGestionCours() {
        loadPage("/views/gestionnaires/cours-view.fxml");
    }

    @FXML
    private void toMesCours() {
        loadPage("/views/professeurs/presence-view.fxml");
    }

    @FXML
    private void toPresence() {
        loadPage("/views/gestionnaires/presencesgestion.fxml");
    }

    @FXML
    void toGestionRappdesprof(ActionEvent event) {
        loadPage("/views/gestionnaires/rapportgestionnaire.fxml");
    }

    @FXML
    void toGestionDashboard(ActionEvent event) {
        loadPage("/views/admin/statistiques.fxml");
    }


    // Méthode pour charger les pages dans mainContent
    private void loadPage(String page) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(page));
            Parent root = loader.load();
            mainContent.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleButtonMouseEnter(javafx.scene.input.MouseEvent event) {
        // Récupère le bouton concerné
        Button btn = (Button) event.getSource();
        // Sauvegardez éventuellement la valeur initiale du style si besoin
        btn.setStyle("-fx-background-color: white; -fx-text-fill: black;");
    }

    @FXML
    private void handleButtonMouseExit(javafx.scene.input.MouseEvent event) {
        Button btn = (Button) event.getSource();
        // Remettre le style par défaut, ici vous devez indiquer le style initial de chaque bouton
        // Vous pouvez par exemple stocker le style par défaut dans une variable ou utiliser une logique conditionnelle selon l'ID du bouton.
        // Pour cet exemple, nous utilisons une couleur par défaut commune.
        btn.setStyle("-fx-background-color: #34495E; -fx-text-fill: #ECF0F1;");
    }



    @FXML
    private void handleLogout() {
        SessionManager.logout();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/auth/login-view.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void toLogout() {
        // Fermer la session de l'utilisateur
        SessionManager.logout();

        // Charger la page de connexion
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/auth/login-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
