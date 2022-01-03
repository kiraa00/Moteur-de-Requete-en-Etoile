package qengine.program;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.jena.query.ResultSet;
import org.eclipse.rdf4j.query.parser.ParsedQuery;

public class QuerySave implements Cloneable {
	private String id;
	private ParsedQuery parsedQuery;
	private String requete;
	private ArrayList<String> result;
	private ArrayList<String> resultJena;
	private ResultSet resultJenaSet;
	private long timeReponse;
	private long timeReponseJena;

	public ResultSet getResultJenaSet() {
		return resultJenaSet;
	}

	public void setResultJenaSet(ResultSet resultJenaSet) {
		this.resultJenaSet = resultJenaSet;
	}

	public String getId() {
		return id;
	}

	public QuerySave(String id, ParsedQuery parsedQuery, String requete) {
		this.id = id;
		this.parsedQuery = parsedQuery;
		this.requete = requete;
	}

	public Object clone() throws CloneNotSupportedException {
		return (QuerySave) super.clone();
	}

	public ParsedQuery getParsedQuery() {
		return parsedQuery;
	}

	public String getRequete() {
		return requete;
	}

	public ArrayList<String> getResult() {
		return result;
	}

	public void setResult(ArrayList<String> result) {
		this.result = result;
	}

	public ArrayList<String> getResultJena() {
		return resultJena;
	}

	public void setResultJena(ArrayList<String> resultJena) {
		this.resultJena = resultJena;
	}

	public long getTimeReponse() {
		return timeReponse;
	}

	public void setTimeReponse(long timeReponse) {
		this.timeReponse = timeReponse;
	}

	public long getTimeReponseJena() {
		return timeReponseJena;
	}

	public void setTimeReponseJena(long timeReponseJena) {
		this.timeReponseJena = timeReponseJena;
	}

}
