package org.jscookie.test.integration;

import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ExplodedImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith( Arquillian.class )
public class CookiesEncodingIT {
	@Deployment
	public static Archive<?> createDeployment() {
		GenericArchive qunitFiles = ShrinkWrap.create( GenericArchive.class )
				.as( ExplodedImporter.class )
				.importDirectory( "src/main/webapp" )
				.as( GenericArchive.class );

		WebArchive war = ShrinkWrap.create( WebArchive.class )
			.addPackage( "org.jscookie.test.integration" )
			.merge( qunitFiles, "/", Filters.includeAll() );

		System.out.println( war.toString( true ) );

		return war;
	}

	@RunAsClient
	@Test
	public void read_qunit_test( @ArquillianResource URL baseURL ) {
		System.out.println( "Available at: " + baseURL.toString() );
		Assert.assertTrue( true );
	}
}
