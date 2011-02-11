package utils.dto;

import java.util.ArrayList;

import models.Detail;
import models.Line;
import models.Pricing;
import models.Profile;
import models.Section;

public class PricingConverter {

	public static PricingDTO convertToDTO(Pricing pricing) {
		PricingDTO pricingDto = new PricingDTO();
		
		pricingDto.id = pricing.id;
		pricingDto.title = pricing.title;
		pricingDto.code = pricing.code;
		pricingDto.price = pricing.getPrice();
		
		pricingDto.profiles = new ArrayList<ProfileDTO>();
		for (Profile profile : pricing.profiles) {
			pricingDto.profiles.add(convertProfileToDTO(profile));
		}
		
		pricingDto.sections = new ArrayList<SectionDTO>();
		for (Section section : pricing.sections) {
			pricingDto.sections.add(convertSectionToDTO(section));
		}
		
		return pricingDto;
	}
	
	public static ProfileDTO convertProfileToDTO(Profile profile) {
		ProfileDTO profileDto = new ProfileDTO();
		
		profileDto.id = profile.id;
		profileDto.position = profile.position;
		profileDto.rate = profile.rate;
		profileDto.title = profile.title;
		profileDto.amount = profile.pricing.getAmountByProfile(profile);
		
		return profileDto;
	}
	
	public static SectionDTO convertSectionToDTO(Section section) {
		SectionDTO sectionDto = new SectionDTO();
		
		sectionDto.id = section.id;
		sectionDto.position = section.position;
		sectionDto.title = section.title;
		sectionDto.price = section.getPrice();
		sectionDto.lines = new ArrayList<LineDTO>();
		for (Line line : section.lines) {
			sectionDto.lines.add(convertLineToDTO(line));
		}
		
		sectionDto.details = new ArrayList<DetailDTO>();
		for (Profile profile : section.pricing.profiles) {
			Double amount = section.getAmountByProfile(profile);
			DetailDTO detailDto = new DetailDTO();
			detailDto.amount = amount;
			detailDto.profileId = profile.id;
			sectionDto.details.add(detailDto);
		}
		
		return sectionDto;
	}
	
	public static LineDTO convertLineToDTO(Line line) {
		LineDTO lineDto = new LineDTO();
		
		lineDto.id = line.id;
		lineDto.position = line.position;
		lineDto.title = line.title;
		lineDto.price = line.getPrice();
		lineDto.details = new ArrayList<DetailDTO>();
		for (Profile profile : line.section.pricing.profiles) {
			Detail detail = line.getDetailByProfile(profile);
			if (detail != null) {
				lineDto.details.add(convertDetailToDTO(detail));
			} else {
				lineDto.details.add(new DetailDTO(profile.id, 0D));
			}
		}
		
		return lineDto;
	}

	public static DetailDTO convertDetailToDTO(Detail detail) {
		DetailDTO detailDto = new DetailDTO();
		detailDto.id = detail.id;
		detailDto.profileId = detail.profile.id;
		detailDto.amount = detail.amount;
		
		return detailDto;
	}
}
