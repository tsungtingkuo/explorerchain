import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Chain {

  String chainName = "";

  public static String getNodeAddress(String chainName) throws Exception {
    Chain chain = new Chain(chainName);
    JSONObject jo = chain.cliObject("getinfo");
    return jo.get("nodeaddress").toString();
  }

  public Chain(String chainName) {
    this.chainName = chainName;
  }

  public JSONArray cliArray(String command) throws Exception {
    String result = this.cli(command);
    return (JSONArray) this.cliParse(result);
  }

  public JSONObject cliObject(String command) throws Exception {
    String result = this.cli(command);
    return (JSONObject) this.cliParse(result);
  }

  public Object cliParse(String result) throws Exception {
    StringReader sr = new StringReader(result);
    JSONParser parser = new JSONParser();
    return parser.parse(sr);
  }

  public String cli(String command) throws Exception {
    Process p = Runtime.getRuntime().exec("multichain-cli " + this.chainName + " " + command);
    // p.waitFor();
    BufferedReader buf = new BufferedReader(new InputStreamReader(p.getInputStream()));
    String line = "";
    String output = "";

    // Skip the first 2 lines
    int count = 0;
    while ((line = buf.readLine()) != null) {
      if (count > 1) {
        output += line + "\n";
      }
      count++;
    }

    return output;
  }
}
