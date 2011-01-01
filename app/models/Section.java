package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import org.hibernate.envers.Audited;

import play.Logger;
import play.data.validation.Required;
import play.db.jpa.JPABase;
import play.db.jpa.Model;

@Entity
@Audited
public class Section extends Model {

	@Required
	@Column(name="section_position") // position, order,... are reserved keywords
	public Long position;
	
	@Required
	public String title;
	
	@Required
	@ManyToOne
	public Pricing pricing;

	@OneToMany(mappedBy="section", cascade=CascadeType.ALL)
	@OrderBy("position")
	public List<Line> lines;
	
	public Section(Pricing pricing, String title) {
		this.pricing = pricing;
		this.title = title;
		this.position = this.pricing.sections.size() + 1L;
	}
	
	public Line getLineByPosition(Long position) {
		for (Line line : lines) {
			if (line.position.equals(position)) {
				return line;
			}
		}
		return null;
	}
	
	
	public void up() {
		Section previousSection = pricing.getSectionByPosition(position - 1);
		if (previousSection != null) {
			previousSection.position++;
			previousSection.save();
			position--;
			save();
		}
	}

	public void down() {
		Section nextSection = pricing.getSectionByPosition(position + 1);
		if (nextSection != null) {
			nextSection.position--;
			nextSection.save();
			position++;
			save();
		}
	}
	
	/**
	 * Computes the amount (number of days) of this Section
	 * @return the total amount of this Section
	 */
	public Double getAmount() {
		Double amount = 0D;
		if (lines != null) {
			for (Line line : lines) {
				amount += line.getAmount();
			}
		}
		return amount;
	}

	public Double getAmountByProfile(Profile profile) {
		Double amount = 0D;
		for (Line line : lines) {
			amount += line.getAmountByProfile(profile);
		}
		return amount;
	}
	
	/**
	 * Computes the price of this Section
	 * @return the total price of this Section
	 */
	public Double getPrice() {
		Double amount = 0D;
		if (lines != null) {
			for (Line line : lines) {
				amount += line.getPrice();
			}
		}
		return amount;
	}
	
	public Double getPriceByProfile(Profile profile) {
		Double price = 0D;
		for(Line line : lines) {
			price += line.getPriceByProfile(profile);
		}
		return price;
	}
}
