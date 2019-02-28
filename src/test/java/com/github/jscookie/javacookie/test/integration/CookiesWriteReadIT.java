package com.github.jscookie.javacookie.test.integration;

import com.github.jscookie.javacookie.test.integration.test.utils.Debug;
import com.github.jscookie.javacookie.test.unit.utils.IntegrationUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.File;
import java.net.URL;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@RunWith(Arquillian.class)
public class CookiesWriteReadIT {
  private static Debug debug = Debug.FALSE;

  @Deployment
  public static WebArchive createDeployment() {
    WebArchive war = IntegrationUtils.createCommonDeployment()
      .addAsWebInfResource(
        new File("src/test/resources/web.xml"),
        "web.xml"
      );

    System.out.println(" ----- LOGGING THE FILES ADDED TO JBOSS");
    System.out.println(war.toString(true));
    System.out.println(" ----- END OF LOGGING THE FILES ADDED TO JBOSS");

    return war;
  }

  @RunAsClient
  @Test
  public void write_read(@ArquillianResource URL baseURL) {
    WebDriver driver = new FirefoxDriver();
    driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);

    // Set simple cookie
    driver.navigate().to(baseURL + "write?name=c&value=v");
    Set<Cookie> cookies = driver.manage().getCookies();
    Assert.assertEquals(1, cookies.size());
    Assert.assertEquals("c", cookies.iterator().next().getName());
    Assert.assertEquals("v", cookies.iterator().next().getValue());

    // Read cookie
    driver.navigate().to(baseURL + "read?name=c");
    Assert.assertEquals("{\"name\":\"c\",\"value\":\"v\"}", driver.findElement(By.id("json")).getText());

    if (debug.is(false)) {
      driver.quit();
    }
  }

  @RunAsClient
  @Test
  public void write_read_json(@ArquillianResource URL baseURL) {
    WebDriver driver = new FirefoxDriver();
    driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);

    // Set JSON cookie
    driver.navigate().to(baseURL + "write_json?name=c&value=v");
    Set<Cookie> cookies = driver.manage().getCookies();
    Assert.assertEquals(1, cookies.size());
    Assert.assertEquals("c", cookies.iterator().next().getName());
    Assert.assertEquals("{%22attr1%22:%22v%22%2C%22attr2%22:%22v%22}", cookies.iterator().next().getValue());

    // Read cookie
    driver.navigate().to(baseURL + "read_json?name=c");
    Assert.assertEquals("{\"attr1\":\"v\",\"attr2\":\"v\"}", driver.findElement(By.id("json")).getText());

    if (debug.is(false)) {
      driver.quit();
    }
  }

}
