import org.junit.*;

import java.util.*;
import play.test.*;
import models.*;

public class PricingTest extends UnitTest {

	/**
	 * Test case initialization (Fixtures cleaning)
	 */
    @Before
    public void setup() {
      Fixtures.deleteAll();        
      Fixtures.load("data.yml");
    }

    /**
     * Testing pricing retrieving
     */
    @Test
    public void searchPricing() {
    	// Initial number of pricing
        assertEquals(2, Pricing.count());

        // This one shouldn't exist
        assertNull(Pricing.findByCode("P0"));

        // Finding initial data
        Pricing p1 = Pricing.findByCode("P1");
        assertNotNull(p1);
        Pricing p2 = Pricing.findByCode("P2");
        assertNotNull(p2);
        
        // Modification on a pricing
        p1.code = "CH1";
        p1.save();
        assertNotNull(Pricing.findByCode("CH1"));
        assertEquals(2, Pricing.findAll().size());
        
        // Creating a new pricing
        Pricing p3 = new Pricing();
        p3.code = "P3";
        p3.title = "Pricing 3";
        p3.save();
        assertNotNull(Pricing.findByCode("P3"));
        assertEquals(3, Pricing.findAll().size());
    	
    }

    /**
     * Testing pricing computation
     */
    @Test
    public void computePricing() {
    	Pricing p1 = Pricing.findByCode("P1");
        assertEquals(2, p1.sections.size());
        assertEquals((Double)15D, p1.getAmount());
        assertEquals((Double)7900D, p1.getPrice());
        
        Profile p1cp = p1.getProfileByPosition(1L);
        assertNotNull(p1cp);
        assertEquals("CP", p1cp.title);
        assertEquals((Double)700D, p1cp.rate);
        p1cp.rate = 650D;
        p1cp.save();
        assertEquals((Double)7650D, p1.getPrice());
        
    }
    
    /**
     * Testing manipulation of sections and lines
     */
    @Test
    public void linesOperations() {
    	Pricing p1 = Pricing.findByCode("P1");
    	List<Section> sections = p1.sections;
    	assertEquals(2, sections.size());
    	String titleSection1 = sections.get(0).title;
    	String titleSection2 = sections.get(1).title;
    	
    	// This shouldn't move anything
    	sections.get(0).up();
    	p1.refresh();
    	sections = p1.sections;
    	assertEquals(titleSection1, sections.get(0).title);
    	assertEquals(titleSection2, sections.get(1).title);
    	
    	//TODO: complete this test
    }
    
    /**
     * Testing operations on profiles (retrieving, moving, adding)
     */
    @Test
    public void profilesOperations() {
    	// Verifying data initial loading
    	Pricing p1 = Pricing.findByCode("P1");
    	List<Profile> profiles = p1.profiles;
    	assertEquals(3, profiles.size());
    	assertEquals("CP", profiles.get(0).title);
    	assertEquals("DEV", profiles.get(1).title);
    	assertEquals("INT", profiles.get(2).title);
    	
    	// This operation shouldn't move anything
    	profiles.get(0).up();
    	p1.refresh();
    	profiles = p1.profiles;
    	assertEquals(3, profiles.size());
    	assertEquals("CP", profiles.get(0).title);
    	assertEquals("DEV", profiles.get(1).title);
    	assertEquals("INT", profiles.get(2).title);

    	// Let's move a little bit
    	profiles.get(0).down();
    	p1.refresh();
    	profiles = p1.profiles;
    	assertEquals(3, profiles.size());
    	assertEquals("DEV", profiles.get(0).title);
    	assertEquals("CP", profiles.get(1).title);
    	assertEquals("INT", profiles.get(2).title);
    	
    	// One more time
    	profiles.get(1).down();
    	p1.refresh();
    	profiles = p1.profiles;
    	assertEquals(3, profiles.size());
    	assertEquals("DEV", profiles.get(0).title);
    	assertEquals("INT", profiles.get(1).title);
    	assertEquals("CP", profiles.get(2).title);

    	// This ont shouldn't move anything
    	profiles.get(2).down();
    	p1.refresh();
    	profiles = p1.profiles;
    	assertEquals(3, profiles.size());
    	assertEquals("DEV", profiles.get(0).title);
    	assertEquals("INT", profiles.get(1).title);
    	assertEquals("CP", profiles.get(2).title);

    	// One last move
    	profiles.get(1).up();
    	p1.refresh();
    	profiles = p1.profiles;
    	assertEquals(3, profiles.size());
    	assertEquals("INT", profiles.get(0).title);
    	assertEquals("DEV", profiles.get(1).title);
    	assertEquals("CP", profiles.get(2).title);
    	
    	// New profile must be insterted in last position
    	Profile newProfile = new Profile(p1);
    	newProfile.save();
    	p1.refresh();
    	profiles = p1.profiles;
    	assertEquals(4, profiles.size());
    	assertEquals("INT", profiles.get(0).title);
    	assertEquals("DEV", profiles.get(1).title);
    	assertEquals("CP", profiles.get(2).title);
    }
    
    @Test
    public void pricingTag() {
    	
    }

}
