import models.Pricing;
import models.User;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.test.Fixtures;

@OnApplicationStart
public class Bootstrap extends Job {
	public void doJob() {
		if (Pricing.count() == 0) {
			Fixtures.load("initial-data.yml");
		}
		if (User.count() == 0) {
			Fixtures.load("initial-user-data.yml");
		}
	}

}
