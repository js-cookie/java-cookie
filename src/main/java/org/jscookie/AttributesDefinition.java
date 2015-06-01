package org.jscookie;

public abstract class AttributesDefinition {
	abstract Expiration expires();
	abstract String path();
	abstract String domain();
	abstract Boolean secure();
}
