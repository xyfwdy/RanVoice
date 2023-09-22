package cc.ranmc;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class AudioCaptureThread extends Thread {
    private final Socket socket;

    public AudioCaptureThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            DataInputStream input = new DataInputStream(socket.getInputStream());

            while (true) {
                Main.getGui().getListTextArea().setText("在线列表 " + input.readUTF());
            }
        } catch (IOException e) {
            Main.getGui().getConnectLabel().setText("连接服务器失败,请重新启动");
        }
    }
}