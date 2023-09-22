package cc.ranmc;

import lombok.Getter;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import java.awt.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class AudioPlaybackThread extends Thread {
    private final Socket socket;
    @Getter
    private DataOutputStream output;

    public AudioPlaybackThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        try {
            output = new DataOutputStream(socket.getOutputStream());

            Main.getConfig().send();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, Main.getFormat());
            TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(Main.getFormat());
            line.start();
            while (true) {
                int bytesRead = line.read(Main.getBuffer(), 0, Main.getBuffer().length);
                int volume = calculateVolume(Main.getBuffer(), bytesRead) / 80;
                Main.getGui().getProgressBar().setValue(volume);
                if (Main.getGui().getProgressBar().getValue() >= Main.getConfig().getData().getInt("antiNoise", 5)) {
                    output.writeByte(1);
                    output.write(Main.getBuffer(), 0, bytesRead);
                    Main.getGui().getProgressBar().setForeground(Color.ORANGE);
                } else {
                    Main.getGui().getProgressBar().setForeground(Color.GRAY);
                }
            }
        } catch (IOException | LineUnavailableException e) {
            Main.getGui().getConnectLabel().setText("连接服务器失败,请重新启动");
        }
    }

    private static int calculateVolume(byte[] audioData, int length) {
        long sum = 0;
        for (int i = 0; i < length; i += 2) {
            short audioSample = (short) ((audioData[i + 1] << 8) | audioData[i]);
            sum += audioSample * audioSample;
        }
        double rms = Math.sqrt((double) sum / ((double) length / 2));
        return (int) rms;
    }
}
