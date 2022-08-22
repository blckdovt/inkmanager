package com.database;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="kunden")
public class Kunde {

	@Id
	@Column(name="kundeId")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int kundeId;
	@Column(name="KundeNachname")
	private String kundeNachname;
	@Column(name="KundeVorname")
	private String kundeVorname;
	@Column(name="Geburtstag")
	private LocalDate geburtstag;
	
	@ManyToMany(mappedBy="kundenliste", cascade=CascadeType.ALL)
	private List<Benutzer> benutzerliste = new ArrayList<>();
	
	@OneToMany(targetEntity=Dokument.class, cascade=CascadeType.ALL)
	@JoinColumn(name="kundeId", referencedColumnName="kundeId")
	private List<Dokument> dokumentenliste = new ArrayList<>();
	
	@OneToMany(targetEntity=Termin.class, cascade=CascadeType.ALL, mappedBy="kunde")
	private List<Termin> terminliste = new ArrayList<>();
	
	@OneToMany(targetEntity=Motiv.class, cascade=CascadeType.ALL, mappedBy="kunde")
	private List<Motiv> motivliste = new ArrayList<>();
	
	public int getKundeId() {
		return kundeId;
	}
	
	public void setKundeId(int kundeId) {
		this.kundeId = kundeId;
	}
	
	public String getKundeNachname() {
		return kundeNachname;
	}
	
	public void setKundeNachname(String kundeNachname) {
		this.kundeNachname = kundeNachname;
	}
	
	public String getKundeVorname() {
		return kundeVorname;
	}
	
	public void setKundeVorname(String kundeVorname) {
		this.kundeVorname = kundeVorname;
	}
	
	public LocalDate getGeburtstag() {
		return geburtstag;
	}
	
	public void setGeburtstag(LocalDate geburtstag) {
		this.geburtstag = geburtstag;
	}
	
	public List<Benutzer> getBenutzerListe() {
		return benutzerliste;
	}

	public void setBenutzerListe(List<Benutzer> benutzerListe) {
		this.benutzerliste = benutzerListe;
	}

	public void benutzerHinzufuegen(Benutzer benutzer) {
		this.benutzerliste.add(benutzer);
	}
	
	public void benutzerLoeschen(Benutzer benutzer) {
		this.benutzerliste.remove(benutzer);
	}

	public List<Benutzer> getBenutzerliste() {
		return benutzerliste;
	}

	public void setBenutzerliste(List<Benutzer> benutzerliste) {
		this.benutzerliste = benutzerliste;
	}

	public List<Dokument> getDokumentenliste() {
		return dokumentenliste;
	}

	public void setDokumentenliste(List<Dokument> dokumentenliste) {
		this.dokumentenliste = dokumentenliste;
	}

	public List<Termin> getTerminliste() {
		return terminliste;
	}

	public void setTerminliste(List<Termin> terminliste) {
		this.terminliste = terminliste;
	}

	public List<Motiv> getMotivliste() {
		return motivliste;
	}

	public void setMotivliste(List<Motiv> motivliste) {
		this.motivliste = motivliste;
	}
}
