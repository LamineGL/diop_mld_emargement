package sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.controllers.Gestionnaire;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.dao.PresenceDAO;
import sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.models.Emargement;

import java.util.List;

public class PresenceGestionController {

    @FXML private TableView<Emargement> tablePresences;
    @FXML private TableColumn<Emargement, String> colNomProf;
    @FXML private TableColumn<Emargement, String> colDateCours;
    @FXML private TableColumn<Emargement, String> colHeureCours;
    @FXML private TableColumn<Emargement, String> colStatut;
    @FXML private Button btnModifierPresence;
    @FXML private Label lblMessage;

    private final PresenceDAO presenceDAO = new PresenceDAO();

    @FXML
    public void initialize() {
        // Associer les colonnes aux attributs de l’entité Emargement
        colNomProf.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getProfesseur().getNom()));

        colDateCours.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDate().toString()));

        colHeureCours.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getHeure().toString()));

        colStatut.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatut()));

        // Charger les données
        loadPresences();
    }

    /**
     * Charge les présences dans la TableView.
     */
    private void loadPresences() {
        List<Emargement> presencesList = presenceDAO.getAllPresences();
        ObservableList<Emargement> observablePresences = FXCollections.observableArrayList(presencesList);
        tablePresences.setItems(observablePresences);
    }

    @FXML
    void modifierPresence(ActionEvent event) {
        Emargement selectedEmargement = tablePresences.getSelectionModel().getSelectedItem();

        if (selectedEmargement == null) {
            lblMessage.setText("❌ Sélectionnez une présence à modifier !");
            return;
        }

        // Boîte de dialogue pour choisir le nouveau statut
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Modifier le statut");
        alert.setHeaderText("Sélectionnez le nouveau statut");

        ButtonType present = new ButtonType("Présent");
        ButtonType absent = new ButtonType("Absent");
        ButtonType justifie = new ButtonType("Justifié");
        ButtonType cancel = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(present, absent, justifie, cancel);

        alert.showAndWait().ifPresent(response -> {
            if (response == present) {
                selectedEmargement.setStatut("Présent");
            } else if (response == absent) {
                selectedEmargement.setStatut("Absent");
            } else if (response == justifie) {
                selectedEmargement.setStatut("Justifié");
            }


            presenceDAO.updatePresence(selectedEmargement);
            loadPresences();
            lblMessage.setText("✅ Statut mis à jour !");
        });

    }

    /**
     * Modifier le statut d'une présence.
     */
    @FXML
    private void modifierPresence() {
    }
}
