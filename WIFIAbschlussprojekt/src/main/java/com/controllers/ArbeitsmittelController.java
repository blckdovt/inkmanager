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

public class ArbeitsmittelController implements Initializable{

	@FXML
    private TableColumn<Arbeitsmittel, LocalDate> ablaufCol;

    @FXML
    private AnchorPane anchor;

    @FXML
    private TableColumn<Arbeitsmittel, String> arbeitsmittelCol;

    @FXML
    private TableColumn<Arbeitsmittel, String> chargeNumberCol;

    @FXML
    private Button arbeitsmittel;

    @FXML
    private TableColumn<Arbeitsmittel, String> herstellerCol;

    @FXML
    private TableColumn<Arbeitsmittel, Integer> idCol;

    @FXML
    private Button kundenstamm;

    @FXML
    private Button logoutButton;

    @FXML
    private Button motive;

    @FXML
    private TableView<Arbeitsmittel> arbeitsmitteltabelle;

    @FXML
    private Button termine;

    @FXML
    private VBox vbButtons;
    
    @FXML
    private Button verwaltungButton = new Button();

    @FXML
    private Button warenlisteButton;
    
    @FXML
    private Button reservierenButton;
    
	private ObservableList<Arbeitsmittel> arbeitsmittelList;
    
	
	public void initialize(URL arg0, ResourceBundle arg1) {
		if(!LoginController.angemeldeterBenutzer.isAdmin()) {
			verwaltungButton.setDisable(true);
		}
		
		arbeitsmittelList = FXCollections.observableArrayList();
		
    	idCol.setCellValueFactory(new PropertyValueFactory<Arbeitsmittel, Integer>("arbeitsmittelId"));
		chargeNumberCol.setCellValueFactory(new PropertyValueFactory<Arbeitsmittel, String>("chargeNumber"));
		arbeitsmittelCol.setCellValueFactory(new PropertyValueFactory<Arbeitsmittel, String>("arbeitsmittelName"));
		herstellerCol.setCellValueFactory(new PropertyValueFactory<Arbeitsmittel, String>("hersteller"));
		ablaufCol.setCellValueFactory(new PropertyValueFactory<Arbeitsmittel, LocalDate>("ablaufdatum"));
		
		listeBefuellen();
	}
    
    public void listeBefuellen(){
    	arbeitsmittelList.clear();
		
		Configuration config = new Configuration().configure().addAnnotatedClass(Arbeitsmittel.class).addAnnotatedClass(Benutzer.class);

		SessionFactory sf = config.buildSessionFactory();

		Session session = sf.openSession();

		Transaction txn = session.beginTransaction();
		
		Query<Arbeitsmittel> query = session.createQuery("FROM Arbeitsmittel WHERE benutzerid is null");
		
		List<Arbeitsmittel> arbeitsmittel = query.list();
		
		arbeitsmittelList = FXCollections.observableArrayList(arbeitsmittel);
		arbeitsmitteltabelle.setItems(arbeitsmittelList);
		
		txn.commit();
		session.close();
		return;
    }
    
    @FXML
    void arbeitsmittelReservieren(ActionEvent event) {
    	Configuration config = new Configuration().configure().addAnnotatedClass(Arbeitsmittel.class).addAnnotatedClass(Benutzer.class);

		SessionFactory sf = config.buildSessionFactory();

		Session session = sf.openSession();

		Transaction txn = session.beginTransaction();
		
		Benutzer benutzer = session.get(Benutzer.class, LoginController.angemeldeterBenutzer.getBenutzerId());
		benutzer.getArbeitsmittelliste().add(arbeitsmitteltabelle.getSelectionModel().getSelectedItem());
		
		session.update(arbeitsmitteltabelle.getSelectionModel().getSelectedItem());
		
		txn.commit();
		session.close();
		
		listeBefuellen();
    }

    @FXML
    void arbeitsmittelVerwalten(ActionEvent event) throws IOException {
    	Parent root = FXMLLoader.load(getClass().getResource("../gui/arbeitsmittelVerwaltung.fxml"));

		Stage stage = new Stage();
		Scene scene = new Scene(root);

		stage.setScene(scene);
		stage.setTitle("InkManager");
		stage.setResizable(false);
		stage.setOnCloseRequest(e -> listeBefuellen());
		stage.show();
    }

    @FXML
    void warenlisteAnzeigen(ActionEvent event) throws IOException {
    	Parent root = FXMLLoader.load(getClass().getResource("../gui/warenliste.fxml"));

		Stage stage = new Stage();
		Scene scene = new Scene(root);

		stage.setScene(scene);
		stage.setTitle("InkManager");
		stage.setResizable(false);
		stage.setOnCloseRequest(e -> listeBefuellen());
		stage.show();
    }
    
    
    // Menü-Buttons /////////////////////////////////////////////////////////////////////////
    
    @FXML
    void goToKundenstamm(ActionEvent event) throws IOException{
    	LogIn login = new LogIn();
		login.changeScene("kundenstamm.fxml");
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
    
    // GETTERS & SETTERS ///////////////////////////////////////////////////////////////////

	public TableColumn<Arbeitsmittel, LocalDate> getAblaufCol() {
		return ablaufCol;
	}

	public void setAblaufCol(TableColumn<Arbeitsmittel, LocalDate> ablaufCol) {
		this.ablaufCol = ablaufCol;
	}

	public AnchorPane getAnchor() {
		return anchor;
	}

	public void setAnchor(AnchorPane anchor) {
		this.anchor = anchor;
	}

	public TableColumn<Arbeitsmittel, String> getArbeitsmittelCol() {
		return arbeitsmittelCol;
	}

	public void setArbeitsmittelCol(TableColumn<Arbeitsmittel, String> arbeitsmittelCol) {
		this.arbeitsmittelCol = arbeitsmittelCol;
	}

	public TableColumn<Arbeitsmittel, String> getChargeNumberCol() {
		return chargeNumberCol;
	}

	public void setChargeNumberCol(TableColumn<Arbeitsmittel, String> chargeNumberCol) {
		this.chargeNumberCol = chargeNumberCol;
	}

	public Button getFarben() {
		return arbeitsmittel;
	}

	public void setFarben(Button farben) {
		this.arbeitsmittel = farben;
	}

	public TableColumn<Arbeitsmittel, String> getHerstellerCol() {
		return herstellerCol;
	}

	public void setHerstellerCol(TableColumn<Arbeitsmittel, String> herstellerCol) {
		this.herstellerCol = herstellerCol;
	}

	public TableColumn<Arbeitsmittel, Integer> getIdCol() {
		return idCol;
	}

	public void setIdCol(TableColumn<Arbeitsmittel, Integer> idCol) {
		this.idCol = idCol;
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

	public TableView<Arbeitsmittel> getArbeitsmitteltabelle() {
		return arbeitsmitteltabelle;
	}

	public void setArbeitsmitteltabelle(TableView<Arbeitsmittel> arbeitsmitteltabelle) {
		this.arbeitsmitteltabelle = arbeitsmitteltabelle;
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

	public ObservableList<Arbeitsmittel> getList() {
		return arbeitsmittelList;
	}

	public void setList(ObservableList<Arbeitsmittel> list) {
		this.arbeitsmittelList = list;
	}
	
}
