package com.ftn.EUprava.crud;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.ftn.EUprava.models.Korisnik;
import com.ftn.EUprava.models.MedicinskiRadnik;
import com.ftn.EUprava.models.Pacijent;
import com.ftn.EUprava.models.Vakcina;
import com.ftn.EUprava.models.VakcinaModel;

@Service
@Primary
@Qualifier("fajloviVakcina")
public class CrudVakcina {

    @Value("${vakcine.pathToFile}")
    private String pathToFile;
	private Map<Long, VakcinaModel> vakcine = new HashMap<>();
	private Long nextId = 1L;
	
	private Map<Long, VakcinaModel> readFromFile() {
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
				Vakcina vakcina = Vakcina.valueOf(tokens[1]);
				String mesto = tokens[2];
				boolean active = Boolean.parseBoolean(tokens[3]);
				VakcinaModel novaVakcina = new VakcinaModel(id,vakcina,mesto,active);
				vakcine.put(Long.parseLong(tokens[0]), novaVakcina);
				
				if(nextId<id) {
					nextId=id;
				}
			}
		} catch (Exception e) {
			System.out.println("Greska prilikom citanja fajla.");
			e.printStackTrace();
		}
		return vakcine;
	}
	
	public VakcinaModel findOne(Long id) {
		VakcinaModel found = null;
		for (VakcinaModel vakcina : vakcine.values()) {
			if (vakcina.getId() == id) {
				found = vakcina;
				break;
			}
		}
		return found;
	}

	public List<VakcinaModel> findAll() {
		Map<Long, VakcinaModel> vakcine = readFromFile();
		return new ArrayList<VakcinaModel>(vakcine.values());
	}
}
