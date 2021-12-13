package com.ftn.EUprava.crud;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.*;

import com.ftn.EUprava.models.Korisnik;
import com.ftn.EUprava.models.MedicinskiRadnik;
import com.ftn.EUprava.models.Pacijent;
import com.ftn.EUprava.models.PrijavaZaVakcinaciju;

@Service
@Primary
@Qualifier("fajloviKorisnici")
public class CrudKorisnik {
	
    @Value("${korisnici.pathToFile}")
    private String pathToFile;
	private Map<Long, Korisnik> korisnici = new HashMap<>();
	private Long nextId = 1L;
	
	private Map<Long, Korisnik> readFromFile() {
		try {
			Path path = Paths.get(pathToFile);
			System.out.println(path.toFile().getAbsolutePath());
			List<String> lines = Files.readAllLines(path, Charset.forName("UTF-8"));

			for (String line : lines) {
				line = line.trim();
				if (line.equals("") || line.indexOf('#') == 0)
					continue;
				String[] tokens = line.split(",");
				Long id = Long.parseLong(tokens[0]);
				String firstName = tokens[1];
				String lastName = tokens[2];
				String jmbg = tokens[3];
				String password = tokens[4];
				String typeOfUser = tokens[5];
				boolean active = Boolean.parseBoolean(tokens[6]);
				boolean prijavljen = Boolean.parseBoolean(tokens[7]);
				
				if(typeOfUser.equals("pacijent")) {
					Pacijent pacijent = new Pacijent(id,firstName,lastName,jmbg,password,typeOfUser,active,prijavljen);
					korisnici.put(Long.parseLong(tokens[0]), pacijent);
				}else {
					MedicinskiRadnik medicinskiRadnik = new MedicinskiRadnik(id,firstName,lastName,jmbg,password,typeOfUser,active,prijavljen);
					korisnici.put(Long.parseLong(tokens[0]), medicinskiRadnik);
				}
				
				if(nextId<id) {
					nextId=id;
				}
			}
		} catch (Exception e) {
			System.out.println("Greska prilikom citanja fajla.");
			e.printStackTrace();
		}
		return korisnici;
	}
	
	public Korisnik findOne(String jmbg) {
		Map<Long, Korisnik> korisnici = readFromFile();		
		return korisnici.get(jmbg);
	}
	
	public Korisnik findOneByID(long id) {
		Korisnik found = null;
		for (Korisnik korisnik : korisnici.values()) {
			if (korisnik.getId() == id) {
				found = korisnik;
				break;
			}
		}
		return found;
	}

	public Korisnik findOne(String jmbg, String password) {
		Korisnik korisnik = findOne(jmbg);
		if (korisnik != null && korisnik.getPassword().equals(password))
			return korisnik;
		return null;
	}
	
	public Korisnik findOneByJmbg(String jmbg) {
		Korisnik found = null;
		for (Korisnik korisnik : korisnici.values()) {
			if (korisnik.getJmbg().equals(jmbg)) {
				found = korisnik;
				break;
			}
		}
		return found;
	}
	public List<Korisnik> findAll() {
		Map<Long, Korisnik> korisnici = readFromFile();
		return new ArrayList<Korisnik>(korisnici.values());
	}
	
	private Map<Long, Korisnik> saveToFile(Map<Long, Korisnik> korisnici) {

        @SuppressWarnings("unchecked")
		Map<Long, Korisnik> ret = new HashMap();

        try {
            Path path = Paths.get(pathToFile);
            System.out.println(path.toFile().getAbsolutePath());
            List<String> lines = new ArrayList<String>();
            for (Korisnik korisnik : korisnici.values()) {
                String line = korisnik.getId() + ","+
                		korisnik.getFirstName() + ","+  
                		korisnik.getLastName() + ","+  
                		korisnik.getJmbg() + ","+  
                		korisnik.getPassword() + ","+  
                		korisnik.getTypeOfUser() + "," +
                		korisnik.isActive() + "," +
                		korisnik.isPrijavljen();
                lines.add(line);
                ret.put(korisnik.getId(), korisnik);
            }

            Files.write(path, lines, Charset.forName("UTF-8"));

        } catch (Exception e) {
           System.out.println("Greska prilikom citanja fajla.");
            e.printStackTrace();
        }
        return ret;
    }
		
	public Korisnik update(Korisnik korisnik) {
        Map<Long, Korisnik> korisnici = readFromFile();
        korisnici.put(korisnik.getId(), korisnik);
        saveToFile(korisnici);
        return korisnik;
	}
}
