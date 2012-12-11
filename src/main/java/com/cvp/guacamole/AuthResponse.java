package com.cvp.guacamole;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="")
public class AuthResponse implements java.io.Serializable {

	private static final long serialVersionUID = 8493750208646707464L;

	private String username;
	
	private List<Connection> connections;
	
	public String getUsername() {
		return username;
	}

	@XmlElement(name="username")
	public void setUsername(String username) {
		this.username = username;
	}
	
	@XmlElementWrapper(name="connections")
	public List<Connection> getConnections() {
		return connections;
	}

	public void setConnections(List<Connection> connections) {
		this.connections = connections;
	}
	
}
