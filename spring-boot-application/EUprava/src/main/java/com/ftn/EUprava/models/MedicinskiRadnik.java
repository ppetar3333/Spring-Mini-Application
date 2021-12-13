package com.ftn.EUprava.models;

public class MedicinskiRadnik extends Korisnik {

	public MedicinskiRadnik(long id, String firstName, String lastName, String jmbg, String password, String typeOfUser,
			boolean active, boolean prijavljen) {
		super(id, firstName, lastName, jmbg, password, typeOfUser, active, prijavljen);
	}

}
