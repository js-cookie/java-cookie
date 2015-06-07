package org.jscookie.test.integration.encoding;

import java.net.URL;
import java.util.concurrent.TimeUnit;

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
import org.jscookie.test.integration.qunit.QUnitPageObject;
import org.jscookie.test.integration.qunit.QUnitResults;
import org.jscookie.test.integration.test.utils.Debug;
import org.jscookie.test.unit.utils.IntegrationUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.PageFactory;

@RunWith( Arquillian.class )
public class CookiesEncodingIT {
	private static Debug debug = Debug.FALSE;

	@Deployment
	public static Archive<?> createDeployment() {
		GenericArchive qunitFiles = ShrinkWrap.create( GenericArchive.class )
			.as( ExplodedImporter.class )
			.importDirectory( "bower_components/js-cookie/" )
			.as( GenericArchive.class );

		WebArchive war = IntegrationUtils.createCommonDeployment()
			.merge( qunitFiles, "/", Filters.includeAll() );

		System.out.println( " ----- LOGGING THE FILES ADDED TO JBOSS" );
		System.out.println( war.toString( true ) );
		System.out.println( " ----- END OF LOGGING THE FILES ADDED TO JBOSS" );

		return war;
	}

	@RunAsClient
	@Test
	public void read_qunit_test( @ArquillianResource URL baseURL ) {
		WebDriver driver = new FirefoxDriver();
		driver.manage().timeouts().implicitlyWait( 20, TimeUnit.SECONDS );

		EncodingPageObject encoding = PageFactory.initElements( driver, EncodingPageObject.class );
		encoding.navigateTo( baseURL );
		QUnitPageObject qunit = encoding.qunit();
		QUnitResults results = qunit.waitForTests( debug );

		int expectedPasses = results.getTotal();
		int actualPasses = results.getPassed();
		Assert.assertEquals( "should pass all tests", expectedPasses, actualPasses );

		if ( debug.is( false ) ) {
			driver.quit();
		}
	}
}
