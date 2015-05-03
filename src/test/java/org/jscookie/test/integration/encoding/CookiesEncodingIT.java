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
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.PageFactory;

import com.gargoylesoftware.htmlunit.BrowserVersion;

@RunWith( Arquillian.class )
public class CookiesEncodingIT {
	private static boolean DEBUG_JS = false;

	@Deployment
	public static Archive<?> createDeployment() {
		boolean RECURSIVE_TRUE = true;

		GenericArchive qunitFiles = ShrinkWrap.create( GenericArchive.class )
			.as( ExplodedImporter.class )
			.importDirectory( "src/main/webapp" )
			.as( GenericArchive.class );

		WebArchive war = ShrinkWrap.create( WebArchive.class )
			.addPackages( RECURSIVE_TRUE, "org.jscookie.test.integration" )
			.merge( qunitFiles, "/", Filters.includeAll() );

		System.out.println( " ----- LOGGING THE FILES ADDED TO JBOSS" );
		System.out.println( war.toString( true ) );
		System.out.println( " ----- END OF LOGGING THE FILES ADDED TO JBOSS" );

		return war;
	}

	@RunAsClient
	@Test
	public void read_qunit_test( @ArquillianResource URL baseURL ) {
		WebDriver driver = createDriver();
		driver.manage().timeouts().implicitlyWait( 20, TimeUnit.SECONDS );

		EncodingPageObject encoding = PageFactory.initElements( driver, EncodingPageObject.class );
		encoding.navigateTo( baseURL );
		QUnitPageObject qunit = encoding.qunit();
		QUnitResults results = qunit.waitForTests();

		int expectedPasses = results.getTotal();
		int actualPasses = results.getPassed();
		Assert.assertEquals( "should pass all tests", expectedPasses, actualPasses );

		dispose( driver );
	}

	private WebDriver createDriver() {
		if ( DEBUG_JS ) {
			return new FirefoxDriver();
		}

		HtmlUnitDriver driver = new HtmlUnitDriver( BrowserVersion.CHROME );
		driver.setJavascriptEnabled( true );

		return driver;
	}

	private void dispose( WebDriver driver ) {
		if ( !DEBUG_JS ) {
			driver.quit();
		}
	}
}
