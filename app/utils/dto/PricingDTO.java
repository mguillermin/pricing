package utils.dto;

import java.util.Date;
import java.util.List;

import play.data.validation.Required;

public class PricingDTO {

	public Long id;
	
	public String code;
	
	public String title;
	
	public Double price; 
	
	public List<SectionDTO> sections;
	
	public List<ProfileDTO> profiles;
}
