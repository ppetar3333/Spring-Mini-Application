package com.ftn.EUprava.models;

import java.time.LocalDateTime;

public class PrijavaZaVakcinaciju {

	private long id;
	private Pacijent pacijent;
	private LocalDateTime dateOfCheckIn;
	private Vakcina typeOfVaccine;
	private Doza dose;
	private boolean active;
	private boolean vakcinisan;
	
	public PrijavaZaVakcinaciju(long id, Pacijent pacijent, LocalDateTime dateOfCheckIn, Vakcina typeOfVaccine,
			Doza dose, boolean active, boolean vakcinisan) {
		super();
		this.id = id;
		this.pacijent = pacijent;
		this.dateOfCheckIn = dateOfCheckIn;
		this.typeOfVaccine = typeOfVaccine;
		this.dose = dose;
		this.active = active;
		this.vakcinisan = vakcinisan;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Pacijent getPacijent() {
		return pacijent;
	}

	public void setPacijent(Pacijent pacijent) {
		this.pacijent = pacijent;
	}

	public LocalDateTime getDateOfCheckIn() {
		return dateOfCheckIn;
	}

	public void setDateOfCheckIn(LocalDateTime dateOfCheckIn) {
		this.dateOfCheckIn = dateOfCheckIn;
	}

	public Vakcina getTypeOfVaccine() {
		return typeOfVaccine;
	}

	public void setTypeOfVaccine(Vakcina typeOfVaccine) {
		this.typeOfVaccine = typeOfVaccine;
	}

	public Doza getDose() {
		return dose;
	}

	public void setDose(Doza dose) {
		this.dose = dose;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	
	public boolean isVakcinisan() {
		return vakcinisan;
	}

	public void setVakcinisan(boolean vakcinisan) {
		this.vakcinisan = vakcinisan;
	}

	@Override
	public String toString() {
		return "PrijavaZaVakcinaciju [id=" + id + ", pacijent=" + pacijent + ", dateOfCheckIn=" + dateOfCheckIn
				+ ", typeOfVaccine=" + typeOfVaccine + ", dose=" + dose + ", active=" + active + ", vakcinisan="
				+ vakcinisan + "]";
	}

}
