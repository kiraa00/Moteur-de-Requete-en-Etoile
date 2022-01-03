package qengine.program;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.repository.config.AbstractDelegatingRepositoryImplConfig;
import org.eclipse.rdf4j.rio.helpers.AbstractRDFHandler;

public class Dictionnaire {
	private int id;
	private HashMap<Integer, String> dictionnaire;
	private HashMap<String, Integer> dictionnaireInverse;

	
	public Dictionnaire() {
		this.id =0; 
		this.dictionnaire = new HashMap<Integer, String>();
		this.dictionnaireInverse = new HashMap<String, Integer>();
	}
	

	public HashMap<Integer, String> getdictionnaire(){
		return this.dictionnaire;
	}
	
	public HashMap<String, Integer> getdictionnaireInverse(){
		return this.dictionnaireInverse;
	}
	
	public String getValeurdictionnaire(Integer cle) {
		return getdictionnaire().get(cle);
	}
	
	public Integer getCledictionnaireInverse(String valeur) {
		return getdictionnaireInverse().get(valeur);
	}
	
	public void addIddictionnaire(String subject, String predicat, String object) {
		if(!dictionnaire.containsValue(subject)) {
			dictionnaire.put(id,subject);
			dictionnaireInverse.put(subject,id);
			id++;
		}

		if(!dictionnaire.containsValue(predicat)) {
			dictionnaire.put(id,predicat);
			dictionnaireInverse.put(predicat,id);
			id++;
		}

		if(!dictionnaire.containsValue(object)) {
			dictionnaire.put(id,object);
			dictionnaireInverse.put(object, id);
			id++;
		}
	}
	
	
	public void affichagedictionnaire() {
		for (Integer cle : this.getdictionnaire().keySet()) {
			System.out.println("la clé : " +cle+ " à pour ressource : " +dictionnaire.get(cle));			
		}
	}
	
		
}
