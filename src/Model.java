import java.io.*;
import java.util.*;
import javax.xml.bind.*;
import utility.*;

public class Model extends BaseModel {

  double[] modelMean = null;
  double[][] modelVariance = null;
  Flag flag = Flag.UNKNOWN;
  double result = 0.0d;

  String fromSite = "";
  String toSite = "";
  Date time = null;
  int iteration = 0;

  public static void main(String[] args) throws Exception {
    Model em =
        new Model("model_mean_train.txt", "model_variance_train.txt", "result_train.txt");

    FileOutputStream fos = new FileOutputStream("em.bin");
    ObjectOutputStream oos1 = new ObjectOutputStream(fos);
    oos1.writeObject(em);
    oos1.flush();

    String s = toObjectHexString(em);
    System.out.println(s);

    em = (Model) fromObjectHexString(s);
    em.saveModel("model_mean_train.txt", "model_variance_train.txt", "result_train.txt");
  }

  public String toString() {
    return flag + ", " + fromSite + ", " + toSite + ", " + time;
  }

  public Model(
      String modelMeanFileName,
      String modelVarianceFileName,
      String resultFileName,
      Flag flag,
      String fromSite,
      String toSite,
      Date time,
      int iteration)
      throws Exception {
    this.loadModel(modelMeanFileName, modelVarianceFileName, resultFileName);
    this.flag = flag;
    this.fromSite = fromSite;
    this.toSite = toSite;
    this.time = time;
    this.iteration = iteration;
  }

  public Model(
      String modelMeanFileName, String modelVarianceFileName, String resultFileName, Flag flag)
      throws Exception {
    this.loadModel(modelMeanFileName, modelVarianceFileName, resultFileName);
    this.flag = flag;
  }

  public Model(String modelMeanFileName, String modelVarianceFileName, String resultFileName)
      throws Exception {
    this.loadModel(modelMeanFileName, modelVarianceFileName, resultFileName);
  }

  public void loadModel(
      String modelMeanFileName, String modelVarianceFileName, String resultFileName)
      throws Exception {
    this.loadModel(modelMeanFileName, modelVarianceFileName);
    this.result = Utility.loadDoubleArray(resultFileName)[0];
  }

  public void saveModel(
      String modelMeanFileName, String modelVarianceFileName, String resultFileName)
      throws Exception {
    this.saveModel(modelMeanFileName, modelVarianceFileName);
    double[] resultArray = new double[1];
    resultArray[0] = this.result;
    Utility.saveDoubleArray(resultFileName, resultArray);
  }

  public Model(String modelMeanFileName, String modelVarianceFileName) throws Exception {
    this.loadModel(modelMeanFileName, modelVarianceFileName);
  }

  public void loadModel(String modelMeanFileName, String modelVarianceFileName) throws Exception {
    this.modelMean = Utility.loadDoubleArray(modelMeanFileName);
    this.modelVariance = Utility.loadDouble2DArray(modelVarianceFileName, "\t");
  }

  public void saveModel(String modelMeanFileName, String modelVarianceFileName) throws Exception {
    Utility.saveDoubleArray(modelMeanFileName, this.modelMean);
    Utility.saveDouble2DArray(modelVarianceFileName, this.modelVariance, "\t");
  }

  public Flag getFlag() {
    return flag;
  }

  public void setFlag(Flag flag) {
    this.flag = flag;
  }

  public double getResult() {
    return result;
  }

  public void setResult(double result) {
    this.result = result;
  }

  public String getFromSite() {
    return fromSite;
  }

  public void setFromSite(String fromSite) {
    this.fromSite = fromSite;
  }

  public String getToSite() {
    return toSite;
  }

  public void setToSite(String toSite) {
    this.toSite = toSite;
  }

  public Date getTime() {
    return time;
  }

  public void setTime(Date time) {
    this.time = time;
  }

  public int getIteration() {
    return iteration;
  }

  public void setIteration(int iteration) {
    this.iteration = iteration;
  }
}
