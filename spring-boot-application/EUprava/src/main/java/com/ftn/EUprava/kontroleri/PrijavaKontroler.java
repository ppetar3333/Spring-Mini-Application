package com.ftn.EUprava.kontroleri;

import java.io.IOException;

import java.io.PrintWriter;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ftn.EUprava.bean.SecondConfiguration.ApplicationMemory;
import com.ftn.EUprava.crud.CrudKarton;
import com.ftn.EUprava.crud.CrudKorisnik;
import com.ftn.EUprava.crud.CrudPrijava;
import com.ftn.EUprava.crud.CrudVakcina;
import com.ftn.EUprava.models.Korisnik;

@Controller
@RequestMapping(value="/PrijavaOdjava")
public class PrijavaKontroler {
	
	public static final String KORISNIK_KEY = "korisnik";
	
	@Autowired
	private ApplicationMemory memorijaAplikacije;
	
	@Autowired
	private ServletContext servletContext;
	
	@Autowired
	private CrudKorisnik crudKorisnik;
	
	@Autowired
	private CrudPrijava crudPrijava;
	
	@Autowired
	private CrudKarton crudKarton;
	
	@Autowired
	private CrudVakcina crudVakcine;
	
	private String bURL; 
	
	@PostConstruct
	public void init() {
		bURL = servletContext.getContextPath()+"/";			
	}
	
	@SuppressWarnings("unchecked")
	@GetMapping(value="/Login")
	public void getLogin(@RequestParam(required = false) String jmbg, @RequestParam(required = false) String password,
			HttpSession session, HttpServletResponse response) throws IOException {
		postLogin(jmbg,password,session,response);
	}
	
	@SuppressWarnings("unchecked")
	@PostMapping(value="/Login")
	@ResponseBody
	public void postLogin(@RequestParam(required = false) String jmbg, @RequestParam(required = false) String password,
			HttpSession session, HttpServletResponse response) throws IOException {
		
		System.out.println("Korisnici:");
		System.out.println(crudKorisnik.findAll().toString());
		System.out.println("");
		System.out.println("Prijave:");
		System.out.println(crudPrijava.findAll().toString());
		System.out.println("");
		System.out.println("Kartoni:");
		System.out.println(crudKarton.findAll().toString());
		System.out.println("");
		System.out.println("Vakcine:");
		System.out.println(crudVakcine.findAll().toString());
		
		String greska = "";
		Korisnik korisnik = crudKorisnik.findOneByJmbg(jmbg);
		if (korisnik == null) {
			greska = "neispravan jmbg <br/>";
			System.out.println("Neispravan jmbg");
		}else if (!korisnik.getPassword().equals(password)) {
			greska = "neispravna sifra<br/>";
			System.out.println("Neispravna lozinka");
		}else {
			System.out.println("Uspesno ulogovan kao " + korisnik.getTypeOfUser());
		}

		if(!greska.equals("")) {
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out;	
			out = response.getWriter();
			
			StringBuilder retVal = new StringBuilder();
			retVal.append(
					"<!DOCTYPE html>\r\n" + 
					"<html>\r\n" + 
					"<head>\r\n" + 
					"	<meta charset=\"UTF-8\">\r\n" + 
					"	<base href=\"/EUprava/\">	\r\n" + 
					"	<title>Prijava korisnika</title>\r\n" + 
					"</head>\r\n" + 
					"<body>\r\n");
			if(!greska.equals(""))
				retVal.append(
					"	<div>"+greska+"</div>\r\n");
			retVal.append(
					"	<form method=\"post\" action=\"PrijavaOdjava/Login\">\r\n" + 
					"		<table>\r\n" + 
					"			<caption>Prijava korisnika na sistem</caption>\r\n" + 
					"			<tr><th>JMBG</th><td><input type=\"text\" value=\"\" name=\"jmbg\" required/></td></tr>\r\n" + 
					"			<tr><th>passowrd</th><td><input type=\"password\" value=\"\" name=\"password\" required/></td></tr>\r\n" + 
					"			<tr><th></th><td><input type=\"submit\" value=\"Prijavi se\" /></td>\r\n" + 
					"		</table>\r\n" + 
					"	</form>\r\n" + 
					"	<p>Medicinski radnik, jmbg: 2222222222222 ^^ password: test</p>\r\n"
					+ "	<p>Pacijent (nije privaljen i nije vakcinisan), jmbg: 1231231231238 ^^ password: test</p>\r\n"
					+ "	<p>Pacijent (prijavljen, ali nije vakcinisan), jmbg: 1231231231233 ^^ password: test</p>\r\n"
					+ "	<p>Pacijent (vakcinisan prvom dozom), jmbg: 1231231231234 ^^ passowrd: test </p>\r\n"
					+ "	<p>Pacijent (vakcinisan drugom dozom), jmbg: 3213213213213 ^^ password: test</p>"
					+ "	<br/>\r\n" +
					"</body>" + 
					"</html>");
			
			out.write(retVal.toString());
			return;
		}
		
		if (session.getAttribute(KORISNIK_KEY) != null)
			greska = "korisnik je već prijavljen na sistem morate se prethodno odjaviti<br/>";
		
		if(!greska.equals("")) {
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out;	
			out = response.getWriter();
			
			StringBuilder retVal = new StringBuilder();
			retVal.append(
					"<!DOCTYPE html>\r\n" + 
					"<html>\r\n" + 
					"<head>\r\n" + 
					"	<meta charset=\"UTF-8\">\r\n" + 
					"	<base href=\"/EUprava/\">	\r\n" + 
					"	<title>Prijava korisnika</title>\r\n" + 
					"</head>\r\n" + 
					"<body>\r\n");
			if(!greska.equals(""))
				retVal.append(
					"	<div>"+greska+"</div>\r\n");
			retVal.append(
					"	<a href=\"index.html\">Povratak</a>\r\n" + 
					"	<br/>\r\n" + 
					"</body>\r\n" + 
					"</html>");
			
			out.write(retVal.toString());
			return;
		}
		
		session.setAttribute(PrijavaKontroler.KORISNIK_KEY, korisnik);
		
		if(korisnik.getTypeOfUser().equals("pacijent")) {
			response.sendRedirect(bURL+"pacijent");	// prikazati stranicu za medicinskog radnika
		}else {
			response.sendRedirect(bURL+"radnik"); // prikazati stranicu za pacijenta
		}
	}
	
	@SuppressWarnings("unchecked")
	@GetMapping(value="/Logout")
	public void getLogout(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		Korisnik korisnik = (Korisnik) request.getSession().getAttribute(PrijavaKontroler.KORISNIK_KEY);
		String greska = "";
		if(korisnik==null)
			greska="korisnik nije prijavljen<br/>";
		
		if(!greska.equals("")) {
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out;	
			out = response.getWriter();
			
			StringBuilder retVal = new StringBuilder();
			retVal.append(
					"<!DOCTYPE html>\r\n" + 
					"<html>\r\n" + 
					"<head>\r\n" +
					"	<meta charset=\"UTF-8\">\r\n" + 
					"	<base href=\"/EUprava/\">	\r\n" + 
					"	<title>Prijava korisnika</title>\r\n" + 
					"</head>\r\n" + 
					"<body>\r\n");
			if(!greska.equals(""))
				retVal.append(
					"	<div>"+greska+"</div>\r\n");
			retVal.append(
					"	<form method=\"post\" action=\"PrijavaOdjava/Login\">\r\n" + 
					"		<table>\r\n" + 
					"			<caption>Prijava korisnika na sistem</caption>\r\n" + 
					"			<tr><th>JMBG</th><td><input type=\"text\" value=\"\" name=\"jmbg\" required/></td></tr>\r\n" + 
					"			<tr><th>Password</th><td><input type=\"password\" value=\"\" name=\"password\" required/></td></tr>\r\n" + 
					"			<tr><th></th><td><input type=\"submit\" value=\"Prijavi se\" /></td>\r\n" + 
					"		</table>\r\n" + 
					"	</form>" + 
					"	<br/>" + 
					"	<ul>" + 
					"		<li><a href=\"PrijavaOdjava/Logout\">Odjavi se</a></li>\r\n" + 
					"	</ul>" +
					"</body>" + 
					"</html>");
			
			out.write(retVal.toString());
			return;
		}
		
		request.getSession().removeAttribute(PrijavaKontroler.KORISNIK_KEY);
		request.getSession().invalidate();
		response.sendRedirect(bURL+"PrijavaOdjava/Login");
	}
	
	
}
