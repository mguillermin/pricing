package models;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.UniqueConstraint;

import org.hibernate.envers.Audited;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
@Audited
public class Pricing extends Model {

	@Required
	public String code;
	
	@Required
	public String title;
	
	public Date updatedAt;
	
	@OneToMany (mappedBy="pricing", cascade = CascadeType.ALL)
	@OrderBy ("position")
	public List<Section> sections;
	
	@OneToMany (mappedBy="pricing", cascade = CascadeType.ALL)
	@OrderBy ("position")
	public List<Profile> profiles;
	
	/**
	 * Used by other entities to notify pricing of a modification
	 */
	public void updateUpdatedAt() {
		updatedAt = new Date();
		save();
	}
	
	public Pricing() {
		updatedAt = new Date();
	}
	
	public static Pricing findByCode(String code) {
		Pricing chiffrage = Pricing.find("byCode", code).first();
		return chiffrage;
	}

	/**
	 * Computes the amount (number of days) of this Pricing
	 * @return the amount of this Pricing
	 */
	public Double getAmount() {
		Double amount = 0D;
		if (sections != null) {
			for (Section section : sections) {
				amount += section.getAmount();
			}
		}
		return amount;
	}

	/**
	 * Computes the total amount for a given profile
	 * @param profile
	 * @return
	 */
	public Double getAmountByProfile(Profile profile) {
		Double amount = 0D;
		for (Section section : sections) {
			amount += section.getAmountByProfile(profile);
		}
		return amount;
	}
	
	
	/**
	 * Computes the total price of this Pricing
	 * @return the price of this Pricing
	 */
	public Double getPrice() {
		Double price = 0D;
		if (sections != null) {
			for (Section section : sections) {
				price += section.getPrice();
			}
		}
		return price;
	}
	
	public Section getSectionByPosition(Long position) {
		for (Section section : sections) {
			if (section.position.equals(position)) {
				return section;
			}
		}
		return null;
	}

	public Profile getProfileByPosition(Long position) {
		for (Profile profile : profiles) {
			if (profile.position.equals(position)) {
				return profile;
			}
		}
		return null;
	}
}
