package controllers;

import java.util.List;

import models.Pricing;
import play.mvc.Controller;

public class Application extends Controller {

    public static void index() {
    	List<Pricing> pricings = Pricing.findAll();
        render(pricings);
    }

}