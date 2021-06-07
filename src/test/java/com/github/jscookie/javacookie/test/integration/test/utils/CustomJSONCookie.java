package com.github.jscookie.javacookie.test.integration.test.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.jscookie.javacookie.CookieValue;

public class CustomJSONCookie implements CookieValue {
  private String attr1;
  private String attr2;

  @JsonCreator
  public CustomJSONCookie(@JsonProperty("attr1") String attr1, @JsonProperty("attr2") String attr2) {
    this.attr1 = attr1;
    this.attr2 = attr2;
  }

  public String getAttr1() {
    return attr1;
  }

  public String getAttr2() {
    return attr2;
  }
}
