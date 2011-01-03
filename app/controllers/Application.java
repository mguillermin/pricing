package controllers;

import java.util.List;

import models.Pricing;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Application extends Controller {

    public static void index() {
    	List<Pricing> pricings = Pricing.find("order by updatedAt desc").fetch();
        render(pricings);
    }

}