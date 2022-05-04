package com.beatplaylist.utilities.network.utilities;

public class ServerUtilities {

	private static ServerUtilities instance = new ServerUtilities();

	public static ServerUtilities getInstance() {
		return instance;
	}

	public String read_host, write_host = "0.0.0.0", image_host = "0.0.0.0", image_location = "Earth", server_location = "Earth";

	public void setReadWriteHost(String read, String write) {
		setReadHost(read);
		setWriteHost(write);
	}

	public void setWriteHost(String value) {
		this.write_host = value;
	}

	public void setReadHost(String value) {
		this.read_host = value;
	}

	public void setServerLocation(String value) {
		this.server_location = value;
	}

	public void setImageHost(String value) {
		this.image_host = value;
	}

	public void setImageLocation(String value) {
		this.image_location = value;
	}

	public String getWriteHost() {
		return this.write_host;
	}

	public String getReadHost() {
		return this.read_host;
	}

	public String getImageHost() {
		return this.image_host;
	}

	public String getServerLocation() {
		return this.server_location;
	}
}