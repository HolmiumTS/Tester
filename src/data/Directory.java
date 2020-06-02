package data;

import java.util.regex.*;
import java.io.*;
import java.util.*;

public class Directory {
  public static File[]
  local(File dir, final String regex) {
    return dir.listFiles(new FilenameFilter() {
      private Pattern pattern = Pattern.compile(regex);

      public boolean accept(File dir, String name) {
        return pattern.matcher(
                new File(name).getName()).matches();
      }
    });
  }

  /**
   * 将某个path下的所有文件转换为File的形式并放入某个数组之中返回
   **/
  public static File[]
  local(String path, final String regex) { // Overloaded
    return local(new File(path), regex);
  }


}
