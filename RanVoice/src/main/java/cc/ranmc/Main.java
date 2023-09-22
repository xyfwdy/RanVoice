package cc.ranmc;

import lombok.Getter;
import lombok.Setter;

import javax.sound.sampled.AudioFormat;
import java.io.IOException;
import java.net.*;

public class Main {
    public static final String VERSION = "Alpha 1.0";
    @Getter
    private static final Config config = new Config();
    public static String HOST = config.getData().getStr("host", "127.0.0.1");
    public static final int PORT = 2789;

    @Getter
    private static byte[] buffer = new byte[1024];
    @Getter
    private static final AudioFormat format = new AudioFormat(8000, 8, 1, true, true);
    @Getter
    private static final GUI gui = new GUI();
    @Getter
    @Setter
    private static AudioCaptureThread captureThread;
    @Getter
    @Setter
    private static AudioPlaybackThread playbackThread;
    @Getter
    private static Socket socket;

    public static void main(String[] args) {
        connect();
    }

    public static boolean isNotConnected() {
        return socket == null || !socket.isConnected();
    }

    public static void connect() {
        if (socket != null) return;
        long startTime = System.currentTimeMillis();
        try {
            socket = new Socket(HOST, PORT);
        } catch (IOException e) {
            gui.getConnectLabel().setText("连接服务器失败,请重新连接");
            return;
        }
        long endTime = System.currentTimeMillis();
        gui.getConnectLabel().setText("已连接到服务器 " + (endTime - startTime) + "ms");

        captureThread = new AudioCaptureThread(socket);
        playbackThread = new AudioPlaybackThread(socket);

        captureThread.start();
        playbackThread.start();
    }

}
