package qengine.program;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.rio.helpers.AbstractRDFHandler;

/**
 * Le RDFHandler intervient lors du parsing de donnees et permet d'appliquer un traitement pour chaque element lu par le parseur.
 *
 * Ce qui servira surtout dans le programme est la methode {@link #handleStatement(Statement)} qui va permettre de traiter chaque triple lu.
 *
 * à adapter/recrire selon vos traitements.
 */

public final class MainRDFHandler extends AbstractRDFHandler {	
	
	private Dictionnaire dict = new Dictionnaire();
	private Index index = new Index(dict);
	private static int nombreTriplet =0;
	
	@Override
	public void handleStatement(Statement st) {
//		System.out.println(st.getSubject()+ "\t" + st.getPredicate() +"\t"+st.getObject() );
		dict.addIddictionnaire(st.getSubject().stringValue(), st.getPredicate().stringValue(), st.getObject().stringValue());
		index.creatIndex(st.getSubject().stringValue(), st.getPredicate().stringValue(), st.getObject().stringValue());
		nombreTriplet++;
		};
		
	
	public Dictionnaire getDict(){
		return this.dict;
	}
	

	public Index getIndex(){
		return this.index;
	}


	public int getNombreTriplet() {
		return nombreTriplet;
	}
	
}
