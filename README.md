# Java Cookie

A simple Java API for handling cookies

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

## JSON and Data Binding

java-cookie provides unobstrusive JSON storage for cookies and data binding.

When creating a cookie, you can pass an Object instead of String in the value. If you do so, java-cookie will store the stringified JSON representation of the value using [jackson databind](https://github.com/FasterXML/jackson-databind/#use-it).

Consider the following class:

```java
public class Person {
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

the default cookie attributes can be set globally by setting properties of the `.defaults()` instance or individually for each call to `.set(...)` by passing an `Attributes` instance in the last argument. Per-call attributes override the default attributes.

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
cookies.set( "name", "value", Attributes.empty()
  .expires( Expiration.date( date_2015_06_07_23h38m46s ) )
);
cookies.get( "name" ); // => "value"
cookies.remove( "name" );
```
