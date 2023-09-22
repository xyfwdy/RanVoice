package cc.ranmc;

import cn.hutool.json.JSONObject;
import lombok.Getter;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;

public class Config {

    private final File file = new File(System.getProperty("user.dir") + File.separator + "config.txt");
    @Getter
    private JSONObject data = new JSONObject();

    public Config() {
        try {
            if (file.exists()) {
                data = new JSONObject(FileUtils.fileRead(file, "UTF-8"));
            } else {
                FileUtils.fileWrite(file, "UTF-8", "{}");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void save() {
        try {
            FileUtils.fileWrite(file, "UTF-8", data.toString());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void send() {
        try {
            Main.getPlaybackThread().getOutput().writeByte(2);
            Main.getPlaybackThread().getOutput().writeUTF(data.toString());

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        save();
    }
}