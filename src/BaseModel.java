import java.io.*;
import java.util.*;
import javax.xml.bind.*;

public class BaseModel implements Serializable {

  public static String toObjectHexString(BaseModel bm) throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos2 = new ObjectOutputStream(baos);
    oos2.writeObject(bm);
    oos2.flush();
    byte b[] = baos.toByteArray();
    return DatatypeConverter.printHexBinary(b);
  }

  public static BaseModel fromObjectHexString(String s) throws Exception {
    byte b[] = DatatypeConverter.parseHexBinary(s);
    ByteArrayInputStream bais = new ByteArrayInputStream(b);
    ObjectInputStream ois = new ObjectInputStream(bais);
    Object o = ois.readObject();
    if (o instanceof BaseModel) {
      return (BaseModel) o;
    } else {
      return null;
    }
  }
}
