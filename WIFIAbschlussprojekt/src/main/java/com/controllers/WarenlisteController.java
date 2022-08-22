package com.controllers;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import com.database.Arbeitsmittel;
import com.database.Benutzer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class WarenlisteController implements Initializable{

	@FXML
	private TableColumn<Arbeitsmittel, LocalDate> ablaufCol;

	@FXML
	private TableColumn<Arbeitsmittel, String> arbeitsmittelCol;

	@FXML
	private TableColumn<Arbeitsmittel, String> chargeNumberCol;

	@FXML
	private DialogPane dialog;

	@FXML
	private TableColumn<Arbeitsmittel, String> herstellerCol;

	@FXML
	private TableColumn<Arbeitsmittel, Integer> idCol;

	@FXML
	private Button stornierenButton;

	@FXML
	private TableView<Arbeitsmittel> arbeitsmitteltabelleWarenliste;

	@FXML
	private Label warenlisteLabel;

	private ObservableList<Arbeitsmittel> warenlisteList;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		warenlisteList = FXCollections.observableArrayList();

		idCol.setCellValueFactory(new PropertyValueFactory<Arbeitsmittel, Integer>("arbeitsmittelId"));
		chargeNumberCol.setCellValueFactory(new PropertyValueFactory<Arbeitsmittel, String>("chargeNumber"));
		arbeitsmittelCol.setCellValueFactory(new PropertyValueFactory<Arbeitsmittel, String>("arbeitsmittelName"));
		herstellerCol.setCellValueFactory(new PropertyValueFactory<Arbeitsmittel, String>("hersteller"));
		ablaufCol.setCellValueFactory(new PropertyValueFactory<Arbeitsmittel, LocalDate>("ablaufdatum"));

		listeBefuellen();
	}

	public void listeBefuellen() {
		warenlisteList.clear();

		Configuration config = new Configuration().configure().addAnnotatedClass(Arbeitsmittel.class).addAnnotatedClass(Benutzer.class);

		SessionFactory sf = config.buildSessionFactory();

		Session session = sf.openSession();

		Transaction txn = session.beginTransaction();

		Query<Arbeitsmittel> query = session.createQuery("FROM Arbeitsmittel WHERE benutzerid=" + LoginController.angemeldeterBenutzer.getBenutzerId());

		List<Arbeitsmittel> arbeitsmittel = query.list();

		warenlisteList = FXCollections.observableArrayList(arbeitsmittel);
		arbeitsmitteltabelleWarenliste.setItems(warenlisteList);
		arbeitsmitteltabelleWarenliste.refresh();

		txn.commit();
		session.close();
	}

	@FXML
	void stornieren(ActionEvent event) {
		Configuration config = new Configuration().configure().addAnnotatedClass(Arbeitsmittel.class).addAnnotatedClass(Benutzer.class);

		SessionFactory sf = config.buildSessionFactory();

		Session session = sf.openSession();

		Transaction txn = session.beginTransaction();

		Benutzer benutzer = session.get(Benutzer.class, LoginController.angemeldeterBenutzer.getBenutzerId());
		Arbeitsmittel abm = session.get(Arbeitsmittel.class, arbeitsmitteltabelleWarenliste.getSelectionModel().getSelectedItem().getArbeitsmittelId());
		
		benutzer.getArbeitsmittelliste().remove(abm);
	
		session.update(abm);
		
		txn.commit();
		session.close();
		
		listeBefuellen();
	}

}
