package com.github.jscookie.javacookie.test.unit;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.jscookie.javacookie.CookieSerializationException;
import com.github.jscookie.javacookie.CookieValue;
import com.github.jscookie.javacookie.Cookies;
import com.github.jscookie.javacookie.test.unit.utils.BaseTest;

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
		Mockito.verify( response ).addHeader( "Set-Cookie", "c=1; Path=/" );
	}

	@Test
	public void write_boolean_type() throws CookieSerializationException {
		cookies.set( "c", true );
		Mockito.verify( response ).addHeader( "Set-Cookie", "c=true; Path=/" );
	}

	@Test
	public void write_JSON_array_with_string() throws CookieSerializationException {
		cookies.set( "c", Arrays.asList( "v" ) );
		Mockito.verify( response ).addHeader( "Set-Cookie", "c=[%22v%22]; Path=/" );
	}

	@Test
	public void write_custom_type_with_string_prop() throws CookieSerializationException {
		cookies.set( "c", new CustomTypeString( "v" ) );
		Mockito.verify( response ).addHeader( "Set-Cookie", "c={%22property%22:%22v%22}; Path=/" );
	}

	@Test
	public void write_custom_type_with_boolean_prop() throws CookieSerializationException {
		cookies.set( "c", new CustomTypeBoolean( true ) );
		Mockito.verify( response ).addHeader( "Set-Cookie", "c={%22property%22:true}; Path=/" );
	}

	@Test
	public void write_custom_type_with_number_prop() throws CookieSerializationException {
		cookies.set( "c", new CustomTypeInteger( 1 ) );
		Mockito.verify( response ).addHeader( "Set-Cookie", "c={%22property%22:1}; Path=/" );
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
