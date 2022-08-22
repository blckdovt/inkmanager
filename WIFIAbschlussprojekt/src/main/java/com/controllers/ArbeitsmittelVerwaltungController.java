package com.controllers;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import com.database.Arbeitsmittel;
import com.database.Benutzer;
import com.database.Dokument;
import com.database.Kunde;

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
import javafx.stage.Stage;

public class ArbeitsmittelVerwaltungController implements Initializable{

	@FXML
    private TableColumn<Arbeitsmittel, String> arbeitsmittelCol;
	
	@FXML
    private TableColumn<Arbeitsmittel, LocalDate> ablaufCol;

    @FXML
    private Button bearbeitenButton;

    @FXML
    private TableColumn<Arbeitsmittel, String> chargeNumberCol;

    @FXML
    private TableColumn<Arbeitsmittel, String> herstellerCol;

    @FXML
    private Button hinzufuegenButton;

    @FXML
    private TableColumn<Arbeitsmittel, Integer> idCol;

    @FXML
    private Button loeschenArbeitsmittel;

    @FXML
    private TableView<Arbeitsmittel> arbeitsmitteltabelleVerwaltung;
    
    private ObservableList<Arbeitsmittel> arbeitsmittelVerwaltungList;
    

	public void initialize(URL arg0, ResourceBundle arg1) {
		arbeitsmittelVerwaltungList = FXCollections.observableArrayList();
		
		idCol.setCellValueFactory(new PropertyValueFactory<Arbeitsmittel, Integer>("arbeitsmittelId"));
		chargeNumberCol.setCellValueFactory(new PropertyValueFactory<Arbeitsmittel, String>("chargeNumber"));
		arbeitsmittelCol.setCellValueFactory(new PropertyValueFactory<Arbeitsmittel, String>("arbeitsmittelName"));
		herstellerCol.setCellValueFactory(new PropertyValueFactory<Arbeitsmittel, String>("hersteller"));
		ablaufCol.setCellValueFactory(new PropertyValueFactory<Arbeitsmittel, LocalDate>("ablaufdatum"));
		
		listeBefuellen();
	}

	// Methode, um Table nach Veränderungen zu aktualisieren
    private void listeBefuellen() {
    	arbeitsmittelVerwaltungList.clear();
		
    	Session session = LoginController.getSf().openSession();

		Transaction txn = session.beginTransaction();
		
		Query<Arbeitsmittel> query = session.createQuery("FROM Arbeitsmittel WHERE benutzerid is null");
		
		List<Arbeitsmittel> arbeitsmittel = query.list();

		arbeitsmittelVerwaltungList = FXCollections.observableArrayList(arbeitsmittel);
		arbeitsmitteltabelleVerwaltung.setItems(arbeitsmittelVerwaltungList);
		arbeitsmitteltabelleVerwaltung.refresh();
		
		txn.commit();
		session.close();
		return;
	}

    @FXML
    void hinzufuegenArbeitsmittel(ActionEvent event) throws IOException {
    	Parent root = FXMLLoader.load(getClass().getResource("../gui/arbeitsmittelVerwaltungDialog.fxml"));

		Stage stage = new Stage();
		Scene scene = new Scene(root);

		stage.setScene(scene);
		stage.setTitle("InkManager");
		stage.setResizable(false);
		stage.setOnCloseRequest(e -> listeBefuellen());
		stage.show();
    }
    
	@FXML
    void bearbeitenArbeitsmittel(ActionEvent event) throws IOException {
		FXMLLoader fxmlloader = new FXMLLoader(getClass().getResource("../gui/arbeitsmittelVerwaltungDialog.fxml"));

		Parent root = fxmlloader.load();

		Stage stage = new Stage();
		Scene scene = new Scene(root);

		ArbeitsmittelVerwaltungDialogController arbeitsmittelcontroller = fxmlloader.getController();
		arbeitsmittelcontroller.getChargeNumberText().setText(arbeitsmitteltabelleVerwaltung.getSelectionModel().getSelectedItem().getChargeNumber());
		arbeitsmittelcontroller.getArbeitsmittelText().setText(arbeitsmitteltabelleVerwaltung.getSelectionModel().getSelectedItem().getArbeitsmittelName());
		arbeitsmittelcontroller.getHerstellerText().setText(arbeitsmitteltabelleVerwaltung.getSelectionModel().getSelectedItem().getHersteller());
		arbeitsmittelcontroller.setExpdate(arbeitsmitteltabelleVerwaltung.getSelectionModel().getSelectedItem().getAblaufdatum());
		arbeitsmittelcontroller.getAblaufDatumauswahl().setValue(arbeitsmitteltabelleVerwaltung.getSelectionModel().getSelectedItem().getAblaufdatum()); 
		arbeitsmittelcontroller.setArbeitsmittel(arbeitsmitteltabelleVerwaltung.getSelectionModel().getSelectedItem());
		
		stage.setScene(scene);
		stage.setTitle("InkManager");
		stage.setResizable(false);
		stage.setOnCloseRequest(e -> listeBefuellen());
		stage.show();
    }

    @FXML
    void loeschenArbeitsmittel(ActionEvent event) {
    	Session session = LoginController.getSf().openSession();

		Transaction txn = session.beginTransaction();
		
		session.delete(arbeitsmitteltabelleVerwaltung.getSelectionModel().getSelectedItem());
		
		txn.commit();
		session.close();
		
		listeBefuellen();
    }
    
    // GETTERS & SETTERS ///////////////////////////////////////////////////////////////////

	public TableColumn<Arbeitsmittel, String> getArbeitsmittelCol() {
		return arbeitsmittelCol;
	}

	public void setArbeitsmittelCol(TableColumn<Arbeitsmittel, String> arbeitsmittelCol) {
		this.arbeitsmittelCol = arbeitsmittelCol;
	}

	public TableColumn<Arbeitsmittel, LocalDate> getAblaufCol() {
		return ablaufCol;
	}

	public void setAblaufCol(TableColumn<Arbeitsmittel, LocalDate> ablaufCol) {
		this.ablaufCol = ablaufCol;
	}

	public Button getBearbeitenButton() {
		return bearbeitenButton;
	}

	public void setBearbeitenButton(Button bearbeitenButton) {
		this.bearbeitenButton = bearbeitenButton;
	}

	public TableColumn<Arbeitsmittel, String> getChargeNumberCol() {
		return chargeNumberCol;
	}

	public void setChargeNumberCol(TableColumn<Arbeitsmittel, String> chargeNumberCol) {
		this.chargeNumberCol = chargeNumberCol;
	}

	public TableColumn<Arbeitsmittel, String> getHerstellerCol() {
		return herstellerCol;
	}

	public void setHerstellerCol(TableColumn<Arbeitsmittel, String> herstellerCol) {
		this.herstellerCol = herstellerCol;
	}

	public Button getHinzufuegenButton() {
		return hinzufuegenButton;
	}

	public void setHinzufuegenButton(Button hinzufuegenButton) {
		this.hinzufuegenButton = hinzufuegenButton;
	}

	public TableColumn<Arbeitsmittel, Integer> getIdCol() {
		return idCol;
	}

	public void setIdCol(TableColumn<Arbeitsmittel, Integer> idCol) {
		this.idCol = idCol;
	}

	public Button getLoeschenArbeitsmittel() {
		return loeschenArbeitsmittel;
	}

	public void setLoeschenArbeitsmittel(Button loeschenArbeitsmittel) {
		this.loeschenArbeitsmittel = loeschenArbeitsmittel;
	}

	public TableView<Arbeitsmittel> getArbeitsmitteltabelle() {
		return arbeitsmitteltabelleVerwaltung;
	}

	public void setArbeitsmitteltabelle(TableView<Arbeitsmittel> arbeitsmitteltabelle) {
		this.arbeitsmitteltabelleVerwaltung = arbeitsmitteltabelle;
	}

	public ObservableList<Arbeitsmittel> getList() {
		return arbeitsmittelVerwaltungList;
	}

	public void setList(ObservableList<Arbeitsmittel> list) {
		this.arbeitsmittelVerwaltungList = list;
	}

}
