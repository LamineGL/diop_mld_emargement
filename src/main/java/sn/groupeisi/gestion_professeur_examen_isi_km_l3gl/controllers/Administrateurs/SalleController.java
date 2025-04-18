package sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.controllers.Administrateurs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.dao.SalleDao;
import sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.models.Salle;

import java.util.List;
import java.util.Optional;

public class SalleController {

    @FXML
    private TextField txtLibelle;

    @FXML
    private Button btnAjouter;

    @FXML
    private Button btnModifier;

    @FXML
    private Button btnDelete;

    @FXML
    private TableView<Salle> tableSalles;

    @FXML
    private TableColumn<Salle, Long> colId;

    @FXML
    private TableColumn<Salle, String> colLibelle;

    private final SalleDao salleDAO = new SalleDao();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colLibelle.setCellValueFactory(new PropertyValueFactory<>("libelle"));
        chargerSalles();
        configurerBoutons();
        configurerDoubleClicTableView();
    }

    private void chargerSalles() {
        List<Salle> salles = salleDAO.getToutesLesSalles();
        ObservableList<Salle> observableSalles = FXCollections.observableArrayList(salles);
        tableSalles.setItems(observableSalles);
    }

    private void configurerBoutons() {
        btnAjouter.setDisable(false);
        btnModifier.setDisable(true);
        btnDelete.setDisable(true);
    }

    private void configurerDoubleClicTableView() {
        tableSalles.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() == 2) {
                Salle selectedSalle = tableSalles.getSelectionModel().getSelectedItem();
                if (selectedSalle != null) {
                    txtLibelle.setText(selectedSalle.getLibelle());
                    btnAjouter.setDisable(true);
                    btnModifier.setDisable(false);
                    btnDelete.setDisable(false);
                }
            }
        });
    }

    @FXML
    void AjouterSalle(ActionEvent event) {
        String libelle = txtLibelle.getText().trim();

        if (libelle.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le libellé ne peut pas être vide !");
            return;
        }

        if (salleDAO.existeLibelle(libelle)) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Une salle avec ce libellé existe déjà !");
            return;
        }

        Salle salle = new Salle(libelle);
        salleDAO.ajouterSalle(salle);
        chargerSalles();
        txtLibelle.clear();
        configurerBoutons();
        showAlert(Alert.AlertType.INFORMATION, "Succès", "Salle ajoutée avec succès !");
    }

    @FXML
    void ModifierSalle(ActionEvent event) {
        Salle selectedSalle = tableSalles.getSelectionModel().getSelectedItem();

        if (selectedSalle == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner une salle à modifier !");
            return;
        }

        String newLibelle = txtLibelle.getText().trim();

        if (newLibelle.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le libellé ne peut pas être vide !");
            return;
        }

        if (!selectedSalle.getLibelle().equals(newLibelle) && salleDAO.existeLibelle(newLibelle)) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Une salle avec ce libellé existe déjà !");
            return;
        }

        selectedSalle.setLibelle(newLibelle);
        salleDAO.modifierSalle(selectedSalle);
        chargerSalles();
        txtLibelle.clear();
        configurerBoutons();
        showAlert(Alert.AlertType.INFORMATION, "Succès", "Salle modifiée avec succès !");
    }

    @FXML
    void supprimerSalle(ActionEvent event) {
        Salle selectedSalle = tableSalles.getSelectionModel().getSelectedItem();

        if (selectedSalle == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner une salle à supprimer !");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText("Êtes-vous sûr de vouloir supprimer cette salle ?");
        confirmation.setContentText("Cette action est irréversible.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            salleDAO.supprimerSalle(selectedSalle.getId());
            chargerSalles();
            txtLibelle.clear();
            configurerBoutons();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Salle supprimée avec succès !");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
