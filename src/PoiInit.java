import java.io.*;
import java.util.*;
import utility.*;

public class PoiInit {

  public static void model(String testSite) throws Exception {

    Config pc = new Config(testSite);
    Log.reset(pc.logFileName);

    System.out.println();
    System.out.println("Proof-of-Information Init: " + pc.siteName + " (" + pc.nodeAddress + ")");

    System.out.println();
    System.out.print("Submitting empty model to blockchain for clearing previous results ... ");
    Model em =
        new Model(
            pc.modelMeanFileName,
            pc.modelVarianceFileName,
            pc.testingResultFileName,
            Flag.CLEAR,
            pc.siteName,
            pc.siteName,
            null,
            0);
    if (Util.submitModel(pc, em)) {
      System.out.println("done.");
    } else {
      System.out.println("failed.");
    }

    System.out.print(
        "Waiting for result clearing from all sites, polling every "
            + (pc.pollingTimePeriod / 1000)
            + " seconds ... ");
    //System.out.println();
    boolean cleared = false;
    while (true) {
      cleared = isResultsCleared(pc);
      if (cleared) {
        break;
      }
      Thread.sleep(pc.pollingTimePeriod);
    }
    //System.out.println();
    System.out.println("done.");

    System.out.print(
        "Waiting "
            + (pc.waitingTimePeriod / 1000)
            + " seconds to confirm clearance of previous results for other sites ... ");
    Thread.sleep(pc.waitingTimePeriod);
    System.out.println("done.");

    System.out.println();
    System.out.print("Initializing model ... ");
    Matlab.run("ExplorerInit", testSite);
    System.out.println(
        "done, local error: training = "
            + Util.getTrainingResult(pc)
            + ", testing = "
            + Util.getTestingResult(pc)
            + ".");
    Log.appendResult(pc, "Initialize");

    // Stop with initialization only
    if (pc.maxIteration < 1) {

      System.out.print(
          "Waiting "
              + (pc.waitingTimePeriod / 1000)
              + " seconds to compute model for other sites ... ");
      Thread.sleep(pc.waitingTimePeriod);
      System.out.println("done.");

      System.out.println();
      System.out.print("Submitting individual test error to blockchain for result collection ... ");
      em =
          new Model(
              pc.modelMeanFileName,
              pc.modelVarianceFileName,
              pc.testingResultFileName,
              Flag.TEST,
              pc.siteName,
              pc.siteName,
              null,
              0);
      if (Util.submitModel(pc, em)) {
        System.out.println("done.");
      } else {
        System.out.println("failed.");
      }

      System.out.println();
      System.out.println("Max iteration = 0, stopped.");
      return;
    }

    System.out.print("Submitting model to blockchain for initialization ... ");
    em =
        new Model(
            pc.modelMeanFileName,
            pc.modelVarianceFileName,
            pc.trainingResultFileName,
            Flag.INITIALIZE,
            pc.siteName,
            pc.siteName,
            null,
            0);
    if (Util.submitModel(pc, em)) {
      System.out.println("done.");
    } else {
      System.out.println("failed.");
    }

    System.out.print(
        "Waiting for initialization of all sites, polling every "
            + (pc.pollingTimePeriod / 1000)
            + " seconds ... ");
    //System.out.println();
    Model minErrorModel = null;
    while (true) {
      minErrorModel = findMinErrorModel(pc);
      if (minErrorModel != null) {
        break;
      }
      Thread.sleep(pc.pollingTimePeriod);
    }
    //System.out.println();
    System.out.println("done.");
    System.out.println(
        "Best site = "
            + minErrorModel.getFromSite()
            + " with error = "
            + minErrorModel.getResult()
            + " (local = "
            + em.getResult()
            + ").");

    System.out.print(
        "Waiting "
            + (pc.waitingTimePeriod / 1000)
            + " seconds for other sites to update initial model ... ");
    Thread.sleep(pc.waitingTimePeriod);
    System.out.println("done.");

    if (pc.siteName.equalsIgnoreCase(minErrorModel.getFromSite())) {
      System.out.print("Submitting model to blockchain as best initial model ... ");
      em =
          new Model(
              pc.modelMeanFileName,
              pc.modelVarianceFileName,
              pc.trainingResultFileName,
              Flag.TRANSFER,
              pc.siteName,
              pc.siteName,
              null,
              1);
      if (Util.submitModel(pc, em)) {
        System.out.println("done, error = " + em.getResult());
      } else {
        System.out.println("failed.");
      }
    }

    PoiIter.iterate(pc, null, false, testSite);
  }

  public static Model findMinErrorModel(Config pc) throws Exception {
    HashSet<String> completedSites = new HashSet<String>();
    ArrayList<Model> models = Util.getRecentModels(pc);
    //System.out.println();

    for (Model em : models) {
      //System.out.println(em);
      if (em.getFlag() == Flag.INITIALIZE) {
        completedSites.add(em.getFromSite());
      }
    }

    if (completedSites.size() >= pc.totalSiteNumber) {
      Model model = models.get(0);
      double minError = model.getResult();
      for (Model em : models) {
        if (em.getFlag() == Flag.INITIALIZE && minError > em.getResult()) {
          minError = em.getResult();
          model = em;
        }
      }
      return model;
    }
    return null;
  }

  public static boolean isResultsCleared(Config pc) throws Exception {
    HashSet<String> completedSites = new HashSet<String>();
    ArrayList<Model> models = Util.getRecentModels(pc);
    //System.out.println();

    for (Model em : models) {
      //System.out.println(em);
      if (em.getFlag() == Flag.CLEAR) {
        completedSites.add(em.getFromSite());
      }
    }

    if (completedSites.size() >= pc.totalSiteNumber) {
      return true;
    }
    return false;
  }
}
