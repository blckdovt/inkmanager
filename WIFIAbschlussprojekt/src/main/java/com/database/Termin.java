package com.database;

import java.time.LocalDate;
import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="termine")
public class Termin {
	
	@Id
	@Column(name="terminId")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int terminId;
	@Column(name="Datum")
	private LocalDate datum;
	@Column(name="Uhrzeit")
	private LocalTime uhrzeit;
	@Column(name="Info")
	private String info;
	@Transient
	private int kundeId;
	@Transient
	private String kundeNachname;
	@Transient
	private String kundeVorname;
	
	@ManyToOne
	@JoinColumn(name="kundeId", referencedColumnName="kundeId")
	private Kunde kunde;
	
	
	public Termin() {
		
	}
	
	public Termin(Kunde kunde) {
		this.kunde = kunde;
	}
	
	public int getTerminId() {
		return terminId;
	}
	
	public void setTerminId(int terminId) {
		this.terminId = terminId;
	}
	
	public LocalDate getDatum() {
		return datum;
	}
	
	public void setDatum(LocalDate datum) {
		this.datum = datum;
	}
	
	public LocalTime getUhrzeit() {
		return uhrzeit;
	}
	
	public void setUhrzeit(LocalTime uhrzeit) {
		this.uhrzeit = uhrzeit;
	}
	
	public String getInfo() {
		return info;
	}
	
	public void setInfo(String info) {
		this.info = info;
	}

	public Kunde getKunde() {
		return kunde;
	}

	public void setKunde(Kunde kunde) {
		this.kunde = kunde;
	}

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

}
