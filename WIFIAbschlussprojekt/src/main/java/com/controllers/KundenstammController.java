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
import com.gui.LogIn;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class KundenstammController implements Initializable{

	@FXML
	private AnchorPane anchor;

	@FXML
	private Button arbeitsmittel;

	@FXML
	private TableColumn<Kunde, LocalDate> geburtstagCol;

	@FXML
	private TableColumn<Kunde, Integer> idCol;

	@FXML
	private Button kundeBearbeiten;

	@FXML
	private Button kundeHinzufuegen;

	@FXML
	private Button kundeLoeschen;

	@FXML
	private Button kundenstamm;

	@FXML
	private Button logoutButton;

	@FXML
	private Button motive;

	@FXML
	private TableColumn<Kunde, String> nachnameCol;

	@FXML
	private TableView<Kunde> kundentabelle;

	@FXML
	private Button termine;

	@FXML
	private VBox vbButtons;

	@FXML
	private TableColumn<Kunde, String> vornameCol;

	private ObservableList<Kunde> list = FXCollections.observableArrayList();

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		idCol.setCellValueFactory(new PropertyValueFactory<Kunde, Integer>("kundeId"));
		vornameCol.setCellValueFactory(new PropertyValueFactory<Kunde, String>("kundeVorname"));
		nachnameCol.setCellValueFactory(new PropertyValueFactory<Kunde, String>("kundeNachname"));
		geburtstagCol.setCellValueFactory(new PropertyValueFactory<Kunde, LocalDate>("geburtstag"));

		listeBefuellen();
	}

	// Methode, um Table nach Veränderungen zu aktualisieren
	public void listeBefuellen() {
		list.clear();
		
		Session session = LoginController.getSf().openSession();

		Transaction txn = session.beginTransaction();

		Benutzer benutzer = session.get(Benutzer.class, LoginController.angemeldeterBenutzer.getBenutzerId());

		list = FXCollections.observableArrayList(benutzer.getKundenliste());
		kundentabelle.setItems(list);

		txn.commit();
		session.close();
		return;
	}

	@FXML
	void kundeHinzufuegen(ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("../gui/kundenstammDialog.fxml"));

		Stage stage = new Stage();
		Scene scene = new Scene(root);

		stage.setScene(scene);
		stage.setTitle("InkManager");
		stage.setResizable(false);
		stage.setOnCloseRequest(e -> listeBefuellen());
		stage.show();
	}

	@FXML
	void kundeBearbeiten(ActionEvent event) throws IOException {
		FXMLLoader fxmlloader = new FXMLLoader(getClass().getResource("../gui/kundenstammDialog.fxml"));

		Parent root = fxmlloader.load();

		Stage stage = new Stage();
		Scene scene = new Scene(root);

		// Bevor Stage geladen wird, wird hier der Controller des FXMLs geladen, um die
		// TextFields, etc. vorab auszufüllen
		KundenstammDialogController kundenstammcontroller = fxmlloader.getController();
		kundenstammcontroller.setKunde(kundentabelle.getSelectionModel().getSelectedItem());

		kundenstammcontroller.getVornameText().setText(kundentabelle.getSelectionModel().getSelectedItem().getKundeVorname());
		kundenstammcontroller.getNachnameText().setText(kundentabelle.getSelectionModel().getSelectedItem().getKundeNachname());
		kundenstammcontroller.getGebDatumAuswahl().setValue(kundentabelle.getSelectionModel().getSelectedItem().getGeburtstag());
		kundenstammcontroller.setGeb(kundentabelle.getSelectionModel().getSelectedItem().getGeburtstag()); 

		kundenstammcontroller.getDokumentLoeschen().setDisable(false);
		kundenstammcontroller.getHinzufuegenButton().setDisable(false);

		// Hier werden die Dokumente, die zu einem Kunden geschlüsselt wurden, geladen und die Liste
		// im DialogController befüllt
		Session session = LoginController.getSf().openSession();

		Transaction txn = session.beginTransaction();

		Kunde kundeDB = session.get(Kunde.class, kundentabelle.getSelectionModel().getSelectedItem().getKundeId());

		ObservableList<Dokument> dokumentenliste = FXCollections.observableArrayList(kundeDB.getDokumentenliste());
		kundenstammcontroller.getDokumententabelle().setItems(dokumentenliste);

		txn.commit();
		session.close();
		
		// erst dann wird Stage geladen
		stage.setScene(scene);
		stage.setTitle("InkManager");
		stage.setResizable(false);
		stage.setOnCloseRequest(e -> listeBefuellen());
		stage.show();
	}

	@FXML
	void loescheKunde(ActionEvent event) {		
		Session session = LoginController.getSf().openSession();

		Transaction txn = session.beginTransaction();

		Kunde kunde = session.get(Kunde.class, kundentabelle.getSelectionModel().getSelectedItem().getKundeId());

		Benutzer benutzer = session.get(Benutzer.class, LoginController.angemeldeterBenutzer.getBenutzerId());

		benutzer.getKundenliste().remove(kunde);
		kunde.getBenutzerliste().remove(benutzer);

		session.delete(kunde);

		txn.commit();
		session.close();

		listeBefuellen();
	}

	// Menü-Buttons /////////////////////////////////////////////////////////////////////////

	@FXML
	void goToArbeitsmittel(ActionEvent event) throws IOException{
		LogIn login = new LogIn();
		login.changeScene("arbeitsmittel.fxml");
	}

	@FXML
	void goToMotive(ActionEvent event) throws IOException{
		LogIn login = new LogIn();
		login.changeScene("motive.fxml");
	}

	@FXML
	void goToTermine(ActionEvent event) throws IOException{
		LogIn login = new LogIn();
		login.changeScene("termine.fxml");
	}

	@FXML
	void userLogOut(ActionEvent event) throws IOException{
		LogIn login = new LogIn();
		login.changeScene("login.fxml");
	}

	// GETTERS & SETTERS //////////////////////////////////////////////////////////////////////////////////

	public AnchorPane getAnchor() {
		return anchor;
	}

	public void setAnchor(AnchorPane anchor) {
		this.anchor = anchor;
	}

	public Button getArbeitsmittel() {
		return arbeitsmittel;
	}

	public void setArbeitsmittel(Button arbeitsmittel) {
		this.arbeitsmittel = arbeitsmittel;
	}

	public TableColumn<Kunde, LocalDate> getGeburtstagCol() {
		return geburtstagCol;
	}

	public void setGeburtstagCol(TableColumn<Kunde, LocalDate> geburtstagCol) {
		this.geburtstagCol = geburtstagCol;
	}

	public TableColumn<Kunde, Integer> getIdCol() {
		return idCol;
	}

	public void setIdCol(TableColumn<Kunde, Integer> idCol) {
		this.idCol = idCol;
	}

	public Button getKundeBearbeiten() {
		return kundeBearbeiten;
	}

	public void setKundeBearbeiten(Button kundeBearbeiten) {
		this.kundeBearbeiten = kundeBearbeiten;
	}

	public Button getKundeHinzufuegen() {
		return kundeHinzufuegen;
	}

	public void setKundeHinzufuegen(Button kundeHinzufuegen) {
		this.kundeHinzufuegen = kundeHinzufuegen;
	}

	public Button getKundeLoeschen() {
		return kundeLoeschen;
	}

	public void setKundeLoeschen(Button kundeLoeschen) {
		this.kundeLoeschen = kundeLoeschen;
	}

	public Button getKundenstamm() {
		return kundenstamm;
	}

	public void setKundenstamm(Button kundenstamm) {
		this.kundenstamm = kundenstamm;
	}

	public Button getLogoutButton() {
		return logoutButton;
	}

	public void setLogoutButton(Button logoutButton) {
		this.logoutButton = logoutButton;
	}

	public Button getMotive() {
		return motive;
	}

	public void setMotive(Button motive) {
		this.motive = motive;
	}

	public TableColumn<Kunde, String> getNachnameCol() {
		return nachnameCol;
	}

	public void setNachnameCol(TableColumn<Kunde, String> nachnameCol) {
		this.nachnameCol = nachnameCol;
	}

	public TableView<Kunde> getKundentabelle() {
		return kundentabelle;
	}

	public void setKundentabelle(TableView<Kunde> kundentabelle) {
		this.kundentabelle = kundentabelle;
	}

	public Button getTermine() {
		return termine;
	}

	public void setTermine(Button termine) {
		this.termine = termine;
	}

	public VBox getVbButtons() {
		return vbButtons;
	}

	public void setVbButtons(VBox vbButtons) {
		this.vbButtons = vbButtons;
	}

	public TableColumn<Kunde, String> getVornameCol() {
		return vornameCol;
	}

	public void setVornameCol(TableColumn<Kunde, String> vornameCol) {
		this.vornameCol = vornameCol;
	}

	public ObservableList<Kunde> getList() {
		return list;
	}

	public void setList(ObservableList<Kunde> list) {
		this.list = list;
	}

}
