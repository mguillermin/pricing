import org.junit.*;

import java.util.*;
import play.test.*;
import models.*;

public class BasicTest extends UnitTest {

    @Before
    public void setup() {
      Fixtures.deleteAll();        
    }

    @Test
    public void fullTest() {
    	Fixtures.load("data.yml");

        assertEquals(2, Pricing.count());

        assertNull(Pricing.findByCode("P0"));

        Pricing ch1 = Pricing.findByCode("P1");
        assertNotNull(ch1);
        
        Pricing ch2 = Pricing.findByCode("P2");
        assertNotNull(ch2);

        assertEquals(2, ch1.sections.size());

        assertEquals((Double)15D, ch1.getAmount());

        assertEquals((Double)7900D, ch1.getPrice());
        
        Profile p1cp = ch1.getProfileByPosition(1L);
        assertNotNull(p1cp);
        assertEquals("CP", p1cp.title);
        assertEquals((Double)700D, p1cp.rate);
        
        p1cp.rate = 650D;
        p1cp.save();
        
        assertEquals((Double)7650D, ch1.getPrice());
    }
}
