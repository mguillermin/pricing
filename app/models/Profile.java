package models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.Audited;

import play.data.validation.Required;
import play.db.jpa.JPABase;
import play.db.jpa.Model;

@Entity
@Audited
public class Profile extends Model {

	@Required
	@Column(name="profile_position") // position, order,... are reserved keywords
	public Long position;

	public String title;
	
	public Double rate;
	
	@ManyToOne (cascade=CascadeType.PERSIST)
	public Pricing pricing;
		
	@OneToMany (mappedBy="profile", cascade=CascadeType.ALL)
	public Set<Detail> details;
	
	public Profile(Pricing pricing) {
		this.pricing = pricing;
		this.position = this.pricing.profiles.size() + 1L;
	}

    public <T extends JPABase> T delete() {
    	T result = super.delete();
    	this.pricing.recomputeProfilesPositions();
    	return result;
    }
    	
	public void up() {
		Profile previousProfile = pricing.getProfileByPosition(position - 1);
		if (previousProfile != null) {
			previousProfile.position++;
			previousProfile.save();
			position--;
			save();
		}
	}

	public void down() {
		Profile nextProfile = pricing.getProfileByPosition(position + 1);
		if (nextProfile != null) {
			nextProfile.position--;
			nextProfile.save();
			position++;
			save();
		}
	}
	
	public Map<Number, Profile> getRevisions() {
		Map<Number, Profile> revisions = new HashMap<Number, Profile>();
		AuditReader ar = AuditReaderFactory.get(em());
		List<Number> revisionNumbers = ar.getRevisions(this.getClass(), id);
		for (Number revisionNumber : revisionNumbers) {
			Profile profile = ar.find(this.getClass(), id, revisionNumber);
			revisions.put(revisionNumber, profile);
		}
		return revisions;
	}
	
	public static Profile findByIdAndRevision(Long id, Number revision) {
		AuditReader ar = AuditReaderFactory.get(em());
		Profile profile = ar.find(Profile.class, id, revision);
		return profile;
	}
}
