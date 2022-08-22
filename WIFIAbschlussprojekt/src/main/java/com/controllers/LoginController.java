package com.controllers;

import java.io.IOException;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import com.database.Benutzer;
import com.database.Kunde;
import com.gui.LogIn;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

	@FXML
	private Label errorMsg;

	@FXML
	private Button loginButton;

	@FXML
	private PasswordField passwortTF;

	@FXML
	private TextField usernameTF;

	@FXML
	private Button userverwaltung;

	public static Benutzer angemeldeterBenutzer;
	
	private static Configuration config;
	
	private static SessionFactory sf;
	
	// statischer Initialisierer um nur einmal die Configuration aufzurufen und die SessionFactory
	// zu bilden
	static {
		config = new Configuration().configure().addAnnotatedClass(Benutzer.class);
		sf = config.buildSessionFactory();
	}

	@FXML
	void userLogIn(ActionEvent event) throws IOException {

		// Abfrage des Formulars (Richtigkeit & Vollständigkeit)
		if(usernameTF.getText().isEmpty() && passwortTF.getText().isEmpty()) {
			errorMsg.setText("Bitte LogIn-Daten eingeben.");
			return;
		}
		else if(usernameTF.getText().isEmpty() && !passwortTF.getText().isEmpty()){
			errorMsg.setText("Bitte Username eingeben.");
			return;
		}
		else if(passwortTF.getText().isEmpty()) {
			errorMsg.setText("Bitte Passwort eingeben.");
			return;
		}

		LogIn login = new LogIn();

		// Datenbank - User holen/abfragen

		Session session = sf.openSession();

		Transaction txn = session.beginTransaction();	

		Query<Benutzer> query = session.createQuery("FROM Benutzer");

		List<Benutzer> benutzerliste = query.list();
		
		// Wenn kein Benutzer vorhanden ist (z.B. nach Neuinstallation), dann wird ein Admin erstellt
		// Username: admin
		// Passwort: admin
		if(benutzerliste.isEmpty()) {
			Benutzer benutzer = new Benutzer();
			benutzer.setAdmin(true);
			benutzer.setUsername("admin");
			benutzer.setPasswort("admin");
			
			benutzerliste.add(benutzer);
			
			session.save(benutzer);
		}
		
		// alle Benutzer durchgehen
		for(Benutzer b : benutzerliste) {

			if(b.getUsername().equals(usernameTF.getText())) {

				// Passwortabfrage -> wenn Username gefunden

				if(passwortTF.getText().equals(b.getPasswort())) {
					errorMsg.setText("Anmelden erfolgreich!");
					angemeldeterBenutzer = b;
					login.changeScene("main.fxml");

					txn.commit();
					session.close();

					return;
				}
				else{
					errorMsg.setText("Falsches Passwort.");

					txn.commit();
					session.close();

					return;
				}
			}

		}

		errorMsg.setText("Kein User mit diesem Username gefunden.");

		txn.commit();
		session.close();

	}

	@FXML
	void userverwaltung(ActionEvent event) throws IOException {
		 
		// Abfrage des Formulars (Richtigkeit & Vollständigkeit)
		if(usernameTF.getText().isEmpty() && passwortTF.getText().isEmpty()) {
			errorMsg.setText("Bitte Admin LogIn-Daten eingeben.");
			return;
		}
		else if(usernameTF.getText().isEmpty() && !passwortTF.getText().isEmpty()){
			errorMsg.setText("Bitte Admin-Username eingeben.");
			return;
		}
		else if(passwortTF.getText().isEmpty()) {
			errorMsg.setText("Bitte Admin-Passwort eingeben.");
			return;
		}

		LogIn login = new LogIn();

		// Datenbank - User holen/abfragen

		Session session = sf.openSession();

		Transaction txn = session.beginTransaction();	

		Query<Benutzer> query = session.createQuery("FROM Benutzer");

		List<Benutzer> benutzer = query.list();

		for(Benutzer b : benutzer) {

			//hier wird zuzüglich Abgefragt ob Benutzer ein Adminrechte hat
			if(b.isAdmin() && b.getUsername().equals(usernameTF.getText())) {

				// Passwortabfrage -> wenn Username gefunden

				if(passwortTF.getText().equals(b.getPasswort())) {
					errorMsg.setText("Anmelden erfolgreich!");
					login.changeScene("userverwaltung.fxml");

					txn.commit();
					session.close();

					return;
				}
				else{
					errorMsg.setText("Falsches Admin-Passwort.");

					txn.commit();
					session.close();

					return;
				}
			}

		}

		errorMsg.setText("Kein Admin mit diesem Username gefunden.");

		txn.commit();
		session.close();
	}

	public Label getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(Label errorMsg) {
		this.errorMsg = errorMsg;
	}

	public Button getLoginButton() {
		return loginButton;
	}

	public void setLoginButton(Button loginButton) {
		this.loginButton = loginButton;
	}

	public PasswordField getPasswortTF() {
		return passwortTF;
	}

	public void setPasswortTF(PasswordField passwortTF) {
		this.passwortTF = passwortTF;
	}

	public TextField getUsernameTF() {
		return usernameTF;
	}

	public void setUsernameTF(TextField usernameTF) {
		this.usernameTF = usernameTF;
	}

	public Button getUserverwaltung() {
		return userverwaltung;
	}

	public void setUserverwaltung(Button userverwaltung) {
		this.userverwaltung = userverwaltung;
	}

	public static Benutzer getAngemeldeterBenutzer() {
		return angemeldeterBenutzer;
	}

	public static void setAngemeldeterBenutzer(Benutzer angemeldeterBenutzer) {
		LoginController.angemeldeterBenutzer = angemeldeterBenutzer;
	}

	public static Configuration getConfig() {
		return config;
	}

	public static void setConfig(Configuration config) {
		LoginController.config = config;
	}

	public static SessionFactory getSf() {
		return sf;
	}

	public static void setSf(SessionFactory sf) {
		LoginController.sf = sf;
	}
	
}
