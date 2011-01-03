package controllers;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import play.Logger;

import models.User;

public class Security extends controllers.Secure.Security {

	static boolean authenticate(String username, String password) {
		//return authenticateFromLdap(username, password);
		User user = User.find("byUsername", username).first();
		return user != null && user.password.equals(password);
	}
	
	
	static boolean authenticateFromLdap(String username, String password) {
		boolean authOk = false;
		try {
			Hashtable env = new Hashtable();
			env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
			env.put(Context.PROVIDER_URL, "ldap://ldap.clever-age.net:636");
			env.put(Context.SECURITY_AUTHENTICATION, "simple");
			env.put(Context.SECURITY_PROTOCOL, "ssl");
			env.put(Context.SECURITY_PRINCIPAL,String.format("uid=%s,ou=corporate,dc=clever-age,dc=com", username)); // specify the username
			env.put(Context.SECURITY_CREDENTIALS, password); 
			DirContext ctx = new InitialDirContext(env);
		} catch (NamingException e) {
			Logger.warn(e, "Error during LDAP connection");
		}
		return authOk;
	}
}
