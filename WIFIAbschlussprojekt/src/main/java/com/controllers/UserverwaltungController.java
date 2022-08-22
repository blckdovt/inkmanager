package com.controllers;

import java.io.IOException;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import com.database.Benutzer;
import com.gui.LogIn;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class UserverwaltungController {

	@FXML
	private Button abbrechen;

	@FXML
	private PasswordField passwordfield;

	@FXML
	private PasswordField passwordfield1;

	@FXML
	private Button userLoeschen;

	@FXML
	private Button userSpeichern;

	@FXML
	private TextField usernameTF;

	@FXML
	private CheckBox adminCheckBox;

	@FXML
	private Label adminLabel;

	@FXML
	private Label errorLabel;

	@FXML
	void abbrechen(ActionEvent event) throws IOException {
		LogIn login = new LogIn();
		login.changeScene("login.fxml");
	}

	@FXML
	void userLoeschen(ActionEvent event) {
		// Datenbank - User holen/abfragen

		Session session = LoginController.getSf().openSession();

		Transaction txn = session.beginTransaction();	

		Query<Benutzer> query = session.createQuery("FROM Benutzer");

		List<Benutzer> benutzer = query.list();

		// Benutzer Liste durchgehen und Username abfragen

		for(Benutzer b : benutzer) {
			if(b.getUsername().equals(usernameTF.getText())) {
				// wenn Username vorhanden dann löschen
				session.delete(b);
				break;
			}
		}
		txn.commit();
		session.close();
	}

	@FXML
	void userSpeichern(ActionEvent event) {

		// Richtigkeit des Formulares überprüfen

		if(usernameTF.getText().isEmpty() && passwordfield.getText().isEmpty() && passwordfield1.getText().isEmpty()) {
			errorLabel.setText("Bitte Userdaten vergeben.");
			return;
		}
		else if(usernameTF.getText().isEmpty()) {
			errorLabel.setText("Bitte Username vergeben.");
			return;
		}
		else if(passwordfield.getText().isEmpty() && passwordfield1.getText().isEmpty()) {
			errorLabel.setText("Bitte Passwort vergeben.");
			return;
		}
		else if(!passwordfield.getText().equals(passwordfield1.getText())) {
			errorLabel.setText("Passwörter stimmen nicht überein.");
			return;
		}

		Session session = LoginController.getSf().openSession();

		Transaction txn = session.beginTransaction();

		Query<Benutzer> query = session.createQuery("FROM Benutzer");

		List<Benutzer> benutzer = query.list();

		for(Benutzer b : benutzer) {

			// Wenn User vorhanden -> Update

			if(b.getUsername().equals(usernameTF.getText()) && passwordfield.getText().equals(passwordfield1.getText())) {
				b.setUsername(usernameTF.getText());
				b.setPasswort(passwordfield.getText());
				if(adminCheckBox.isSelected()) {
					b.setAdmin(true);
				}
				else {
					b.setAdmin(false);
				}

				session.update(b);

				errorLabel.setText("Benutzer wurde upgedatet.");

				txn.commit();
				session.close();

				return;
			}

		}

		// sonst neuen User anlegen und speichern

		if (passwordfield.getText().equals(passwordfield1.getText())){
			Benutzer user = new Benutzer();
			user.setUsername(usernameTF.getText());
			user.setPasswort(passwordfield.getText());
			if(adminCheckBox.isSelected()) {
				user.setAdmin(true);
			}
			else {
				user.setAdmin(false);
			}

			session.save(user);

			errorLabel.setText("Benutzer wurde hinzugefügt.");
			
			txn.commit();
			session.close();
		}

	}

}
