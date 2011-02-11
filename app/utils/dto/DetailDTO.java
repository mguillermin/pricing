package utils.dto;

import models.Profile;

public class DetailDTO {

	public DetailDTO() {
		super();
	}
	
	public DetailDTO(Long profileId, double amount) {
		this.profileId = profileId;
		this.amount = amount;
	}

	public Long id;
	
	public Double amount;

	public Long profileId;

}
