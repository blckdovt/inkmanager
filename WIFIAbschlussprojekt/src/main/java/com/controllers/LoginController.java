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

	@FXML
	void userLogIn(ActionEvent event) throws IOException {

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

		Configuration config = new Configuration().configure().addAnnotatedClass(Benutzer.class).addAnnotatedClass(Kunde.class);

		SessionFactory sf = config.buildSessionFactory();

		Session session = sf.openSession();

		Transaction txn = session.beginTransaction();	

		Query<Benutzer> query = session.createQuery("FROM Benutzer");

		List<Benutzer> benutzerliste = query.list();
		
		if(benutzerliste.isEmpty()) {
			Benutzer benutzer = new Benutzer();
			benutzer.setAdmin(true);
			benutzer.setUsername("admin");
			benutzer.setPasswort("admin");
			
			benutzerliste.add(benutzer);
			
			session.save(benutzer);
		}

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

		Configuration config = new Configuration().configure().addAnnotatedClass(Benutzer.class).addAnnotatedClass(Kunde.class);

		SessionFactory sf = config.buildSessionFactory();

		Session session = sf.openSession();

		Transaction txn = session.beginTransaction();	

		Query<Benutzer> query = session.createQuery("FROM Benutzer");

		List<Benutzer> benutzer = query.list();

		for(Benutzer b : benutzer) {

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

}
