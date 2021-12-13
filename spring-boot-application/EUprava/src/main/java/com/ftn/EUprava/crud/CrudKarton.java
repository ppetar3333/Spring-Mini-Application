package com.ftn.EUprava.crud;

import java.io.*;
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
import com.ftn.EUprava.models.Vakcina;
import com.ftn.EUprava.models.VakcinalniKarton;

@Service
@Primary
@Qualifier("fajloviKarton")
public class CrudKarton {

	
    @Value("${vakcinalniKarton.pathToFile}")
    private String pathToFile;
	
	private Map<Long, VakcinalniKarton> vakcinalniKartoni = new HashMap<>();
	private long nextId = 1L;
	
    @Autowired
    private CrudKorisnik crudKorisnik;
    
	private Map<Long, VakcinalniKarton> readFromFile() {
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
				LocalDateTime dateOfVaccinating;
				if(!tokens[3].equals("")) {
					dateOfVaccinating = LocalDateTime.parse(tokens[3],formatter);
				} else {
					dateOfVaccinating = null;
				}
				Vakcina vakcina = Vakcina.valueOf(tokens[4]);
				Doza doza = Doza.valueOf(tokens[5]);
				boolean active = Boolean.parseBoolean(tokens[6]);
				Pacijent pacijent = (Pacijent) crudKorisnik.findOneByID(pacijentID);
				
				VakcinalniKarton karton = new VakcinalniKarton(id,pacijent,dateOfCheckIn,dateOfVaccinating,vakcina,doza,active);
				vakcinalniKartoni.put(Long.parseLong(tokens[0]), karton);
				
				if(nextId<id) {
					nextId=id;
				}
			}
		} catch (Exception e) {
			System.out.println("Greska prilikom citanja fajla.");
			e.printStackTrace();
		}
		return vakcinalniKartoni;
	}
	
	public VakcinalniKarton findOne(long id) {
		Map<Long, VakcinalniKarton> karton = readFromFile();		
		return karton.get(id);
	}
	
	public VakcinalniKarton findOneByID(long id) {
		VakcinalniKarton found = null;
		for (VakcinalniKarton karton : vakcinalniKartoni.values()) {
			if (karton.getId() == id) {
				found = karton;
				break;
			}
		}
		return found;
	}

	public List<VakcinalniKarton> findAll() {
		Map<Long, VakcinalniKarton> karton = readFromFile();
		return new ArrayList<VakcinalniKarton>(karton.values());
	}
	
	private Map<Long, VakcinalniKarton> saveToFile(Map<Long, VakcinalniKarton> karton) {

        @SuppressWarnings("unchecked")
		Map<Long, VakcinalniKarton> ret = new HashMap();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        try {
            Path path = Paths.get(pathToFile);
            System.out.println(path.toFile().getAbsolutePath());
            List<String> lines = new ArrayList<String>();

            for (VakcinalniKarton vkcKarton : karton.values()) {
                String line = vkcKarton.getId() + ","+ 
                		vkcKarton.getPacijent().getId() +","+ 
                		vkcKarton.getDateOfCheckIn().format(formatter) + "," + 
                		vkcKarton.getDateOfVaccinating().format(formatter) + "," + 
                		vkcKarton.getTypeOfVaccine().toString() + "," + 
                		vkcKarton.getDose().toString() +","+ 
                		vkcKarton.isActive();
                lines.add(line);
                ret.put(vkcKarton.getId(), vkcKarton);
            }

            Files.write(path, lines, Charset.forName("UTF-8"));

        } catch (Exception e) {
            System.out.println("Greska prilikom citanja fajla.");
            e.printStackTrace();
        }
        return ret;
    }
	 
		public VakcinalniKarton save(VakcinalniKarton karton) {
			Map<Long, VakcinalniKarton> kartoni = readFromFile();
	        kartoni.put(karton.getId(), karton);
	        saveToFile(kartoni);
	        return karton;
		}

}
