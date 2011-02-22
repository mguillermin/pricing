package controllers;

import models.Pricing;
import models.PricingUpdateChannel;
import play.mvc.Controller;
import utils.dto.PricingConverter;
import utils.dto.PricingDTO;

public class PricingUpdater extends Controller {

	public static void waitUpdate(Long pricingId) {        
        // Here we use continuation to suspend 
        // the execution until a new message has been received
		PricingUpdateChannel channel = PricingUpdateChannel.get(pricingId);
		pricingId = await(channel.nextPricingUpdate());
		Pricing pricing = Pricing.findById(pricingId);
    	PricingDTO pricingDto = PricingConverter.convertToDTO(pricing);
    	renderJSON(pricingDto);
    }
}
