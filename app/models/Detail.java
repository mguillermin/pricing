package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Detail extends Model {

	@ManyToOne
	public Line line;
	
	@ManyToOne
	public Profile profile;

	public Double amount;
	
	public Detail(Line line, Profile profile) {
		this.line = line;
		this.profile = profile;
		this.amount = 0D;
	}
	
	public Detail(Line line, Profile profile, Double amount) {
		this.line = line;
		this.profile = profile;
		this.amount = amount;
	}
	
	/**
	 * Computes the price of this Detail (amount x profile rate)
	 * @return the price of this Detail
	 */
	public Double getPrice() {
		if (this.profile != null && this.amount != null) {
			return this.profile.rate * this.amount;
		} else {
			return 0D;
		}
	}
}
