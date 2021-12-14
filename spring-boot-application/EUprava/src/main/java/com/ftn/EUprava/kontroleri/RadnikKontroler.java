package com.ftn.EUprava.kontroleri;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ftn.EUprava.crud.CrudKarton;
import com.ftn.EUprava.crud.CrudKorisnik;
import com.ftn.EUprava.crud.CrudPrijava;
import com.ftn.EUprava.models.Doza;
import com.ftn.EUprava.models.Korisnik;
import com.ftn.EUprava.models.Pacijent;
import com.ftn.EUprava.models.PrijavaZaVakcinaciju;
import com.ftn.EUprava.models.Vakcina;
import com.ftn.EUprava.models.VakcinalniKarton;

@Controller
@RequestMapping(value="/radnik")
public class RadnikKontroler {

	public static final String PACIJENT_KEY = "radnik";
	
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	@Autowired
	private CrudKorisnik crudKorisnik;
	
	@Autowired
	private CrudPrijava crudPrijave;
	
	@Autowired
	private CrudKarton crudKarton;
	
	@Autowired
	private ServletContext servletContext;
	private  String bURL;
	@Autowired
	private PrijavaKontroler prijavaKontroler;
	
	@PostConstruct
	public void init() {
		bURL = servletContext.getContextPath()+"/";	
	}

	@GetMapping
	@ResponseBody
	public String index(HttpSession session, HttpServletResponse response) throws IOException {	
		StringBuilder retVal = new StringBuilder();
		
		//preuzimanje vrednosti iz sesije za klijenta
		Korisnik korisnik = (Korisnik) session.getAttribute(PrijavaKontroler.KORISNIK_KEY);
		if(korisnik==null) {
			response.sendRedirect(bURL+"index.html");
			return "";
		}
		
		retVal.append(
				"<!DOCTYPE html>\r\n" + 
				"<html>\r\n" + 
				"<head>\r\n" + 
				"	<meta charset=\"UTF-8\">\r\n" + 
	    		"	<base href=\""+bURL+"\">\r\n" + 
				"	<title>EUprava</title>\r\n" + 
	    		"<link rel=\"stylesheet\" type=\"text/css\" href=\"css/index.css\"/>\r\n"+
				"</head>\r\n" + 
				"<body> "+
				"	<div> Prijavljen je:  <strong> "+ korisnik.getFirstName() +" "+ korisnik.getLastName() + "</strong>"+ "</br>"   + "</a> <a href=\"PrijavaOdjava/Logout\">Odjavi se</a></li></div>\r\n" +  "<hr>");
			
		retVal.append(	
				"		<table>\r\n" + 
				"			<caption>Prijave</caption>\r\n" + 
				"			<tr bgcolor=\"gray\">\r\n" + 
				"				<th>ID</th>\r\n" + 
				"				<th>Ime</th>\r\n" + 
				"				<th>Prezime</th>\r\n" + 
				"				<th>Jmbg</th>\r\n" +
				"				<th>Prijava kreirana</th>\r\n" + 
				"				<th>Vakcina</th>\r\n" +
				"				<th>Doza</th>\r\n" +
				"				<th>Pregled kartona</th>\r\n" +
				"				<th>Vakcinacija</th>\r\n" +
				"			</tr>\r\n");
		
		List<PrijavaZaVakcinaciju> listaPrijava = crudPrijave.findAll();
		
		for(int i = 0; i < listaPrijava.size(); i++) {
				retVal.append(
					"			<tr>\r\n" + 
					"				<td>"+ listaPrijava.get(i).getId() +"</td>\r\n" +
					"				<td>"+ listaPrijava.get(i).getPacijent().getFirstName() +"</td>\r\n" +
					"				<td>"+ listaPrijava.get(i).getPacijent().getLastName() +"</td>\r\n" +
					"				<td>"+ listaPrijava.get(i).getPacijent().getJmbg() +"</td>\r\n" +
					"				<td>"+ listaPrijava.get(i).getDateOfCheckIn().format(formatter) +"</td>\r\n" +
					"				<td>"+ listaPrijava.get(i).getTypeOfVaccine().toString() +"</td>\r\n" +
					"				<td>"+ listaPrijava.get(i).getDose().toString() +"</td>\r\n" +
					"				<td>" + 
					"						<a href=\"radnik/pogledajKarton?id="+listaPrijava.get(i).getId()+"\">Pogledaj Vakcinalni Karton</a>" +
					"				</td>" +
					"				<td>");
					if(!listaPrijava.get(i).isVakcinisan()) {
						retVal.append(
									"<a href=\"radnik/vakcinisi?id="+listaPrijava.get(i).getId()+"\">Vakcinisi</a>" +
										"				</td>"			
								);
					}
					retVal.append("</tr>\r\n");
		}
		
		
		retVal.append(
				"</body>\r\n"+
				"</html>\r\n");	
		
		return retVal.toString();
	}
	
	@GetMapping(value = "/pogledajKarton")
	@ResponseBody
	private String pogledajKarton(@RequestParam Long id, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		//preuzimanje vrednosti iz sesije za klijenta
		Korisnik korisnik = (Korisnik) session.getAttribute(PrijavaKontroler.KORISNIK_KEY);
		if(korisnik==null) {
			response.sendRedirect(bURL+"login.html");
			return "";
		}

		PrijavaZaVakcinaciju prijava = crudPrijave.findOne(id);
		if(prijava == null) {
			response.sendRedirect(bURL+"EUprava");
			return "";
		}
		
		response.setContentType("text/html;charset=UTF-8");
		StringBuilder retVal = new StringBuilder();
		retVal.append(
				"<!DOCTYPE html>\r\n" + 
				"<html>\r\n" + 
				"<head>\r\n" + 
				"	<meta charset=\"UTF-8\">\r\n" + 
	    		"	<base href=\""+bURL+"\">\r\n" + 
				"	<title>EUprava</title>\r\n" + 
	    		"<link rel=\"stylesheet\" type=\"text/css\" href=\"css/index.css\"/>\r\n" +
				"</head>\r\n" + 
				"<body> "+
				"	<div>  Prijavljen je:  <strong> "+ korisnik.getFirstName() +" "+ korisnik.getLastName() + "</strong>"+ "</br>"   + "</a> <a href=\"PrijavaOdjava/Logout\">Odjavi se</a></li></div>\r\n" +  "<hr>" + "<a href=\"radnik/\">Nazad</a> \r\n\n\n");
		retVal.append(	
				"		<p> <b> Vakcinalni karton - " + prijava.getPacijent().getFirstName() + " " + 
						prijava.getPacijent().getLastName() + 
						" jmbg: " + prijava.getPacijent().getJmbg() + "</b></p>\r\n" +
						"		<table>\r\n" + 
						"			<tr>\r\n" + 
						"				<th>Prijava Izvrsena</th>\r\n" + 
						"				<th>Vakcinisan</th>\r\n" + 
						"				<th>Vakcina</th>\r\n" + 
						"				<th>Doza</th>\r\n" +
						"			</tr>\r\n"
				);
		
		List<PrijavaZaVakcinaciju> prijavaPacijenta = new ArrayList<>();
		
		for(int i = 0; i < prijavaPacijenta.size(); i++) {
			System.out.println(prijava.getPacijent().getLastName());
			prijavaPacijenta.add(prijava);
		}

		List<VakcinalniKarton> vakcinalniKarton = crudKarton.findAll();
		
		if(prijava.isVakcinisan()) {
			for(int i = 0; i < vakcinalniKarton.size(); i++) {
				if(vakcinalniKarton.get(i).getPacijent().getId() == prijava.getPacijent().getId()) {
					retVal.append(
						"			<tr>\r\n" + 
						"				<td>"+ vakcinalniKarton.get(i).getDateOfCheckIn().format(formatter) +"</td>\r\n" +
						"				<td>"+ vakcinalniKarton.get(i).getDateOfVaccinating().format(formatter) +"</td>\r\n" +
						"				<td>"+ vakcinalniKarton.get(i).getTypeOfVaccine().toString() +"</td>\r\n" +
						"				<td>"+ vakcinalniKarton.get(i).getDose().toString() +"</td>\r\n" 
						);
					retVal.append("</tr>\r\n");
				}
			}
		} else {
			retVal.append(
				"			<tr>\r\n" + 
				"			<td>"+ prijava.getDateOfCheckIn().format(formatter) +"</td>\r\n" +
				"			<td>"+ " " +"</td>\r\n" +
				"			<td>"+ prijava.getTypeOfVaccine().toString() +"</td>\r\n" +
				"			<td>"+ prijava.getDose().toString() +"</td>\r\n" +
				"			</tr>\r\n");
		}
		
		retVal.append(
				"</body>\r\n"+
				"</html>\r\n");	
		
		return retVal.toString();
	}
	
	@GetMapping(value = "/vakcinisi")
	@ResponseBody
	private void vakcinisi(@RequestParam Long id, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws IOException {
		//preuzimanje vrednosti iz sesije za klijenta
		Korisnik korisnik = (Korisnik) session.getAttribute(PrijavaKontroler.KORISNIK_KEY);
		if(korisnik==null) {
			response.sendRedirect(bURL+"login.html");
		}

		PrijavaZaVakcinaciju prijava = crudPrijave.findOne(id);
		if(prijava == null) {
			response.sendRedirect(bURL+"EUprava");
		}
		
		//kod vakcinalnog kartona id, pacijentID, datum iz prijava kreirana, date time now, koja je vakcina, koja je doza, obrisan
		//kod prijave za vakcinaciju promeniti na true
		
		List<VakcinalniKarton> listaKartona = crudKarton.findAll();
		
		Long max = 0l;
		for(VakcinalniKarton vkcK : listaKartona) {
			if(vkcK.getId() > max) {
				max = vkcK.getId();
			}
		}
		
		VakcinalniKarton vkcKarton = new VakcinalniKarton(max+1,prijava.getPacijent(),prijava.getDateOfCheckIn(),LocalDateTime.now(),prijava.getTypeOfVaccine(),prijava.getDose(),prijava.isActive());

		prijava.setVakcinisan(true);

		
		crudPrijave.update(prijava);
		
		crudKarton.save(vkcKarton);
		
		response.sendRedirect(bURL+"radnik/pogledajKarton?id=" + id);
	}
}
