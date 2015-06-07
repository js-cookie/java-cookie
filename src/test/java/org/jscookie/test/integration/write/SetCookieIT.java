package org.jscookie.test.integration.write;

import java.io.IOException;
import java.net.URL;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jscookie.test.unit.utils.IntegrationUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith( Arquillian.class )
public class SetCookieIT {
	@Deployment
	public static Archive<?> createDeployment() {
		WebArchive war = IntegrationUtils.createCommonDeployment();

		System.out.println( " ----- LOGGING THE FILES ADDED TO JBOSS" );
		System.out.println( war.toString( true ) );
		System.out.println( " ----- END OF LOGGING THE FILES ADDED TO JBOSS" );

		return war;
	}

	@RunAsClient
	@Test
	public void read_qunit_test( @ArquillianResource URL baseURL ) throws IOException {
		HttpResponse content = request( baseURL + "multiple-set-cookie" );
		Header[] headers = content.getHeaders( "Set-Cookie" );
		Assert.assertEquals( "Should set a single header", 1, headers.length );
		Assert.assertEquals( "Should not repeat the value", "c=v; Path=/", headers[ 0 ].getValue() );
	}

	private HttpResponse request( String url ) throws IOException {
		Request request = Request.Get( url );
		Response response = request.execute();
		HttpResponse httpResponse = response.returnResponse();
		return httpResponse;
	}
}
