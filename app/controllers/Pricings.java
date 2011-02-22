package controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import models.Detail;
import models.Line;
import models.Pricing;
import models.PricingTag;
import models.PricingUpdateChannel;
import models.Profile;
import models.Section;

import org.apache.commons.lang.StringUtils;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.jopendocument.dom.spreadsheet.SpreadSheet;

import com.sun.tools.javac.resources.version;

import play.Logger;
import play.data.validation.Required;
import play.db.jpa.JPA;
import play.mvc.Controller;
import play.mvc.With;
import utils.PricingExporter;
import utils.VersionInfo;
import utils.dto.PricingConverter;
import utils.dto.PricingDTO;

@With(Secure.class)
public class Pricings extends Controller {

	public static void edit(Long id) {
		Pricing pricing = Pricing.findById(id);
		render(pricing);
	}
	/**
	 * Action displaying the pricing
	 * @param id Pricing Id
	 */
    public static void show(Long id, boolean editable) {
    	Pricing pricing = Pricing.findById(id);
    	Map<Number, VersionInfo> revisions = getPricingRevisions(id);
    	render(pricing, revisions, editable);
    }

    public static void showRevision(Long id, Integer revision) {
    	AuditReader ar = AuditReaderFactory.get(JPA.em());
    	Pricing pricing = ar.find(Pricing.class, id, revision);
    	Map<Number, VersionInfo> revisions = getPricingRevisions(id);
    	boolean editable = false;
    	renderTemplate("Pricings/show.html", pricing, revision, revisions, editable);
    }

    public static void showPricingTag(Long id, Long pricingTagId) {
    	PricingTag pricingTag = PricingTag.findById(pricingTagId);
    	Pricing pricing = pricingTag.getHistorizedPricing();
    	boolean editable = false;
    	renderTemplate("Pricings/show.html", pricing, pricingTag, editable);
    }
    
	public static void export(Long id) {
		PricingExporter exporter = new PricingExporter();
		renderBinary(exporter.export(id));
	}
    
    public static void showHistory(Long id) {
    	Pricing pricing = Pricing.findById(id);
    	Map<Number, VersionInfo> revisions = getPricingRevisions(id);
    	render(pricing, revisions);
    }
    
    /**
     * Action deleting a section
     * @param id Section Id to delete
     */
    public static void deleteSection(Long id) {
    	Section section = Section.findById(id);
    	Pricing pricing = section.pricing;
    	section.delete();
    	updatePricing(pricing);
    	edit(pricing.id);
    }

    /**
     * Action deleting a line
     * @param id Line Id to delete
     */
    public static void deleteLine(Long id) {
    	Line line = Line.findById(id);
    	Pricing pricing = line.section.pricing;
    	line.delete();
    	updatePricing(pricing);
    	edit(pricing.id);
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
        	updatePricing(pricing);
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
    	updatePricing(pricing);
    	Application.index();
    }
    
    /**
     * Pull up a section
     * @param sectionId id of the Section
     */
    public static void sectionUp(Long sectionId) {
    	Section section = Section.findById(sectionId);
    	section.up();
    	updatePricing(section.pricing);
    	edit(section.pricing.id);
    }

    /**
     * Push dowb a section
     * @param sectionId id of the Section
     */
    public static void sectionDown(Long sectionId) {
    	Section section = Section.findById(sectionId);
    	section.down();
    	updatePricing(section.pricing);
    	edit(section.pricing.id);
    }

    /**
     * Pull up a line
     * @param lineId id of the Line
     */
    public static void lineUp(Long lineId) {
    	Line line = Line.findById(lineId);
    	line.up();
    	updatePricing(line.section.pricing);
    	edit(line.section.pricing.id);
    }

    /**
     * Push down a line
     * @param lineId id of the Line
     */
    public static void lineDown(Long lineId) {
    	Line line = Line.findById(lineId);
    	line.down();
    	updatePricing(line.section.pricing);
    	edit(line.section.pricing.id);
    }
    
    /**
     * Pull up a profile
     * @param profileId id of the Profile
     */
    public static void profileUp(Long profileId) {
    	Profile profile = Profile.findById(profileId);
    	profile.up();
    	updatePricing(profile.pricing);
    	edit(profile.pricing.id);
    }

    /**
     * Push down a profile
     * @param profileId id of the Profile
     */
    public static void profileDown(Long profileId) {
    	Profile profile = Profile.findById(profileId);
    	profile.down();
    	updatePricing(profile.pricing);
    	edit(profile.pricing.id);
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
    	updatePricing(pricing);
    	show(pricingId, true);
    }
    
    /**
     * Add a section to the pricing
     * @param pricingId id of the pricing
     */
    public static void addSection(Long pricingId) {
    	Pricing pricing = Pricing.findById(pricingId);
    	Section section = new Section(pricing, "Section " + String.valueOf(pricing.sections.size() + 1L));
    	section.save();
    	updatePricing(pricing);
    	boolean editable = true;
    	render(section, pricing, editable);
    }
    
    /**
     * Add a line to a section
     * @param sectionId id of the section
     */
    public static void addLine(Long sectionId) {
    	Section section = Section.findById(sectionId);
    	Pricing pricing = section.pricing;
    	Line line = new Line(section, "Line " + String.valueOf(section.lines.size() +1L));
    	line.save();
    	updatePricing(pricing);
    	boolean editable = true;
    	render(line, pricing, editable);
    }
    
    /**
     * Delete a profile
     * @param id id of the profile
     */
    public static void deleteProfile(Long id) {
    	Profile profile = Profile.findById(id);
    	Pricing pricing = profile.pricing;
    	profile.delete();
    	updatePricing(pricing);
    	edit(pricing.id);
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
        	updatePricing(section.pricing);
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
        	updatePricing(line.section.pricing);
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
        	updatePricing(pricing);
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
        	updatePricing(pricing);
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
    	    	updatePricing(detail.profile.pricing);
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
	    	updatePricing(profile.pricing);
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
        	updatePricing(profile.pricing);
        	renderText(profile.title);
    	}
    	renderText(value);
    }
    
    public static void getPricingJSON(Long id) {
    	Pricing pricing = Pricing.findById(id);
    	PricingDTO pricingDto = PricingConverter.convertToDTO(pricing);
    	renderJSON(pricingDto);
    }

    /**
     * Retrieve all the revisions of a given pricing
     * @param id
     * @return
     */
	private static Map<Number, VersionInfo> getPricingRevisions(Long id) {
		AuditReader ar = AuditReaderFactory.get(JPA.em());
		// We use a TreeMap with a reverse Comparator to get the most recent revisions first
    	Map<Number, VersionInfo> revisions = new TreeMap<Number, VersionInfo>(Collections.reverseOrder());
    	List<Number> revisionNumbers = ar.getRevisions(Pricing.class, id);
    	for (Number revisionNumber : revisionNumbers) {
    		Pricing pricing = ar.find(Pricing.class, id, revisionNumber);
    		VersionInfo versionInfo = new VersionInfo();
    		versionInfo.version = revisionNumber;
    		versionInfo.updatedBy = pricing.updatedBy;
    		versionInfo.updatedAt = pricing.updatedAt;
    		revisions.put(revisionNumber, versionInfo);
		}
		return revisions;
	}

	/**
	 * Update fields updatedAt & updateBy of the pricing
	 * @param pricing
	 */
	protected static void updatePricing(Pricing pricing) {
		pricing.update(new Date(), Security.connected());
		// Notifying the update
		PricingUpdateChannel.get(pricing.id).updatePricing(pricing.id);
	}
}
