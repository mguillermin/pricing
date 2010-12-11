package models;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

@Entity
public class Profile extends Model {

	public String title;
	
	public Double rate;
	
	@ManyToOne
	public Pricing pricing;
		
	@OneToMany (mappedBy="profile", cascade=CascadeType.ALL)
	public Set<Detail> details;
	
}
