package qengine.program;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.eclipse.rdf4j.model.Value;

public class Index {

	private Dictionnaire dict;
	private HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> spo, pso, osp, sop, pos, ops;
	public Index(Dictionnaire dict) {
		this.dict = dict;
		spo = new HashMap<Integer, HashMap<Integer, ArrayList<Integer>>>();
		pso = new HashMap<Integer, HashMap<Integer, ArrayList<Integer>>>();
		osp = new HashMap<Integer, HashMap<Integer, ArrayList<Integer>>>();
		sop = new HashMap<Integer, HashMap<Integer, ArrayList<Integer>>>();
		pos = new HashMap<Integer, HashMap<Integer, ArrayList<Integer>>>();
		ops = new HashMap<Integer, HashMap<Integer, ArrayList<Integer>>>();
	}

	public HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> getSpo() {
		return spo;
	}

	public HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> getPso() {
		return pso;
	}

	public HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> getOsp() {
		return osp;
	}

	public HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> getSop() {
		return sop;
	}

	public HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> getPos() {
		return pos;
	}

	public HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> getOps() {
		return ops;
	}

	public HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> getIndex(String store) {
		HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> result = null;
		switch (store) {
		case "spo":
			result = getSpo();
			break;
		case "pso":
			result = getPso();
			break;
		case "osp":
			result = getOsp();
			break;
		case "sop":
			result = getSop();
			break;
		case "pos":
			result = getPos();
			break;
		case "ops":
			result = getOps();
			break;
		}
		return result;
	}

	public void creatIndex(String subject, String property, String object) {
//		addIndex(dict.getdictionnaireInverse().get(subject), dict.getdictionnaireInverse().get(property),
//				dict.getdictionnaireInverse().get(object), this.spo);
//		addIndex(dict.getdictionnaireInverse().get(property), dict.getdictionnaireInverse().get(subject),
//				dict.getdictionnaireInverse().get(object),this.pso);
//		addIndex(dict.getdictionnaireInverse().get(object), dict.getdictionnaireInverse().get(subject),
//				dict.getdictionnaireInverse().get(property),this.osp);
//		addIndex(dict.getdictionnaireInverse().get(subject), dict.getdictionnaireInverse().get(object),
//				dict.getdictionnaireInverse().get(property),this.sop);
		addIndex(dict.getdictionnaireInverse().get(property), dict.getdictionnaireInverse().get(object),
				dict.getdictionnaireInverse().get(subject),this.pos);
		addIndex(dict.getdictionnaireInverse().get(object), dict.getdictionnaireInverse().get(property),
				dict.getdictionnaireInverse().get(subject),this.ops);

	}

	public void addIndex(int r1, int r2, int r3, HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> index) {
		
		//Nous définissons r1,r2,r3 en fonction du type de l'index que nous passons en parametre 
		
		HashMap<Integer, ArrayList<Integer>> subCollect;
		if (index.get(r1) == null) {
			subCollect = new HashMap<Integer, ArrayList<Integer>>();
		} else {
			subCollect = index.get(r1);
		}
		ArrayList<Integer> subsubCollect;
		if (subCollect.get(r2) == null) {
			subsubCollect = new ArrayList<Integer>();
		} else {
			subsubCollect = subCollect.get(r2);
		}
		if(!subsubCollect.contains(r3)) {
			subsubCollect.add(r3);
		}
		subCollect.put(r2, subsubCollect);

		index.put(r1, subCollect);
	}
	
	public void affichageIndex(String type) {
		System.out.println("Index type "+type);
		for (Entry<Integer, HashMap<Integer, ArrayList<Integer>>> r1 : this.getIndex(type).entrySet()) {
			System.out.print("la cle : " +r1.getKey()+ " a comme sous index: ");
			for(Entry<Integer, ArrayList<Integer>> r2: r1.getValue().entrySet()) {
				System.out.print(", "+r2.getKey()+": "+r2.getValue().toString());
			}
			System.out.println();
		}
	}
}
