# Java Cookie [![Build Status](https://travis-ci.org/js-cookie/java-cookie.svg?branch=master)](https://travis-ci.org/js-cookie/java-cookie)

A simple Java API for handling cookies

* Supports Java 8+, Servlet 2.2+
* [Unobtrusive](#json-data-binding) JSON Data Binding support
* [RFC 6265](http://www.rfc-editor.org/rfc/rfc6265.txt) compliant
* Enable [custom decoding](#converter)

## Installation

Include the maven dependency in your `pom.xml`:

```xml
<dependency>
  <groupId>com.github.js-cookie</groupId>
  <artifactId>java-cookie</artifactId>
  <version>0.0.2</version>
</dependency>
```

If you don't use Maven you can build the artifact from this repository, by installing [git SCM](https://git-scm.com/downloads), [Maven](https://maven.apache.org/download.cgi) and executing the commands below:

```shell
$ git clone https://github.com/js-cookie/java-cookie.git
$ cd java-cookie
$ mvn install -P simple
```

The artifact will be created inside the `java-cookie/target` folder.

## Basic Usage

Create a cookie, valid across the entire site

```java
Cookies cookies = Cookies.initFromServlet( request, response );
cookies.set( "name", "value" );
```

Create a cookie that expires 7 days from now, valid across the entire site:

```java
Cookies cookies = Cookies.initFromServlet( request, response );
cookies.set( "name", "value", Attributes.empty()
  .expires( Expiration.days( 7 ) )
);
```

Create an expiring cookie, valid to the path of the current page:

```java
Cookies cookies = Cookies.initFromServlet( request, response );
cookies.set( "name", "value", Attributes.empty()
  .expires( Expiration.days( 7 ) )
  .path( "" )
);
```

Read cookie:

```java
Cookies cookies = Cookies.initFromServlet( request, response );
cookies.get( "name" ); // => "value"
cookies.get( "nothing" ); // => null
```

Read all available cookies:

```java
Cookies cookies = Cookies.initFromServlet( request, response );
Map<String, String> all = cookies.get(); // => {name=value}
```

Delete cookie:

```java
Cookies cookies = Cookies.initFromServlet( request, response );
cookies.remove( "name" );
```

Delete a cookie valid to the path of the current page:

```java
Cookies cookies = Cookies.initFromServlet( request, response );
cookies.set( "name", "value", Attributes.empty()
  .path( "" )
);
cookies.remove( "name" ); // fail!
cookies.remove( "name", Attributes.empty().path( "path" ) ); // removed!
```

*IMPORTANT! when deleting a cookie, you must pass the exact same path, domain and secure attributes that were used to set the cookie, unless you're relying on the [default attributes](#cookie-attributes).*

## JSON Data Binding

java-cookie provides unobtrusive JSON storage for cookies with data binding.

When creating a cookie, you can pass a few supported types instead of String in the value. If you do so, java-cookie will store the stringified JSON representation of the value using [jackson databind](https://github.com/FasterXML/jackson-databind/#use-it).

Consider the following class that implements the `CookieValue` interface:

```java
public class Person implements CookieValue {
  private int age;
  public Person( int age ) {
    this.age = age;
  }
  public int getAge() {
    return age;
  }
}
```

And the following usage:

```java
Cookies cookies = Cookies.initFromServlet( request, response );
cookies.set( "name", new Person( 25 ) );
```

When reading a cookie with the default `get()` api, you receive the string representation stored in the cookie:

```java
Cookies cookies = Cookies.initFromServlet( request, response );
String value = cookies.get( "name" ); // => "{\"age\":25}"
```

If you pass the type reference, it will parse the JSON into a new instance:

```java
Cookies cookies = Cookies.initFromServlet( request, response );
Person adult = cookies.get( "name", Person.class );
if ( adult != null ) {
  adult.getAge(); // => 25
}
```

## Encoding

This project is [RFC 6265](http://tools.ietf.org/html/rfc6265#section-4.1.1) compliant. All special characters that are not allowed in the cookie-name or cookie-value are encoded with each one's UTF-8 Hex equivalent using [percent-encoding](http://en.wikipedia.org/wiki/Percent-encoding).  
The only character in cookie-name or cookie-value that is allowed and still encoded is the percent `%` character, it is escaped in order to interpret percent input as literal.  
To override the default cookie decoding you need to use a [converter](#converter).

## Cookie Attributes

The default cookie attributes can be set globally by setting properties of the `.defaults()` instance or individually for each call to `.set(...)` by passing an `Attributes` instance in the last argument. Per-call attributes override the default attributes.

```java
Cookies cookies = Cookies.initFromServlet( request, response );
cookies.defaults()
  .secure( true )
  .httpOnly( true );
cookies.set( "name", "value", Attributes.empty()
  .httpOnly( false ) // override defaults
);
```

### expires

Define when the cookie will be removed. Value can be an `Expiration.days()` which will be interpreted as days from time of creation, a `java.util.Date` or an `org.joda.time.DateTime` instance. If omitted, the cookie becomes a session cookie.

**Default:** Cookie is removed when the user closes the browser.

**Examples:**

```java
DateTime date_2015_06_07_23h38m46s = new DateTime( 2015, 6, 7, 23, 38, 46 );
Cookies cookies = Cookies.initFromServlet( request, response );
cookies.set( "name", "value", Attributes.empty()
  .expires( Expiration.date( date_2015_06_07_23h38m46s ) )
);
cookies.get( "name" ); // => "value"
cookies.remove( "name" );
```

### path

Define the path where the cookie is available.

**Default:** `/`

**Examples:**

```java
Cookies cookies = Cookies.initFromServlet( request, response );
Attributes validToTheCurrentPage = Attributes.empty().path( "" );
cookies.set( "name", "value", validToTheCurrentPath );
cookies.get( "name" ); // => "value"
cookies.remove( "name", validToTheCurrentPath );
```

### domain

Define the domain where the cookie is available

**Default:** Domain of the page where the cookie was created

**Examples:**

```java
Cookies cookies = Cookies.initFromServlet( request, response );
cookies.set( "name", "value", Attributes.empty().domain( "sub.domain.com" ) );
cookies.get( "name" ); // => null (need to read at "sub.domain.com")
```

### secure

A `Boolean` indicating if the cookie transmission requires a secure protocol (https)

**Default:** No secure protocol requirement

**Examples:**

```java
Cookies cookies = Cookies.initFromServlet( request, response );
Attributes secureCookie = Attributes.empty().secure( true );
cookies.set( "name", "value", secureCookie );
cookies.get( "name" ); // => "value"
cookies.remove( "name", secureCookie );
```

### httpOnly

A `Boolean` indicating if the cookie should be restricted to be manipulated only in the server.

**Default:** The cookie can be manipulated in the server and in the client

**Examples:**

```java
Cookies cookies = Cookies.initFromServlet( request, response );
Attributes httpOnlyCookie = Attributes.empty().httpOnly( true );
cookies.set( "name", "value", httpOnlyCookie );
cookies.get( "name" ); // => "value"
cookies.remove( "name", httpOnlyCookie );
```

### sameSite

Define whether your cookie should be restricted to a first party or same-site context

**Default:** not set

Note that more recent browsers are making "Lax" the default value even without specifying anything here.

**Examples:**

```java
Cookies cookies = Cookies.initFromServlet( request, response );
cookies.set( "name", "value", Attributes.empty().sameSite( "Lax" ) );
cookies.get( "name" ); // => "value"
```

## Converter

Create a new instance of the api that overrides the default decoding implementation.  
All methods that rely in a proper decoding to work, such as `remove()` and `get()`, will run the converter first for each cookie.  
The returning String will be used as the cookie value.

Example from reading one of the cookies that can only be decoded using the Javascript `escape` function:

``` java
// document.cookie = 'escaped=%u5317';
// document.cookie = 'default=%E5%8C%97';

Cookies cookies = Cookies.initFromServlet( request, response );
Cookies escapedCookies = cookies.withConverter(new Cookies.Converter() {
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

escapedCookies.get( "escaped" ); // => 北
escapedCookies.get( "default" ); // => 北
escapedCookies.get(); // => {escaped=北, default=北}
```

Instead of passing a converter inline, you can also create a custom strategy by implementing the `ConverterStrategy` interface:

```java
class CustomConverter implements ConverterStrategy {
  @Override
  public String convert( String value, String name ) throws ConverterException {
    return value;
  }
}
```

```java
Cookies cookies = Cookies.initFromServlet( request, response );
Cookies cookiesWithCustomConverter = cookies.withConverter( new CustomConverter() );
```

## Contributing

Check out the [Contributing Guidelines](CONTRIBUTING.md).
