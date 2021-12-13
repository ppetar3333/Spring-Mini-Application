package com.ftn.EUprava.models;

public class VakcinaModel {
	private Long id;
	private Vakcina vakcina;
	private String mesto;
	private boolean active;
	
	public VakcinaModel(Long id, Vakcina vakcina, String mesto, boolean active) {
		super();
		this.id = id;
		this.vakcina = vakcina;
		this.mesto = mesto;
		this.active = active;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Vakcina getVakcina() {
		return vakcina;
	}

	public void setVakcina(Vakcina vakcina) {
		this.vakcina = vakcina;
	}

	public String getMesto() {
		return mesto;
	}

	public void setMesto(String mesto) {
		this.mesto = mesto;
	}

	
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public String toString() {
		return "VakcinaModel [id=" + id + ", vakcina=" + vakcina + ", mesto=" + mesto + ", active=" + active + "]" + "\n";
	}
}
