package org.jscookie.test.integration.test.utils;

public enum Debug {
	TRUE( true ), FALSE( false );
	private boolean state;
	private Debug( boolean state ) {
		this.state = state;
	}
	public boolean is( boolean state ) {
		return this.state == state;
	}
}
