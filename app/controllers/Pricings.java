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

	/**
	 * Action displaying the pricing
	 * @param id Pricing Id
	 */
    public static void show(Long id) {
    	Pricing pricing = Pricing.findById(id);
    	render(pricing);
    }

    /**
     * Action deleting a section
     * @param id Section Id to delete
     */
    public static void deleteSection(Long id) {
    	Section section = Section.findById(id);
    	Pricing pricing = section.pricing;
    	section.delete();
    	show(pricing.id);
    }

    /**
     * Action deleting a line
     * @param id Line Id to delete
     */
    public static void deleteLine(Long id) {
    	Line line = Line.findById(id);
    	Pricing pricing = line.section.pricing;
    	line.delete();
    	show(pricing.id);
    }
    
    /**
     * Displays the pricing editing form
     * @param pricingId Pricing Id
     */
    public static void editPricing(Long pricingId) {
    	if (pricingId != null) {
    		Pricing pricing = Pricing.findById(pricingId);
    		params.put("code", pricing.code);
    		params.put("title", pricing.title);
    	}
    	render();
    }
    
    /**
     * Save the edited pricing
     * @param pricingId Pricing Id
     * @param code new code value
     * @param title new title value
     */
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
    
    /**
     * Pull up a section
     * @param sectionId id of the Section
     */
    public static void sectionUp(Long sectionId) {
    	Section section = Section.findById(sectionId);
    	section.up();
    	show(section.pricing.id);
    }

    /**
     * Push dowb a section
     * @param sectionId id of the Section
     */
    public static void sectionDown(Long sectionId) {
    	Section section = Section.findById(sectionId);
    	section.down();
    	show(section.pricing.id);
    }

    /**
     * Pull up a line
     * @param lineId id of the Line
     */
    public static void lineUp(Long lineId) {
    	Line line = Line.findById(lineId);
    	line.up();
    	show(line.section.pricing.id);
    }

    /**
     * Push down a line
     * @param lineId id of the Section
     */
    public static void lineDown(Long lineId) {
    	Line line = Line.findById(lineId);
    	line.down();
    	show(line.section.pricing.id);
    }
    
    /**
     * Add a profile to the pricing
     * @param pricingId id of the pricing
     */
    public static void addProfile(Long pricingId) {
    	Pricing pricing = Pricing.findById(pricingId);
    	Profile profile = new Profile(pricing);
    	profile.title = "P" + (pricing.profiles.size() + 1L);
    	profile.rate = 0D;
    	profile.save();
    	show(pricingId);
    }
    
    /**
     * Add a section to the pricing
     * @param pricingId id of the pricing
     */
    public static void addSection(Long pricingId) {
    	Pricing pricing = Pricing.findById(pricingId);
    	Section section = new Section(pricing, "Section " + String.valueOf(pricing.sections.size() + 1L));
    	section.save();
    	show(pricingId);
    }
    
    /**
     * Add a line to a section
     * @param sectionId id of the section
     */
    public static void addLine(Long sectionId) {
    	Section section = Section.findById(sectionId);
    	Line line = new Line(section, "Line " + String.valueOf(section.lines.size() +1L));
    	line.save();
    	show(section.pricing.id);
    }
    
    /**
     * Delete a profile
     * @param id id of the profile
     */
    public static void deleteProfile(Long id) {
    	Profile profile = Profile.findById(id);
    	Pricing pricing = profile.pricing;
    	profile.delete();
    	show(pricing.id);
    }
    
    /**
     * Edit the title of a section (from edit inplace)
     * @param id id of the HTML element that launched the request
     * @param value new value
     * @throws Exception
     */
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

    
    /**
     * Edit the title of a line (from edit inplace)
     * @param id id of the HTML element that launched the request
     * @param value new value
     * @throws Exception
     */
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
    
    
    /**
     * Edit the code of a pricing (from edit inplace)
     * @param id id of the HTML element that launched the request
     * @param value new value
     * @throws Exception
     */
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
    
    
    /**
     * Edit the title of a pricing (from edit inplace)
     * @param id id of the HTML element that launched the request
     * @param value new value
     * @throws Exception
     */
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
    
    
    /**
     * Edit the value of a detail (from edit inplace)
     * @param id id of the HTML element that launched the request
     * @param value new value
     * @throws Exception
     */
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
    
    
    /**
     * Edit the rate of a profile (from edit inplace)
     * @param id id of the HTML element that launched the request
     * @param value new value
     * @throws Exception
     */
    public static void editProfileRate(String id, String value) {
    	if (StringUtils.startsWith(id, "profile-rate-")) {
    		String profileId = StringUtils.removeStart(id, "profile-rate-");
    		Profile profile = Profile.findById(Long.valueOf(profileId));
    		profile.rate = Double.parseDouble(value);
    		profile.save();
    		// Formatting the result to render
			DecimalFormat df = new DecimalFormat("#.##");
			renderText(df.format(profile.rate));
    	}
    	renderText(value);
    }

    
    /**
     * Edit the title of a profile (from edit inplace)
     * @param id id of the HTML element that launched the request
     * @param value new value
     * @throws Exception
     */
    public static void editProfileTitle(String id, String value) {
    	if (StringUtils.startsWith(id, "profile-title-")) {
    		String profileId = StringUtils.removeStart(id, "profile-title-");
    		Profile profile = Profile.findById(Long.valueOf(profileId));
    		profile.title = value;
    		profile.save();
        	renderText(profile.title);
    	}
    	renderText(value);
    }

}
