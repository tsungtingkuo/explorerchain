import java.util.*;
import utility.*;

public class PoiIter {

  public static void iterate(Config pc, Date latestEvaluateTime, boolean previousConsensusFound, String testSite)
      throws Exception {

    Model em = null;
    String consensus = null;
    boolean consensusFound = previousConsensusFound;
    boolean showConsensus = false;
    int internalIteration = 1;
    int consensusIteration = 1;
    int evaluatedIteration = 0;
    boolean showMessage = true;

    while (true) {

      if (showMessage) {
        System.out.println(
            "Proof-of-Information Iter: Polling every "
                + (pc.pollingTimePeriod / 1000)
                + " seconds, Ctrl+C to stop.");
        System.out.println();
        showMessage = false;
      }

      System.out.print(".");
      Model latestOutsideUpdateModel = findLatestOutsideUpdateModel(pc);
      if (latestOutsideUpdateModel != null
          && (latestEvaluateTime == null
              || latestEvaluateTime.before(latestOutsideUpdateModel.getTime()))
          && (evaluatedIteration < latestOutsideUpdateModel.getIteration()
              || latestOutsideUpdateModel.getFlag() == Flag.CONSENSUS)) {

        latestEvaluateTime = latestOutsideUpdateModel.getTime();
        consensusIteration = latestOutsideUpdateModel.getIteration();
        evaluatedIteration = consensusIteration;

        System.out.println();
        System.out.println();
        System.out.println("Outside update model found, iteration = " + consensusIteration + ".");
        System.out.print("Loading model from blockchain ... ");
        latestOutsideUpdateModel.saveModel(pc.modelMeanFileName, pc.modelVarianceFileName);
        System.out.println("done.");

        if (latestOutsideUpdateModel.getIteration() >= pc.maxIteration || latestOutsideUpdateModel.getFlag() == Flag.CONSENSUS) {
          consensus = latestOutsideUpdateModel.getFromSite();
          consensusFound = true;
          showConsensus = true;
          System.out.println();
        } else {
          consensus = null;
          consensusFound = false;
          showConsensus = false;

          System.out.print("Evaluating model ... ");
          Matlab.run("ExplorerEval", testSite);
          System.out.println(
              "done, local error: training = "
                  + Util.getTrainingResult(pc)
                  + ", testing = "
                  + Util.getTestingResult(pc)
                  + ".");
          Log.appendResult(pc, "Evaluate");

          System.out.print("Submitting error to blockchain ... ");
          em =
              new Model(
                  pc.modelMeanFileName,
                  pc.modelVarianceFileName,
                  pc.trainingResultFileName,
                  Flag.EVALUATE,
                  pc.siteName,
                  pc.siteName,
                  null,
                  latestOutsideUpdateModel.getIteration());
          if (Util.submitModel(pc, em)) {
            System.out.println("done.");
          } else {
            System.out.println("failed.");
          }
          System.out.println();
        }
      }

      System.out.print(".");
      Model latestIncomingTransferModel = findLatestIncomingTransferModel(pc);

      if (!consensusFound && latestIncomingTransferModel != null) {
        consensusIteration = latestIncomingTransferModel.getIteration();

        System.out.println();
        System.out.println();
        System.out.println("Incoming transfer model found, iteration = " + consensusIteration + ".");
        System.out.print("Loading model from blockchain ... ");
        latestIncomingTransferModel.saveModel(pc.modelMeanFileName, pc.modelVarianceFileName);
        System.out.println("done.");

        if (!pc.siteName.equalsIgnoreCase(latestIncomingTransferModel.getFromSite())) {
          System.out.print(
              "Updating model received from "
                  + latestIncomingTransferModel.getFromSite()
                  + " ... ");
          Matlab.run("ExplorerUpdate", testSite);
          System.out.println(
              "done, local error: training = "
                  + Util.getTrainingResult(pc)
                  + ", testing = "
                  + Util.getTestingResult(pc)
                  + ".");
          Log.appendResult(pc, "Update");
        } else {
          System.out.println("Model self-tranferred from local site.");
        }

        System.out.print("Submitting model to blockchain ... ");

        if(consensusIteration >= pc.maxIteration) {
          em =
              new Model(
                  pc.modelMeanFileName,
                  pc.modelVarianceFileName,
                  pc.trainingResultFileName,
                  Flag.CONSENSUS,
                  pc.siteName,
                  pc.siteName,
                  null,
                  latestIncomingTransferModel.getIteration());
        } else {
          em =
              new Model(
                  pc.modelMeanFileName,
                  pc.modelVarianceFileName,
                  pc.trainingResultFileName,
                  Flag.UPDATE,
                  pc.siteName,
                  pc.siteName,
                  null,
                  latestIncomingTransferModel.getIteration());
        }

        if (Util.submitModel(pc, em)) {
          System.out.println("done.");
        } else {
          System.out.println("failed.");
        }

        if (latestIncomingTransferModel.getIteration() >= pc.maxIteration) {
          consensus = pc.siteName;
          consensusFound = true;
          showConsensus = true;
        } else {

          System.out.print(
              "Waiting "
                  + (pc.waitingTimePeriod / 1000)
                  + " seconds to collect errors from other sites ... ");
          Thread.sleep(pc.waitingTimePeriod);
          System.out.println("done.");

          System.out.print("Selecting next model ... ");
          Model nextModel = findMaxErrorModel(pc, em, latestIncomingTransferModel.getIteration());
          System.out.println("done.");

          showMessage = true;

          if (nextModel != null) {
            internalIteration = nextModel.getIteration() + 1;
          }

          if (nextModel != null && !pc.siteName.equalsIgnoreCase(nextModel.getFromSite())) {
            System.out.println(
                "Next site = "
                    + nextModel.getFromSite()
                    + " with error = "
                    + nextModel.getResult()
                    + " (local = "
                    + em.getResult()
                    + ").");
            System.out.print("Transferring model to " + nextModel.getFromSite() + " ... ");
            em =
                new Model(
                    pc.modelMeanFileName,
                    pc.modelVarianceFileName,
                    pc.trainingResultFileName,
                    Flag.TRANSFER,
                    pc.siteName,
                    nextModel.getFromSite(),
                    null,
                    internalIteration);
            if (Util.submitModel(pc, em)) {
              System.out.println("done.");
            } else {
              System.out.println("failed.");
            }

          } else {
            consensus = pc.siteName;
            consensusFound = true;
            showConsensus = true;

            System.out.print("Submitting consensus model to blockchain ... ");
            em =
                new Model(
                    pc.modelMeanFileName,
                    pc.modelVarianceFileName,
                    pc.trainingResultFileName,
                    Flag.CONSENSUS,
                    pc.siteName,
                    pc.siteName,
                    null,
                    consensusIteration);
            if (Util.submitModel(pc, em)) {
              System.out.print("done.");
            } else {
              System.out.print("failed.");
            }
          }
        }
      }

      if (consensusFound && showConsensus) {
        System.out.println();
        System.out.println();
        System.out.println(
            "Proof-of-Information Iter: Consensus model identified or max iteration reached, model update paused.");

        System.out.println();
        System.out.println(
            "Consensus model = "
                + consensus
                + ", local error: training = "
                + Util.getTrainingResult(pc)
                + ", testing = "
                + Util.getTestingResult(pc)
                + ", iteration = "
                + consensusIteration
                + ".");
        Log.appendResult(pc, "Consensus");
        System.out.println();

        showMessage = false;
        showConsensus = false;

        if (pc.stopAfterConsensus) {

          // Wait for errors
          int errorWaitingTime = pc.waitingTimePeriod;
          System.out.print(
              "Waiting "
                  + (errorWaitingTime / 1000)
                  + " seconds to evaluate result for other sites ... ");
          Thread.sleep(errorWaitingTime);
          System.out.println("done.");

          System.out.print(
              "Submitting consensus test error to blockchain for result collection ... ");
          em =
              new Model(
                  pc.modelMeanFileName,
                  pc.modelVarianceFileName,
                  pc.testingResultFileName,
                  Flag.TEST,
                  pc.siteName,
                  pc.siteName,
                  null,
                  consensusIteration);
          if (Util.submitModel(pc, em)) {
            System.out.println("done.");
          } else {
            System.out.println("failed.");
          }

          Util.saveIteration(pc, testSite, consensusIteration); 

          return;
        }
      }

      Thread.sleep(pc.pollingTimePeriod);
    }
  }

  public static Model findOutsideRandomModel(Config pc, Model currentModel)
      throws Exception {
    HashSet<String> outsideSites = new HashSet<String>();
    ArrayList<Model> models = Util.getRecentModels(pc);
    for (Model em : models) {
      if (!pc.siteName.equalsIgnoreCase(em.getFromSite())) {
        outsideSites.add(em.getFromSite());
      }
    }

    ArrayList<String> al = new ArrayList<String>(outsideSites);
    int index = new Random().nextInt(al.size());
    String site = al.get(index);

    Model model = null;
    for (Model em : models) {
      if (site.equalsIgnoreCase(em.getFromSite())) {
        model = em;
      }
    }

    return model;
  }

  public static Model findMaxErrorModel(Config pc, Model currentModel, int iteration)
      throws Exception {
    ArrayList<Model> models = Util.getRecentModels(pc);
    double maxError = currentModel.getResult();
    Model model = null;
    for (Model em : models) {
      if (em.getFlag() == Flag.EVALUATE && maxError < em.getResult() && em.getIteration() == iteration) {
        maxError = em.getResult();
        model = em;
      }
    }
    return model;
  }

  public static Model findLatestIncomingTransferModel(Config pc) throws Exception {
    ArrayList<Model> models = Util.getRecentModels(pc);
    Model model = null;
    for (Model em : models) {
      if (em.getFlag() == Flag.TRANSFER && pc.siteName.equalsIgnoreCase(em.getToSite())) {
        model = em;
      }
    }
    return model;
  }

  public static Model findLatestOutsideUpdateModel(Config pc) throws Exception {
    ArrayList<Model> models = Util.getRecentModels(pc);
    Model model = null;
    for (Model em : models) {
      if ((em.getFlag() == Flag.UPDATE || em.getFlag() == Flag.CONSENSUS) && !pc.siteName.equalsIgnoreCase(em.getFromSite())) {
        model = em;
      }
    }
    return model;
  }
}
