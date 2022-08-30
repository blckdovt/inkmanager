package com.controllers;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import com.database.Benutzer;
import com.database.Kunde;
import com.database.Termin;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class TermineDialogController implements Initializable{

	private Termin termin;

	private LocalDate terminDatum;

	@FXML
	private Label datumLabel;

	@FXML
	private DatePicker datum;

	@FXML
	private TextField datumText;

	@FXML
	private Label kundenIdLabel;

	@FXML
	private ChoiceBox<String> kundeAuswahl;

	@FXML
	private Button speichernTermin;

	@FXML
	private Label uhrzeitLabel;

	@FXML
	private TextField uhrzeitStunden;

	@FXML
	private TextField uhrzeitMinuten;

	@FXML
	private Label doppelpunkt;

	@FXML
	private Label errMsg;

	ObservableList<String> list;

	// Nur 2 Zahlen zulassen (Textfeld) für Eingabe für Stunden
	// Nur 2 Zahlen zulassen (Textfeld) für Eingaben für Minuten
	// AUßerdem wird Anzeige für ChoiceBox auf besser lesebaren String geändert
	public void initialize(URL arg0, ResourceBundle arg1) {
		uhrzeitStunden.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				if(!arg2.matches("\\d*")) {
					uhrzeitStunden.setText(arg2.replaceAll("[^\\d]", ""));
				}
				if(uhrzeitStunden.getText().length() > 2) {
					String s = uhrzeitStunden.getText().substring(0,2);
					uhrzeitStunden.setText(s);
				}
			}

		});

		uhrzeitMinuten.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				if(!arg2.matches("\\d*")) {
					uhrzeitMinuten.setText(arg2.replaceAll("[^\\d]", ""));
				}
				if(uhrzeitMinuten.getText().length() > 2) {
					String s = uhrzeitMinuten.getText().substring(0,2);
					uhrzeitMinuten.setText(s);
				}
			}

		});

		Session session = LoginController.getSf().openSession();

		Transaction txn = session.beginTransaction();
		
		Benutzer benutzer = session.get(Benutzer.class, LoginController.angemeldeterBenutzer.getBenutzerId());

		List<String> kundennamen = FXCollections.observableArrayList();
		
		for(Kunde kunde: benutzer.getKundenliste()) {
			String kundenname = "(" + kunde.getKundeId() + ") " + kunde.getKundeNachname() + ", " + kunde.getKundeVorname();
			kundennamen.add(kundenname);
		}

		list = FXCollections.observableArrayList(kundennamen);
		kundeAuswahl.getItems().addAll(list);

		txn.commit();
		session.close();
	}

	// Abfrage des Formulars (Richtigkeit & Vollständigkeit)
	// Es wird abgefragt, ob ein Termin bereits übergeben worden ist
	// Wenn man schon gespeichert hat und Fenster nicht verlassen hat, so wird
	// der Termin berabeitet und kein neuer erstellt
	@FXML
	void speichern(ActionEvent event) {
			
		if(terminDatum == null) {
			errMsg.setText("Datum eintragen.");
			return;
		}
		else if(uhrzeitStunden.getText().isEmpty() && uhrzeitMinuten.getText().isEmpty()) {
			errMsg.setText("Uhrzeit eintragen.");
			return;
		}
		else if(uhrzeitStunden.getText().isEmpty() || uhrzeitMinuten.getText().isEmpty() || Integer.valueOf(uhrzeitStunden.getText()) > 23 || Integer.valueOf(uhrzeitMinuten.getText()) > 59) {
			errMsg.setText("Gültige Uhrzeit eintragen.");
			return;
		}
		else if(kundeAuswahl.getSelectionModel().getSelectedItem() == null) {
			errMsg.setText("Kunde auswählen.");
			return;
		}
		
		Session session = LoginController.getSf().openSession();

		Transaction txn = session.beginTransaction();

		if(termin == null) {
			String uhrzeitString = uhrzeitStunden.getText() + ":" + uhrzeitMinuten.getText();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
			LocalTime uhrzeit = LocalTime.parse(uhrzeitString, formatter);
			
			StringBuilder kundeIdSB = new StringBuilder();
			String kundenname = kundeAuswahl.getSelectionModel().getSelectedItem();
			for(int i = 0; i < kundenname.length(); i++) {
				if(Character.isDigit(kundenname.charAt(i))) {
					kundeIdSB.append(kundenname.charAt(i));
				}
			}
			String kundeId = kundeIdSB.toString();

			Kunde kunde = session.get(Kunde.class, Integer.valueOf(kundeId));

			Benutzer benutzer = session.get(Benutzer.class, LoginController.angemeldeterBenutzer.getBenutzerId());

			Termin termin = new Termin(kunde);
			termin.setDatum(terminDatum);
			termin.setUhrzeit(uhrzeit);
			termin.setKunde(kunde);
			this.termin = termin;

			kunde.getTerminliste().add(termin);
			benutzer.getTerminliste().add(termin);

			session.save(termin);

			errMsg.setText("Termin wurde hinzugefügt.");
			
			txn.commit();
			session.close();
			return;
		}
		else {
			termin.setDatum(terminDatum);
			
			String uhrzeitString = uhrzeitStunden.getText() + ":" + uhrzeitMinuten.getText();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
			LocalTime uhrzeit = LocalTime.parse(uhrzeitString, formatter);
			termin.setUhrzeit(uhrzeit);

			StringBuilder kundeIdSB = new StringBuilder();
			String kundenname = kundeAuswahl.getSelectionModel().getSelectedItem();
			for(int i = 0; i < kundenname.length(); i++) {
				if(Character.isDigit(kundenname.charAt(i))) {
					kundeIdSB.append(kundenname.charAt(i));
				}
			}
			String kundeId = kundeIdSB.toString();
			
			Kunde kunde = session.get(Kunde.class, Integer.valueOf(kundeId));
			
			termin.setKunde(kunde);
			
			session.update(termin);
			
			errMsg.setText("Termin wurde upgedatet.");
			
			txn.commit();
			session.close();
		}
	}

	@FXML
	void termindatumEintragen(ActionEvent event) {
		terminDatum = datum.getValue();
	}

	// GETTERS & SETTERS ///////////////////////////////////////////////////

	public Label getDatumLabel() {
		return datumLabel;
	}

	public void setDatumLabel(Label datumLabel) {
		this.datumLabel = datumLabel;
	}

	public DatePicker getDatumPicker() {
		return datum;
	}

	public void setDatumPicker(DatePicker datumPicker) {
		this.datum = datumPicker;
	}

	public TextField getDatumText() {
		return datumText;
	}

	public void setDatumText(TextField datumText) {
		this.datumText = datumText;
	}

	public Label getKundenIdLabel() {
		return kundenIdLabel;
	}

	public void setKundenIdLabel(Label kundenIdLabel) {
		this.kundenIdLabel = kundenIdLabel;
	}

	public Button getSpeichernTermin() {
		return speichernTermin;
	}

	public void setSpeichernTermin(Button speichernTermin) {
		this.speichernTermin = speichernTermin;
	}

	public Label getUhrzeitLabel() {
		return uhrzeitLabel;
	}

	public void setUhrzeitLabel(Label uhrzeitLabel) {
		this.uhrzeitLabel = uhrzeitLabel;
	}

	public DatePicker getDatum() {
		return datum;
	}

	public void setDatum(DatePicker datum) {
		this.datum = datum;
	}

	public ChoiceBox<String> getKundeAuswahl() {
		return kundeAuswahl;
	}

	public void setKundeAuswahl(ChoiceBox<String> kundeAuswahl) {
		this.kundeAuswahl = kundeAuswahl;
	}

	public Termin getTermin() {
		return termin;
	}

	public void setTermin(Termin termin) {
		this.termin = termin;
	}

	public LocalDate getTerminDatum() {
		return terminDatum;
	}

	public void setTerminDatum(LocalDate terminDatum) {
		this.terminDatum = terminDatum;
	}


	public TextField getUhrzeitStunden() {
		return uhrzeitStunden;
	}


	public void setUhrzeitStunden(TextField uhrzeitStunden) {
		this.uhrzeitStunden = uhrzeitStunden;
	}


	public TextField getUhrzeitMinuten() {
		return uhrzeitMinuten;
	}


	public void setUhrzeitMinuten(TextField uhrzeitMinuten) {
		this.uhrzeitMinuten = uhrzeitMinuten;
	}


	public Label getDoppelpunkt() {
		return doppelpunkt;
	}


	public void setDoppelpunkt(Label doppelpunkt) {
		this.doppelpunkt = doppelpunkt;
	}

}
