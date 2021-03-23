package com.github.jscookie.javacookie;

public abstract class AttributesDefinition {
	public abstract AttributesDefinition expires( Expiration expiration );
	abstract Expiration expires();
	public abstract AttributesDefinition path( String path );
	abstract String path();
	public abstract AttributesDefinition domain( String domain );
	abstract String domain();
	public abstract AttributesDefinition secure( Boolean secure );
	abstract Boolean secure();
	public abstract AttributesDefinition httpOnly( Boolean httpOnly );
	abstract Boolean httpOnly();
	public abstract AttributesDefinition sameSite( String sameSite );
	abstract String sameSite();
}
