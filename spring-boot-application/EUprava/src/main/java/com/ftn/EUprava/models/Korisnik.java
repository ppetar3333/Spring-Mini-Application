package com.ftn.EUprava.models;

public abstract class Korisnik {
	
	protected long id;
	protected String firstName;
	protected String lastName;
	protected String jmbg;
	protected String password;
	protected String typeOfUser;
	protected boolean active;
	protected boolean prijavljen;
	
	public Korisnik(long id, String firstName, String lastName, String jmbg, String password, String typeOfUser,
			boolean active, boolean prijavljen) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.jmbg = jmbg;
		this.password = password;
		this.typeOfUser = typeOfUser;
		this.active = active;
		this.prijavljen = prijavljen;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getJmbg() {
		return jmbg;
	}

	public void setJmbg(String jmbg) {
		this.jmbg = jmbg;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getTypeOfUser() {
		return typeOfUser;
	}

	public void setTypeOfUser(String typeOfUser) {
		this.typeOfUser = typeOfUser;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	

	public boolean isPrijavljen() {
		return prijavljen;
	}

	public void setPrijavljen(boolean prijavljen) {
		this.prijavljen = prijavljen;
	}

	@Override
	public String toString() {
		return "Korisnik [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", jmbg=" + jmbg
				+ ", password=" + password + ", typeOfUser=" + typeOfUser + ", active=" + active + ", prijavljen="
				+ prijavljen + "]";
	}
}
