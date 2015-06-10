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
