package cc.ranmc;

import java.io.*;
import java.net.*;

public class Main {
    private static final int PORT = 2789;

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(AudioUtil::save));
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            Logger.info("服务器已启动在端口" + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                Logger.info("客户端已连接：" + clientSocket.getInetAddress());
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandler.start();
                AudioUtil.getClientList().add(clientHandler);
                sendList();
            }
        } catch (IOException e) {
            Logger.info(e.getMessage());
        }
    }

    public static void sendList() {
        StringBuilder builder = new StringBuilder("(" + AudioUtil.getClientList().size() + ")\n");
        AudioUtil.getClientList().forEach(client -> {
            builder.append(client.getDisplayName());
            builder.append(client.isMute() ? "(静音)" : "");
            builder.append(client.isMic() ? "" : "(闭麦)");
            builder.append("\n");
        });
        AudioUtil.getClientList().forEach(client -> {
            try {
                client.getOutput().writeUTF(builder.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}