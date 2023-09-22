package cc.ranmc;

import cn.hutool.json.JSONException;
import cn.hutool.json.JSONObject;
import lombok.Getter;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.regex.Pattern;

import static cc.ranmc.AudioUtil.BUFFER;
import static cc.ranmc.Main.sendList;

public class ClientHandler extends Thread {

    private final Socket socket;
    @Getter
    private SourceDataLine line;
    @Getter
    private DataOutputStream output;
    @Getter
    private boolean mute = false;
    @Getter
    private boolean mic = true;
    @Getter
    private String displayName = "未命名";
    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.output = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        try {
            AudioFormat format = new AudioFormat(8000, 8, 1, true, true);
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();

            try {
                while (true) {
                    if (input.readByte() == 2) {
                        try {
                            JSONObject data = new JSONObject(input.readUTF());
                            mute = data.getBool("mute", false);
                            mic = data.getBool("mic", true);
                            Pattern pattern = Pattern.compile("^[a-zA-Z_一-龥]{2,6}$");
                            if (pattern.matcher(data.getStr("name", "未命名")).matches()) {
                                displayName = data.getStr("name", "未命名");
                            }
                            sendList();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        int bytesRead = input.read(BUFFER);
                        if (bytesRead == -1) {
                            break;
                        }
                        if (mic) AudioUtil.line(this, 0, bytesRead);
                    }
                }
            } catch (SocketException e) {
                Logger.info(e.getMessage());
            }
        } catch (IOException | LineUnavailableException e) {
            Logger.info(e.getMessage());
        }
        Logger.info("连接断开：" + socket.getInetAddress());
        AudioUtil.getClientList().remove(this);
        sendList();
    }
}





