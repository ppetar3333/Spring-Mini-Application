package com.ftn.EUprava.models;

import java.time.LocalDateTime;

public class VakcinalniKarton {

	private Long id;
	private Pacijent pacijent;
	private LocalDateTime dateOfCheckIn;
	private LocalDateTime dateOfVaccinating;
	private Vakcina typeOfVaccine;
	private Doza dose;
	private boolean active;
	
	public VakcinalniKarton(Long id, Pacijent pacijent, LocalDateTime dateOfCheckIn, LocalDateTime dateOfVaccinating,
			Vakcina typeOfVaccine, Doza dose, boolean active) {
		super();
		this.id = id;
		this.pacijent = pacijent;
		this.dateOfCheckIn = dateOfCheckIn;
		this.dateOfVaccinating = dateOfVaccinating;
		this.typeOfVaccine = typeOfVaccine;
		this.dose = dose;
		this.active = active;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
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

	public LocalDateTime getDateOfVaccinating() {
		return dateOfVaccinating;
	}

	public void setDateOfVaccinating(LocalDateTime dateOfVaccinating) {
		this.dateOfVaccinating = dateOfVaccinating;
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

	@Override
	public String toString() {
		return "VakcinalniKarton [id=" + id + ", pacijent=" + pacijent + ", dateOfCheckIn=" + dateOfCheckIn
				+ ", dateOfVaccinating=" + dateOfVaccinating + ", typeOfVaccine=" + typeOfVaccine + ", dose=" + dose
				+ ", active=" + active + "]";
	}
	
}
