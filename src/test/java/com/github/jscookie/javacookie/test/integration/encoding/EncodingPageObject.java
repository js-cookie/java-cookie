package com.github.jscookie.javacookie.test.integration.encoding;

import java.net.URL;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import com.github.jscookie.javacookie.test.integration.qunit.QUnitPageObject;

public class EncodingPageObject {
	private WebDriver driver;
	public EncodingPageObject( WebDriver driver ) {
		this.driver = driver;
	}
	EncodingPageObject navigateTo( URL baseURL ) {
		String query = "integration_baseurl=" + baseURL;
		String url = baseURL + "test/encoding.html";
		driver.navigate().to( url + "?" + query );
		return this;
	}
	QUnitPageObject qunit() {
		return PageFactory.initElements( driver, QUnitPageObject.class );
	}
}
