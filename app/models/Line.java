package models;

import java.util.Iterator;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.envers.Audited;

import play.data.validation.Required;
import play.db.jpa.JPABase;
import play.db.jpa.Model;

@Entity
@Audited
public class Line extends Model {

	@Required
	@Column(name="section_position") // position, order,... are reserved keywords
	public Long position;
	
	@ManyToOne
	public Section section;
	
	@Required
	public String title;
	
	@OneToMany (mappedBy="line", cascade=CascadeType.ALL)
	public Set<Detail> details;
	
	/**
	 * Overriding save to notify pricing of the modification
	 */
	@Override
	public <T extends JPABase> T save() {
		section.pricing.updateModifiedAt();
		return super.save();
	}
	
	public Line(Section section, String title) {
		this.section = section;
		this.title = title;
		this.position = this.section.lines.size() + 1L;
	}

	public Detail getDetailByProfile(Profile profile) {
		for (Detail detail : details) {
			if (detail.profile.equals(profile)) {
				return detail;
			}
		}
		return null;
	}
	
	public Double getAmountByProfile(Profile profile) {
		for (Detail detail : details) {
			if (detail.profile.equals(profile)) {
				return detail.amount;
			}
		}
		return 0D;
	}
	
	/**
	 * Computes the amount (total number of days) of this line
	 * @return the amount
	 */
	public Double getAmount() {
		Double amount = 0D;
		if (details != null) {
			for (Detail detail : details) {
				amount += detail.amount;
			}
		}
		return amount;
	}

	public Double getPriceByProfile(Profile profile) {
		for (Detail detail : details) {
			if (detail.profile.equals(profile)) {
				return detail.amount * profile.rate;
			}
		}
		return null;
	}

	/**
	 * Computes the price of this line (Sum of the price of its Details)
	 * @return the price of this line
	 */
	public Double getPrice() {
		Double price = 0D;
		if (details != null) {
			for (Detail detail : details) {
				price += detail.getPrice();
			}
		}
		return price;
	}

	public void up() {
		Line previousLine = section.getLineByPosition(position - 1);
		if (previousLine != null) {
			previousLine.position++;
			previousLine.save();
			position--;
			save();
		}
	}

	public void down() {
		Line nextLine = section.getLineByPosition(position + 1);
		if (nextLine != null) {
			nextLine.position--;
			nextLine.save();
			position++;
			save();
		}
	}
	
}
