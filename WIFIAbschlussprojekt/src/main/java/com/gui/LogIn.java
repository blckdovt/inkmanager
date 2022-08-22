package com.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LogIn extends Application {

	private static Stage stg;
	
	// Start des Programms
	
	@Override
	public void start(Stage primaryStage) throws IOException {			
		
		stg = primaryStage;

		Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
		
		Scene scene = new Scene(root);
		
		primaryStage.setScene(scene);
		primaryStage.setTitle("InkManager");
		primaryStage.setResizable(false);
		primaryStage.show();
	}

	public void changeScene(String fxml) throws IOException {
		Parent pane = FXMLLoader.load(getClass().getResource(fxml));
		stg.getScene().setRoot(pane);
	}
	
	public static void main(String[] args) {
		launch(args);
	}

	public static Stage getStg() {
		return stg;
	}

	public static void setStg(Stage stg) {
		LogIn.stg = stg;
	}
	
}
