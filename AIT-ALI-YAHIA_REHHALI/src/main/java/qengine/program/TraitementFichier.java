package qengine.program;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.query.algebra.Projection;
import org.eclipse.rdf4j.query.algebra.StatementPattern;
import org.eclipse.rdf4j.query.algebra.helpers.AbstractQueryModelVisitor;
import org.eclipse.rdf4j.query.algebra.helpers.StatementPatternCollector;
import org.eclipse.rdf4j.query.parser.ParsedQuery;
import org.eclipse.rdf4j.query.parser.sparql.SPARQLParser;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;
import org.apache.commons.cli.*;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;

final class TraitementFichier {

// Jena Model
	static Jena jena = new Jena();
	public static Model modelJena;

// nombre variable
	public static int nbrTriplet = 0;
	public static int nbrRequete = 0;

// time variable
	public static Date date = new Date();
	public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
	public static long start = 0;
	public static long end = 0;
	public static long startQ = 0;
	public static long endQ = 0;
	public static long startProgramTime = 0;
	public static long endProgramTime = 0;
	public static long creatingDictionaryIndexTime = 0;
	public static long parseQueriesTime = 0;
	public static long warmQueriesTime = 0;
	public static long allQueriesTimeEngine = 0;
	public static long allQueriesTimeJena = 0;
	public static long oneQueryTime = 0;
// path variable

	static final String baseURI = null;
	static String infoPerform = "evaluation.csv";
	static String workingDir = "\\data\\";
	static String outputDir = "\\output\\";
	static String DataName = "workload1\\500K.nt";
	static String QueryRName = "10M";
	static String queryReper = workingDir + QueryRName;
	static String dataFile = workingDir + DataName;
	static String cacheFile = "\\cache\\" + "cache.properties";

	static Dictionnaire dictionnaire;
	static Index index;
	static EngineOpt moteur;
	static CacheQuery cache = new CacheQuery();
	static int nbrdouble = 0;
	static int nbrReq = 0;
	static HashMap<Integer, Integer> NbrRep_req;
//List pour stocker les requêtes

	static ArrayList<QuerySave> Queries = new ArrayList<QuerySave>();
	static ArrayList<QuerySave> FauxQueries;

//==========================================================================================================
// entree du programme
	public static void main(String[] args) throws Exception {
		FauxQueries = new ArrayList<QuerySave>();
// Initialisation de dossier de travail
		startProgramTime = System.currentTimeMillis();
		File directory = new File("");
		String workPath = directory.getAbsolutePath();
		workingDir = workPath + workingDir;
		outputDir = workPath + outputDir +"D2MQ10M\\";
		new File(outputDir).mkdir();
		queryReper = workingDir +QueryRName;
		dataFile = workingDir + DataName;
		File[] files = new File(queryReper).listFiles();

		modelJena = jena.createModel(dataFile);

//Parsing les requêtes

		System.out.println("Réponse au requêtes:");
		System.out.println("_____________________________________________________________________________________");
		for (File f : files) {
			if (!f.isDirectory()) {
				System.out.println(f.getName().toString());
				parseQueries(queryReper + "\\" + f.getName().toString());
			}
		}

// Réponse aux requêtes avec Jena
		try (BufferedWriter sortie = new BufferedWriter(new FileWriter(outputDir + QueryRName + "N.queryset", false));) {
		int cmptreqVide=0;
		NbrRep_req = new HashMap<Integer, Integer>();
		System.out.println("Réponse aux requêtes par Jena");
		
		for (int i = 0; i < Queries.size(); i++) {
			System.out.println(Queries.get(i).getId());
			Query jenaQuery = QueryFactory.create(Queries.get(i).getRequete());
			// reponse par Jena
			ResultSet resultSelect = jena.selectJena(jenaQuery, modelJena);
			// ArrayList<String> resultJena = new ArrayList<String>();
			// Traitement du resultat Jena
			int nbrReponse = 0;
			while (resultSelect.hasNext()) {
				resultSelect.next();
				nbrReponse++;
			}
			// compter le nombre de requete par reponse
			if (NbrRep_req.containsKey(nbrReponse)) {
				int val = NbrRep_req.get(nbrReponse);
				NbrRep_req.put(nbrReponse, val + 1);
			} else {
				NbrRep_req.put(nbrReponse, 1);
			}
			
			if(nbrReponse==0) {
				if(cmptreqVide <433) {
					sortie.write(Queries.get(i).getRequete()+"\n");
					cmptreqVide++;
				}
			}else {
				sortie.write(Queries.get(i).getRequete()+"\n");
			}
		}
				
		}
		
		try (BufferedWriter sortie = new BufferedWriter(new FileWriter(outputDir + QueryRName + "Stat100K.csv", true));) {
			String ligne1 = "Requetes;Doubles";
			String ligne2 = nbrReq+";"+nbrdouble;
			for(Map.Entry stat: NbrRep_req.entrySet()) {
				ligne1+=";req_"+stat.getKey();
				ligne2+=";"+stat.getValue();
			}
			sortie.write("\n"+ligne1+"\n"+ligne2);
		}
		System.out.println("nombre de requêtes doublé: " + nbrdouble);
		for (Integer cle : NbrRep_req.keySet()) {
			System.out.println("le nombre de requête avec réponse " + cle + " : " + NbrRep_req.get(cle));
		}

	}

// ======================================================================== Fonctions à utiliser ============================

// Creation Id Requete
	public static String CreationIdRequete(List<StatementPattern> patterns) {
		String IdReq = "";
		for (int i = 0; i < patterns.size(); i++) {
			String Predicat = patterns.get(i).getPredicateVar().getValue().stringValue();
			String Objet = patterns.get(i).getObjectVar().getValue().stringValue();
			String condId = "P" + Predicat.hashCode() + "O" + Objet.hashCode();
			if (!IdReq.contains(condId)) {
				IdReq += condId;
			}
		}
		return IdReq;
	}

//Traite chaque requï¿½te lue dans {@link #queryFile} avec {@link #processAQuery(ParsedQuery)}.
	private static void parseQueries(String queryFile) throws FileNotFoundException, IOException {

		try (Stream<String> lineStream = Files.lines(Paths.get(queryFile));) {
			SPARQLParser sparqlParser = new SPARQLParser();
			Iterator<String> lineIterator = lineStream.iterator();
			StringBuilder queryString = new StringBuilder();
			while (lineIterator.hasNext())

			{
//				boolean goodQuery;
				String line = lineIterator.next();
				queryString.append(line);
				if (line.trim().endsWith("}")) {
					startQ = System.currentTimeMillis();
					ParsedQuery query = sparqlParser.parseQuery(queryString.toString(), baseURI);
					List<StatementPattern> patterns = StatementPatternCollector.process(query.getTupleExpr());
					String IdReq = CreationIdRequete(patterns);
					if (cache.keyexist(IdReq)) {
						nbrdouble++;
					} else {
						cache.addQuery(IdReq, "");
						Queries.add(new QuerySave(IdReq, query, queryString.toString()));
					}					

					queryString.setLength(0); // Reset le buffer de la requÃªte en chaine vide
					nbrReq++;
				}
			}
//			return Queries;
		} catch (IOException e) {
			e.printStackTrace();
		}
//		return null;

	}

//Traite chaque triple lu dans {@link #dataFile} avec {@link MainRDFHandler}.
	private static void parseData(MainRDFHandler mRDFH) throws FileNotFoundException, IOException {

		try (Reader dataReader = new FileReader(dataFile)) {
			RDFParser rdfParser = Rio.createParser(RDFFormat.NTRIPLES);

			rdfParser.setRDFHandler(mRDFH);

			rdfParser.parse(dataReader, baseURI);
		}
	}

}
