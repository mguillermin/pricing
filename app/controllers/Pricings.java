package controllers;

import net.sf.oval.constraint.MinLength;
import models.Line;
import models.Pricing;
import models.Section;
import play.data.validation.Min;
import play.data.validation.Required;
import play.mvc.Controller;

public class Pricings extends Controller {

    public static void show(Long id) {
    	Pricing pricing = Pricing.findById(id);
    	render(pricing);
    }
    
    public static void editSection(Long pricingId, Long sectionId) {
    	Pricing pricing = Pricing.findById(pricingId);
    	Section section = null;
    	if (sectionId != null) {
    		section = Section.findById(sectionId);
    		params.put("title", section.title);
    		params.put("position", String.valueOf(section.position));
    	}
    	render(pricing, section);
    }

    public static void editSectionAction(
    		Long princigId, Long sectionId,
            @Required(message="Title is required")String title,
            Long position) {
    	Pricing pricing = Pricing.findById(princigId);
    	if (validation.hasErrors()) {
			render("Pricings/editSection.html", pricing);
    	}
    	Section section = null;
    	if (sectionId != null) {
    		section = Section.findById(sectionId);
        	section.title = title;
        	if (position != null) {
        		section.position = position;
        	}
    	} else {
    		section = new Section(pricing, title);
    	}
    	section.save();
    	show(princigId);
    }

    public static void deleteSection(Long id) {
    	Section section = Section.findById(id);
    	Pricing pricing = section.pricing;
    	section.delete();
    	show(pricing.id);
    }

    public static void editLine(Long sectionId, Long lineId) {
    	Section section = Section.findById(sectionId);
    	Line line = null;
    	if (lineId != null) {
    		line = Line.findById(lineId);
    		params.put("title", line.title);
    	}
    	render(section, line);
    }

    public static void editLineAction(
    		Long sectionId, Long lineId, 
            @Required(message="Title is required")String title) {
    	Section section = Section.findById(sectionId);
    	if (validation.hasErrors()) {
			render("Pricings/editLine.html", section);
    	}

    	Line line = null; 
		if (lineId != null) {
    		line = Line.findById(lineId);
    		line.title = title;
    	} else {
    		line = new Line(section, title);
    	}
		line.save();
		show(section.pricing.id);
    }

    public static void deleteLine(Long id) {
    	Line line = Line.findById(id);
    	Pricing pricing = line.section.pricing;
    	line.delete();
    	show(pricing.id);
    }
    
    public static void editPricing(Long pricingId) {
    	if (pricingId != null) {
    		Pricing pricing = Pricing.findById(pricingId);
    		params.put("code", pricing.code);
    		params.put("title", pricing.title);
    	}
    	render();
    }
    
    public static void editPricingAction(Long pricingId, 
    		@Required(message="Code is required") String code, 
    		@Required(message="Title is required") String title) {
    	if (validation.hasErrors()) {
			render("Pricings/editPricing.html");
    	}
    	Pricing pricing = null;
    	if (pricingId != null) {
    		pricing = Pricing.findById(pricingId);
    	} else {
    		pricing = new Pricing();
    	}
    	pricing.code = code;
    	pricing.title = title;
    	pricing.save();
    	
    	Application.index();
    }
    
    public static void sectionUp(Long sectionId) {
    	Section section = Section.findById(sectionId);
    	section.up();
    	show(section.pricing.id);
    }

    public static void sectionDown(Long sectionId) {
    	Section section = Section.findById(sectionId);
    	section.down();
    	show(section.pricing.id);
    }

    public static void lineUp(Long lineId) {
    	Line line = Line.findById(lineId);
    	line.up();
    	show(line.section.pricing.id);
    }

    public static void lineDown(Long lineId) {
    	Line line = Line.findById(lineId);
    	line.down();
    	show(line.section.pricing.id);
    }

}
