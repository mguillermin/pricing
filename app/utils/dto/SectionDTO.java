package utils.dto;

import java.util.List;

public class SectionDTO {

	public Long id;
	
	public Long position;

	public String title;
	
	public Double price;

	public List<LineDTO> lines;
	
	public List<DetailDTO> details;
}
