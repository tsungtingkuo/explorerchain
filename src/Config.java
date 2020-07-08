import java.io.*;
import java.util.*;
import utility.*;

public class Config {

  String modelMeanFileName = null;
  String modelVarianceFileName = null;
  String trainingResultFileName = null;
  String testingResultFileName = null;
  String logFileName = null;
  String iterationFileName = null;

  String configFileName = null;

  String chainName = null;
  String address = null;
  int totalSiteNumber = 0;
  int minDataSize = 0;
  int dataNumber = 0;
  String siteName = null;
  String trainFileName = null;
  String testFileName = null;
  int pollingTimePeriod = 0;
  int waitingTimePeriod = 0;
  int maxIteration = 0;
  boolean stopAfterConsensus = false;

  String nodeAddress = null;

  public Config(String testSite, boolean isClear) throws Exception {

    String dirName = "temp";
    String s = "";
    double[] d = { 0.0 };
    double[][] dd = { { 0.0 } };

    this.modelMeanFileName = dirName + "/model_mean_train_" + testSite + ".txt";
    this.modelVarianceFileName = dirName + "/model_variance_train_" + testSite + ".txt";
    this.trainingResultFileName = dirName + "/result_train_" + testSite + ".txt";
    this.testingResultFileName = dirName + "/result_test_" + testSite + ".txt";
    this.logFileName = dirName + "/log_" + testSite + ".txt";
    this.iterationFileName = dirName + "/iteration_" + testSite + ".txt";

    if(isClear) {
      clear();

      Utility.saveDoubleArray(this.modelMeanFileName, d);
      Utility.saveDouble2DArray(this.modelVarianceFileName, dd);
      Utility.saveDoubleArray(this.trainingResultFileName, d);
      Utility.saveDoubleArray(this.testingResultFileName, d);
      Utility.saveString(this.logFileName, s);
      Utility.saveString(this.iterationFileName, s);
    }

    this.configFileName = "conf/config_" + testSite + ".txt";

    String[] configs = Utility.loadStringArray(configFileName);

    this.chainName = configs[0];
    this.address = configs[1];
    this.minDataSize = Integer.parseInt(configs[2]);
    this.totalSiteNumber = Integer.parseInt(configs[3]);
    this.dataNumber = Integer.parseInt(configs[4]);
    this.siteName = configs[5];
    this.trainFileName = configs[6];
    this.testFileName = configs[7];
    this.pollingTimePeriod = Integer.parseInt(configs[8]);
    this.waitingTimePeriod = Integer.parseInt(configs[9]);
    this.maxIteration = Integer.parseInt(configs[10]);
    this.stopAfterConsensus = Boolean.parseBoolean(configs[11]);

    this.nodeAddress = Chain.getNodeAddress(chainName);
  }

  public Config(String testSite) throws Exception {
    this(testSite, false);
  }

  public void change(
      String datasetName,
      String testSite,
      String stopAfterConsensus)
      throws Exception {

    String[] configs = Utility.loadStringArray(configFileName);

    configs[6] = "./data/" + datasetName + "_train_" + testSite + ".txt";
    configs[7] = "./data/" + datasetName + "_test_" + testSite + ".txt";
    configs[11] = stopAfterConsensus;

    Utility.saveStringArray(configFileName, configs);
  }

  public void clear() {
    new File(this.modelMeanFileName).delete();
    new File(this.modelVarianceFileName).delete();
    new File(this.trainingResultFileName).delete();
    new File(this.testingResultFileName).delete();
    new File(this.logFileName).delete();
    new File(this.iterationFileName).delete();
  }
}
