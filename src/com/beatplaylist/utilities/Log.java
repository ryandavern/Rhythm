package com.beatplaylist.utilities;

public class Log {

	private static Log instance = new Log();

	public static Log getInstance() {
		return instance;
	}

	public void write(String... write) {
		// FileManager filemanager = new FileManager("log.txt");
		// for (String values : write)
		// filemanager.write(values + "\n");
	}
}