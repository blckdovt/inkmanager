package com.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import com.database.Benutzer;
import com.database.Motiv;

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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class MotivlisteController implements Initializable{

	@FXML
	private Button hinzufuegenMotiv;

	@FXML
	private Button loeschenMotiv;

	@FXML
	private Label kundenauswahlLabel;

	@FXML
	private TableView<Motiv> motivliste;

	@FXML
	private TableColumn<Motiv, String> nameCol;

	@FXML
	private TableColumn<Motiv, String> pfadCol;

	@FXML
	private Button loeschenButton;

	private ObservableList<Motiv> list;

	// TableView füllen
	public void initialize(URL arg0, ResourceBundle arg1) {
		list = FXCollections.observableArrayList();

		nameCol.setCellValueFactory(new PropertyValueFactory<Motiv, String>("name"));
		pfadCol.setCellValueFactory(new PropertyValueFactory<Motiv, String>("pfad"));

		listeBefuellen();
	}
	
	// Methode, um Table nach Veränderungen zu aktualisieren (Neubefüllung)
	// holt Daten erneut aus DB
	private void listeBefuellen() {
		list.clear();

		Session session = LoginController.getSf().openSession();

		Transaction txn = session.beginTransaction();

		Benutzer benutzer = session.get(Benutzer.class, LoginController.angemeldeterBenutzer.getBenutzerId());

		list = FXCollections.observableArrayList(benutzer.getMotivliste());
		motivliste.setItems(list);

		txn.commit();
		session.close();
	}

	// öffnet Fenster für das Hinzufügen eines Motivs
	@FXML
	void motivHinzufuegen(ActionEvent event) throws IOException {  	
		Parent root = FXMLLoader.load(getClass().getResource("../gui/motiveDialog.fxml"));

		Stage stage = new Stage();
		Scene scene = new Scene(root);

		stage.setScene(scene);
		stage.setTitle("InkManager");
		stage.setResizable(false);
		stage.setOnCloseRequest(e -> listeBefuellen());
		stage.show();
	}

	// Motiv wird aus Liste entfernt und aus Datenbank gelöscht
	@FXML
	void motivLoeschen(ActionEvent event) {
		Session session = LoginController.getSf().openSession();

		Transaction txn = session.beginTransaction();

		Motiv motiv = session.get(Motiv.class, motivliste.getSelectionModel().getSelectedItem().getMotivId());
		Benutzer benutzer = session.get(Benutzer.class, LoginController.angemeldeterBenutzer.getBenutzerId());
		benutzer.getMotivliste().remove(motiv);

		session.delete(motiv);

		txn.commit();
		session.close();

		listeBefuellen();
	}

	// GETTERS & SETTERS /////////////////////////////////////////////////////////////////

	public Button getLoeschenMotiv() {
		return loeschenMotiv;
	}

	public void setLoeschenMotiv(Button loeschenMotiv) {
		this.loeschenMotiv = loeschenMotiv;
	}

	public Label getKundenauswahlLabel() {
		return kundenauswahlLabel;
	}

	public Button getHinzufuegenMotiv() {
		return hinzufuegenMotiv;
	}

	public void setHinzufuegenMotiv(Button hinzufuegenMotiv) {
		this.hinzufuegenMotiv = hinzufuegenMotiv;
	}

	public void setKundenauswahlLabel(Label kundenauswahlLabel) {
		this.kundenauswahlLabel = kundenauswahlLabel;
	}

	public TableView<Motiv> getMotivliste() {
		return motivliste;
	}

	public void setMotivliste(TableView<Motiv> motivliste) {
		this.motivliste = motivliste;
	}

	public TableColumn<Motiv, String> getNameCol() {
		return nameCol;
	}

	public void setNameCol(TableColumn<Motiv, String> nameCol) {
		this.nameCol = nameCol;
	}

	public TableColumn<Motiv, String> getPfadCol() {
		return pfadCol;
	}

	public void setPfadCol(TableColumn<Motiv, String> pfadCol) {
		this.pfadCol = pfadCol;
	}

	public ObservableList<Motiv> getList() {
		return list;
	}

	public void setList(ObservableList<Motiv> list) {
		this.list = list;
	}

}
