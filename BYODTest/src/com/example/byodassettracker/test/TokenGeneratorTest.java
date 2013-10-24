package com.example.byodassettracker.test;

import junit.framework.TestCase;
import com.example.byodassettracker.TokenGenerator;

public class TokenGeneratorTest extends TestCase {
	TokenGenerator generator;
	
	public TokenGeneratorTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	
	public void testTokenGenerated() throws Exception {
		generator = new TokenGenerator();
		String actual = generator.generateToken("CC:FE:3C:3C:15:02","832e90701f904ad9","unknown","7cf2db5ec261a0fa27a502d3196a6f60");
		String expected = "cef59";
		assertEquals(actual,expected);
	}

}
