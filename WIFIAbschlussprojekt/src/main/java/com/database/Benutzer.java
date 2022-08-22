package com.database;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;


@Entity
@Table(name="benutzer")
public class Benutzer {
	
	@Id
	@Column(name="benutzerId")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int benutzerId;
	@Column(name="Username")
	private String username;
	@Column(name="Passwort")
	private String passwort;
	@Column(name="Admin")
	private boolean admin;
	@Column(name="Deleted")
	private boolean deleted = false;
	
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name="benutzer_kunden",
				joinColumns=@JoinColumn(name="benutzerId"),
				inverseJoinColumns=@JoinColumn(name="kundeId"))
	private List<Kunde> kundenliste = new ArrayList<>();
	
	@OneToMany(targetEntity=Arbeitsmittel.class, cascade=CascadeType.ALL)
	@JoinColumn(name="benutzerId", referencedColumnName="benutzerId")
	private List<Arbeitsmittel> arbeitsmittelliste = new ArrayList<>();
	
	@OneToMany(targetEntity=Termin.class, cascade=CascadeType.ALL)
	@JoinColumn(name="benutzerId", referencedColumnName="benutzerId")
	private List<Termin> terminliste = new ArrayList<>();
	
	@OneToMany(targetEntity=Motiv.class, cascade=CascadeType.ALL)
	@JoinColumn(name="benutzerId", referencedColumnName="benutzerId")
	private List<Motiv> motivliste = new ArrayList<>();
	
	public void kundeHinzufuegen(Kunde kunde) {
		kundenliste.add(kunde);
		kunde.benutzerHinzufuegen(this);
	}
	
	public void kundeLoeschen(Kunde kunde) {
		kundenliste.remove(kunde);
		kunde.benutzerLoeschen(this);
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPasswort() {
		return passwort;
	}
	
	public void setPasswort(String passwort) {
		this.passwort = passwort;
	}
	
	public boolean isAdmin() {
		return admin;
	}
	
	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
	
	public boolean isDeleted() {
		return deleted;
	}
	
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public int getBenutzerId() {
		return benutzerId;
	}

	public void setBenutzerId(int benutzerId) {
		this.benutzerId = benutzerId;
	}

	public List<Kunde> getKundenliste() {
		return kundenliste;
	}

	public void setKundenliste(List<Kunde> kundenliste) {
		this.kundenliste = kundenliste;
	}

	public List<Arbeitsmittel> getArbeitsmittelliste() {
		return arbeitsmittelliste;
	}

	public void setArbeitsmittelliste(List<Arbeitsmittel> arbeitsmittelliste) {
		this.arbeitsmittelliste = arbeitsmittelliste;
	}

	public List<Termin> getTerminliste() {
		return this.terminliste;
	}

	public void setTerminliste(List<Termin> dokumentenliste) {
		this.terminliste = dokumentenliste;
	}

	public List<Motiv> getMotivliste() {
		return motivliste;
	}

	public void setMotivliste(List<Motiv> motivliste) {
		this.motivliste = motivliste;
	}
	
}