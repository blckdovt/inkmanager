package com.controllers;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import com.database.Benutzer;
import com.database.Dokument;
import com.database.Kunde;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.Window;


public class KundenstammDialogController implements Initializable{

	private LocalDate geb;

	private Kunde kunde;

	@FXML
	private Button dokumentLoeschen;

	@FXML
	private TableColumn<Dokument, String> dokumenteCol;

	@FXML
	private Label dokumenteLabel;

	@FXML
	private Label errorMsg;

	@FXML
	private DatePicker gebDatumAuswahl;

	@FXML
	private Label geburtstagLabel;

	@FXML
	private Button hinzufuegenButton;

	@FXML
	private Label nachnameLabel;

	@FXML
	private TextField nachnameText;

	@FXML
	private TableColumn<Dokument, String> pfadCol;

	@FXML
	private Button speichernKunde;

	@FXML
	private TableView<Dokument> dokumententabelle;

	@FXML
	private Label vornameLabel;

	@FXML
	private TextField vornameText;

	private ObservableList<Dokument> list;


	public void initialize(URL arg0, ResourceBundle arg1) {
		list = FXCollections.observableArrayList();

		dokumenteCol.setCellValueFactory(new PropertyValueFactory<Dokument, String>("name"));
		pfadCol.setCellValueFactory(new PropertyValueFactory<Dokument, String>("pfad"));

		// Wenn Kunde noch nicht abgespeichert ist, dann kann kein Dokument hinzugefügt werden
		// --> foreign Key noch nicht vorhanden ==> darum werden Buttons disabled
		if(kunde == null) {
			hinzufuegenButton.setDisable(true);
			dokumentLoeschen.setDisable(true);
		}
	}

	@FXML
	void speichern(ActionEvent event) {
		
		// Abfrage des Formulars (Richtigkeit & Vollständigkeit)
		if(vornameText.getText().isEmpty()) {
			errorMsg.setText("Vorname eintragen.");
			return;
		}
		for(Character buchstabe: vornameText.getText().toCharArray()) {
			if(Character.isDigit(buchstabe)) {
				errorMsg.setText("Im Vorname dürfen keine Zahlen enthalten sein.");
				return;
			}
		}
		if(nachnameText.getText().isEmpty()) {
			errorMsg.setText("Nachname eintragen.");
			return;
		}
		for(Character buchstabe: nachnameText.getText().toCharArray()) {
			if(Character.isDigit(buchstabe)) {
				errorMsg.setText("Im Nachname dürfen keine Zahlen enthalten sein.");
				return;
			}
		}
		if(geb == null) {
			errorMsg.setText("Geburtstag eintragen.");
			return;
		}

		Session session = LoginController.getSf().openSession();

		Transaction txn = session.beginTransaction();

		// Es wird abgefragt, ob ein/e Kund/Innen bereits übergeben worden ist
		// Wenn man schon gespeichert hat und Fenster nicht verlassen hat, so werden
		// die Kund/Innen berabeitet und keine neuen erstellt
		if(kunde == null) {
			Kunde kunde = new Kunde();
			kunde.setKundeVorname(vornameText.getText());
			kunde.setKundeNachname(nachnameText.getText());
			kunde.setGeburtstag(geb);
			this.kunde = kunde;

			hinzufuegenButton.setDisable(false);
			dokumentLoeschen.setDisable(false);

			Benutzer benutzer = session.get(Benutzer.class, LoginController.angemeldeterBenutzer.getBenutzerId());
			benutzer.kundeHinzufuegen(kunde);

			session.save(kunde);

			errorMsg.setText("Kunde wurde hinzugefügt.");

			txn.commit();
			session.close();

			return;
		}
		else {
			kunde.setKundeVorname(vornameText.getText());
			kunde.setKundeNachname(nachnameText.getText());
			kunde.setGeburtstag(geb);

			session.update(kunde);

			errorMsg.setText("Kunde wurde upgedatet.");

			txn.commit();
			session.close();

			return;
		}
	}

	@FXML
	void geburtstagEintragen(ActionEvent event) {
		geb = gebDatumAuswahl.getValue();
	}

	@FXML
	void dokumentHinzufuegen(ActionEvent event) throws IOException {	
		FXMLLoader fxmlloader = new FXMLLoader(getClass().getResource("../gui/dokumentDialog.fxml"));

		Parent root = fxmlloader.load();

		Stage stage = new Stage();
		Scene scene = new Scene(root);

		DokumentDialogController dokumentcontroller = fxmlloader.getController();
		dokumentcontroller.setKunde(kunde);
		
		stage.setScene(scene);
		stage.setTitle("InkManager");
		stage.setResizable(false);
		stage.setOnCloseRequest(e -> listeBefuellen());
		stage.show();
	}

	@FXML
	void dokumentLoeschen(ActionEvent event) {
		Configuration config = new Configuration().configure().addAnnotatedClass(Dokument.class);

		SessionFactory sf = config.buildSessionFactory();

		Session session = sf.openSession();

		Transaction txn = session.beginTransaction();

		session.delete(dokumententabelle.getSelectionModel().getSelectedItem());

		txn.commit();
		session.close();

		if(kunde != null) {
			listeBefuellen();
		}
	}

	// Methode, um Table nach Veränderungen zu aktualisieren
	public void listeBefuellen() {
		list.clear();

		Session session = LoginController.getSf().openSession();

		Transaction txn = session.beginTransaction();

		Kunde kundeDB = session.get(Kunde.class, kunde.getKundeId());

		list = FXCollections.observableArrayList(kundeDB.getDokumentenliste());
		dokumententabelle.setItems(list);
		dokumententabelle.refresh();

		txn.commit();
		session.close();
		return;
	}

	// GETTERS & SETTERS //////////////////////////////////////////////////

	public TableColumn<Dokument, String> getDokumenteCol() {
		return dokumenteCol;
	}

	public void setDokumenteCol(TableColumn<Dokument, String> dokumenteCol) {
		this.dokumenteCol = dokumenteCol;
	}

	public Label getDokumenteLabel() {
		return dokumenteLabel;
	}

	public void setDokumenteLabel(Label dokumenteLabel) {
		this.dokumenteLabel = dokumenteLabel;
	}

	public Label getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(Label errorMsg) {
		this.errorMsg = errorMsg;
	}

	public DatePicker getGebDatumAuswahl() {
		return gebDatumAuswahl;
	}

	public void setGebDatumAuswahl(DatePicker gebDatumAuswahl) {
		this.gebDatumAuswahl = gebDatumAuswahl;
	}

	public Label getGeburtstagLabel() {
		return geburtstagLabel;
	}

	public void setGeburtstagLabel(Label geburtstagLabel) {
		this.geburtstagLabel = geburtstagLabel;
	}

	public Button getHinzufuegenButton() {
		return hinzufuegenButton;
	}

	public void setHinzufuegenButton(Button hinzufügenButton) {
		this.hinzufuegenButton = hinzufügenButton;
	}

	public Label getNachnameLabel() {
		return nachnameLabel;
	}

	public void setNachnameLabel(Label nachnameLabel) {
		this.nachnameLabel = nachnameLabel;
	}

	public TextField getNachnameText() {
		return nachnameText;
	}

	public void setNachnameText(TextField nachnameText) {
		this.nachnameText = nachnameText;
	}

	public TableColumn<Dokument, String> getPfadCol() {
		return pfadCol;
	}

	public void setPfadCol(TableColumn<Dokument, String> pfadCol) {
		this.pfadCol = pfadCol;
	}

	public Button getSpeichernKunde() {
		return speichernKunde;
	}

	public void setSpeichernKunde(Button speichernKunde) {
		this.speichernKunde = speichernKunde;
	}

	public TableView<Dokument> getTableview() {
		return dokumententabelle;
	}

	public void setTableview(TableView<Dokument> tableview) {
		this.dokumententabelle = tableview;
	}

	public Label getVornameLabel() {
		return vornameLabel;
	}

	public void setVornameLabel(Label vornameLabel) {
		this.vornameLabel = vornameLabel;
	}

	public TextField getVornameText() {
		return vornameText;
	}

	public void setVornameText(TextField vornameText) {
		this.vornameText = vornameText;
	}

	public Kunde getKunde() {
		return kunde;
	}

	public void setKunde(Kunde kunde) {
		this.kunde = kunde;
	}

	public LocalDate getGeb() {
		return geb;
	}

	public void setGeb(LocalDate geb) {
		this.geb = geb;
	}

	public Button getDokumentLoeschen() {
		return dokumentLoeschen;
	}

	public void setDokumentLoeschen(Button dokumentLoeschen) {
		this.dokumentLoeschen = dokumentLoeschen;
	}

	public TableView<Dokument> getDokumententabelle() {
		return dokumententabelle;
	}

	public void setDokumententabelle(TableView<Dokument> dokumententabelle) {
		this.dokumententabelle = dokumententabelle;
	}

	public ObservableList<Dokument> getList() {
		return list;
	}

	public void setList(ObservableList<Dokument> list) {
		this.list = list;
	}



}
