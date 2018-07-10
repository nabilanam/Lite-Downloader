package com.nabilanam.litedownloader.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *
 * @author nabil
 */
public class FileUtil {
	
	private static final File FILE = new File(GlobalConstants.DB_NAME);

	public static Database deserialize() {
		Database db;
		try (FileInputStream fis = new FileInputStream(FILE);
				ObjectInputStream inputStream = new ObjectInputStream(fis)) {
			db = (Database) inputStream.readObject();
		} catch (IOException | ClassNotFoundException ex) {
			db = null;
		}
		return db;
	}

	public static void serialize(Database db) throws FileNotFoundException, IOException {
		FILE.createNewFile();
		try (FileOutputStream fos = new FileOutputStream(FILE);
				ObjectOutputStream outputStream = new ObjectOutputStream(fos)) {
			outputStream.writeObject(db);
		}
	}
}
