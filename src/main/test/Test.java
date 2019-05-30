import com.rainple.utils.FileUtils;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * @description:
 * @author: rainple
 * @create: 2019-05-30 18:06
 **/
public class Test {

    public static void main(String[] args) throws IOException {
        long totalChar = FileUtils.getTotalChar("C:\\\\Users\\\\rainple\\\\Desktop\\\\pom.xml");
        System.out.println(totalChar);
    }

}
