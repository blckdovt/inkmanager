package com.controllers;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
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
import com.database.Motiv;
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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MotiveController implements Initializable{

	@FXML
	private AnchorPane anchor;

	@FXML
	private Button arbeitsmittel;

	@FXML
	private ImageView imageView;

	@FXML
	private Button kundenstamm;

	@FXML
	private Button logoutButton;

	@FXML
	private Button motive;

	@FXML
	private Button moveLeftButton;

	@FXML
	private Button moveRightButton;

	@FXML
	private Button termine;

	@FXML
	private VBox vbButtons;

	@FXML
	private Button motivliste;

	@FXML
	private Button speichernButton;

	@FXML
	private ChoiceBox<String> kundenAuswahl;

	@FXML
	private Button loeschenKunde;

	@FXML
	private Label bildNameLabel;

	@FXML
	private Label imageLabel = new Label();

	private ObservableList<String> kundenliste;

	private ArrayList<Motiv> motiveAL = new ArrayList<>();
	private int index;


	public void initialize(URL arg0, ResourceBundle arg1) {
		choiceBoxLaden();

		bilderlisteLaden();
		
		bildLaden();
	}

	// Bilder für Anzeige laden
	public void bilderlisteLaden() {
		motiveAL.clear();
		
		Session session = LoginController.getSf().openSession();

		Transaction txn = session.beginTransaction();

		Benutzer benutzer = session.get(Benutzer.class, LoginController.angemeldeterBenutzer.getBenutzerId());

		List<Motiv> motive = benutzer.getMotivliste();
		motive.sort(Comparator.comparing(Motiv::getMotivId));

		motiveAL.addAll(motive);
		index = 0;

		txn.commit();
		session.close();
	}

	public void choiceBoxLaden() {
		kundenAuswahl.setValue(null);

		if(kundenliste == null) {
			Session session = LoginController.getSf().openSession();

			Transaction txn = session.beginTransaction();

			Benutzer benutzer = session.get(Benutzer.class, LoginController.angemeldeterBenutzer.getBenutzerId());

			List<String> kundennamen = FXCollections.observableArrayList();

			for(Kunde kunde: benutzer.getKundenliste()) {
				String kundenname = "(" + kunde.getKundeId() + ") " + kunde.getKundeNachname() + ", " + kunde.getKundeVorname();
				kundennamen.add(kundenname);
			}

			kundenliste = FXCollections.observableArrayList(kundennamen);
			kundenAuswahl.getItems().addAll(kundenliste);

			txn.commit();
			session.close();
		}
	}

	// Methode um die Bilderliste durchzugehen
	// Bild wird angezeigt - mit Button Links und Rechts kann durchgeblätter werden
	public void bildLaden() {
		moveLeftButton.setDisable(true);
		moveRightButton.setDisable(true);
		
		kundenAuswahl.setDisable(false);

		
		if(motiveAL.isEmpty()) {
			return;
		}
		if(index > 0) {
			moveLeftButton.setDisable(false);
		}
		if(index < motiveAL.size()-1) {
			moveRightButton.setDisable(false);
		}

		Path pfad = Paths.get(motiveAL.get(index).getPfad().toString());

		imageView = new ImageView(pfad.toUri().toString());
		imageView.setFitHeight(imageLabel.getPrefHeight());
		imageView.setFitWidth(imageLabel.getPrefWidth());
		imageView.setPreserveRatio(true);
		imageLabel.setGraphic(imageView);
		bildNameLabel.setText(motiveAL.get(index).getName());
		
		if(!motiveAL.isEmpty()) {
			checkKundeMotiv();
		}
	}
	
	@FXML
	void goLeft(ActionEvent event) {
		index--;
		choiceBoxLaden();
		bildLaden();
	}

	@FXML
	void goRight(ActionEvent event) {
		index++;
		choiceBoxLaden();
		bildLaden();
	}
	
	// hier wird abgefragt ob Kunde zu Motiv geschlüsselt wurde
	// wenn ja dann wird CheckBox disabled
	public void checkKundeMotiv() {		
		if(motiveAL.get(index).getKunde() != null) {
			String kundenname = "(" + motiveAL.get(index).getKunde().getKundeId() + ") " + motiveAL.get(index).getKunde().getKundeNachname() + ", " + motiveAL.get(index).getKunde().getKundeVorname();
			kundenAuswahl.setValue(kundenname);
			kundenAuswahl.setDisable(true);
		}
	}

	@FXML
	void motivlisteAnzeigen(ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("../gui/motivliste.fxml"));

		Stage stage = new Stage();
		Scene scene = new Scene(root);
		
		stage.setScene(scene);
		stage.setTitle("InkManager");
		stage.setResizable(false);
		stage.setOnCloseRequest(e -> {	
			choiceBoxLaden();
			bilderlisteLaden();
			bildLaden();
		});
		stage.show();
	}

	@FXML
	void kundeSpeichern(ActionEvent event) {
		Session session = LoginController.getSf().openSession();
		session.clear();

		Transaction txn = session.beginTransaction();
		
		// Hier muss mittels des Strings die ID gefunden werden, um den Kunden zu laden
		StringBuilder kundeIdSB = new StringBuilder();
		String kundenname = kundenAuswahl.getSelectionModel().getSelectedItem();
		for(int i = 0; i < kundenname.length(); i++) {
			if(Character.isDigit(kundenname.charAt(i))) {
				kundeIdSB.append(kundenname.charAt(i));
			}
		}
		String kundeId = kundeIdSB.toString();

		Kunde kunde = session.get(Kunde.class, Integer.valueOf(kundeId));
		kunde.getMotivliste().add(motiveAL.get(index));
		
		motiveAL.get(index).setKunde(kunde);
		
		session.update(motiveAL.get(index));
		
		txn.commit();
		session.close();
		
		kundenAuswahl.setDisable(true);
	}

	@FXML
	void kundeLoeschen(ActionEvent event) {
		Session session = LoginController.getSf().openSession();

		Transaction txn = session.beginTransaction();
		
		// Hier muss mittels des Strings die ID gefunden werden, um den Kunden zu laden
		StringBuilder kundeIdSB = new StringBuilder();
		String kundenname = kundenAuswahl.getSelectionModel().getSelectedItem();
		for(int i = 0; i < kundenname.length(); i++) {
			if(Character.isDigit(kundenname.charAt(i))) {
				kundeIdSB.append(kundenname.charAt(i));
			}
		}
		String kundeId = kundeIdSB.toString();
		
		Motiv motiv = session.get(Motiv.class, motiveAL.get(index).getMotivId());

		Kunde kunde = session.get(Kunde.class, Integer.valueOf(kundeId));
		kunde.getMotivliste().remove(motiv);
		
		motiv.setKunde(null);
		
		session.update(motiv);
		
		txn.commit();
		session.close();
		
		kundenAuswahl.setDisable(false);
		kundenAuswahl.setValue(null);
	}

	// MENÜ - BUTTONS /////////////////////////////////////////////////////////////////////////

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
	void goToTermine(ActionEvent event) throws IOException{
		LogIn login = new LogIn();
		login.changeScene("termine.fxml");
	}

	@FXML
	void userLogOut(ActionEvent event) throws IOException{
		LogIn login = new LogIn();
		login.changeScene("login.fxml");
	}

	// GETTERS & SETTERS ////////////////////////////////////////////////////////////////////////

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

	public ImageView getImageView() {
		return imageView;
	}

	public void setImageView(ImageView imageView) {
		this.imageView = imageView;
	}

	public Button getKundenstamm() {
		return kundenstamm;
	}

	public void setKundenstamm(Button kundenstamm) {
		this.kundenstamm = kundenstamm;
	}

	public ChoiceBox<String> getKundenAuswahl() {
		return kundenAuswahl;
	}

	public void setKundenAuswahl(ChoiceBox<String> kundenAuswahl) {
		this.kundenAuswahl = kundenAuswahl;
	}

	public ObservableList<String> getKundenliste() {
		return kundenliste;
	}

	public void setKundenliste(ObservableList<String> kundenliste) {
		this.kundenliste = kundenliste;
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

	public Button getMoveLeftButton() {
		return moveLeftButton;
	}

	public void setMoveLeftButton(Button moveLeftButton) {
		this.moveLeftButton = moveLeftButton;
	}

	public Button getMoveRight() {
		return moveRightButton;
	}

	public void setMoveRight(Button moveRightButton) {
		this.moveRightButton = moveRightButton;
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

	public Button getMotivliste() {
		return motivliste;
	}

	public void setMotivliste(Button motivliste) {
		this.motivliste = motivliste;
	}

	public Button getSpeichernButton() {
		return speichernButton;
	}

	public void setSpeichernButton(Button speichernButton) {
		this.speichernButton = speichernButton;
	}

	public Button getLoeschenKunde() {
		return loeschenKunde;
	}

	public void setLoeschenKunde(Button loeschenKunde) {
		this.loeschenKunde = loeschenKunde;
	}

	public Label getBildNameLabel() {
		return bildNameLabel;
	}

	public void setBildNameLabel(Label bildNameLabel) {
		this.bildNameLabel = bildNameLabel;
	}

	public Button getMoveRightButton() {
		return moveRightButton;
	}

	public void setMoveRightButton(Button moveRightButton) {
		this.moveRightButton = moveRightButton;
	}

	public Label getImageLabel() {
		return imageLabel;
	}

	public void setImageLabel(Label imageLabel) {
		this.imageLabel = imageLabel;
	}

	public ArrayList<Motiv> getMotivpfadliste() {
		return motiveAL;
	}

	public void setMotivpfadliste(ArrayList<Motiv> motivpfadliste) {
		this.motiveAL = motivpfadliste;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

}
