package org.jscookie.test.unit;

import java.util.Arrays;

import javax.servlet.http.Cookie;

import org.jscookie.CookieSerializationException;
import org.jscookie.CookieValue;
import org.jscookie.Cookies;
import org.jscookie.test.unit.utils.BaseTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith( MockitoJUnitRunner.class )
public class CookiesJSONWriteTest extends BaseTest {
	private Cookies cookies;

	@Before
	public void before() {
		cookies = Cookies.initFromServlet( request, response );
	}

	@Test
	public void write_int_type() throws CookieSerializationException {
		cookies.set( "c", 1 );

		ArgumentCaptor<Cookie> argument = ArgumentCaptor.forClass( Cookie.class );
		Mockito.verify( response ).addCookie( argument.capture() );

		Cookie actual = argument.getValue();
		Assert.assertEquals( "1", actual.getValue() );
	}

	@Test
	public void write_boolean_type() throws CookieSerializationException {
		cookies.set( "c", true );

		ArgumentCaptor<Cookie> argument = ArgumentCaptor.forClass( Cookie.class );
		Mockito.verify( response ).addCookie( argument.capture() );

		Cookie actual = argument.getValue();
		Assert.assertEquals( "true", actual.getValue() );
	}

	@Test
	public void write_JSON_array_with_string() throws CookieSerializationException {
		cookies.set( "c", Arrays.asList( "v" ) );

		ArgumentCaptor<Cookie> argument = ArgumentCaptor.forClass( Cookie.class );
		Mockito.verify( response ).addCookie( argument.capture() );

		Cookie actual = argument.getValue();
		Assert.assertEquals( "[%22v%22]", actual.getValue() );
	}

	@Test
	public void write_custom_type_with_string_prop() throws CookieSerializationException {
		cookies.set( "c", new CustomTypeString( "v" ) );

		ArgumentCaptor<Cookie> argument = ArgumentCaptor.forClass( Cookie.class );
		Mockito.verify( response ).addCookie( argument.capture() );

		Cookie actual = argument.getValue();
		Assert.assertEquals( "{%22property%22:%22v%22}", actual.getValue() );
	}

	@Test
	public void write_custom_type_with_boolean_prop() throws CookieSerializationException {
		cookies.set( "c", new CustomTypeBoolean( true ) );

		ArgumentCaptor<Cookie> argument = ArgumentCaptor.forClass( Cookie.class );
		Mockito.verify( response ).addCookie( argument.capture() );

		Cookie actual = argument.getValue();
		Assert.assertEquals( "{%22property%22:true}", actual.getValue() );
	}

	@Test
	public void write_custom_type_with_number_prop() throws CookieSerializationException {
		cookies.set( "c", new CustomTypeInteger( 1 ) );

		ArgumentCaptor<Cookie> argument = ArgumentCaptor.forClass( Cookie.class );
		Mockito.verify( response ).addCookie( argument.capture() );

		Cookie actual = argument.getValue();
		Assert.assertEquals( "{%22property%22:1}", actual.getValue() );
	}

	class CustomTypeString implements CookieValue {
		private String property;
		private CustomTypeString( String property ) {
			this.property = property;
		}
		public String getProperty() {
			return property;
		}
	}

	class CustomTypeBoolean implements CookieValue {
		private Boolean property;
		private CustomTypeBoolean( Boolean property ) {
			this.property = property;
		}
		public Boolean getProperty() {
			return property;
		}
	}

	class CustomTypeInteger implements CookieValue {
		private Integer property;
		private CustomTypeInteger( Integer property ) {
			this.property = property;
		}
		public Integer getProperty() {
			return property;
		}
	}
}
