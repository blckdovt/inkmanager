package com.controllers;

import java.time.LocalDate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import com.database.Arbeitsmittel;
import com.database.Benutzer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ArbeitsmittelVerwaltungDialogController {

	private LocalDate expdate;

	private Arbeitsmittel arbeitsmittel;

	@FXML
	private DatePicker ablaufDatumauswahl;

	@FXML
	private Label ablaufLabel;

	@FXML
	private Label arbeitsmittelLabel;

	@FXML
	private TextField arbeitsmittelText;

	@FXML
	private Label chargeNumberLabel;

	@FXML
	private TextField chargeNumberText;

	@FXML
	private Label errMsg;

	@FXML
	private Label herstellerLabel;

	@FXML
	private TextField herstellerText;

	@FXML
	private Button speicherArbeitsmittel;

	@FXML
	void speichern(ActionEvent event) {
		
		// Abfrage des Formulars (Richtigkeit & Vollständigkeit)
		if(chargeNumberText.getText().isEmpty()) {
			errMsg.setText("Charge Number eintragen.");
			return;
		}
		else if(arbeitsmittelText.getText().isEmpty()) {
			errMsg.setText("Arbeitsmittel eintragen.");
			return;
		}
		else if(herstellerText.getText().isEmpty()) {
			errMsg.setText("Herstellername eintragen.");
			return;
		}
		else if(expdate == null) {
			errMsg.setText("Ablaufdatum eintragen.");
			return;
		}

		Session session = LoginController.getSf().openSession();

		Transaction txn = session.beginTransaction();

		// Es wird abgefragt, ob ein Arbeitsmittel bereits übergeben worden ist
		// Wenn man schon gespeichert hat und Fenster nicht verlassen hat, so wird
		// das Arbeitsmittel berabeitet und kein neues erstellt
		if(arbeitsmittel == null) {
			Arbeitsmittel abm = new Arbeitsmittel();
			abm.setChargeNumber(chargeNumberText.getText());
			abm.setArbeitsmittelName(arbeitsmittelText.getText());
			abm.setHersteller(herstellerText.getText());
			abm.setAblaufdatum(expdate);
			this.arbeitsmittel = abm;
			
			session.save(abm);
			
			txn.commit();
			session.close();

			return;
		}
		else {
			arbeitsmittel.setChargeNumber(chargeNumberText.getText());
			arbeitsmittel.setArbeitsmittelName(arbeitsmittelText.getText());
			arbeitsmittel.setHersteller(herstellerText.getText());
			arbeitsmittel.setAblaufdatum(expdate);

			session.update(arbeitsmittel);

			txn.commit();
			session.close();

			return;
		}
	}

	@FXML
	void ablaufdatumEintragen(ActionEvent event) {
		expdate = ablaufDatumauswahl.getValue();
	}

	// GETTERS & SETTERS ///////////////////////////////////////////////////////////////////

	public LocalDate getExpdate() {
		return expdate;
	}

	public void setExpdate(LocalDate expdate) {
		this.expdate = expdate;
	}

	public Label getArbeitsmittelLabel() {
		return arbeitsmittelLabel;
	}

	public void setArbeitsmittelLabel(Label arbeitsmittelLabel) {
		this.arbeitsmittelLabel = arbeitsmittelLabel;
	}

	public TextField getArbeitsmittelText() {
		return arbeitsmittelText;
	}

	public void setArbeitsmittelText(TextField arbeitsmittelText) {
		this.arbeitsmittelText = arbeitsmittelText;
	}

	public Label getChargeNumberLabel() {
		return chargeNumberLabel;
	}

	public void setChargeNumberLabel(Label chargeNumberLabel) {
		this.chargeNumberLabel = chargeNumberLabel;
	}

	public TextField getChargeNumberText() {
		return chargeNumberText;
	}

	public void setChargeNumberText(TextField chargeNumberText) {
		this.chargeNumberText = chargeNumberText;
	}

	public Label getHerstellerLabel() {
		return herstellerLabel;
	}

	public void setHerstellerLabel(Label herstellerLabel) {
		this.herstellerLabel = herstellerLabel;
	}

	public TextField getHerstellerText() {
		return herstellerText;
	}

	public void setHerstellerText(TextField herstellerText) {
		this.herstellerText = herstellerText;
	}

	public Button getSpeicherArbeitsmittel() {
		return speicherArbeitsmittel;
	}

	public void setSpeicherArbeitsmittel(Button speicherArbeitsmittel) {
		this.speicherArbeitsmittel = speicherArbeitsmittel;
	}

	public DatePicker getAblaufDatumauswahl() {
		return ablaufDatumauswahl;
	}

	public void setAblaufDatumauswahl(DatePicker ablaufDatumauswahl) {
		this.ablaufDatumauswahl = ablaufDatumauswahl;
	}

	public Label getAblaufLabel() {
		return ablaufLabel;
	}

	public void setAblaufLabel(Label ablaufLabel) {
		this.ablaufLabel = ablaufLabel;
	}

	public Label getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(Label errMsg) {
		this.errMsg = errMsg;
	}

	public Arbeitsmittel getArbeitsmittel() {
		return arbeitsmittel;
	}

	public void setArbeitsmittel(Arbeitsmittel arbeitsmittel) {
		this.arbeitsmittel = arbeitsmittel;
	}

}
