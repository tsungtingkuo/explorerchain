import java.io.*;
import java.text.*;
import java.util.*;

public class Log {

  private static String fileName = null;

  public static void appendResult(Config pc, String action) throws Exception {
    String time = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
    append(
        time
            + ", "
            + Util.getFormattedTrainingResult(pc)
            + ", "
            + Util.getFormattedTestingResult(pc)
            + ", "
            + action);
  }

  public static void append(String line) throws Exception {
    PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(Log.fileName, true)));
    pw.println(line);
    pw.flush();
    pw.close();
  }

  public static void reset(String fileName) throws Exception {
    PrintWriter pw = new PrintWriter(fileName);
    pw.flush();
    pw.close();
    Log.fileName = fileName;
  }

  public static void reopen(String fileName) throws Exception {
    Log.fileName = fileName;
  }
}
