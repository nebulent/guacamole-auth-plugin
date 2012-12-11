package com.cvp.guacamole;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="")
public class AuthRequest implements java.io.Serializable {

	private static final long serialVersionUID = 1375904217066615365L;

	private String username;
	
	private String password;
	
	public String getUsername() {
		return username;
	}

	@XmlElement(name="username")
	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	@XmlElement(name="password")
	public void setPassword(String password) {
		this.password = password;
	}
	
}
