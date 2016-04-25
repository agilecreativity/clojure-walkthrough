package com.gentest;
public abstract class AbstractJavaClass {
  public AbstractJavaClass(String a, String b) {
    System.out.println("Constructor: a, b");
  }
  public AbstractJavaClass(String a) {
    System.out.println("Constructor: a");
  }
  // The abstract method
  public abstract String getCurrentStatus();
  public string getSecret() {
    return "The Secret";
  }
}
