package com.database;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="Motive")
public class Motiv {

	@Id
	@Column(name="motivId")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int motivId;
	@Column(name="Name")
	private String name;
	@Column(name="Pfad")
	private String pfad;
	
	@ManyToOne
	@JoinColumn(name="kundeId", referencedColumnName="kundeId")
	private Kunde kunde;
	
	public int getMotivId() {
		return motivId;
	}
	
	public void setMotivId(int motivId) {
		this.motivId = motivId;
	}
	
	public String getPfad() {
		return pfad;
	}
	
	public void setPfad(String pfad) {
		this.pfad = pfad;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Kunde getKunde() {
		return kunde;
	}

	public void setKunde(Kunde kunde) {
		this.kunde = kunde;
	}

}
