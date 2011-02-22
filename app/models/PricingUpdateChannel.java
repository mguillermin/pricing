package models;

import java.util.HashMap;

import play.libs.F.EventStream;
import play.libs.F.Promise;

public class PricingUpdateChannel {
	
    static HashMap<Long, PricingUpdateChannel> instances = new HashMap<Long, PricingUpdateChannel>();
    
    final EventStream<Long> pricingEvent = new EventStream<Long>();
    
    public static PricingUpdateChannel get(Long pricingId) {
        if(!instances.containsKey(pricingId)) {
            instances.put(pricingId, new PricingUpdateChannel());
        }
        return instances.get(pricingId);
    }

    public Promise<Long> nextPricingUpdate() {
    	return pricingEvent.nextEvent();
    }
    
    public void updatePricing(Long pricingId) {
    	pricingEvent.publish(pricingId);
    }
}
