import java.io.*;
import java.util.*;
import utility.*;

public class PoiNew {

  public static void update(String testSite) throws Exception {

    Config pc = new Config(testSite);
    Log.reopen(pc.logFileName);

    System.out.println();
    System.out.println("Proof-of-Information New: " + pc.siteName + " (" + pc.nodeAddress + ")");

    System.out.println();
    System.out.print("Checking latest consensus model from blockchain ... ");
    Model latestModel = findLatestModel(pc);
    if (latestModel != null) {
      boolean previousConsensusFound = false;
      System.out.println(
          "found, latest = "
              + latestModel.getFromSite()
              + " with error = "
              + latestModel.getResult()
              + ".");

      System.out.print("Loading model from blockchain ... ");
      latestModel.saveModel(pc.modelMeanFileName, pc.modelVarianceFileName);
      System.out.println("done.");

      System.out.print("Evaluating model ... ");
      Matlab.run("ExplorerEval", testSite);
      System.out.println(
          "done, local error: training = "
              + Util.getTrainingResult(pc)
              + ", testing = "
              + Util.getTestingResult(pc)
              + ".");
      Log.appendResult(pc, "New");

      System.out.print("Comparing ... ");
      Model em =
          new Model(
              pc.modelMeanFileName,
              pc.modelVarianceFileName,
              pc.trainingResultFileName,
              Flag.EVALUATE,
              pc.siteName,
              pc.siteName,
              null,
              latestModel.getIteration());
      if (em.getResult() >= latestModel.getResult()) {
        System.out.println("local site has equal or larger error = " + em.getResult() + ".");
        System.out.print("Transferring model to this site for updating ... ");
        em =
            new Model(
                pc.modelMeanFileName,
                pc.modelVarianceFileName,
                pc.trainingResultFileName,
                Flag.TRANSFER,
                latestModel.getFromSite(),
                pc.siteName,
                null,
                1);
        if (Util.submitModel(pc, em)) {
          System.out.println("done, iteration reset to 1");
        } else {
          System.out.println("failed.");
        }
      } else {
        System.out.println("local site has smaller error = " + em.getResult() + ".");
        previousConsensusFound = true;
      }

      PoiIter.iterate(pc, new Date(), previousConsensusFound, testSite);
    } else {
      System.out.println(
          "not found, please run Proof-of-Information Init instead or wait for the consensus model to be initialized/updated.");
    }
  }

  public static Model findLatestModel(Config pc) throws Exception {
	  ArrayList<Model> models = Util.getRecentModels(pc);
    Model model = null;
    for (Model em : models) {
      if (em.getFlag() == Flag.CONSENSUS) {
        model = em;
      }
    }
    return model;
  }
}
