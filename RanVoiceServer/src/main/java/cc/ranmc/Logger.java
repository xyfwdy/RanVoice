package cc.ranmc;

import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

    public static void info(String text) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("[HH:mm:ss] ");
        Date date = new Date();
        text = timeFormat.format(date) + text;
        System.out.println(text);
        try {
            File file = new File(System.getProperty("user.dir") + File.separator + "log" + File.separator + dateFormat.format(date) + ".txt");
            String orgin = "";
            if (file.exists()) {
                orgin = FileUtils.fileRead(file, "UTF-8");
            }
            FileUtils.fileWrite(file, "UTF-8", orgin + "\n" + text);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}