package sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.controllers.Auth;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.dao.UsersImpl;
import sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.models.Users;
import sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.utils.SessionManager;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField txtEmail;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private CheckBox chkShowPassword;

    @FXML
    private Button btnLogin;

    @FXML
    private Label lblError;

    private final UsersImpl usersDAO = new UsersImpl();
    @FXML
//    void SeConnecter(ActionEvent event) {
//        String email = txtEmail.getText();
//        String password = txtPassword.getText();
//
//        Users user = usersDAO.findByEmail(email);
//        if (user != null && user.getPassword().equals(password)) {
//            SessionManager.setCurrentUser(user);
//            navigateToDashboard(user.getRole());
//        } else {
//            lblError.setText(" Email ou mot de passe incorrect !");
//        }
//    }

    //Button apres la gestion des roles
    void SeConnecter(ActionEvent event) {
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            lblError.setText("Veuillez remplir tous les champs !");
            return;
        }

        System.out.println("Email saisi : " + email);
        System.out.println("Mot de passe saisi : " + password);

        Users user = usersDAO.findByEmail(email);

        if (user != null) {
            System.out.println("Utilisateur trouvé : " + user.getEmail());
            System.out.println("Mot de passe en base : " + user.getPassword());

            if (password.equals(user.getPassword())) { // Comparaison directe
                System.out.println("✅ Connexion réussie !");
                SessionManager.setCurrentUser(user);
                navigateToDashboard();
            } else {
               // System.out.println("Mot de passe incorrect !");
                lblError.setText("Email ou mot de passe incorrect !");
            }
        } else {
            //System.out.println(" Utilisateur non trouvé !");
            lblError.setText("Email ou mot de passe incorrect !");
        }
    }




    @FXML
    public void initialize() {
        chkShowPassword.setOnAction(event -> AfficherPasswordVisibility());
    }

    @FXML
    private void AfficherPasswordVisibility() {
        if (chkShowPassword.isSelected()) {
            txtPassword.setPromptText(txtPassword.getText()); // Stocke temporairement le mot de passe dans le PromptText
            txtPassword.setText(""); // Vide le champ pour afficher uniquement le prompt
        } else {
            txtPassword.setText(txtPassword.getPromptText()); // Récupère la valeur du prompt
            txtPassword.setPromptText(""); // Réinitialise le PromptText
        }
    }


    //    private void navigateToDashboard(String role) {
//        String view = switch (role) {
//            case "Admin" -> "/views/admin/utilisateur-view.fxml";
//            case "Professeur" -> "/views/professeurs/professeur-view.fxml";
//            case "Gestionnaire" -> "/views/gestionnaires/dashboard-view.fxml";
//            default -> null;
//        };
//
//        if (view != null) {
//            try {
//                FXMLLoader loader = new FXMLLoader(getClass().getResource(view));
//                Scene scene = new Scene(loader.load());
//                Stage stage = (Stage) btnLogin.getScene().getWindow();
//                stage.setScene(scene);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }else{
//            lblError.setText(" Erreur lors de la redirection");
//        }
//    }
 //  //  Nouvelle version de la navigation avec gestion des rôles
private void navigateToDashboard() {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/dashboard/dashboard-view.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) btnLogin.getScene().getWindow();
        stage.setScene(scene);
    } catch (IOException e) {
        e.printStackTrace();
        lblError.setText("Erreur lors de la redirection vers le tableau de bord.");
    }
}

}