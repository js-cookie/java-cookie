package org.jscookie;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jdt.annotation.Nullable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class Cookies implements CookiesDefinition {
	private HttpServletRequest request;
	private HttpServletResponse response;
	private AttributesDefinition defaults = Attributes.empty().path( "/" );
	private ConverterStrategy converter;
	private ObjectMapper mapper = new ObjectMapper();

	private static final String LSTRING_FILE = "javax.servlet.http.LocalStrings";
	private static ResourceBundle lStrings = ResourceBundle.getBundle( LSTRING_FILE );

	private Cookies( HttpServletRequest request, HttpServletResponse response, ConverterStrategy converter ) {
		this( request, response );
		this.converter = converter;
	}

	public Cookies( HttpServletRequest request, HttpServletResponse response ) {
		this.request = request;
		this.response = response;
	}

	@Override
	public synchronized String get( String name ) {
		if ( name == null || name.length() == 0 ) {
			throw new IllegalArgumentException( lStrings.getString( "err.cookie_name_blank" ) );
		}

		Cookie[] cookies = request.getCookies();
		if ( cookies == null ) {
			return null;
		}

		for ( Cookie cookie : cookies ) {
			String decodedName = decode( cookie.getName() );

			if ( !name.equals( decodedName ) ) {
				continue;
			}
			
			return decodeValue( cookie, decodedName );
		}

		return null;
	}

	@Override
	public <T> T get( String name, Class<T> dataType ) throws CookieParseException {
		String value = get( name );
		try {
			return mapper.readValue( value, dataType );
		} catch ( IOException e ) {
			throw new CookieParseException( e );
		}
	}

	@Override
	public <T> T get( String name, TypeReference<T> typeRef ) throws CookieParseException {
		String value = get( name );
		try {
			return mapper.readValue( value, typeRef );
		} catch ( IOException e ) {
			throw new CookieParseException( e );
		}
	}

	@Override
	public Map<String, String> get() {
		Map<String, String> result = new HashMap<String, String>();

		Cookie[] cookies = request.getCookies();
		if ( cookies == null ) {
			return null;
		}

		for ( Cookie cookie : cookies ) {
			String decodedName = decode( cookie.getName() );
			String decodedValue = decodeValue( cookie, decodedName );
			result.put( decodedName, decodedValue );
		}

		return result;
	}

	@Override
	public synchronized void set( String name, String value, AttributesDefinition attributes ) {
		if ( name == null || name.length() == 0 ) {
			throw new IllegalArgumentException( lStrings.getString( "err.cookie_name_blank" ) );
		}
		if ( value == null ) {
			throw new IllegalArgumentException();
		}
		if ( attributes == null ) {
			throw new IllegalArgumentException();
		}

		String encodedName = encode( name );
		String encodedValue = encode( value );

		Cookie cookie = new Cookie( encodedName, encodedValue );
		attributes = extend( defaults, attributes );

		Expiration expires = attributes.expires();
		if ( expires != null ) {
			cookie.setMaxAge( expires.toSecondsFromNow() );
		}

		String path = attributes.path();
		if ( path != null ) {
			cookie.setPath( path );
		}

		String domain = attributes.domain();
		if ( domain != null ) {
			cookie.setDomain( domain );
		}

		Boolean secure = attributes.secure();
		if ( secure != null ) {
			cookie.setSecure( secure );
		}

		response.addCookie( cookie );
	}

	@Override
	public void set( String name, int value, AttributesDefinition attributes ) throws CookieSerializationException {
		set( name, String.valueOf( value ), attributes );
	}

	@Override
	public void set( String name, boolean value, AttributesDefinition attributes ) throws CookieSerializationException {
		set( name, String.valueOf( value ), attributes );
	}

	@Override
	public <T> void set( String name, List<T> value, AttributesDefinition attributes ) throws CookieSerializationException {
		try {
			set( name, mapper.writeValueAsString( value ), attributes );
		} catch ( JsonProcessingException e ) {
			throw new CookieSerializationException( e );
		}
	}

	@Override
	public void set( String name, CookieValue value, AttributesDefinition attributes ) throws CookieSerializationException {
		try {
			set( name, mapper.writeValueAsString( value ), attributes );
		} catch ( JsonProcessingException e ) {
			throw new CookieSerializationException( e );
		}
	}

	@Override
	public void set( String name, String value ) {
		if ( name == null || name.length() == 0 ) {
			throw new IllegalArgumentException( lStrings.getString( "err.cookie_name_blank" ) );
		}
		if ( value == null ) {
			throw new IllegalArgumentException();
		}
		set( name, value, defaults );
	}

	@Override
	public void set( String name, int value ) throws CookieSerializationException {
		set( name, value, Attributes.empty() );
	}

	@Override
	public void set( String name, boolean value ) {
		set( name, String.valueOf( value ) );
	}

	@Override
	public <T> void set( String name, List<T> value ) throws CookieSerializationException {
		set( name, value, Attributes.empty() );
	}

	@Override
	public void set( String name, CookieValue value ) throws CookieSerializationException {
		set( name, value, Attributes.empty() );
	}

	@Override
	public void remove( String name, AttributesDefinition attributes ) {
		if ( name == null || name.length() == 0 ) {
			throw new IllegalArgumentException( lStrings.getString( "err.cookie_name_blank" ) );
		}
		if ( attributes == null ) {
			throw new IllegalArgumentException();
		}

		set( name, "", extend( attributes, Attributes.empty()
			.expires( Expiration.days( -1 ) ))
		);
	}

	@Override
	public void remove( String name ) {
		if ( name == null || name.length() == 0 ) {
			throw new IllegalArgumentException( lStrings.getString( "err.cookie_name_blank" ) );
		}
		remove( name, Attributes.empty() );
	}

	@Override
	public void setDefaults( AttributesDefinition defaults ) {
		if ( defaults == null ) {
			throw new IllegalArgumentException();
		}
		this.defaults = defaults;
	}

	@Override
	public Cookies withConverter( ConverterStrategy converter ) {
		return new Cookies( request, response, converter );
	}

	private Attributes extend( AttributesDefinition a, AttributesDefinition b ) {
		return Attributes.empty().merge( a ).merge( b );
	}

	private String encode( String plain ) {
		String encoded = plain;
		for ( int i = 0; i < plain.length(); i++ ) {
			Character character = plain.charAt( i );

			boolean isDigit = Character.isDigit( character );
			if ( isDigit ) {
				continue;
			}

			boolean isAsciiUppercaseLetter = character >= 'A' && character <= 'Z';
			if ( isAsciiUppercaseLetter ) {
				continue;
			}

			boolean isAsciiLowercaseLetter = character >= 'a' && character <= 'z';
			if ( isAsciiLowercaseLetter ) {
				continue;
			}

			boolean isAllowed =
					character == '!' || character == '#'  || character == '$' ||
					character == '&' || character == '\'' || character == '*' ||
					character == '+' || character == '-'  || character == '.' ||
					character == '^' || character == '_'  || character == '`' ||
					character == '|' || character == '~';
			if ( isAllowed ) {
				continue;
			}

			try {
				CharArrayWriter hexSequence = new CharArrayWriter();
				byte[] bytes = character.toString().getBytes( StandardCharsets.UTF_8.name() );
				for ( int bytesIndex = 0; bytesIndex < bytes.length; bytesIndex++ ) {
					char left = Character.forDigit( bytes[ bytesIndex ] >> 4 & 0xF, 16 );
					char right = Character.forDigit( bytes[ bytesIndex ] & 0xF, 16 );
					hexSequence
						.append( '%' )
						.append( left )
						.append( right );
				}
				String target = character.toString();
				String sequence = hexSequence.toString().toUpperCase();
				encoded = encoded.replace( target, sequence );
			} catch ( UnsupportedEncodingException e ) {
				e.printStackTrace();
			}
		}
		return encoded;
	}

	private String decode( String encoded ) {
		String decoded = encoded;
		Pattern pattern = Pattern.compile( "(%[0-9A-Z]{2})+" );
		Matcher matcher = pattern.matcher( encoded );
		if ( !matcher.matches() ) {
			return decoded;
		}
		for ( int groupIndex = 0; groupIndex < matcher.groupCount(); groupIndex++ ) {
			String encodedChar = matcher.group( groupIndex );
			String[] encodedBytes = encodedChar.split( "%" );
			byte[] bytes = new byte[ encodedBytes.length - 1 ];
			for ( int i = 1; i < encodedBytes.length; i++ ) {
				String encodedByte = encodedBytes[ i ];
				bytes[ i - 1 ] = ( byte )Integer.parseInt( encodedByte, 16 );
			}
			try {
				String decodedChar = new String( bytes, StandardCharsets.UTF_8.toString() );
				decoded = decoded.replace( encodedChar, decodedChar );
			} catch ( UnsupportedEncodingException e ) {
				e.printStackTrace();
			}
		}
		return decoded;
	}

	private String decodeValue( Cookie cookie, String decodedName ) {
		String decodedValue = null;

		if ( converter != null ) {
			try {
				decodedValue = converter.convert( cookie.getValue(), decodedName );
			} catch ( ConverterException e ) {
				e.printStackTrace();
			}
		}

		if ( decodedValue == null ) {
			decodedValue = decode( cookie.getValue() );
		}

		return decodedValue;
	}

	public static class Attributes extends AttributesDefinition {
		private Expiration expires;
		private String path;
		private String domain;
		private boolean secure;

		private Attributes() {}

		public static Attributes empty() {
			return new Attributes();
		}

		@Override
		@Nullable
		Expiration expires() {
			return expires;
		}
		public Attributes expires( @Nullable Expiration expires ) {
			this.expires = expires;
			return this;
		}

		@Override
		@Nullable
		String path() {
			return path;
		}
		public Attributes path( @Nullable String path ) {
			this.path = path;
			return this;
		}

		@Override
		@Nullable
		String domain() {
			return domain;
		}
		public Attributes domain( @Nullable String domain ) {
			this.domain = domain;
			return this;
		}

		@Override
		boolean secure() {
			return secure;
		}
		public Attributes secure( boolean secure ) {
			this.secure = secure;
			return this;
		}

		private Attributes merge( AttributesDefinition reference ) {
			if ( reference.path() != null ) {
				path = reference.path();
			}
			if ( reference.domain() != null ) {
				domain = reference.domain();
			}
			if ( reference.expires() != null ) {
				expires = reference.expires();
			}
			secure = reference.secure();
			return this;
		}
	}

	public static abstract class Converter implements ConverterStrategy {}
}
