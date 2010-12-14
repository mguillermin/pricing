package controllers;

import java.text.DecimalFormat;

import models.Detail;
import models.Line;
import models.Pricing;
import models.Profile;
import models.Section;

import org.apache.commons.lang.StringUtils;

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
    
    public static void addProfile(Long pricingId) {
    	Pricing pricing = Pricing.findById(pricingId);
    	Profile profile = new Profile(pricing);
    	profile.title = "P" + (pricing.profiles.size() + 1L);
    	profile.rate = 0D;
    	profile.save();
    	show(pricingId);
    }
    
    public static void deleteProfile(Long id) {
    	Profile profile = Profile.findById(id);
    	Pricing pricing = profile.pricing;
    	profile.delete();
    	show(pricing.id);
    }
    
    public static void editProfileTitle(String id, String value) {
    	if (StringUtils.startsWith(id, "profile-")) {
    		String profileId = StringUtils.removeStart(id, "profile-");
    		Profile profile = Profile.findById(Long.valueOf(profileId));
    		profile.title = value;
    		profile.save();
        	show(profile.pricing.id);
    	}
    	renderText(value);
    }

    public static void editSectionTitle(String id, String value) throws Exception {
    	if (StringUtils.startsWith(id, "section-")) {
    		id = StringUtils.removeStart(id, "section-");
    		Section section = Section.findById(Long.valueOf(id));
    		section.title = value;
    		section.save();
        	renderText(value);
    	} else {
    		throw new Exception("Exception during section title edition");
    	}
    }

    public static void editLineTitle(String id, String value) throws Exception {
    	if (StringUtils.startsWith(id, "line-")) {
    		id = StringUtils.removeStart(id, "line-");
    		Line line = Line.findById(Long.valueOf(id));
    		line.title = value;
    		line.save();
        	renderText(value);
    	} else {
    		throw new Exception("Exception during line title edition");
    	}
    }
    
    public static void editPricingCode(String id, String value) throws Exception {
    	if (StringUtils.startsWith(id, "pricing-code-")) {
    		id = StringUtils.removeStart(id, "pricing-code-");
    		Pricing pricing = Pricing.findById(Long.valueOf(id));
    		pricing.code = value;
    		pricing.save();
        	renderText(value);
    	} else {
    		throw new Exception("Exception during pricing code edition");
    	}    	
    }
    
    public static void editPricingTitle(String id, String value) throws Exception {
    	if (StringUtils.startsWith(id, "pricing-title-")) {
    		id = StringUtils.removeStart(id, "pricing-title-");
    		Pricing pricing = Pricing.findById(Long.valueOf(id));
    		pricing.title = value;
    		pricing.save();
        	renderText(value);
    	} else {
    		throw new Exception("Exception during pricing title edition");
    	}    	
    }
    
    public static void editDetail(String id, String value) throws Exception {
    	// Managing data input with "," or "." as decimal separator
    	value = StringUtils.replace(value, ",", ".");
		Double amount = Double.valueOf(value);
		
		// Checking if we are called from a correct element
    	if (StringUtils.startsWith(id, "detail-")) {
    		// id should be formed with detail-${line.id}-${profile.id}
    		String[] splittedId = StringUtils.split(id, "-");
    		if (splittedId.length == 3) {
    			// Extracting ids
    			String lineId = splittedId[1];
    			String profileId = splittedId[2];
    			// Getting entities
    			Line line = Line.findById(Long.valueOf(lineId));
    			Profile profile = Profile.findById(Long.valueOf(profileId));
    			Detail detail = line.getDetailByProfile(profile);
    			// Create Detail if needed
    			if (detail == null) {
    				detail = new Detail(line, profile); 
    			}
    			// Saving new value
    			detail.amount = amount;
    			detail.save();
    			
    			// Formatting the result to render
    			DecimalFormat df = new DecimalFormat("#.##");
    			renderText(df.format(amount));
    		} else {
    			throw new Exception("Exception during pricing detail edition : malformed id");
    		}
    	} else {
    		throw new Exception("Exception during pricing detail edition : malformed id");
    	}
    }
    
    public static void editProfileRate(String id, String value) {
    	if (StringUtils.startsWith(id, "profile-")) {
    		String profileId = StringUtils.removeStart(id, "profile-");
    		Profile profile = Profile.findById(Long.valueOf(profileId));
    		profile.rate = Double.parseDouble(value);
    		profile.save();
        	show(profile.pricing.id);
    	}
    	renderText(value);
    }
}
