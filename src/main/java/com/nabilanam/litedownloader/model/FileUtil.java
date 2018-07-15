package com.nabilanam.litedownloader.model;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 *
 * @author nabil
 */
public class FileUtil {

	private static Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(Path.class, new PathConverter()).create();

	public static Database deserialize() {
		Database db = null;
		try (Reader reader = new FileReader(GlobalConstants.DB_NAME)) {
			db = gson.fromJson(reader, Database.class);
		} catch (FileNotFoundException e) {
			//
		} catch (IOException e) {
			//
		}
		return db;
	}

	public static void serialize(Database db) throws FileNotFoundException, IOException {
		try (Writer writer = new FileWriter(GlobalConstants.DB_NAME)) {
			gson.toJson(db, writer);
		}
	}
}
