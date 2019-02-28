package com.github.jscookie.javacookie.test.unit;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.jscookie.javacookie.CookieParseException;
import com.github.jscookie.javacookie.Cookies;
import com.github.jscookie.javacookie.test.unit.utils.BaseTest;

@RunWith( MockitoJUnitRunner.class )
public class CookiesJSONReadTest extends BaseTest {
	private Cookies cookies;

	@Before
	public void before() {
		cookies = Cookies.initFromServlet( request, response );
	}

	@Test
	public void read_int_type() throws CookieParseException {
		Mockito.when( request.getHeader( "cookie" ) ).thenReturn( "c=1" );

		String actual = cookies.get( "c" );
		String expected = "1";
		Assert.assertEquals( expected, actual );

		int actual2 = cookies.get( "c", Integer.class );
		int expected2 = 1;
		Assert.assertEquals( expected2, actual2 );
	}

	@Test
	public void read_boolean_type() throws CookieParseException {
		Mockito.when( request.getHeader( "cookie" ) ).thenReturn( "c=true" );

		String actual = cookies.get( "c" );
		String expected = "true";
		Assert.assertEquals( expected, actual );

		boolean actual2 = cookies.get( "c", Boolean.class );
		boolean expected2 = true;
		Assert.assertEquals( expected2, actual2 );
	}

	@Test
	public void read_JSON_array_with_string() throws CookieParseException {
		Mockito.when( request.getHeader( "cookie" ) ).thenReturn( "c=[%22v%22]" );

		String actual = cookies.get( "c" );
		String expected = "[\"v\"]";
		Assert.assertEquals( expected, actual );

		String actual2 = cookies.get( "c", new TypeReference<List<String>>() {} ).get( 0 );
		String expected2 = "v";
		Assert.assertEquals( expected2, actual2 );
	}

	@Test
	public void read_custom_type_with_string_prop() throws CookieParseException {
		Mockito.when( request.getHeader( "cookie" ) ).thenReturn( "c={%22property%22:%22v%22}" );

		String actual = cookies.get( "c" );
		String expected = "{\"property\":\"v\"}";
		Assert.assertEquals( expected, actual );

		String actual2 = cookies.get( "c", CustomTypeString.class ).getProperty();
		String expected2 = "v";
		Assert.assertEquals( expected2, actual2 );
	}

	@Test
	public void read_custom_type_with_boolean_prop() throws CookieParseException {
		Mockito.when( request.getHeader( "cookie" ) ).thenReturn( "c={%22property%22:true}" );

		String actual = cookies.get( "c" );
		String expected = "{\"property\":true}";
		Assert.assertEquals( expected, actual );

		Boolean actual2 = cookies.get( "c", CustomTypeBoolean.class ).getProperty();
		Boolean expected2 = true;
		Assert.assertEquals( expected2, actual2 );
	}

	@Test
	public void read_custom_type_with_number_prop() throws CookieParseException {
		Mockito.when( request.getHeader( "cookie" ) ).thenReturn( "c={%22property%22:1}" );

		String actual = cookies.get( "c" );
		String expected = "{\"property\":1}";
		Assert.assertEquals( expected, actual );

		Integer actual2 = cookies.get( "c", CustomTypeInteger.class ).getProperty();
		Integer expected2 = 1;
		Assert.assertEquals( expected2, actual2 );
	}

	@Test
	public void read_missing_cookie() throws CookieParseException {
		Mockito.when( request.getHeader( "cookie" ) ).thenReturn( null );

		Assert.assertNull( cookies.get( "c" ) );
		Assert.assertNull( cookies.get( "c", CustomTypeInteger.class ) );
		Assert.assertNull( cookies.get( "c", CustomTypeBoolean.class ) );
		Assert.assertNull( cookies.get( "c", CustomTypeString.class ) );
		Assert.assertNull( cookies.get( "c", new TypeReference<List<String>>() {} ) );
	}

	private static class CustomTypeString {
		private String property;
		@JsonProperty( "property" )
		private String getProperty() {
			return property;
		}
	}

	private static class CustomTypeBoolean {
		private Boolean property;
		@JsonProperty( "property" )
		private Boolean getProperty() {
			return property;
		}
	}

	private static class CustomTypeInteger {
		private Integer property;
		@JsonProperty( "property" )
		private Integer getProperty() {
			return property;
		}
	}
}
