package com.github.jscookie.javacookie.test.unit.utils;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

import java.io.File;

public class IntegrationUtils {
  public static WebArchive createCommonDeployment() {
    boolean RECURSIVE_TRUE = true;
    return ShrinkWrap.create(WebArchive.class)
        .addPackage("com.github.jscookie.javacookie")
        .addPackages(RECURSIVE_TRUE, "com.github.jscookie.javacookie.test.integration")
        .addAsLibraries(
            Maven.resolver()
                .loadPomFromFile("pom.xml")
                .resolve(
                    "joda-time:joda-time",
                    "com.fasterxml.jackson.core:jackson-databind"
                )
                .withTransitivity()
                .as(File.class)
        );
  }
}
