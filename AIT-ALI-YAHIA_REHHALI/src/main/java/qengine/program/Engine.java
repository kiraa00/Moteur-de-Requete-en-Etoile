package qengine.program;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.rdf4j.query.algebra.Projection;
import org.eclipse.rdf4j.query.algebra.StatementPattern;
import org.eclipse.rdf4j.query.algebra.helpers.StatementPatternCollector;

public class Engine {
	private Dictionnaire dictionnaire;
	private Index index;
	
	public Engine(Dictionnaire dictionnaire, Index index) {
		this.dictionnaire = dictionnaire;
		this.index=index;
	}
	
	public ArrayList<Integer> query(List<StatementPattern> patterns){
		//le choix de l'index dépend du variable qu'on recherche
		HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> idx = index.getIndex("pos");
		if(idx.size() > index.getIndex("ops").size()) {
			idx = index.getIndex("ops");
		}
		ArrayList<Integer> sujetL = new ArrayList();
		HashMap<Integer, String> dict = this.dictionnaire.getdictionnaire();
		HashMap<String, Integer> dictInverse = this.dictionnaire.getdictionnaireInverse();
		
//		System.out.println("Requête:");
		for (int i = 0; i < patterns.size(); i++) {
			String Predicat = patterns.get(i).getPredicateVar().getValue().stringValue();
			String Objet = patterns.get(i).getObjectVar().getValue().stringValue();
			if (dictInverse.containsKey(Predicat) && dictInverse.containsKey(Objet)) {
				int cleP = dictInverse.get(Predicat);
				int cleO = dictInverse.get(Objet);

				ArrayList<Integer> sujetT = idx.get(cleP).get(cleO);
				//System.out.println("condition " + i + ":");
				if (sujetT != null) {
//					for (int j = 0; j < sujetT.size(); j++) {
//						System.out.print(sujetT.get(j) + ",");
//					}

					if (i != 0) {
						sujetL = intersection(sujetL, sujetT);
					} else {
						sujetL = sujetT;
					}
				} else {
					sujetL = new ArrayList();
					break;
				}
//				System.out.println();
			}
		}
		return sujetL;
	}
	
	
	public static <T> ArrayList<T> intersection(ArrayList<T> list1, ArrayList<T> list2) {
		ArrayList<T> list = new ArrayList<T>();

		for (T t : list1) {
			if (list2.contains(t)) {
				list.add(t);
			}
		}

		return list;
	}
}
