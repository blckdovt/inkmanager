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
    	File file = fc.showOpenDialog(LogIn.getStg());
    	
    	if(file.getPath().toString().endsWith(".jpg") ||
    		file.getPath().toString().endsWith(".jpeg") ||
    		file.getPath().toString().endsWith(".png") ||
    		file.getPath().toString().endsWith(".gif"))
    	{
    		pfadText.setText(file.getPath().toString());
		}
    	else {
    		errMsg.setText("Bitte [.png, .jpg, .jpeg, .gif] - Datei auswählen.");
    	}
    }
    
    @FXML
    void speicherMotiv(ActionEvent event) {
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
    	
    	Configuration config = new Configuration().configure().addAnnotatedClass(Motiv.class).addAnnotatedClass(Benutzer.class);

		SessionFactory sf = config.buildSessionFactory();

		Session session = sf.openSession();

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
