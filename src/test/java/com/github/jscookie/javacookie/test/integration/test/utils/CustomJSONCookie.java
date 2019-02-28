package com.github.jscookie.javacookie.test.integration.test.utils;

import com.github.jscookie.javacookie.CookieValue;

public class CustomJSONCookie implements CookieValue {
  private String attr1;
  private String attr2;

  public CustomJSONCookie(String attr1, String attr2) {
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
