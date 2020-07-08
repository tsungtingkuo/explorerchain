public class Matlab {
  public static void run(String fileName, String testSite) throws Exception {
    Process p = Runtime.getRuntime().exec("./m.sh " +  fileName + " " + testSite);
    p.waitFor();
  }
}
