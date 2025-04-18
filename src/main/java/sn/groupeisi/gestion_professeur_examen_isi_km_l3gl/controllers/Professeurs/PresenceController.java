package sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.controllers.Professeurs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.dao.PresenceDAO;
import sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.models.Cours;
import sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.models.Emargement;
import sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.models.Users;
import sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.utils.SessionManager;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class PresenceController {

    @FXML private TableView<Cours> tableCours;
    @FXML private TableColumn<Cours, String> colNomCours;
    @FXML private TableColumn<Cours, LocalTime> colHeureDebut;
    @FXML private TableColumn<Cours, LocalTime> colHeureFin;
    @FXML private TableColumn<Cours, String> colSalle;
    @FXML private TableColumn<Cours, String> colStatut;
    @FXML private Label lblMessage;
    @FXML private Button btnEnregistrerPresence;

    private final PresenceDAO presenceDAO = new PresenceDAO();

    @FXML
    public void initialize() {
        btnEnregistrerPresence.setDisable(true); // Désactiver le bouton au chargement
        loadCoursDuProfesseur();

        // Permet d'activer/désactiver le bouton selon la sélection
        tableCours.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            btnEnregistrerPresence.setDisable(newSelection == null);
        });
    }

    private void loadCoursDuProfesseur() {
        Users professeur = SessionManager.getCurrentUser();

        if (professeur == null || !professeur.getRole().equalsIgnoreCase("Professeur")) {
            System.out.println("Aucun professeur connecté !");
            return;
        }

        List<Cours> coursList = presenceDAO.getCoursDuProfesseur(professeur.getId());

        for (Cours cours : coursList) {
            Emargement emargement = presenceDAO.getEmargement(professeur.getId(), cours.getId());
            if (emargement == null) {
                cours.setStatut("Absent");
            } else {
                cours.setStatut(emargement.getStatut());
            }
        }

        ObservableList<Cours> observableCours = FXCollections.observableArrayList(coursList);
        colNomCours.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colHeureDebut.setCellValueFactory(new PropertyValueFactory<>("heureDebut"));
        colHeureFin.setCellValueFactory(new PropertyValueFactory<>("heureFin"));
        colSalle.setCellValueFactory(new PropertyValueFactory<>("salle"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        tableCours.setItems(observableCours);
    }

    @FXML
    private void enregistrerPresence() {
        Users professeur = SessionManager.getCurrentUser();
        Cours selectedCours = tableCours.getSelectionModel().getSelectedItem();

        if (selectedCours == null) {
            lblMessage.setText("❌ Sélectionnez un cours !");
            return;
        }

        LocalDate dateAujourdhui = LocalDate.now();
        LocalTime maintenant = LocalTime.now();
        LocalTime heureDebut = selectedCours.getHeureDebut();
        LocalTime heureFin = selectedCours.getHeureFin();

        // Vérifier si le cours appartient à aujourd'hui, passé ou futur
        if (selectedCours.getDateCours().isBefore(dateAujourdhui)) {
            Emargement emargement = presenceDAO.getEmargement(professeur.getId(), selectedCours.getId());
            lblMessage.setText("📅 Cours passé. Statut: " + (emargement != null ? emargement.getStatut() : "Absent"));
            return;
        }
        if (selectedCours.getDateCours().isAfter(dateAujourdhui)) {
            lblMessage.setText("📅 Vous ne pouvez pas émarger ce cours, il n'est pas encore programmé pour aujourd'hui.");
            return;
        }

        // Vérifier si la présence a déjà été enregistrée
        Emargement emargement = presenceDAO.getEmargement(professeur.getId(), selectedCours.getId());
        if (emargement != null) {
            lblMessage.setText("✅ Présence déjà enregistrée : " + emargement.getStatut());
            return;
        }

        // Heure limite d'émargement
        LocalTime heureLimiteDebut = heureDebut.minusMinutes(5);
        LocalTime heureLimiteFin = heureFin.plusMinutes(5);

        String statut;

        // 🔴 Émergement avant 5 minutes du début du cours
        if (maintenant.isBefore(heureLimiteDebut)) {
            lblMessage.setText("🛑 Émergement impossible avant 5 min du début du cours.\nStatut: ❌ Absent.");
            return;
        }

        // ✅ Émergement entre 5 minutes avant le début et 5 minutes après la fin
        if (maintenant.isAfter(heureLimiteDebut) && maintenant.isBefore(heureLimiteFin)) {
            if (maintenant.isBefore(heureDebut)) {
                lblMessage.setText("⏳ Vous pouvez enregistrer votre présence.");
            } else if (maintenant.equals(heureDebut)) {
                lblMessage.setText("✅ Vous êtes à l'heure.");
            } else if (maintenant.isAfter(heureDebut) && maintenant.isBefore(heureFin)) {
                int retard = (int) Duration.between(heureDebut, maintenant).toMinutes();
                lblMessage.setText("⏳ Vous êtes en retard de " + retard + " minutes.");
            } else {
                lblMessage.setText("⚠️ Vous validez votre présence en fin de cours.");
            }
            statut = "Présent";
        } else {
            // 🛑 Émergement après 5 minutes de la fin du cours
            lblMessage.setText("❌ Trop tard, le cours est terminé. Vous êtes marqué(e) absent(e).");
            statut = "Absent";
        }

        // Enregistrer la présence
        Emargement nouvelEmargement = new Emargement(dateAujourdhui, maintenant, statut, professeur, selectedCours);
        presenceDAO.enregistrerPresence(nouvelEmargement);

        // Mettre à jour le statut dans la liste affichée
        selectedCours.setStatut(statut);

        lblMessage.setText("✅ Statut mis à jour : " + statut);
        loadCoursDuProfesseur(); // Rafraîchir la liste après validation
    }
}
