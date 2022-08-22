package com.controllers;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import com.database.Benutzer;
import com.database.Dokument;
import com.database.Kunde;
import com.gui.LogIn;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

public class DokumentDialogController {

    @FXML
    private Button browseDokumente;

    @FXML
    private Label nameLabel;

    @FXML
    private TextField nameText;

    @FXML
    private TextField pfadText;
    
    @FXML
    private Button speichernButton;
    
    @FXML
    private Label errMsg = new Label();
    
    private Kunde kunde;

    
    @FXML
    void browseDokumente(ActionEvent event) {
    	FileChooser fc = new FileChooser();
    	File file = fc.showOpenDialog(LogIn.getStg());
    	
    	pfadText.setText(file.getPath().toString());
    }
    
    @FXML
    void speicherDokumente(ActionEvent event) {
    	if(nameText.getText().isEmpty() && pfadText.getText().isEmpty()) {
    		errMsg.setText("Bitte Name und Dateipfad angeben.");
    		return;
    	}
    	else if (pfadText.getText().isEmpty()) {
    		errMsg.setText("Bitte Dateipfad angeben.");
    		return;
    	}
    	else if (nameText.getText().isEmpty()) {
    		errMsg.setText("Bitte Name angeben.");
    		return;
    	}
    	
    	Configuration config = new Configuration().configure().addAnnotatedClass(Kunde.class).addAnnotatedClass(Dokument.class).addAnnotatedClass(Benutzer.class);

		SessionFactory sf = config.buildSessionFactory();

		Session session = sf.openSession();

		Transaction txn = session.beginTransaction();

		Dokument doc = new Dokument();
		doc.setName(nameText.getText());
		doc.setPfad(pfadText.getText());

		Kunde kundeDB = session.get(Kunde.class, kunde.getKundeId());
		kundeDB.getDokumentenliste().add(doc);

		session.save(doc);

		txn.commit();
		session.close();
    }

	public Button getBrowseDokumente() {
		return browseDokumente;
	}

	public void setBrowseDokumente(Button browseDokumente) {
		this.browseDokumente = browseDokumente;
	}

	public Label getNameLabel() {
		return nameLabel;
	}

	public void setNameLabel(Label nameLabel) {
		this.nameLabel = nameLabel;
	}

	public TextField getNameText() {
		return nameText;
	}

	public void setNameText(TextField nameText) {
		this.nameText = nameText;
	}

	public TextField getPfadText() {
		return pfadText;
	}

	public void setPfadText(TextField pfadText) {
		this.pfadText = pfadText;
	}

	public Button getSpeichernButton() {
		return speichernButton;
	}

	public void setSpeichernButton(Button speichernButton) {
		this.speichernButton = speichernButton;
	}

	public Kunde getKunde() {
		return kunde;
	}

	public void setKunde(Kunde kunde) {
		this.kunde = kunde;
	}

}
