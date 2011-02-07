package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;

import play.db.jpa.Model;

@Entity
public class PricingTag extends Model {

	@ManyToOne
	private Pricing pricing;
	
	private Number revision;
	
	private String title;
	
	public PricingTag(Pricing pricing, Number revision, String title) {
		this.pricing = pricing;
		this.revision = revision;
		this.title = title;
	}
	
	public Pricing getHistorizedPricing() {
		AuditReader ar = AuditReaderFactory.get(em());
		Pricing pricing = ar.find(Pricing.class, this.pricing.id, revision);
		return pricing;
	}
}
