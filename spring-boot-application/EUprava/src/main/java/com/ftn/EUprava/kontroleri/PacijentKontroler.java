package com.ftn.EUprava.kontroleri;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.catalina.core.ApplicationContext;
import org.springframework.beans.BeansException;
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
import com.ftn.EUprava.models.Doza;
import com.ftn.EUprava.models.Korisnik;
import com.ftn.EUprava.models.Pacijent;
import com.ftn.EUprava.models.PrijavaZaVakcinaciju;
import com.ftn.EUprava.models.Vakcina;
import com.ftn.EUprava.models.VakcinaModel;
import com.ftn.EUprava.models.VakcinalniKarton;

@Controller
@RequestMapping(value="/pacijent")
public class PacijentKontroler {
	
	public static final String PACIJENT_KEY = "pacijent";
	
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	
	@Autowired
	private CrudKorisnik crudKorisnik;
	
	@Autowired
	private CrudPrijava crudPrijave;
	
	@Autowired 
	private CrudVakcina crudVakcina;
	
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
		Pacijent korisnik = (Pacijent) session.getAttribute(PrijavaKontroler.KORISNIK_KEY);
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
				"				<th>Izmeni</th>\r\n" +
				"			</tr>\r\n");
		
		List<PrijavaZaVakcinaciju> listaPrijava = crudPrijave.findAll();
		
		for(int i = 0; i < listaPrijava.size(); i++) {
			if(listaPrijava.get(i).getPacijent().getId() == korisnik.getId() && !listaPrijava.get(i).isVakcinisan()) {
				retVal.append(
					"			<tr>\r\n" + 
					"				<td>"+ listaPrijava.get(i).getId() +"</td>\r\n" +
					"				<td>"+ listaPrijava.get(i).getPacijent().getFirstName() +"</td>\r\n" +
					"				<td>"+ listaPrijava.get(i).getPacijent().getLastName() +"</td>\r\n" +
					"				<td>"+ listaPrijava.get(i).getPacijent().getJmbg() +"</td>\r\n" +
					"				<td>"+ listaPrijava.get(i).getDateOfCheckIn().format(formatter) +"</td>\r\n" +
					"				<td>"+ listaPrijava.get(i).getTypeOfVaccine().toString() +"</td>\r\n" +
					"				<td>"+ listaPrijava.get(i).getDose().toString() +"</td>\r\n");
					if(listaPrijava.get(i).getDose().equals(Doza.prva)) {
						retVal.append(						
								"				<td>" + 
								"					<form method=\"post\" action=\"pacijent/izmeni\">\r\n" + 
								"						<a href=\"pacijent/izmeni?id="+listaPrijava.get(i).getId()+"\">Izmeni</a>" +
								"					</form>\r\n" +
								"				</td>");
					}
				retVal.append("</tr>\r\n");
			}
		}
		
		retVal.append( " <a href=\"pacijent/prijaviSe\"> Prijavi se za vakcinaciju </a> \r\n");		


		retVal.append(
				"</body>\r\n"+
				"</html>\r\n");	
		
		return retVal.toString();
	}
	
	@GetMapping(value = "/prijaviSe")
	@ResponseBody
	private String prijaviSe(HttpSession session, HttpServletResponse response) throws IOException {
		
		//preuzimanje vrednosti iz sesije za klijenta
		Pacijent korisnik = (Pacijent) session.getAttribute(PrijavaKontroler.KORISNIK_KEY);
		if(korisnik==null) {
			response.sendRedirect(bURL+"index.html");
			return "";
		}
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
				"	<div> Prijavljen je:  <strong> "+ korisnik.getFirstName() +" "+ korisnik.getLastName() + "</strong>"+ "</br>"   + "</a> <a href=\"PrijavaOdjava/Logout\">Odjavi se</a></li>" +  "</div>\r\n" +  "<hr>");
		retVal.append("<a href=\"pacijent/\">Nazad</a></br></br> \r\n");
		
		Long vakcinaId = 1l;
		VakcinaModel vakcina = null;
		if (vakcinaId!= null && crudVakcina.findOne(vakcinaId)!=null) {
			vakcina = crudVakcina.findOne(vakcinaId);
			System.out.println(vakcina);
		}
		
		List<Doza> listaDoza = Arrays.asList(Doza.values());
		System.out.println(listaDoza);
		
		retVal.append(
				"					<form method=\"post\" action=\"pacijent/prijava\">\r\n" + 
				"			<tr>\r\n"+
				"				<th>Vakcine</th>\r\n"+
				"				<td>\r\n"+		
				"					<select name=\"vakcID\">\r\n");	
		for (VakcinaModel vakcinaModel : crudVakcina.findAll()) { 
			retVal.append(
				"						<option value=\""+vakcinaModel.getId()+"\" "+(vakcinaModel.equals(vakcina)?"selected":"")+">"+vakcinaModel.getVakcina()+"</option>\r\n");
		}	
		retVal.append(
				"					</select>\r\n"+
				"				</td>\n\r"+
				"			</tr>\r\n<br/><br/>");
		
		retVal.append(
				"			<tr>\r\n"+
				"				<th>Doza</th>\r\n"+
				"				<td>\r\n"+		
				"					<select name=\"doze\">\r\n");	
		for (Doza doze : listaDoza) { 
			retVal.append(
				"						<option value=\""+ doze.toString() + "\"" + "selected:" + ">" +doze.toString()+"</option>\r\n");
		}	
		retVal.append(
				"					</select>\r\n"+
				"				</td>\n\r"+
				"			</tr>\r\n<br/>");
		retVal.append(						
				"				<td>" + 
				"						<br/><input type=\"submit\" value=\"Prijavi se\"></td>\r\n" + 
				"				</td>" +
				"				</form>\r\n");
		retVal.append(
				"</body>\r\n"+
				"</html>\r\n");	
		
		return retVal.toString();
	}
	
	@GetMapping(value = "/prijava")
	@ResponseBody
	private void getPrijava(@RequestParam Long vakcID, @RequestParam String doze,
			HttpSession session, HttpServletResponse response) throws IOException {
		setPrijava(vakcID,doze,session,response);
	}
	
	@PostMapping(value = "/prijava")
	@ResponseBody
	private String setPrijava(@RequestParam Long vakcID, @RequestParam String doze, HttpSession session, HttpServletResponse response) throws IOException {
		
		//preuzimanje vrednosti iz sesije za klijenta
		Pacijent korisnik = (Pacijent) session.getAttribute(PrijavaKontroler.KORISNIK_KEY);
		if(korisnik==null) {
			response.sendRedirect(bURL+"index.html");
		}
		
		List<PrijavaZaVakcinaciju> svePrijave = crudPrijave.findAll();
		Long max = 0L;
		for(PrijavaZaVakcinaciju prijava : svePrijave) {
			if(prijava.getId() > max) {
				max = prijava.getId();
			}
		}
		
		VakcinaModel vakcina = crudVakcina.findOne(vakcID);
		
		PrijavaZaVakcinaciju novaPrijava = new PrijavaZaVakcinaciju(max+1,korisnik,LocalDateTime.now(),vakcina.getVakcina(),Doza.valueOf(doze),true,false);
		
		StringBuilder retVal = new StringBuilder();
		
		List<Integer> samoPrvaDoza = new ArrayList<>();
		List<Integer> cekaVakcinaciju = new ArrayList<>();
		List<Integer> neMozeDaSePrijavi = new ArrayList<>();
		List<Integer> istaDoza = new ArrayList<>();
		List<Integer> istaVakcina = new ArrayList<>();
		List<Integer> vakcinisanPrvomDozom = new ArrayList<>();
		List<Integer> vakcinisanPrvomIDrugomDozom = new ArrayList<>();
		
		for(PrijavaZaVakcinaciju prijava : svePrijave) {
			if(prijava.getPacijent().getId() == korisnik.getId()) {
				if(prijava.isVakcinisan()) {
					System.out.println(korisnik.getFirstName() + " je vakcinisan vakcinom " + prijava.getTypeOfVaccine() + " i dozom " + prijava.getDose());
					if(vakcina.getVakcina().toString().equals(prijava.getTypeOfVaccine().toString())) {
						istaVakcina.add(1);
						if(prijava.getDose().toString().equals(Doza.prva.toString())) {
							vakcinisanPrvomDozom.add(1);
						}
						if(prijava.getDose().toString().equals(Doza.druga.toString())) {
							vakcinisanPrvomIDrugomDozom.add(1);
						}
					}
					cekaVakcinaciju.add(1);
				} // else prijavljen je i vakcinisan sad moze sledecu dozu ali mora ista vakcina da bude
				else {
					neMozeDaSePrijavi.add(1);
				}
				if(prijava.isVakcinisan() && doze.equals(prijava.getDose().toString())) {
					istaDoza.add(1);
				}
			} else {
				samoPrvaDoza.add(1);
			}
		}
		System.out.println("samo prva doza" +samoPrvaDoza.size());
		System.out.println("cekaVakcinaciju" + cekaVakcinaciju.size());
		System.out.println("ne moze da se prijavi " + neMozeDaSePrijavi.size());
		System.out.println("Ista doza: "+istaDoza.size());
		System.out.println("Vakcinisan prvom dozom:" + vakcinisanPrvomDozom.size());
		
		if(samoPrvaDoza.size() == max && cekaVakcinaciju.size() == 0) { // nije ni prijavljen a nije ni vakcinisan, znaci moze samo prvu dozu i bilo koju vakcinu
			System.out.println("Moze samo prvu dozu, a vakcinu koju hoce.");
			if(!doze.equals(Doza.prva.toString())) {
				retVal.append("<p>Mozete primiti samo prvu dozu jer niste vakcinisani, a vakcinu koju zelite.<p>\r\n" +
						  "<a href=\""+bURL + "pacijent/prijaviSe" +"\">Nazad</a>\r\n");		
			} else {
				metodaZaCuvanjePrijave(korisnik, novaPrijava, retVal);
			}
		}
		
		if(cekaVakcinaciju.size() == 0 && samoPrvaDoza.size() != max) { // slucaj ako je prijavljen za prvu dozu a ceka vakcinaciju
			System.out.println("Pacijent ceka na vakcinaciju ne moze da se prijavi.");
			if(cekaVakcinaciju.size() == 0) {
				retVal.append("<p>Ne mozete se prijaviti za sledecu dozu ako niste vakcinisani.<p>\r\n" +
						  "<a href=\""+bURL + "pacijent" +"\">Nazad</a>\r\n");
			} else {
				metodaZaCuvanjePrijave(korisnik, novaPrijava, retVal);
			}
		}
		
		if(neMozeDaSePrijavi.size() == 1 && cekaVakcinaciju.size() != 0 && istaDoza.size() == 1) { // npr vakcinisan je drugom dozom a sad ceka trecu
			System.out.println("Kada se vakcinisete mozete se prijaviti za sledecu dozu.");
			if(neMozeDaSePrijavi.size() == 1) {
				retVal.append("<p>Kada se vakcinisete mozete se prijaviti za sledecu dozu.<p>\r\n" +
						  "<a href=\""+bURL + "pacijent" +"\">Nazad</a>\r\n");
			} else {
				metodaZaCuvanjePrijave(korisnik, novaPrijava, retVal);
			}
		}
		
		if(istaDoza.size() == 1 && neMozeDaSePrijavi.size() != 1) { // ako je vakcinisan prvom dozom ne moze opet prvom
			System.out.println("Ne mozete se dva puta vakcinisati istom dozom.");
			if(istaDoza.size() == 1) {
				retVal.append("<p>Ne mozete se dva puta vakcinisati istom dozom.<p>\r\n" +
						  "<a href=\""+bURL + "pacijent/prijaviSe" +"\">Nazad</a>\r\n");
			} else {
				metodaZaCuvanjePrijave(korisnik, novaPrijava, retVal);
			}
		}
		
		System.out.println("Ista vakcina: " + istaVakcina.size());
		System.out.println("Vakcinisan i prvom i drugom dozom: " + vakcinisanPrvomIDrugomDozom.size());
		
		if(neMozeDaSePrijavi.size() == 0 && istaDoza.size() == 0 && samoPrvaDoza.size() != max) { // znaci da se prijavio i vakcinisao sad moze samo sledecu dozu da izabere
			System.out.println("Znaci da se prijavio i vakcinisao moze samo sledecu dozu da izabere i istu vakcinu");
			// proveriti da li je izabrao istu vakcinu
			// ako je vakcina iz dropBox-a jednaka vakcini iz vakcinalniKarton unutar toga if koji:
			// proveriti da li je izabrao drugu dozu ako je vakcinisan prvom
			// ako je doza iz fajla jednaka prvoj onda moze samo drugu dozu
			if(istaVakcina.size() >= 1) {
				if(vakcinisanPrvomDozom.size() == 1 && vakcinisanPrvomIDrugomDozom.size() == 0) { // vakcinisan prvom dozom
					if(doze.equals(Doza.druga.toString())) {
						metodaZaCuvanjePrijave(korisnik, novaPrijava, retVal);
					} else {
						System.out.println("Vakcinisani ste prvom dozom mozete samo drugu da izaberete");
						retVal.append("<p>Posto ste vakcinisani prvom dozom mozete samo drugu da izaberete.<p>\r\n" +
								  "<a href=\""+bURL + "pacijent/prijaviSe" +"\">Nazad</a>\r\n");	
					}
				}
				if(vakcinisanPrvomIDrugomDozom.size() == 1 && vakcinisanPrvomDozom.size() == 1) { // vakcinisan i prvom i drugom dozom
					if(doze.equals(Doza.treca.toString())) {
						metodaZaCuvanjePrijave(korisnik, novaPrijava, retVal);
					} else {
						System.out.println("Vakcinisani ste prvom i drugom dozom mozete samo trecu da izaberete");
						retVal.append("<p>Posto ste vakcinisani prvom i drugom dozom mozete samo trecu da izaberete.<p>\r\n" +
								  "<a href=\""+bURL + "pacijent/prijaviSe" +"\">Nazad</a>\r\n");
					}
				}
			} else {
				retVal.append("<p>Ne mozete menjati vakcinu, pokusajte ponovo sa vakcinom kojom ste vec vakcinisani<p>\r\n" +
						  "<a href=\""+bURL + "pacijent/prijaviSe" +"\">Nazad</a>\r\n");
			}
		}	

		
		retVal.append(
				"</body>\r\n"+
				"</html>\r\n");	
		
		return retVal.toString();
	}

	private void metodaZaCuvanjePrijave(Pacijent korisnik, PrijavaZaVakcinaciju novaPrijava, StringBuilder retVal) {
		korisnik.setPrijavljen(true);
		crudKorisnik.update(korisnik);
		crudPrijave.update(novaPrijava);
		
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
				"	<div> Prijavljen je:  <strong> "+ korisnik.getFirstName() +" "+ korisnik.getLastName() + "</strong>"+ "</br>"   + "</a> <a href=\"PrijavaOdjava/Logout\">Odjavi se</a></li>" +  "</div>\r\n" +  "<hr>");
		
		retVal.append(
				"		<p><b>Uspesna prijava!</b></p>\r\n" +
				"		<a href=\"pacijent\">Pogledaj Svoje Prijave</a>\r\n"	
				);
	}

	private PrijavaZaVakcinaciju prijava;
	
	@GetMapping(value = "/izmeni")
	@ResponseBody
	private String setIzmena(@RequestParam Long id, HttpSession session, HttpServletResponse response) throws IOException {
		
		//preuzimanje vrednosti iz sesije za klijenta
		Pacijent korisnik = (Pacijent) session.getAttribute(PrijavaKontroler.KORISNIK_KEY);
		if(korisnik==null) {
			response.sendRedirect(bURL+"index.html");
		}
		
		prijava = crudPrijave.findOne(id);
		if(prijava == null) {
			response.sendRedirect(bURL+"EUprava");
			return "";
		}
		
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
				"	<div> Prijavljen je:  <strong> "+ korisnik.getFirstName() +" "+ korisnik.getLastName() + "</strong>"+ "</br>"   + "</a> <a href=\"PrijavaOdjava/Logout\">Odjavi se</a></li>" +  "</div>\r\n" +  "<hr>");
		
		retVal.append(
				"		<a href=\"pacijent\">Nazad</a>\r\n" +
				"		<p><b>Izmena Vakcine</b></p><br/>\r\n" +
				"		<p>Ime: " + prijava.getPacijent().getFirstName() + "</p>\r\n" +
				"		<p>Prezime: " + prijava.getPacijent().getLastName() + "</p>\r\n" +
				"		<p>JMBG: " + prijava.getPacijent().getJmbg() + "</p>\r\n"
				);
		
		
		// vakcina + vakcina u drop boxu da izabere
		Long vakcinaId = 1l;
		VakcinaModel vakcina = null;
		if (vakcinaId!= null && crudVakcina.findOne(vakcinaId)!=null) {
			vakcina = crudVakcina.findOne(vakcinaId);
			System.out.println(vakcina);
		}
		
		List<Doza> listaDoza = Arrays.asList(Doza.values());
		System.out.println(listaDoza);
		
		retVal.append(
				"					<form method=\"post\" action=\"pacijent/izmenaVakcine\">\r\n" + 
				"			<tr>\r\n"+
				"				<th>Vakcine</th>\r\n"+
				"				<td>\r\n"+		
				"					<select name=\"vakcID\">\r\n");	
		for (VakcinaModel vakcinaModel : crudVakcina.findAll()) { 
			retVal.append(
				"						<option value=\""+vakcinaModel.getId()+"\" "+(vakcinaModel.equals(vakcina)?"selected":"")+">"+vakcinaModel.getVakcina()+"</option>\r\n");
		}	
		retVal.append(
				"					</select>\r\n"+
				"				</td>\n\r"+
				"			</tr>\r\n<br/><br/>");
		
		retVal.append(						
				"				<td>" + 
				"						<br/><input type=\"submit\" value=\"Potvrdi\"></td>\r\n" + 
				"				</td>" +
				"				</form>\r\n");
		
		retVal.append(
				"</body>\r\n"+
				"</html>\r\n");	
		
		return retVal.toString();
	}
	
	@PostMapping(value = "/izmenaVakcine")
	@ResponseBody
	private String izmenaVakcine(@RequestParam Long vakcID, HttpSession session, HttpServletResponse response) throws IOException {
		
		//preuzimanje vrednosti iz sesije za klijenta
		Pacijent korisnik = (Pacijent) session.getAttribute(PrijavaKontroler.KORISNIK_KEY);
		if(korisnik==null) {
			response.sendRedirect(bURL+"index.html");
		}
		
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
				"	<div> Prijavljen je:  <strong> "+ korisnik.getFirstName() +" "+ korisnik.getLastName() + "</strong>"+ "</br>"   + "</a> <a href=\"PrijavaOdjava/Logout\">Odjavi se</a></li>" +  "</div>\r\n" +  "<hr>");
		
		
		VakcinaModel novaVakcina = crudVakcina.findOne(vakcID);
		
		prijava.setTypeOfVaccine(Vakcina.valueOf(novaVakcina.getVakcina().toString()));
		
		System.out.println(prijava.getTypeOfVaccine().toString());
		
		crudPrijave.update(prijava);
		
		retVal.append(
				"	<p>Uspesno ste izmenili tip vakcine u: " + "<b>" + prijava.getTypeOfVaccine().toString() + "</b></p>\r\n" +
				"   <a href=\"pacijent\">Povratak na prijave</a>\r\n"
				);
		
		retVal.append(
				"</body>\r\n"+
				"</html>\r\n");	
		
		return retVal.toString();
	}
}
