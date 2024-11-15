/*
 */
package org.yarnandtail.andhow.valuetype;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.api.ParsingException;

/**
 *
 * @author ericeverman
 */
public class IntTypeTest {
	


	@Test
	public void testParseHappyPath() throws ParsingException {
		
		IntType type = IntType.instance();
		
		assertEquals(new Integer(-1234), type.parse("-1234"));
		assertEquals(new Integer(0), type.parse("0"));
		assertNull(type.parse(null));
	}
	
	@Test
	public void testParseDecimalNumber() {
		IntType type = IntType.instance();
		assertFalse(type.isParsable("1234.1234"));

		assertThrows(ParsingException.class, () ->
			type.parse("1234.1234")
		);
	}
	
	@Test
	public void testParseNotANumber() {
		IntType type = IntType.instance();
		assertFalse(type.isParsable("apple"));

		assertThrows(ParsingException.class, () ->
			type.parse("apple")
		);
	}
	
	@Test
	public void testParseEmpty() throws ParsingException {
		IntType type = IntType.instance();
		assertFalse(type.isParsable(""));

		assertThrows(ParsingException.class, () ->
			type.parse("")
		);
	}
	
	@Test
	public void testParseTooBig() {
		IntType type = IntType.instance();
		assertFalse(type.isParsable("9999999999999999999999999999999999999999"));

		assertThrows(ParsingException.class, () ->
			type.parse("9999999999999999999999999999999999999999")
		);
	}
	
	@Test
	public void testParseTooSmall() {
		IntType type = IntType.instance();
		assertFalse(type.isParsable("-9999999999999999999999999999999999999999"));

		assertThrows(ParsingException.class, () ->
			type.parse("-9999999999999999999999999999999999999999")
		);
	}
	
	@Test
	public void testCast() {
		
		IntType type = IntType.instance();
		
		Object o = new Integer(999);
		assertEquals(new Integer(999), type.cast(o));
		assertTrue(type.cast(o) instanceof Integer);
	}
	
}
