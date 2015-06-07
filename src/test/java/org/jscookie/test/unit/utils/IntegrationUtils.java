package org.jscookie.test.unit.utils;

import java.io.File;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

public class IntegrationUtils {
	public static WebArchive createCommonDeployment() {
		boolean RECURSIVE_TRUE = true;
		return ShrinkWrap.create( WebArchive.class )
			.addPackage( "org.jscookie" )
			.addPackages( RECURSIVE_TRUE, "org.jscookie.test.integration" )
			.addAsLibraries(
				Maven.resolver()
					.loadPomFromFile( "pom.xml" )
					.resolve(
						"joda-time:joda-time",
						"com.fasterxml.jackson.core:jackson-databind"
					)
					.withTransitivity()
					.as( File.class )
			);
	}
}
