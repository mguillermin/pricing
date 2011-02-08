package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;

import play.db.jpa.Model;

@Entity
public class PricingTag extends Model {

	@ManyToOne
	public Pricing pricing;
	
	public Integer revision;
	
	public String title;
	
	public Date createdAt;
	
	public String updatedBy;
	
	public PricingTag(Pricing pricing, Number revision, String title) {
		this.pricing = pricing;
		this.revision = revision.intValue();
		this.title = title;
		this.createdAt = new Date();
		this.updatedBy = this.pricing.updatedBy;
	}
	
	public Pricing getHistorizedPricing() {
		AuditReader ar = AuditReaderFactory.get(em());
		Pricing pricing = ar.find(Pricing.class, this.pricing.id, revision);
		return pricing;
	}
}
