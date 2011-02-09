package controllers;

import java.util.Collections;
import java.util.List;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;

import models.Pricing;
import models.PricingTag;
import play.db.jpa.JPA;
import play.mvc.Controller;

public class PricingTags extends Controller {

	public static void list(Long pricingId) {
		Pricing pricing = Pricing.findById(pricingId);
		List<PricingTag> tags = pricing.pricingTags;
		render(tags);
	}
	
	public static void create(Long pricingId, String title) {
		Pricing pricing = Pricing.findById(pricingId);
		Number revision = pricing.getCurrentRevisionNumber();
		PricingTag tag = new PricingTag(pricing, revision, title);
		tag.save();
		Pricings.show(pricingId, true);
	}
	
	public static void delete(Long id) {
		PricingTag tag = PricingTag.findById(id);
		Pricing pricing = tag.pricing;
		tag.delete();
		Pricings.show(pricing.id, true);
	}
}
