package org.yarnandtail.andhow;

import java.util.Properties;
import javax.naming.NamingException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;

/**
 * A test base class that COMPLETELY kills AndHow between each test and test classes.
 * <p>
 * This util class is intentionally placed in the test directory because it is
 * never intended to be distributed, not even for use by others in their testing.
 * <p>
 * <em>THIS CLASS DOES DANGEROUS THINGS THAT SHOULD NEVER BE DONE NEAR APPLICATION CODE</em>
 * <p>
 * For utilities to test your application code with AndHow, see the AndHow
 * Test Harness module.  For examples using the test harness, see the AndHow
 * Sample Usages module and its many sub-modules, which are stand-alone simulated
 * projects complete with testing.
 * 
 * 
 * @author ericeverman
 */
public class AndHowCoreTestBase {
	
	/**
	 * System properties before the tests and subclass @BeforeClass initializer
	 * are run.  Properties prior to the initialization of this test class are
	 * stored and then reinstated after the test class is complete.
	 */
	private static Properties beforeClassSystemProps;
	
	/**
	 * System properties before an individual test is run and the subclass @Before
	 * initializers are run.  Properties prior to a test are
	 * stored and then reinstated after the test is complete.  If a Test classes
	 * uses a @BeforeClass initializer that sets system properties, this will
	 * reinstate to that state.
	 */
	private Properties beforeTestSystemProps;
	
	
	/**
	 * Builder for a temporary JNDI context
	 */
	private static SimpleNamingContextBuilder builder;
	
	@BeforeAll
	public static void killAndHowStateBeforeClass() {
		AndHowCoreTestUtil.destroyAndHow();
	}
	
	@BeforeAll
	public static void storeSysPropsBeforeClass() {
		beforeClassSystemProps = AndHowCoreTestUtil.clone(System.getProperties());
	}
	
	@BeforeEach
	public void killAndHowStateBeforeTest() {
		AndHowCoreTestUtil.destroyAndHow();
	}
	
	@BeforeEach
	public void storeSysPropsBeforeTest() {
		beforeTestSystemProps = AndHowCoreTestUtil.clone(System.getProperties());
	}
	
	@AfterEach
	public void killAndHowStateAfterTest() {
		AndHowCoreTestUtil.destroyAndHow();
	}
	
	@AfterEach
	public void clearJNDIAfterTest() {
		if (builder != null) {
			builder.clear();
		}
	}
	
	@AfterEach
	public void restoreSysPropsAfterTest() {
		System.setProperties(beforeTestSystemProps);
	}
	
	
	@AfterAll
	public static void killAndHowStateAfterClass() {
		AndHowCoreTestUtil.destroyAndHow();
	}
	
	@AfterAll
	public static void restoreSysPropsAfterClass() {
		System.setProperties(beforeClassSystemProps);
	}
	
	/**
	 * Simple consistent way to get an empty JNDI context.
	 * 
	 * bind() each variable, then call build().
	 * 
	 * @return
	 * @throws NamingException 
	 */
	public SimpleNamingContextBuilder getJndi() throws NamingException {
		if (builder == null) {
			builder = SimpleNamingContextBuilder.emptyActivatedContextBuilder();
		}
		return builder;
	}
	
}
