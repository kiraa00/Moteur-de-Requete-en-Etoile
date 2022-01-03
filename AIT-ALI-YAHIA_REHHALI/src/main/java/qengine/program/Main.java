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

/**
 * Programme simple lisant un fichier de requete et un fichier de donnees.
 * 
 * Les entrees sont donnees ici de maniere statique, Ã vous de programmer les
 * entrees par passage d'arguments en ligne de commande comme demandï¿½ dans
 * l'ï¿½noncï¿½.
 * 
 * Le present programme se contente de vous montrer la voie pour lire les
 * triples et requetes depuis les fichiers ; ce sera Ã  vous d'adapter/reecrire
 * le code pour finalement utiliser les requetes et interroger les donnees. On
 * ne s'attend pas forcemment Ã  ce que vous gardiez la meme structure de code,
 * vous pouvez tout reecrire.
 * 
 * @author Olivier Rodriguez <olivier.rodriguez1@umontpellier.fr>
 */
final class Main {
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
	public static long createModelJenaTime = 0;
	public static long parseQueriesTime = 0;
	public static long warmQueriesTime = 0;
	public static long allQueriesTimeEngine = 0;
	public static long allQueriesTimeJena = 0;
	public static long oneQueryTime = 0;
// path variable
	static final String baseURI = null;
	static String infoPerform = "evaluation";
	static String workingDir = "\\data\\";
	static String outputDir = "\\output\\";
	static String OutputName = "resultat";
	static String DataName = "100K.nt";
	static String QueryName = "sample_query.queryset";
	static String outputFile = outputDir + OutputName;
	static String queryFile = workingDir + QueryName;
	static String dataFile = workingDir + DataName;
	static String cacheFile = "\\cache\\" + "cache.properties";
	static String type = "moteur";

	static Dictionnaire dictionnaire;
	static Index index;
	static EngineOpt moteur;
	static CacheQuery cache;
//List pour stocker les requêtes
	static ArrayList<QuerySave> Queries;
	static ArrayList<QuerySave> FauxQueries;
// ===============================================================================================
//Methode utilise ici lors du parsing de requete sparql pour agir sur l'objet obtenu

// le retour de la requête c'est pour savoir si la requête est déja passé on l'a fait pour traiter les fichiers de requêtes
	public static boolean processAQuery(QuerySave q) throws IOException {
		List<StatementPattern> patterns = StatementPatternCollector.process(q.getParsedQuery().getTupleExpr());
		if (cache.keyexist(q.getId())) {
			String result = cache.getResult(q.getId());
			String[] temp = result.split("\n");
			ArrayList<String> ResultList = new ArrayList<String>(Arrays.asList(temp));
			q.setResult(ResultList);
		} else {
			ArrayList<Integer> sujetL = moteur.query(patterns);
			if (sujetL != null) {
				ArrayList<String> result = new ArrayList<String>();
				String affiche = "";
				for (int j = 0; j < sujetL.size(); j++) {
					String res = dictionnaire.getdictionnaire().get(sujetL.get(j));
					affiche += res + "\n";
					result.add(res);
				}
				Collections.sort(result);
				q.setResult(result);
				cache.addQuery(q.getId(), affiche);
				return true;
			}

		}
		return false;
//		System.out.println("first pattern : " + patterns.get(i));
//		System.out.println("variables to project : ");

		// Utilisation d'une classe anonyme
//		query.getTupleExpr().visit(new AbstractQueryModelVisitor<RuntimeException>() {
//
//			public void meet(Projection projection) {
//				// System.out.println(projection.getProjectionElemList().getElements());
//			}
//		});
	}

//Moteur sans utilisation du cache
	public static void processAQueryNoCache(QuerySave q) throws IOException {
		List<StatementPattern> patterns = StatementPatternCollector.process(q.getParsedQuery().getTupleExpr());
		ArrayList<Integer> sujetL = moteur.query(patterns);
		if (sujetL != null) {
			ArrayList<String> result = new ArrayList<String>();
			String affiche = "";
			for (int j = 0; j < sujetL.size(); j++) {
				String res = dictionnaire.getdictionnaire().get(sujetL.get(j));
				result.add(res);
			}
			Collections.sort(result);
			q.setResult(result);
		}

//		System.out.println("first pattern : " + patterns.get(i));
//		System.out.println("variables to project : ");

		// Utilisation d'une classe anonyme
//		query.getTupleExpr().visit(new AbstractQueryModelVisitor<RuntimeException>() {
//
//			public void meet(Projection projection) {
//				// System.out.println(projection.getProjectionElemList().getElements());
//			}
//		});
	}

//==========================================================================================================
// entree du programme
	public static void main(String[] args) throws Exception {
		FauxQueries = new ArrayList<QuerySave>();
// Initialisation de dossier de travail
		startProgramTime = System.currentTimeMillis();
		File directory = new File("");
		String workPath = directory.getAbsolutePath();
		workingDir = workPath + workingDir;
		outputDir = workPath + outputDir;
// Initialisation des objets
		MainRDFHandler mRDFH = new MainRDFHandler();
		Options options = new Options();
		dictionnaire = mRDFH.getDict();
		index = mRDFH.getIndex();
		moteur = new EngineOpt(dictionnaire, index, cache);
// definir les option de la ligne de commande Input output
		Option queryOption = new Option("q", "queries", true, "query file path");
		queryOption.setRequired(true);
		options.addOption(queryOption);

		Option dataOption = new Option("d", "data", true, "data file path");
		dataOption.setRequired(true);
		options.addOption(dataOption);

		Option outputOption = new Option("o", "output", true, "output file path");
		outputOption.setRequired(true);
		options.addOption(outputOption);

		Option JenaOption = new Option("jena", false, "Jena verification");
		options.addOption(JenaOption);

		Option WarmOption = new Option("warm", true, "pourcentage de query warm up");
		options.addOption(WarmOption);

		Option CacheOption = new Option("cache", false, "pourcentage de query warm up");
		options.addOption(CacheOption);

		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd = null;

		try {
			cmd = parser.parse(options, args, true);
			cmd.toString();
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			formatter.printHelp("utility-name", options);

			System.exit(1);
		}

		if (cmd.hasOption("q")) {
			QueryName = cmd.getOptionValue("queries");
			queryFile = workingDir + QueryName;
		}
		if (cmd.hasOption("o")) {
			OutputName = cmd.getOptionValue("output");
			outputDir = outputDir + OutputName + "\\";
			new File(outputDir).mkdir();
		}
		if (cmd.hasOption("d")) {
			DataName = cmd.getOptionValue("data");
			dataFile = workingDir + DataName;
		}

// load cache
		File cacheF = null;
		if (cmd.hasOption("cache")) {
			System.out.println("load cache ----");
			cacheF = new File(workPath + cacheFile);
			FileInputStream cacheInput = loadCache(cacheF);
			cache = new CacheQuery(cacheInput);
			type += "withCache";
		}
// Creation de Jena		
		if (cmd.hasOption("jena")) {
			start = System.currentTimeMillis();
			modelJena = jena.createModel(dataFile);
			end = System.currentTimeMillis();
			createModelJenaTime = end - start;
		}

// Dictionnaire et Index
		start = System.currentTimeMillis();
		System.out.println("création Dictionnaire index");
		System.out.println("_____________________________________________________________________________________");
		System.out.println();
		System.out.println();
		parseData(mRDFH);
		nbrTriplet = mRDFH.getNombreTriplet();
		System.out.println();
		end = System.currentTimeMillis();
		creatingDictionaryIndexTime = end - start;
		System.out.println("temps création index et dictionnaire: " + creatingDictionaryIndexTime);
// affichage du dictionnaire:
		// System.out.println("\t\t\t----------------------------Dictionnaire--------------------------------");
		// dictionnaire.affichagedictionnaire();

		// System.out.println("\t\t\t----------------------------Index--------------------------------");

// afichage de l'index:
		// System.out.println("\t\t\t////////////////////////////////////////////////////////////////////////");
		// index.affichageIndex("spo");
		// index.getIndex("spo");
		// System.out.println("\t\t\t////////////////////////////////////////////////////////////////////////");
//Parsing les requêtes
		start = System.currentTimeMillis();
		System.out.println("Réponse au requêtes:");
		System.out.println("_____________________________________________________________________________________");
		Queries = parseQueries();
		end = System.currentTimeMillis();
		parseQueriesTime = end - start;
		System.out.println("Temps de parser toutes les requêtes " + parseQueriesTime);

// warm up du moteur (le pourcentage de warm est donnée en argument)
		if (cmd.hasOption("warm")) {
			type += "Warm";
			start = System.currentTimeMillis();
			int prcnt = Integer.parseInt(cmd.getOptionValue("warm"));
			int nbrWarm = (prcnt * Queries.size()) / 100;
			ArrayList<QuerySave> QueriesWarm = RandomWarmUp(Queries, nbrWarm);
			// echauffement du moteur
			System.out.println("echauffement du moteur avec " + prcnt + " : " + nbrWarm + " requêtes");
			for (QuerySave q : QueriesWarm) {
				processAQueryNoCache(q);
//				System.out.println(q.getId() + "result: "+q.getResult().toString());
			}
			end = System.currentTimeMillis();
			warmQueriesTime = end - start;
			System.out.println("Temps d'echauffement:  " + warmQueriesTime);
			QueriesWarm.clear();
		} else {
			type += "Cold";
		}

//Réponse aux requêtes avec notre moteur
		System.out.println("Réponse aux requêtes par moteur");
		start = System.currentTimeMillis();
		for (QuerySave q : Queries) {// le resultat est stocker dans l'objet query as ArrayList<String>
			startQ = System.currentTimeMillis();
			if (cmd.hasOption("cache")) {
				processAQuery(q);
			} else {
				processAQueryNoCache(q);
			}
			endQ = System.currentTimeMillis();
			oneQueryTime = endQ - startQ;
			if (oneQueryTime == 0) {
				oneQueryTime = 1;
			}
			q.setTimeReponse(oneQueryTime);
		}
		end = System.currentTimeMillis();
		allQueriesTimeEngine = end - start;
		System.out.println("Temps de réponse avec moteur:  " + allQueriesTimeEngine);
// Réponse aux requêtes avec Jena
		if (cmd.hasOption("jena")) {
			System.out.println("Réponse aux requêtes par Jena");
			for (QuerySave q : Queries) {
				Query jenaQuery = QueryFactory.create(q.getRequete());
				// reponse par Jena
				ResultSet resultSelect = jena.selectJena(jenaQuery, modelJena);
				q.setResultJenaSet(resultSelect);
				oneQueryTime = jena.TempOneQuery;
				if (oneQueryTime == 0) {
					oneQueryTime = 1;
				}
				q.setTimeReponseJena(oneQueryTime);

			}
			allQueriesTimeJena=jena.TempJena;
			System.out.println("Temps de réponse avec Jena:  " + allQueriesTimeJena);

// verification du resultat
			for (QuerySave q : Queries) {
				ArrayList<String> resultJena = new ArrayList<String>();
				// Traitement du resultat Jena
				ResultSet resultSelect = q.getResultJenaSet();
				while (resultSelect.hasNext()) {
					String temp = resultSelect.next().toString();
					temp = temp.replaceAll("\\s+", "").replaceAll("\\)\\(", ";").replaceAll("\\(", "")
							.replaceAll("v0=", "").replaceAll("\\)", "").replace("?", "").replace(">", "")
							.replace("<", "").replace("\"", "");
					resultJena.add(temp);
				}
				Collections.sort(resultJena);
				q.setResultJena(resultJena);
				ArrayList<String> resultMoteur = q.getResult();
				if (resultMoteur != null && !resultJena.isEmpty()) {
					if (resultMoteur.size() == resultJena.size()) {
						if (!listEqualsIgnoreOrder(resultMoteur, resultJena)) {
							FauxQueries.add(q);
						}
					} else {
						FauxQueries.add(q);
					}
				}
			}

		}
//========================================================== Export des fichiers ========================================================
// ecriture de résultat du moteur
		try (BufferedWriter outM = new BufferedWriter(new FileWriter(outputDir + "Moteur", false));
				BufferedWriter outJ = new BufferedWriter(new FileWriter(outputDir + "Jena", false));) {
			for (QuerySave q : Queries) {
				String resMString = "";
				String resJString = "";
				if (q.getResult() != null) {
					resMString = q.getResult().toString();
				}

				outM.write("Requete: " + q.getId() + "\n" + q.getRequete() + "\nResult: " + resMString
						+ "\n temps réponse " + q.getTimeReponse() + "\n\n");
				if (cmd.hasOption("jena")) {
					if (!q.getResultJena().isEmpty()) {
						resJString = q.getResultJena().toString();
					}
					outJ.write("Requete: " + q.getId() + "\n" + q.getRequete() + "\nResult: " + resJString
							+ "\n temps réponse " + q.getTimeReponseJena() + "\n\n");
				}
			}
		}
// ecriture des requêtes fausse
		try (BufferedWriter outF = new BufferedWriter(new FileWriter(outputDir + "Fausse", false));) {
			for (QuerySave q : FauxQueries) {
				String resMString = "";
				String resJString = "";
				if (q.getResult() != null) {
					resMString = q.getResult().toString();
				}
				if (!q.getResultJena().isEmpty()) {
					resJString = q.getResultJena().toString();
				}
				outF.write("Requete: " + q.getId() + "\n" + q.getRequete() + "\nResult Moteur: " + resMString
						+ "\nResult Jena: " + resJString + "\n\n");
			}
//			System.out.println(Queries.size());

		}

// save cache
		if (cmd.hasOption("cache")) {
			System.out.println("SAVE CACHE");
			saveCache(cacheF);
			System.out.println();
		}
// fin programme
		System.out.println("Fin le résultat est dans le dossier output");
		endProgramTime = System.currentTimeMillis();
		System.out.println("temps du programme: " + (endProgramTime - startProgramTime));
// save evaluation
		try (BufferedWriter eval = new BufferedWriter(new FileWriter(outputDir + infoPerform + ".csv", true));) {
			eval.write("\n" + "Date" + ";" + "type" + ";" + "Input Data" + ";" + "Input Requete" + ";"
					+ "Nombre Triplet" + ";" + "Nombre requete" + ";" + "Temps Data" + ";" + "Temps Parse Requêtes"
					+ "Temps reponse" + ";" + "Temps Programme");
			// save evaluation moteur
			eval.write("\n" + dateFormat.format(date) + ";" + type + ";" + DataName + ";" + QueryName + ";" + nbrTriplet
					+ ";" + nbrRequete + ";" + creatingDictionaryIndexTime + ";" + parseQueriesTime + ";"
					+ allQueriesTimeEngine + ";" + (endProgramTime - startProgramTime));

			// save evaluation Jena
			if (cmd.hasOption("jena")) {
				eval.write("\n" + dateFormat.format(date) + ";" + "Jena" + ";" + DataName + ";" + QueryName + ";"
						+ nbrTriplet + ";" + nbrRequete + ";" + createModelJenaTime + ";" + parseQueriesTime + ";"
						+ allQueriesTimeJena + ";" + (endProgramTime - startProgramTime));
			}
			eval.write("\n");
		}

	}

// ======================================================================== Fonctions à utiliser ============================

// Creer une liste pour le warm up
	public static ArrayList<QuerySave> RandomWarmUp(ArrayList<QuerySave> Queries, int nombre)
			throws CloneNotSupportedException {
		ArrayList<QuerySave> selected = new ArrayList<QuerySave>();
		Random random = new Random();
		int listSize = Queries.size();

		// Avoid a deadlock
		if (nombre >= listSize) {
			return Queries;
		}

		// Get a random item until we got the requested amount
		while (selected.size() < nombre) {
			QuerySave q = Queries.get(random.nextInt(listSize));
			selected.add((QuerySave) q.clone());
		}

		return selected;
	}

// Comparaison deux liste
	public static <T> boolean listEqualsIgnoreOrder(List<T> list1, List<T> list2) {
		return new HashSet<>(list1).equals(new HashSet<>(list2));
	}

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

// LOAD CACHE FUNCTION
	private static FileInputStream loadCache(File f) {
		if (f.isFile() && f.canRead()) {
			try {
				FileInputStream in = new FileInputStream(f);
				return in;
			} catch (IOException ex) {

			}
		}
		return null;
	}

	private static void saveCache(File f) {
		try {
			FileOutputStream out = new FileOutputStream(f);
			cache.saveCache(out);
		} catch (IOException ex) {

		}
	}

	private static void saveQuery(String query) throws IOException {
		try (BufferedWriter newFileRequete = new BufferedWriter(new FileWriter(workingDir + "N" + QueryName, true));) {
			newFileRequete.write(query + "\n");
		}
	}

//Traite chaque requï¿½te lue dans {@link #queryFile} avec {@link #processAQuery(ParsedQuery)}.
	private static ArrayList<QuerySave> parseQueries() throws FileNotFoundException, IOException {
		/**
		 * Try-with-resources
		 * 
		 * @see <a href=
		 *      "https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html">Try-with-resources</a>
		 */
		/*
		 * On utilise un stream pour lire les lignes une par une, sans avoir Ã  toutes
		 * les stocker entiï¿½rement dans une collection.
		 */
		try (Stream<String> lineStream = Files.lines(Paths.get(queryFile));) {
			SPARQLParser sparqlParser = new SPARQLParser();
			Iterator<String> lineIterator = lineStream.iterator();
			StringBuilder queryString = new StringBuilder();
			ArrayList<QuerySave> Queries = new ArrayList<QuerySave>();
			while (lineIterator.hasNext())
			/*
			 * On stocke plusieurs lignes jusqu'Ã  ce que l'une d'entre elles se termine par
			 * un '}' On considÃ¨re alors que c'est la fin d'une requÃªte
			 */
			{
//				boolean goodQuery;
				String line = lineIterator.next();
				queryString.append(line);
				if (line.trim().endsWith("}")) {
					startQ = System.currentTimeMillis();
					ParsedQuery query = sparqlParser.parseQuery(queryString.toString(), baseURI);
					List<StatementPattern> patterns = StatementPatternCollector.process(query.getTupleExpr());
					String IdReq = CreationIdRequete(patterns);
					Queries.add(new QuerySave(IdReq, query, queryString.toString()));
//Pour filtrer les doublons dans query file astuce en utilisant le cache

//					goodQuery = processAQuery(query, out);// Traitement de la requete, a adapter/reecrire pour votre
//															// programme
//					if (goodQuery) {
//						saveQuery(queryString.toString());
//					}
//					endQ = System.currentTimeMillis();
//					oneQueryTimeEngine = endQ - startQ;
//					if (oneQueryTimeEngine == 0) {
//						oneQueryTimeEngine = 1;
//					}
//					out.write("durée de la requête: " + oneQueryTimeEngine);
//					out.write("\n\n");
					queryString.setLength(0); // Reset le buffer de la requÃªte en chaine vide
					nbrRequete++;
				}
			}
			return Queries;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}

//Traite chaque triple lu dans {@link #dataFile} avec {@link MainRDFHandler}.
	private static void parseData(MainRDFHandler mRDFH) throws FileNotFoundException, IOException {

		try (Reader dataReader = new FileReader(dataFile)) {
			// On va parser des donnï¿½es au format n-triples
			RDFParser rdfParser = Rio.createParser(RDFFormat.NTRIPLES);

			rdfParser.setRDFHandler(mRDFH);

			// Parsing et traitement de chaque triple par le handler
			rdfParser.parse(dataReader, baseURI);
		}
	}

}
