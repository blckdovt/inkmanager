package com.controllers;

import java.io.File;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import com.database.Benutzer;
import com.database.Motiv;
import com.gui.LogIn;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

public class MotiveDialogController {
	
	@FXML
    private Button browseMotive;

    @FXML
    private Label nameLabel;

    @FXML
    private TextField nameText;

    @FXML
    private TextField pfadText;
    
    @FXML
    private Button speichernButton;
    
    @FXML
    private Label errMsg;

    @FXML
    void browseMotive(ActionEvent event) {
    	FileChooser fc = new FileChooser();
    	
    	// festlegen, dass nur gewisse Dateien (Bilder) ausgewählt werden
    	fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image files ", "*.png ", "*.jpg", "*.jpeg", "*.gif"));
    	File file = fc.showOpenDialog(null);
    	
    	// falls falsches File ausgewählt wurde wird error ausgegeben
    	if(file != null) {
    		pfadText.setText(file.getPath().toString());
		}
    	else {
    		errMsg.setText("Bitte [.png, .jpg, .jpeg, .gif] - Datei auswählen.");
    	}
    }
    
    @FXML
    void speicherMotiv(ActionEvent event) {
    	
    	// Abfrage des Formulars (Richtigkeit & Vollständigkeit)
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
    	
    	Session session = LoginController.getSf().openSession();

		Transaction txn = session.beginTransaction();

		Motiv motiv = new Motiv();
		motiv.setName(nameText.getText());
		motiv.setPfad(pfadText.getText());
		
		Benutzer benutzer = session.get(Benutzer.class, LoginController.angemeldeterBenutzer.getBenutzerId());
		benutzer.getMotivliste().add(motiv);

		session.save(motiv);

		txn.commit();
		session.close();
    }
    
    // GETTERS & SETTERS ////////////////////////////////////////////////////////////////////

	public Button getBrowseMotive() {
		return browseMotive;
	}

	public void setBrowseMotive(Button browseMotive) {
		this.browseMotive = browseMotive;
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
    
}
