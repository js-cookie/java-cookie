package org.jscookie.test.integration.qunit;

import java.io.IOException;

import org.jscookie.test.integration.test.utils.Debug;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;

public class QUnitPageObject {
	private WebDriver driver;
	private static int TIMEOUT_IN_SECONDS = 1800;
	private ObjectMapper mapper = new ObjectMapper();
	public QUnitPageObject( WebDriver driver ) {
		this.driver = driver;
	}
	public QUnitResults waitForTests( final Debug debug ) {
		return new WebDriverWait( driver, TIMEOUT_IN_SECONDS )
		.until(new Function<WebDriver, QUnitResults>() {
			@Override
			public QUnitResults apply( WebDriver input ) {
				JavascriptExecutor js;
				if ( driver instanceof JavascriptExecutor ) {
					js = ( JavascriptExecutor )driver;
					String result = ( String )js.executeScript(
						"return window.global_test_results && JSON.stringify(window.global_test_results)"
					);
					System.out.println( "Waiting for 'window.global_test_results': " + result );
					if ( result == null ) {
						return null;
					}
					if ( debug.is( true ) ) {
						return null;
					}
					try {
						return mapper.readValue( result, QUnitResults.class );
					} catch ( IOException cause ) {
						throw new GlobalTestResultException( result, cause );
					}
				}
				return null;
			}
		});
	}
	private class GlobalTestResultException extends IllegalStateException {
		private static final long serialVersionUID = 1;
		private GlobalTestResultException( String result, Throwable cause ) {
			super( "Failed to read 'window.global_test_results': " + result, cause );
		}
	}
}
