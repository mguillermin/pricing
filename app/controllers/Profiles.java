package controllers;

import java.util.Map;

import models.Profile;
import play.mvc.Controller;

public class Profiles extends Controller {

	public static void show(Long id) {
		Profile profile = Profile.findById(id);
		Map<Number, Profile> revisions = profile.getRevisions();
		render(profile, revisions);
	}
	
	public static void showRevision(Long id, Number revision) {
		Profile profile = Profile.findByIdAndRevision(id, revision);
		render(profile);
	}
	
}
