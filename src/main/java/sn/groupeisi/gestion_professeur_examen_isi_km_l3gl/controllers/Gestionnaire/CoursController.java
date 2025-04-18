package sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.controllers.Gestionnaire;

import javafx.beans.property.SimpleStringProperty;
import sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.dao.NotificationDAO;
import sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.models.*;
import javafx.util.StringConverter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
//import javafx.css.converter.StringConverter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.dao.CoursDAO;
import sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.dao.SalleDao;
import sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.dao.UsersImpl;
import sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.models.Salle;
import sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.models.Users;

import javafx.scene.control.TextField;
import sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.utils.EmailService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public class CoursController {

    @FXML
    private TextField txtNomCours, txtDescription;

    @FXML
    private ComboBox<Users> comboProfesseur;

    @FXML
    private ComboBox<Salle> comboSalle;

    @FXML
    private Spinner<Integer> spinnerHeureDebut;

    @FXML
    private Spinner<Integer> spinnerHeureFin;


    @FXML
    private TableView<Cours> tableCours;

    @FXML
    private TableColumn<Cours, Long> colId;

    @FXML
    private TableColumn<Cours, String> colNom, colDescription;

    @FXML
    private TableColumn<Cours, String> colNomProf, colSalle;

    @FXML
    private TableColumn<Cours, LocalTime> colHeureDebut, colHeureFin;
    @FXML
    private TableColumn<Cours, LocalDate> colDateCours;

    @FXML
    private Button btnAjouter, btnModifier, btnDelete;

    @FXML  private DatePicker datePickerJour;



    private final CoursDAO coursDAO = new CoursDAO();
    private final UsersImpl usersDAO = new UsersImpl();
    private final SalleDao salleDAO = new SalleDao();
    private final NotificationDAO notificationDAO = new NotificationDAO();


    @FXML
    public void initialize() {

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colNomProf.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProfesseur().getNom() + " " + cellData.getValue().getProfesseur().getPrenom()));
        colSalle.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSalle().getLibelle()));
        colHeureDebut.setCellValueFactory(new PropertyValueFactory<>("heureDebut"));
        colHeureFin.setCellValueFactory(new PropertyValueFactory<>("heureFin"));
        colDateCours.setCellValueFactory(new PropertyValueFactory<>("dateCours"));




        datePickerJour.setConverter(new StringConverter<>() {
            @Override
            public String toString(LocalDate date) {
                return (date != null) ? String.valueOf(date.getDayOfMonth()) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                try {
                    int day = Integer.parseInt(string);
                    return LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), day);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        });

        comboProfesseur.setConverter(new StringConverter<Users>() {
            @Override
            public String toString(Users user) {
                return user != null ? user.getNom() + " " + user.getPrenom() : "";
            }

            @Override
            public Users fromString(String string) {
                return null; // Non utilisé
            }
        });

        comboSalle.setConverter(new StringConverter<Salle>() {
            @Override
            public String toString(Salle salle) {
                return salle != null ? salle.getLibelle() : "";
            }

            @Override
            public Salle fromString(String string) {
                return null; // Non utilisé
            }
        });


        chargerCours();
        chargerProfesseurs();
        chargerSalles();
        configurerBoutons();
        configurerDoubleClicTableView();

        configurerSpinners();
    }

    private void configurerSpinners() {
        SpinnerValueFactory<Integer> heureDebutFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 8);
        SpinnerValueFactory<Integer> heureFinFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 10);

        spinnerHeureDebut.setValueFactory(heureDebutFactory);
        spinnerHeureFin.setValueFactory(heureFinFactory);
    }

    private void chargerCours() {
        List<Cours> coursList = coursDAO.getAllCours();
        ObservableList<Cours> observableCours = FXCollections.observableArrayList(coursList);
        tableCours.setItems(observableCours);
        tableCours.refresh();
    }

    private void chargerProfesseurs() {
        ObservableList<Users> professeurs = FXCollections.observableArrayList(usersDAO.getAll());
        comboProfesseur.setItems(professeurs);
    }

    private void chargerSalles() {
        ObservableList<Salle> salles = FXCollections.observableArrayList(salleDAO.getToutesLesSalles());
        comboSalle.setItems(salles);
    }

    private void configurerBoutons() {
        btnAjouter.setDisable(false);
        btnModifier.setDisable(true);
        btnDelete.setDisable(true);
    }

    private void configurerDoubleClicTableView() {
        tableCours.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() == 2) {
                Cours selectedCours = tableCours.getSelectionModel().getSelectedItem();
                if (selectedCours != null) {
                    txtNomCours.setText(selectedCours.getNom());
                    txtDescription.setText(selectedCours.getDescription());
                    spinnerHeureDebut.getValueFactory().setValue(selectedCours.getHeureDebut().getHour());
                    spinnerHeureFin.getValueFactory().setValue(selectedCours.getHeureFin().getHour());
                    comboProfesseur.setValue(selectedCours.getProfesseur());
                    comboSalle.setValue(selectedCours.getSalle());

                    btnAjouter.setDisable(true);
                    btnModifier.setDisable(false);
                    btnDelete.setDisable(false);
                }
            }
        });
    }

    @FXML
    void AjouterCours(ActionEvent event) {
        String nom = txtNomCours.getText().trim();
        String description = txtDescription.getText().trim();
        Users professeur = comboProfesseur.getValue();
        Salle salle = comboSalle.getValue();
        LocalDate dateCours = datePickerJour.getValue();

        if (dateCours == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez sélectionner une date !");
            return;
        }

        // 📌 Vérifier que la date n'est pas passée
        if (dateCours.isBefore(LocalDate.now())) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Vous ne pouvez pas ajouter un cours à une date passée !");
            return;
        }

        if (spinnerHeureDebut.getValue() == null || spinnerHeureFin.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez sélectionner une heure de début et de fin !");
            return;
        }

        LocalTime heureDebut = LocalTime.of(spinnerHeureDebut.getValue(), 0);
        LocalTime heureFin = LocalTime.of(spinnerHeureFin.getValue(), 0);

        if (nom.isEmpty() || description.isEmpty() || professeur == null || salle == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Tous les champs sont obligatoires !");
            return;
        }

        if (heureDebut.isAfter(LocalTime.of(23, 0))) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "L'heure de début ne peut pas être après 23h !");
            return;
        }

        if (heureFin.isBefore(heureDebut)) {
            showAlert(Alert.AlertType.WARNING, "Attention", "L'heure de fin doit être après l'heure de début !");
            return;
        }

        if (heureDebut.plusHours(5).isBefore(heureFin)) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Un cours ne peut pas durer plus de 5 heures !");
            return;
        }

        // 📌 Vérifier les conflits d'horaires
        if (coursDAO.existeConflitHoraire(salle.getId(), dateCours, heureDebut, heureFin, null)) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Conflit d'horaire détecté !");
            return;
        }


        // 📌 Enregistrer le cours
        Cours cours = new Cours(nom, description, heureDebut, heureFin, dateCours, professeur, salle);
        coursDAO.ajouterCours(cours);

        // 📧 Envoi de l'e-mail au professeur
        String sujet = "Nouveau cours attribué";
        String message = "Bonjour " + professeur.getNom() + ",\n\n"
                + "Un nouveau cours vous a été attribué : " + nom + "\n"
                + "Date : " + dateCours + "\n"
                + "Heure : " + heureDebut + " - " + heureFin + "\n"
                + "Salle : " + salle.getLibelle() + "\n\n"
                + "Merci.";

        boolean emailEnvoye = EmailService.envoyerEmail(professeur.getEmail(), sujet, message);

        // 📌 Enregistrer la notification en base de données
        Notification notification = new Notification(message, professeur.getEmail());
        notificationDAO.ajouterNotification(notification);

        showAlert(Alert.AlertType.INFORMATION, "Succès", "Cours ajouté avec succès "
                + (emailEnvoye ? "et notification envoyée !" : "mais l'envoi de l'email a échoué."));

        clearFields();
    }



    @FXML
    void ModifierCours(ActionEvent event) {
        Cours selectedCours = tableCours.getSelectionModel().getSelectedItem();

        if (selectedCours == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner un cours à modifier !");
            return;
        }

        String nom = txtNomCours.getText().trim();
        String description = txtDescription.getText().trim();
        Users professeur = comboProfesseur.getValue();
        Salle salle = comboSalle.getValue();
        LocalDate dateCours = datePickerJour.getValue();

        if (dateCours == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez sélectionner une date !");
            return;
        }

        if (dateCours.isBefore(LocalDate.now())) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Vous ne pouvez pas modifier un cours pour une date passée !");
            return;
        }

        if (spinnerHeureDebut.getValue() == null || spinnerHeureFin.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez sélectionner une heure de début et de fin !");
            return;
        }

        LocalTime heureDebut = LocalTime.of(spinnerHeureDebut.getValue(), 0);
        LocalTime heureFin = LocalTime.of(spinnerHeureFin.getValue(), 0);

        if (nom.isEmpty() || description.isEmpty() || professeur == null || salle == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Tous les champs sont obligatoires !");
            return;
        }

        if (heureDebut.isAfter(LocalTime.of(23, 0))) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "L'heure de début ne peut pas être après 23h !");
            return;
        }

        if (heureFin.isBefore(heureDebut)) {
            showAlert(Alert.AlertType.WARNING, "Attention", "L'heure de fin doit être après l'heure de début !");
            return;
        }

        if (heureDebut.plusHours(5).isBefore(heureFin)) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Un cours ne peut pas durer plus de 5 heures !");
            return;
        }

        if (coursDAO.existeConflitHoraire(salle.getId(), dateCours, heureDebut, heureFin, selectedCours.getId())) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Conflit d'horaire détecté !");
            return;
        }


        selectedCours.setNom(nom);
        selectedCours.setDescription(description);
        selectedCours.setProfesseur(professeur);
        selectedCours.setSalle(salle);
        selectedCours.setHeureDebut(heureDebut);
        selectedCours.setHeureFin(heureFin);
        selectedCours.setDateCours(dateCours);

        coursDAO.modifierCours(selectedCours);
        chargerCours();
        clearFields();
        showAlert(Alert.AlertType.INFORMATION, "Succès", "Cours modifié avec succès !");

        clearFields();
    }



    @FXML
    void SupprimerCours(ActionEvent event) {
        Cours selectedCours = tableCours.getSelectionModel().getSelectedItem();

        if (selectedCours == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner un cours à supprimer !");
            return;
        }

        // 📌 Empêcher de supprimer un cours passé
        if (selectedCours.getDateCours().isBefore(LocalDate.now())) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Vous ne pouvez pas supprimer un cours passé !");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                "Êtes-vous sûr de vouloir supprimer ce cours ?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = confirmation.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            coursDAO.supprimerCours(selectedCours.getId());
            chargerCours();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Cours supprimé avec succès !");
        }
        clearFields();
    }

    private void clearFields() {
        txtNomCours.clear();
        txtDescription.clear();
        comboProfesseur.setValue(null);
        comboSalle.setValue(null);
        spinnerHeureDebut.getValueFactory().setValue(8);
        spinnerHeureFin.getValueFactory().setValue(10);
    }


    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
