import java.io.*;
import java.util.*;
import utility.*;

public class ExplorerChain {

  public static void main(String[] args) throws Exception {

    String datasetName = args[0];
    String testSite = args[1];
    String mode = args[2];

    // Change and reload configuration
    Config pc = new Config(testSite, true);
    switch(mode) {

    case "test":
      pc.change(datasetName, testSite, "true");
      break;

    case "init":

    case "new":
      pc.change(datasetName, testSite, "false");
      break;

    }
    pc = new Config(testSite);

    switch(mode) {

    case "new":
      // Run Proof-of-Information (New)
      PoiNew.update(testSite);
      break;

    case "init":
      // Run Proof-of-Information (Init)
      PoiInit.model(testSite);
      break;

    case "test":
      // Run Proof-of-Information (Init)
      PoiInit.model(testSite);

      // Compute average error
      System.out.print(
        "Waiting for errors from all sites, polling every "
          + (pc.pollingTimePeriod / 1000)
          + " seconds ... ");
      double averageError = -1;
      while (true) {
        averageError = computeAverageConsensusError(pc);
        if (averageError != -1) {
          break;
        }
        Thread.sleep(pc.pollingTimePeriod);
      }
      String iteration = Utility.loadStringArray(pc.iterationFileName)[0];
      System.out.println("Done, average test result among sites = " + (1 - averageError) + ", iteration = " + iteration);
      System.out.println();
      break;
    }

    pc.clear();
  }

  public static double computeAverageConsensusError(Config pc) throws Exception {
    HashSet<String> completedSites = new HashSet<String>();
    ArrayList<Model> models = Util.getRecentModels(pc);

    for (Model em : models) {
      if (em.getFlag() == Flag.TEST) {
        completedSites.add(em.getFromSite());
      }
    }

    double sumError = 0.0d;

    if (completedSites.size() >= pc.totalSiteNumber) {
      System.out.println();
      for (Model em : models) {
        if (em.getFlag() == Flag.TEST) {
          sumError += em.getResult();
        }
      }
      double averageError = sumError / (double) pc.totalSiteNumber;
      return averageError;
    }
    return -1;
  }
}
