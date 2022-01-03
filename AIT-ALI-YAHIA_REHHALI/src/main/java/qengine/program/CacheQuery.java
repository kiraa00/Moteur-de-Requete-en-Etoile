package qengine.program;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class CacheQuery {
	Properties properties;
	FileInputStream file;
	private HashMap<String, String> cache;

	public CacheQuery(FileInputStream file) throws IOException {
		this.cache = new HashMap<String, String>();
		this.properties = new Properties();
		this.file = file;
		this.loadCache();
	}

	public CacheQuery(){
		this.cache = new HashMap<String, String>();
		this.properties = new Properties();
	}

	public boolean keyexist(String Id) {
		return cache.containsKey(Id);
	}

	public HashMap<String, String> getCacheQuery() {
		return this.cache;
	}

	public String getResult(String Id) {
		if (cache.containsKey(Id)) {
			return cache.get(Id);
		}
		return null;
	}

	public void addQuery(String Id, String result) {
		if (!cache.containsKey(Id)) {
			cache.put(Id, result);
		}
	}

	public void saveCache(FileOutputStream out) throws IOException {
		for (Map.Entry<String, String> entry : cache.entrySet()) {
			properties.put(entry.getKey(), entry.getValue().toString());
		}
		properties.store(out, null);
		out.close();
	}

	public void loadCache() throws IOException {
		if (this.file != null) {
			properties.load(this.file);
			ArrayList<Integer> values = new ArrayList<Integer>();
			for (String key : properties.stringPropertyNames()) {
				cache.put(key, properties.get(key).toString());
			}
			file.close();
		} else {
			System.out.println("Empty cache");
		}
	}
}
