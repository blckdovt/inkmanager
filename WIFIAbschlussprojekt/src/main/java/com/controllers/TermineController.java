package com.controllers;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import com.database.Benutzer;
import com.database.Kunde;
import com.database.Termin;
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

public class TermineController implements Initializable{

	@FXML
	private AnchorPane anchor;

	@FXML
	private Button arbeitsmittel;

	@FXML
	private Button bearbeitenTermin;

	@FXML
	private TableColumn<Termin, LocalDate> datumCol;

	@FXML
	private Button hinzufuegenTermin;

	@FXML
	private TableColumn<Termin, Integer> kundeIdCol;

	@FXML
	private Button kundenstamm;

	@FXML
	private Button loeschenTermin;

	@FXML
	private Button logoutButton;

	@FXML
	private Button motive;

	@FXML
	private TableColumn<Termin, String> nachnameCol;

	@FXML
	private TableView<Termin> termintabelle = new TableView<>();

	@FXML
	private Button termine;

	@FXML
	private TableColumn<Termin, LocalTime> uhrzeitCol;

	@FXML
	private VBox vbButtons;

	@FXML
	private TableColumn<Termin, String> vornameCol;
	
	ObservableList<Termin> list = FXCollections.observableArrayList();
	

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		datumCol.setCellValueFactory(new PropertyValueFactory<Termin, LocalDate>("datum"));
		uhrzeitCol.setCellValueFactory(new PropertyValueFactory<Termin, LocalTime>("uhrzeit"));
		kundeIdCol.setCellValueFactory(new PropertyValueFactory<Termin, Integer>("kundeId"));
		vornameCol.setCellValueFactory(new PropertyValueFactory<Termin, String>("kundeNachname"));
		nachnameCol.setCellValueFactory(new PropertyValueFactory<Termin, String>("kundeVorname"));
		
		listeBefuellen();
	}
	
	public void listeBefuellen() {
		list.clear();

		Configuration config = new Configuration().configure().addAnnotatedClass(Termin.class).addAnnotatedClass(Kunde.class);

		SessionFactory sf = config.buildSessionFactory();

		Session session = sf.openSession();

		Transaction txn = session.beginTransaction();

		Query<Termin> query = session.createQuery("FROM Termin WHERE benutzerId=" + LoginController.angemeldeterBenutzer.getBenutzerId());
		
		List<Termin> terminliste = FXCollections.observableArrayList();
		
		for(Termin termin: query.list()) {
			Kunde kunde = session.get(Kunde.class, termin.getKunde().getKundeId());
			termin.setKundeId(kunde.getKundeId());
			termin.setKundeNachname(kunde.getKundeNachname());
			termin.setKundeVorname(kunde.getKundeVorname());
			
			terminliste.add(termin);
		}
		
		list = FXCollections.observableArrayList(terminliste);
		
		termintabelle.setItems(list);
		termintabelle.refresh();
		
		txn.commit();
		session.close();
		return;
	}

	@FXML
	void terminBearbeiten(ActionEvent event) throws IOException {
		FXMLLoader fxmlloader = new FXMLLoader(getClass().getResource("../gui/termineDialog.fxml"));

		Parent root = fxmlloader.load();

		Stage stage = new Stage();
		Scene scene = new Scene(root);
		
		TermineDialogController terminecontroller = fxmlloader.getController();
		terminecontroller.setTermin(termintabelle.getSelectionModel().getSelectedItem());
		terminecontroller.getDatum().setValue(termintabelle.getSelectionModel().getSelectedItem().getDatum());
		terminecontroller.setTerminDatum(termintabelle.getSelectionModel().getSelectedItem().getDatum());
		
		String uhrzeit = termintabelle.getSelectionModel().getSelectedItem().getUhrzeit().toString();
		terminecontroller.getUhrzeitStunden().setText(uhrzeit.charAt(0)+""+uhrzeit.charAt(1));
		terminecontroller.getUhrzeitMinuten().setText(uhrzeit.charAt(3)+""+uhrzeit.charAt(4));
		
		String kundenname = "(" + termintabelle.getSelectionModel().getSelectedItem().getKundeId() + ") " + termintabelle.getSelectionModel().getSelectedItem().getKundeNachname() + ", " + termintabelle.getSelectionModel().getSelectedItem().getKundeVorname();
		terminecontroller.getKundeAuswahl().setValue(kundenname);
		
		stage.setScene(scene);
		stage.setTitle("InkManager");
		stage.setResizable(false);
		stage.setOnCloseRequest(e -> listeBefuellen());
		stage.show();
	}

	@FXML
	void terminHinzufügen(ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("../gui/termineDialog.fxml"));

		Stage stage = new Stage();
		Scene scene = new Scene(root);

		stage.setScene(scene);
		stage.setTitle("InkManager");
		stage.setResizable(false);
		stage.setOnCloseRequest(e ->listeBefuellen());
		stage.show();
	}

	@FXML
	void terminLoeschen(ActionEvent event) {
		Configuration config = new Configuration().configure().addAnnotatedClass(Termin.class).addAnnotatedClass(Benutzer.class).addAnnotatedClass(Kunde.class);

		SessionFactory sf = config.buildSessionFactory();

		Session session = sf.openSession();

		Transaction txn = session.beginTransaction();
		
		Termin termin = session.get(Termin.class, termintabelle.getSelectionModel().getSelectedItem().getTerminId());
		
		Benutzer benutzer = session.get(Benutzer.class, LoginController.angemeldeterBenutzer.getBenutzerId());
		
		Kunde kunde = session.get(Kunde.class, termintabelle.getSelectionModel().getSelectedItem().getKunde().getKundeId());
		
		benutzer.getTerminliste().remove(termin);
		kunde.getTerminliste().remove(termin);
		
		session.delete(termin);
		
		txn.commit();
		session.close();
		
		listeBefuellen();
	}

	// Menü-Buttons /////////////////////////////////////////////////////////////////////////

	@FXML
	void goToKundenstamm(ActionEvent event) throws IOException{
		LogIn login = new LogIn();
		login.changeScene("kundenstamm.fxml");
	}

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
	void userLogOut(ActionEvent event) throws IOException{
		LogIn login = new LogIn();
		login.changeScene("login.fxml");
	}

	// GETTERS & SETTERS ////////////////////////////////////////////////////
	
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

	public Button getBearbeitenTermin() {
		return bearbeitenTermin;
	}

	public void setBearbeitenTermin(Button bearbeitenTermin) {
		this.bearbeitenTermin = bearbeitenTermin;
	}

	public TableColumn<Termin, LocalDate> getDatumCol() {
		return datumCol;
	}

	public void setDatumCol(TableColumn<Termin, LocalDate> datumCol) {
		this.datumCol = datumCol;
	}

	public Button getHinzufuegenTermin() {
		return hinzufuegenTermin;
	}

	public void setHinzufuegenTermin(Button hinzufuegenTermin) {
		this.hinzufuegenTermin = hinzufuegenTermin;
	}

	public TableColumn<Termin, Integer> getKundeIdCol() {
		return kundeIdCol;
	}

	public void setKundeIdCol(TableColumn<Termin, Integer> kundeIdCol) {
		this.kundeIdCol = kundeIdCol;
	}

	public Button getKundenstamm() {
		return kundenstamm;
	}

	public void setKundenstamm(Button kundenstamm) {
		this.kundenstamm = kundenstamm;
	}

	public Button getLoeschenTermin() {
		return loeschenTermin;
	}

	public void setLoeschenTermin(Button loeschenTermin) {
		this.loeschenTermin = loeschenTermin;
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

	public TableColumn<Termin, String> getNachnameCol() {
		return nachnameCol;
	}

	public void setNachnameCol(TableColumn<Termin, String> nachnameCol) {
		this.nachnameCol = nachnameCol;
	}

	public TableView<Termin> getTermintabelle() {
		return termintabelle;
	}

	public void setTermintabelle(TableView<Termin> termintabelle) {
		this.termintabelle = termintabelle;
	}

	public Button getTermine() {
		return termine;
	}

	public void setTermine(Button termine) {
		this.termine = termine;
	}

	public TableColumn<Termin, LocalTime> getUhrzeitCol() {
		return uhrzeitCol;
	}

	public void setUhrzeitCol(TableColumn<Termin, LocalTime> uhrzeitCol) {
		this.uhrzeitCol = uhrzeitCol;
	}

	public VBox getVbButtons() {
		return vbButtons;
	}

	public void setVbButtons(VBox vbButtons) {
		this.vbButtons = vbButtons;
	}

	public TableColumn<Termin, String> getVornameCol() {
		return vornameCol;
	}

	public void setVornameCol(TableColumn<Termin, String> vornameCol) {
		this.vornameCol = vornameCol;
	}

	public ObservableList<Termin> getList() {
		return list;
	}

	public void setList(ObservableList<Termin> list) {
		this.list = list;
	}
	
}
