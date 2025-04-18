package sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.controllers.Administrateurs;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.dao.UsersImpl;
import sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.models.Users;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AddUser implements Initializable {

    // TableView et colonnes de la partie "Liste des utilisateurs"
    @FXML
    private TableView<Users> tableUsers;
    @FXML
    private TableColumn<Users, Integer> colId;
    @FXML
    private TableColumn<Users, String> colNom, colPrenom, colEmail, colRole;

    // Champs du formulaire (à gauche)
    @FXML
    private TextField txtId, txtNom, txtPrenom, txtEmail;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private ComboBox<String> comboRole;
    @FXML
    private CheckBox chkShowPassword;

    @FXML private Button btnTable;
    // Boutons d'action
    @FXML
    private Button btnAjouter, btnModifier, btnSupprimer, btnEffacer;
    @FXML
    private Label lblError; // Pour afficher d'éventuels messages d'erreur (optionnel)

    // DAO et sélection
    private final UsersImpl usersDAO = new UsersImpl();
    private Users selectedUser = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialisation du ComboBox des rôles
        comboRole.setItems(FXCollections.observableArrayList("Admin", "Professeur", "Gestionnaire"));

        // Configuration des colonnes de la TableView
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));

        loadUsers();

        // Désactiver les boutons Modifier et Supprimer tant qu'aucun utilisateur n'est sélectionné
        btnModifier.setDisable(true);
        btnSupprimer.setDisable(true);

        // Déclencher la méthode chargerInfosUtilisateur au double-clic sur la table
        tableUsers.setOnMouseClicked(this::chargerInfosUtilisateur);

        // Gestion du CheckBox pour afficher/masquer le mot de passe
        chkShowPassword.setOnAction(event -> handleShowPassword());
    }

    /**
     * Charge l'ensemble des utilisateurs depuis la DAO et met à jour la TableView.
     */
    private void loadUsers() {
        ObservableList<Users> usersList = usersDAO.getAll();
        tableUsers.setItems(usersList);
        tableUsers.refresh();
    }

    /**
     * Au double-clic sur un utilisateur dans la table, charge ses informations dans le formulaire.
     */
    @FXML
    private void chargerInfosUtilisateur(MouseEvent event) {
        if (event.getClickCount() == 2) {
            selectedUser = tableUsers.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                txtId.setText(String.valueOf(selectedUser.getId()));
                txtNom.setText(selectedUser.getNom());
                txtPrenom.setText(selectedUser.getPrenom());
                txtEmail.setText(selectedUser.getEmail());
                txtPassword.setText(selectedUser.getPassword());
                comboRole.setValue(selectedUser.getRole());

                btnAjouter.setDisable(true);
                btnModifier.setDisable(false);
                btnSupprimer.setDisable(false);
            }
        }
    }

    /**
     * Affiche ou masque le mot de passe en fonction du CheckBox.
     */
    @FXML
    private void handleShowPassword() {
        if (chkShowPassword.isSelected()) {
            // Affiche le mot de passe en utilisant le promptText
            txtPassword.setPromptText(txtPassword.getText());
            txtPassword.setText("");
        } else {
            // Remet le mot de passe dans le PasswordField
            txtPassword.setText(txtPassword.getPromptText());
            txtPassword.setPromptText("");
        }
    }

    /**
     * Méthode appelée lorsque l'on clique sur "Ajouter" dans le formulaire.
     */
    @FXML
    private void ajouterUser(ActionEvent event) {
        if (!validerChamps()) return;

        Users user = new Users(
                txtNom.getText(),
                txtPrenom.getText(),
                txtEmail.getText(),
                txtPassword.getText(),
                comboRole.getValue()
        );

        if (usersDAO.add(user) == 1) {
            showAlert("Succès", "Utilisateur ajouté avec succès !", Alert.AlertType.INFORMATION);
            loadUsers();
            clearFields();
            btnAjouter.setDisable(false);
        }
    }

    /**
     * Méthode appelée lorsque l'on clique sur "Modifier".
     */
    @FXML
    private void modifierUser(ActionEvent event) {
        if (selectedUser == null) {
            showAlert("Erreur", "Aucun utilisateur sélectionné !", Alert.AlertType.ERROR);
            return;
        }
        if (!validerChamps()) return;

        selectedUser.setNom(txtNom.getText());
        selectedUser.setPrenom(txtPrenom.getText());
        selectedUser.setEmail(txtEmail.getText());
        selectedUser.setPassword(txtPassword.getText());
        selectedUser.setRole(comboRole.getValue());

        if (usersDAO.update(selectedUser) == 1) {
            showAlert("Succès", "Utilisateur modifié avec succès !", Alert.AlertType.INFORMATION);
            loadUsers();
            clearFields();
            selectedUser = null;
        }
    }

    /**
     * Méthode appelée lorsque l'on clique sur "Supprimer".
     */
    @FXML
    private void supprimerUser(ActionEvent event) {
        if (selectedUser == null) {
            showAlert("Erreur", "Aucun utilisateur sélectionné !", Alert.AlertType.ERROR);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Voulez-vous vraiment supprimer cet utilisateur ?");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                usersDAO.delete(selectedUser.getId());
                showAlert("Succès", "Utilisateur supprimé avec succès !", Alert.AlertType.INFORMATION);
                loadUsers();
                clearFields();
                selectedUser = null;
            }
        });
    }

    /**
     * Méthode appelée pour effacer les champs du formulaire.
     */
    @FXML
    private void effacerClear(ActionEvent event) {
        clearFields();
    }

    /**
     * Valide les champs obligatoires du formulaire.
     * @return true si tous les champs sont valides, false sinon.
     */
    private boolean validerChamps() {
        StringBuilder messageErreur = new StringBuilder();
        if (txtNom.getText().isEmpty()) messageErreur.append("⚠ Le nom est obligatoire !\n");
        if (txtPrenom.getText().isEmpty()) messageErreur.append("⚠ Le prénom est obligatoire !\n");
        if (txtEmail.getText().isEmpty()) messageErreur.append("⚠ L'email est obligatoire !\n");
        if (txtPassword.getText().isEmpty()) messageErreur.append("⚠ Le mot de passe est obligatoire !\n");
        if (comboRole.getValue() == null) messageErreur.append("⚠ Le rôle est obligatoire !\n");

        if (!messageErreur.toString().isEmpty()) {
            showAlert("Erreur", messageErreur.toString(), Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }

    /**
     * Réinitialise tous les champs du formulaire et réactive les boutons.
     */
    private void clearFields() {
        txtId.clear();
        txtNom.clear();
        txtPrenom.clear();
        txtEmail.clear();
        txtPassword.clear();
        comboRole.setValue(null);
        chkShowPassword.setSelected(false);

        btnAjouter.setDisable(false);
        btnModifier.setDisable(true);
        btnSupprimer.setDisable(true);

        selectedUser = null;
    }

    /**
     * Affiche une alerte avec le titre, le message et le type donné.
     */
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void Retour(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/admin/utilisateur-view.fxml"));
            AnchorPane formPane = loader.load();

            // Récupérer la scène actuelle
            Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            currentStage.setScene(new Scene(formPane));
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
