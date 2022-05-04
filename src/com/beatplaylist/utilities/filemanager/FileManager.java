package com.beatplaylist.utilities.filemanager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class FileManager {

	private Properties properties;
	private String name;
	private String comment;
	private File file;

	public FileManager(String name) {
		this.properties = new Properties();

		try {
			this.name = name;
			String directory = "";
			if (FileUtilities.getInstance().getOperatingSystem() == OperatingSystem.WINDOWS)
				directory = FileUtilities.getInstance().getAppData() + "\\BeatPlaylist";
			else if (FileUtilities.getInstance().getOperatingSystem() == OperatingSystem.MACOS)
				directory = File.separator + "Applications" + File.separator + "BeatPlaylist.app" + File.separator + "Contents" + File.separator + "Settings";
			else
				directory = "Operating System not found!";
			if (!new File(directory).exists()) {
				new File(directory).mkdir();
			}
			this.file = new File(directory, name);
			if (!this.file.exists()) {
				try {
					this.file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			refreshProperties();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public FileManager() {
		String directory = "";
		if (System.getProperty("os.name").toLowerCase().startsWith("win"))
			directory = FileUtilities.getInstance().getAppData() + "\\BeatPlaylist";
		else if (System.getProperty("os.name").toLowerCase().startsWith("mac"))
			directory = "/Applications/BeatPlaylist/Contents/Settings";
		else
			directory = "Operating System not found!";
		if (!new File(directory).exists())
			new File(directory).mkdir();
		// refreshProperties();
	}

	public String getEnvironment() {
		if (System.getProperty("os.name").toLowerCase().startsWith("win"))
			return FileUtilities.getInstance().getAppData() + "\\BeatPlaylist";
		else if (System.getProperty("os.name").toLowerCase().startsWith("mac"))
			return "/Applications/BeatPlaylist/Contents/Settings";
		else
			return "Operating System not found!";
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getComment() {
		return comment;
	}

	public void refreshProperties() {
		try {
			if (!this.file.exists())
				saveProperties();
			FileInputStream fileInputStream = new FileInputStream(file);
			this.properties = new Properties();
			this.properties.load(fileInputStream);
			fileInputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveProperties() {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(this.file);
			this.properties.store(fileOutputStream, this.comment);
			fileOutputStream.flush();
			fileOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setProperty(String key, String value) {
		if (this.properties == null || key.isEmpty() || value.isEmpty())
			return;
		this.properties.setProperty(key, value);

	}

	public Properties getProperties() {
		return this.properties;
	}

	public String getName() {
		return this.name;
	}

	public void write(String text) {
		try {
			FileWriter outfile = new FileWriter(this.file, true);
			if (!this.file.exists())
				this.file.createNewFile();
			BufferedWriter bw = new BufferedWriter(outfile);
			bw.write(text);
			bw.flush();
			bw.close();
			System.out.println(text);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public File getFile() {
		return this.file;
	}
}