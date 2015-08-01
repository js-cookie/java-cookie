package com.github.jscookie.javacookie.test.unit;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.jscookie.javacookie.ConverterException;
import com.github.jscookie.javacookie.ConverterStrategy;
import com.github.jscookie.javacookie.Cookies;
import com.github.jscookie.javacookie.test.unit.utils.BaseTest;

@RunWith( MockitoJUnitRunner.class )
public class CookiesConverterTest extends BaseTest {
	private Cookies cookies;

	@Before
	public void before() {
		cookies = Cookies.initFromServlet( request, response );
	}

	@Test
	public void should_be_able_to_conditionally_decode_a_single_malformed_cookie() {
		Mockito.when( request.getHeader( "cookie" ) ).thenReturn(
			"escaped=%u5317; encoded=%E4%BA%AC"
		);
		Cookies cookies = this.cookies.withConverter(new Cookies.Converter() {
			@Override
			public String convert( String value, String name ) throws ConverterException {
				ScriptEngine javascript = new ScriptEngineManager().getEngineByName( "JavaScript" );
				if ( name.equals( "escaped" ) ) {
					try {
						return javascript.eval( "unescape('" + value + "')" ).toString();
					} catch ( ScriptException e ) {
						throw new ConverterException( e );
					}
				}
				return null;
			}
		});

		String actual = cookies.get( "escaped" );
		String expected = "北";
		Assert.assertEquals( expected, actual );

		actual = cookies.get( "encoded" );
		expected = "京";
		Assert.assertEquals( expected, actual );
	}

	@Test
	public void should_be_able_to_create_a_custom_strategy() {
		this.cookies.withConverter( new CustomConverter() );
	}

	private class CustomConverter implements ConverterStrategy {
		@Override
		public String convert( String value, String name ) throws ConverterException {
			return value;
		}
	}
}
