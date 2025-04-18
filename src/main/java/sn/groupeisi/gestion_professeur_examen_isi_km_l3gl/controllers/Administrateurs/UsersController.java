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

public class UsersController implements Initializable {

    // ========================
    // Champs FXML
    // ========================


    @FXML
    private TableView<Users> tableUsers;
    @FXML
    private TableColumn<Users, Integer> colId;
    @FXML
    private TableColumn<Users, String> colTeacher;
    @FXML
    private TableColumn<Users, String> colEmail;
    @FXML
    private TableColumn<Users, String> colRole;
    @FXML
    private TableColumn<Users, String> colComment;

    @FXML
    private TextField txtSearch;

    @FXML
    private Button btnSearch, btnAjouter, btnModifier, btnSupprimer;


    // Champs cachés (ou pour un popup)
    @FXML
    private TextField txtId, txtNom, txtPrenom, txtEmail;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private ComboBox<String> comboRole;
    @FXML
    private CheckBox chkShowPassword;

    // Labels pour les statistiques
    @FXML
    private Label nombreA;   // Admin
    @FXML
    private Label nombreP;   // Professeurs
    @FXML
    private Label nombreG;   // Gestionnaires
    @FXML
    private Label nombreAll; // Tous

    // DAO
    private final UsersImpl usersDAO = new UsersImpl();
    private Users selectedUser = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialiser la comboRole
        comboRole.setItems(FXCollections.observableArrayList("Admin", "Professeur", "Gestionnaire"));

        // Configuration des colonnes
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        // Colonne Teacher : concaténation Nom + Prénom
        colTeacher.setCellValueFactory(cellData -> {
            Users u = cellData.getValue();
            String fullName = u.getNom() + " " + u.getPrenom();
            return new SimpleStringProperty(fullName);
        });

        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));

        // Colonne Comment : si vous n'avez pas de champ comment, on affiche un tiret
        colComment.setCellValueFactory(cellData -> new SimpleStringProperty("—"));

        // Charger la liste
        loadUsers();

        // Désactiver boutons Modifier / Supprimer au départ
        btnModifier.setDisable(true);
        btnSupprimer.setDisable(true);

        // Gérer le double-clic sur la table
        tableUsers.setOnMouseClicked(this::chargerInfosUtilisateur);

        // Case "Afficher" pour le mot de passe
        chkShowPassword.setOnAction(event -> handleShowPassword());
    }

    /**
     * Charge tous les utilisateurs et met à jour la table et les compteurs.
     */
    private void loadUsers() {
        ObservableList<Users> userList = usersDAO.getAll();
        tableUsers.setItems(userList);
        tableUsers.refresh();

        // Mettre à jour les compteurs (Admin, Prof, Gestionnaire, Tous)
        updateCounters(userList);
    }

    /**
     * Met à jour les compteurs d'utilisateurs par rôle.
     */
    private void updateCounters(ObservableList<Users> list) {
        int adminCount = 0;
        int profCount = 0;
        int gestCount = 0;

        for (Users u : list) {
            switch (u.getRole().toLowerCase()) {
                case "admin":
                    adminCount++;
                    break;
                case "professeur":
                    profCount++;
                    break;
                case "gestionnaire":
                    gestCount++;
                    break;
                default:
                    // Autres rôles éventuels
                    break;
            }
        }
        nombreA.setText(String.valueOf(adminCount));
        nombreP.setText(String.valueOf(profCount));
        nombreG.setText(String.valueOf(gestCount));
        nombreAll.setText(String.valueOf(list.size()));
    }

    /**
     * Gère le double-clic sur la table pour charger les infos d'un utilisateur.
     */
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
     * Affiche ou masque le mot de passe en clair.
     */
    private void handleShowPassword() {
        if (chkShowPassword.isSelected()) {
            txtPassword.setPromptText(txtPassword.getText());
            txtPassword.setText("");
        } else {
            txtPassword.setText(txtPassword.getPromptText());
            txtPassword.setPromptText("");
        }
    }

    /**
     * Bouton Filtrer / Rechercher
     */
    @FXML
    private void handleSearch(ActionEvent event) {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            loadUsers();
        } else {
            // Recherche simple par nom, prénom ou email
            ObservableList<Users> filtered = FXCollections.observableArrayList();
            for (Users u : usersDAO.getAll()) {
                if (u.getNom().toLowerCase().contains(keyword.toLowerCase())
                        || u.getPrenom().toLowerCase().contains(keyword.toLowerCase())
                        || u.getEmail().toLowerCase().contains(keyword.toLowerCase())) {
                    filtered.add(u);
                }
            }
            tableUsers.setItems(filtered);
            tableUsers.refresh();
            updateCounters(filtered); // Mettre à jour les compteurs avec la liste filtrée
        }
    }

    /**
     * Bouton Ajouter un utilisateur
     */
    @FXML
    private void AjouterUser(ActionEvent event) {
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
        }
    }

    /**
     * Bouton Modifier
     */
    @FXML
    private void ModiferUser(ActionEvent event) {
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
     * Bouton Supprimer
     */
    @FXML
    private void SupprimerUser(ActionEvent event) {
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
     * Valide les champs obligatoires.
     */
    private boolean validerChamps() {
        StringBuilder sb = new StringBuilder();
        if (txtNom.getText().isEmpty()) sb.append("Le nom est obligatoire.\n");
        if (txtPrenom.getText().isEmpty()) sb.append("Le prénom est obligatoire.\n");
        if (txtEmail.getText().isEmpty()) sb.append("L'email est obligatoire.\n");
        if (txtPassword.getText().isEmpty()) sb.append("Le mot de passe est obligatoire.\n");
        if (comboRole.getValue() == null) sb.append("Le rôle est obligatoire.\n");

        if (sb.length() > 0) {
            showAlert("Erreur", sb.toString(), Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }

    /**
     * Réinitialise les champs et désactive Modifier/Supprimer.
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
    }

    /**
     * Affiche une boîte de dialogue d'alerte.
     */
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    private void goToAddForm(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/admin/addUser.fxml"));
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
