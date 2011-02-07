package controllers;

import java.util.List;

import models.Pricing;
import models.PricingTag;
import play.mvc.Controller;

public class PricingTags extends Controller {

	public static void list(Long pricingId) {
		Pricing pricing = Pricing.findById(pricingId);
		List<PricingTag> tags = pricing.pricingTags;
		render(tags);
	}
	
}
