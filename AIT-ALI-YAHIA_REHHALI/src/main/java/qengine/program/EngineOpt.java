package qengine.program;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.rdf4j.query.algebra.Projection;
import org.eclipse.rdf4j.query.algebra.StatementPattern;
import org.eclipse.rdf4j.query.algebra.helpers.StatementPatternCollector;

public class EngineOpt {
	private Dictionnaire dictionnaire;
	private Index index;
	private CacheQuery cache;

	public EngineOpt(Dictionnaire dictionnaire, Index index, CacheQuery cache) {
		this.dictionnaire = dictionnaire;
		this.index = index;
		this.cache = cache;
	}

	public ArrayList<Integer> query(List<StatementPattern> patterns) {
		// le choix de l'index dépend du variable qu'on recherche
		HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> idx = index.getIndex("pos");
		if (idx.size() > index.getIndex("ops").size()) {
			idx = index.getIndex("ops");
		}

		HashMap<Integer, String> dict = this.dictionnaire.getdictionnaire();
		HashMap<String, Integer> dictInverse = this.dictionnaire.getdictionnaireInverse();
		// Creation Id Requête
		ArrayList<Integer> sujetL = new ArrayList<Integer>();
		for (int i = 0; i < patterns.size(); i++) {// moteur de recherche des résultats
			String Predicat = patterns.get(i).getPredicateVar().getValue().stringValue();
			String Objet = patterns.get(i).getObjectVar().getValue().stringValue();
			if (dictInverse.containsKey(Predicat) && dictInverse.containsKey(Objet)) {
				int cleP = dictInverse.get(Predicat);
				int cleO = dictInverse.get(Objet);

				ArrayList<Integer> sujetT = idx.get(cleP).get(cleO);
				// System.out.println("condition " + i + ":");
				if (sujetT != null) {
//						for (int j = 0; j < sujetT.size(); j++) {
//							System.out.print(sujetT.get(j) + ",");
//						}
					if (i != 0) {
						sujetL = intersection(sujetT, sujetL);
					} else {
						sujetL = sujetT;
					}
				} else {
					sujetL = new ArrayList<Integer>();
					break;
				}
//					System.out.println();
			}

		}
		if(sujetL.isEmpty()) {
			return null;
		}
		return sujetL;
	}

	public static <T> ArrayList<T> intersection(ArrayList<T> list1, ArrayList<T> list2) {
		ArrayList<T> list = new ArrayList<T>();
		for (T t : list1) {
			if (list2.contains(t) && !list.contains(t)) {
				list.add(t);
			}
		}

		return list;
	}
}
