package com.ftn.EUprava.crud;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.ftn.EUprava.models.Doza;
import com.ftn.EUprava.models.Korisnik;
import com.ftn.EUprava.models.MedicinskiRadnik;
import com.ftn.EUprava.models.Pacijent;
import com.ftn.EUprava.models.PrijavaZaVakcinaciju;
import com.ftn.EUprava.models.Vakcina;
import com.ftn.EUprava.models.VakcinalniKarton;

@Service
@Primary
@Qualifier("fajloviPrijava")
public class CrudPrijava {

    @Value("${prijavaZaVakcinaciju.pathToFile}")
    private String pathToFile;
    
	private Map<Long, PrijavaZaVakcinaciju> prijava = new HashMap<>();
	
    @Autowired
    private CrudKorisnik crudKorisnik;
    
	private long nextId = 1L;
	
	private Map<Long, PrijavaZaVakcinaciju> readFromFile() {
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
				int pacijentID = Integer.parseInt(tokens[1]);
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
				LocalDateTime dateOfCheckIn = LocalDateTime.parse(tokens[2],formatter);
				Vakcina vakcina = Vakcina.valueOf(tokens[3]);
				Doza doza = Doza.valueOf(tokens[4]);
				boolean active = Boolean.parseBoolean(tokens[5]);
				Pacijent pacijent = (Pacijent) crudKorisnik.findOneByID(pacijentID);
				boolean vakcinisan = Boolean.parseBoolean(tokens[6]);
				PrijavaZaVakcinaciju prijave = new PrijavaZaVakcinaciju(id,pacijent,dateOfCheckIn,vakcina,doza,active,vakcinisan);
				prijava.put(Long.parseLong(tokens[0]), prijave);
				
				if(nextId<id) {
					nextId=id;
				}
			}
		} catch (Exception e) {
			System.out.println("Greska prilikom citanja fajla.");
			e.printStackTrace();
		}
		return prijava;
	}
	
	public PrijavaZaVakcinaciju findOne(long id) {
		Map<Long, PrijavaZaVakcinaciju> prijave = readFromFile();		
		return prijave.get(id);
	}
	
	public PrijavaZaVakcinaciju findOneByID(long id) {
		PrijavaZaVakcinaciju found = null;
		for (PrijavaZaVakcinaciju prijava : prijava.values()) {
			if (prijava.getId() == id) {
				found = prijava;
				break;
			}
		}
		return found;
	}
	public List<PrijavaZaVakcinaciju> findAll() {
		Map<Long, PrijavaZaVakcinaciju> prijava = readFromFile();
		return new ArrayList<PrijavaZaVakcinaciju>(prijava.values());
	}
	
	private Map<Long, PrijavaZaVakcinaciju> saveToFile(Map<Long, PrijavaZaVakcinaciju> prijavaZaVakcinaciju) {

        @SuppressWarnings("unchecked")
		Map<Long, PrijavaZaVakcinaciju> ret = new HashMap();

        try {
            Path path = Paths.get(pathToFile);
            System.out.println(path.toFile().getAbsolutePath());
            List<String> lines = new ArrayList<String>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            for (PrijavaZaVakcinaciju prijava : prijavaZaVakcinaciju.values()) {
                String line = prijava.getId() + ","+
                		prijava.getPacijent().getId() + ","+  
                		prijava.getDateOfCheckIn().format(formatter) + ","+  
                		prijava.getTypeOfVaccine().toString() + ","+  
                		prijava.getDose().toString() + ","+  
                		prijava.isActive() + "," +
                		prijava.isVakcinisan();
                lines.add(line);
                ret.put(prijava.getId(), prijava);
            }

            Files.write(path, lines, Charset.forName("UTF-8"));

        } catch (Exception e) {
           System.out.println("Greska prilikom citanja fajla.");
            e.printStackTrace();
        }
        return ret;
    }
		
	public PrijavaZaVakcinaciju update(PrijavaZaVakcinaciju prijava) {
        Map<Long, PrijavaZaVakcinaciju> prijave = readFromFile();
        prijave.put(prijava.getId(), prijava);
        saveToFile(prijave);
        return prijava;
	}
}
