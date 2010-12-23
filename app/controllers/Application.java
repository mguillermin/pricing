package controllers;

import java.util.List;

import models.Pricing;
import play.mvc.Controller;

public class Application extends Controller {

    public static void index() {
    	List<Pricing> pricings = Pricing.find("order by updatedAt desc").fetch();
        render(pricings);
    }

}