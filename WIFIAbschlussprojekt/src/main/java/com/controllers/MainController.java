package com.controllers;

import java.io.IOException;

import com.gui.LogIn;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class MainController {

    @FXML
    private AnchorPane anchor;

    @FXML
    private Button termine;

    @FXML
    private Button farben;
    
    @FXML
    private Button arbeitsmittel;

    @FXML
    private Button kundenstamm;

    @FXML
    private Button logoutButton;

    @FXML
    private Button motive;

    @FXML
    private VBox vbButtons;

    // MENÜ BUTTONS //////////////////////////////////////////////////////////////////////////////////////////////
    
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
    void goToTermine(ActionEvent event) throws IOException{
    	LogIn login = new LogIn();
		login.changeScene("termine.fxml");
    }

    @FXML
    void userLogOut(ActionEvent event) throws IOException{
    	LogIn login = new LogIn();
		login.changeScene("login.fxml");
    }

}
