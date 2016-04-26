package com.examples;
import clojure.lang.RT;
import clojure.lang.Var;
// Note: calling Clojure from Java file
public class Client {
  public static void main(String[] args) throws Exception {
      RT.loadResourceScript("examples.clj");
      Var report = RT.var("clojure.script.examples", "print-report");
      Integer result = (Integer) report.invoke("Siva");
      System.out.println("Result: " + result);
  }
}
