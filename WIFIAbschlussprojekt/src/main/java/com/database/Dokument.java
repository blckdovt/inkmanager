package com.database;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="dokumente")
public class Dokument {
	
	@Id
	@Column(name="dokumentId")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int dokumentId;
	@Column(name="name")
	private String name;
	@Column(name="pfad")
	private String pfad;
	
	public int getDokumentId() {
		return dokumentId;
	}
	
	public void setDokumentId(int dokumentId) {
		this.dokumentId = dokumentId;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPfad() {
		return pfad;
	}

	public void setPfad(String pfad) {
		this.pfad = pfad;
	}
	
}
