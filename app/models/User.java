package models;

import javax.persistence.Entity;
import javax.persistence.UniqueConstraint;

import play.db.jpa.Model;

@Entity
public class User extends Model {

	public String username;
	
	public String password;
	
	public String toString() {
		return username;
	}
}
