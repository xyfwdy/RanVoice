package cc.ranmc;

import lombok.Getter;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AudioUtil {

    @Getter
    private static final List<ClientHandler> clientList = new ArrayList<>();
    private static final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    public static final byte[] BUFFER = new byte[1024];
    public static void save() {
        String filename = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()) + ".wav";
        byte[] audioData = byteArrayOutputStream.toByteArray();
        try {
            AudioFormat format = new AudioFormat(8000, 8, 1, true, true);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(audioData);
            AudioInputStream audioInputStream = new AudioInputStream(byteArrayInputStream, format, audioData.length / format.getFrameSize());
            File audioFile = new File(filename);
            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, audioFile);
        } catch (IOException e) {
            Logger.info(e.getMessage());
        }
    }

    public static void line(ClientHandler clientHandler, int off, int len) {
        clientList.parallelStream()
                .filter(client -> client != clientHandler && !client.isMute() && client.getLine() != null)
                .forEach(client -> client.getLine().write(BUFFER, off, len));
        //byteArrayOutputStream.write(BUFFER, 0, len);
    }

}
