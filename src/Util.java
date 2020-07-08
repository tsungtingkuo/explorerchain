import java.io.*;
import java.util.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import utility.*;

public class Util {

  public static void saveIteration(Config pc, String testSite, int iteration) throws Exception {
    int[] iterations = new int[1];
    iterations[0] = iteration;
    Utility.saveIntegerArray(pc.iterationFileName, iterations);
  }

  public static String getFormattedTrainingResult(Config pc) throws Exception {
    return String.format("%.6f", getTrainingResult(pc));
  }

  public static String getFormattedTestingResult(Config pc) throws Exception {
    return String.format("%.6f", getTestingResult(pc));
  }

  public static double getTrainingResult(Config pc) throws Exception {
    return Utility.loadDoubleArray(pc.trainingResultFileName)[0];
  }

  public static double getTestingResult(Config pc) throws Exception {
    return Utility.loadDoubleArray(pc.testingResultFileName)[0];
  }

  public static boolean submitModel(Config pc, Model em) throws Exception {
    String hexData = Model.toObjectHexString(em);
    Chain chain = new Chain(pc.chainName);
    String sendResult = chain.cli("sendwithmetadata " + pc.address + " 0 " + hexData);
    return !"".equalsIgnoreCase(sendResult);
  }

  public static ArrayList<Model> getRecentModels(Config pc) throws Exception {

    ArrayList<Model> models = new ArrayList<Model>();
    Chain chain = new Chain(pc.chainName);

    String listResult = chain.cli("listaddresstransactions " + pc.address + " " + pc.dataNumber);

    // System.out.println(listResult);

    if ("".equalsIgnoreCase(listResult)) {
      System.out.println("Hex data retrieval failed, please check the address.");
    } else {
      JSONArray ja = (JSONArray) chain.cliParse(listResult);
      // System.out.println("Hex data retrieved successfully.");
      // System.out.println();

      for (Object item : ja) {

        Long time = (Long) ((JSONObject) item).get("time");

        JSONArray dataItem = (JSONArray) ((JSONObject) item).get("data");

        if (dataItem.size() > 0) {
          Date dataTime = new Date(time * 1000);
          String hexData = (String) dataItem.get(0);

          if (hexData.length() > pc.minDataSize) {
            Model em = (Model) Model.fromObjectHexString(hexData);
            if (em != null) {
              em.setTime(dataTime);
              models.add(em);
            }
          }
        }
      }
    }

    return models;
  }
}
