package yarnandtail.andhow;

import javax.naming.NamingException;
import java.util.logging.*;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.junit.BeforeClass;

/**
 * All tests using AppConfig must extend this class so they have access to the
 * one and only AppConfig.Reloader, which is a single backdoor to cause the
 * AppConfig to reload.
 * 
 * @author eeverman
 */
public class AndHowTestBase {
	
	public static AndHow.Reloader reloader = AndHow.builder().buildForNonPropduction();

	/**
	 * Simple consistent way to get an empty JNDI context.
	 * 
	 * bind() each variable, then call build().
	 * 
	 * @return
	 * @throws NamingException 
	 */
	public static SimpleNamingContextBuilder getJndi() throws NamingException {
		SimpleNamingContextBuilder builder = SimpleNamingContextBuilder.emptyActivatedContextBuilder();
		return builder;
	}
	
	@BeforeClass
	public static void setupAllTests() {
		//The SimpleNamingContextBuilder uses Commons Logging, which defaults to
		//using Java logging.  It spews a bunch of stuff the console during tests,
		//so this turns that off.
		Logger.getGlobal().setLevel(Level.SEVERE);
		Logger.getLogger(SimpleNamingContextBuilder.class.getCanonicalName()).setLevel(Level.SEVERE);
	}
	
}