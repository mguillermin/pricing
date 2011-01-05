package controllers;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import models.User;
import play.Logger;
import play.Play;
import utils.BlindSSLSocketFactory;

public class Security extends controllers.Secure.Security {

	static boolean authenticate(String username, String password) {
		if (Play.configuration.getProperty("ldap.auth.active", "true").equals("true")) {
			if (authenticateFromLdap(username, password)) {
				// load or creates the user
				User user = User.find("byUsername", username).first();
				if (user == null) {
					user = new User();
					user.importedFromLdap = true;
					user.isAdmin = false;
					user.username = username;
					user.save();
				}
				return true;
			} else {
				return false;
			}
		}
		else {
			User user = User.find("byUsername", username).first();
			return user != null && user.password.equals(password);
		}
	}
	
	static boolean check(String profile) {
		Logger.info("Checking profile %s for user %s", profile, connected());
        User user = User.find("byUsername", connected()).first();
        if ("isAdmin".equals(profile)) {
            return user.isAdmin;
        }
        else {
            return false;
        }
    }	
	
	static boolean authenticateFromLdap(String username, String password) {
		Logger.info("Authenticating %s via LDAP...", username);
		
		boolean authOk = false;
		try {			
			Hashtable env = new Hashtable();
			env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
			env.put(Context.PROVIDER_URL, Play.configuration.getProperty("ldap.auth.provider.url"));
			env.put(Context.SECURITY_AUTHENTICATION, "simple");
			env.put(Context.SECURITY_PROTOCOL, "ssl");
			env.put(Context.SECURITY_PRINCIPAL,String.format(Play.configuration.getProperty("ldap.auth.username.format"), username));
			env.put(Context.SECURITY_CREDENTIALS, password);

			// We disable ssl certificate check
			env.put("java.naming.ldap.factory.socket", BlindSSLSocketFactory.class.getName());

			DirContext ctx = new InitialDirContext(env);
			// If no exception, authentication is ok
			ctx.close();

			Logger.info("Authentication of %s successfull", username);

			authOk = true;
		} catch (NamingException e) {
			Logger.info("Authentication of %s failed", username);
		}
		return authOk;
	}
}
