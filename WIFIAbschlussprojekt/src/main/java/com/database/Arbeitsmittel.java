package com.database;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name="arbeitsmittel")
public class Arbeitsmittel {
	
	@Id
	@Column(name="arbeitsmittelId")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int arbeitsmittelId;
	@Column(name="chargeNumber")
	private String chargeNumber;
	@Column(name="arbeitsmittelName")
	private String arbeitsmittelName;
	@Column(name="hersteller")
	private String hersteller;
	@Column(name="ablaufdatum")
	private LocalDate ablaufdatum;
	
	public int getArbeitsmittelId() {
		return arbeitsmittelId;
	}
	
	public void setArbeitsmittelId(int arbeitsmittelId) {
		this.arbeitsmittelId = arbeitsmittelId;
	}
	
	public String getChargeNumber() {
		return chargeNumber;
	}
	
	public void setChargeNumber(String chargeNumber) {
		this.chargeNumber = chargeNumber;
	}
	
	public String getArbeitsmittelName() {
		return arbeitsmittelName;
	}
	
	public void setArbeitsmittelName(String arbeitsmittelName) {
		this.arbeitsmittelName = arbeitsmittelName;
	}
	
	public String getHersteller() {
		return hersteller;
	}
	
	public void setHersteller(String hersteller) {
		this.hersteller = hersteller;
	}
	
	public LocalDate getAblaufdatum() {
		return ablaufdatum;
	}
	
	public void setAblaufdatum(LocalDate ablaufdatum) {
		this.ablaufdatum = ablaufdatum;
	}

}
