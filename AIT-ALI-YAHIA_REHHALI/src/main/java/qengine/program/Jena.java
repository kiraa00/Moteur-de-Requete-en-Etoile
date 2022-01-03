package qengine.program;


import java.io.InputStream;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;

public class Jena {
	public static long start = 0;
	public static long end = 0;
	public static long TempJena = 0;
	public static long TempOneQuery = 0;
	public Model createModel(String pathData) {
		Model model = ModelFactory.createDefaultModel();
		String dataset = pathData;
		InputStream in = FileManager.get().open(dataset);
		model.read(in, null, "N-TRIPLES");
		return model;
	}

	public ResultSet selectJena(Query query, Model model) {
		TempOneQuery=0;
		QueryExecution qexec = QueryExecutionFactory.create(query, model);

		try {
			start = System.currentTimeMillis();
			ResultSet results = qexec.execSelect();
			end = System.currentTimeMillis();
			TempOneQuery= end - start;
			TempJena+= end - start;
			results = ResultSetFactory.copyResults(results);
			return results;
		} finally {
			qexec.close();
		}
	}
}