package cc.ranmc;

import lombok.Data;

import javax.swing.*;
import java.awt.*;
import java.util.regex.Pattern;

@Data
public class GUI {
    private JToggleButton muteBtn,micBtn;
    private JFrame frame;
    private JLabel connectLabel, volLabel, micLabel, antiNoiseLabel;
    private JTextArea listTextArea;
    private JSlider volSlider, antiNoiseSlider;
    private JProgressBar progressBar;

    public GUI() {
        frame = new JFrame("游戏语音");
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setLayout(new java.awt.FlowLayout());
        frame.setSize(280, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel hostPanel = new JPanel();
        hostPanel.add(new JLabel("地址"));
        hostPanel.add(Box.createHorizontalStrut(10));
        hostPanel.setLayout(new BoxLayout(hostPanel, BoxLayout.X_AXIS));
        JTextField hostField = new JTextField(Main.getConfig().getData().getStr("host", "127.0.0.1"), 10);
        hostPanel.add(hostField);
        JButton hostBtn = new JButton("连接");
        hostBtn.addActionListener(e -> {
            Main.getConfig().getData().set("host", hostField.getText());
            Main.getConfig().save();
            Main.connect();
        });
        hostPanel.add(Box.createHorizontalStrut(10));
        hostPanel.add(hostBtn);
        frame.add(hostPanel);

        JPanel namePanel = new JPanel();
        namePanel.add(new JLabel("昵称"));
        namePanel.add(Box.createHorizontalStrut(10));
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.X_AXIS));
        JTextField nameField = new JTextField(Main.getConfig().getData().getStr("name", "芝士小XX"), 10);
        namePanel.add(nameField);
        JButton nameBtn = new JButton("更改");
        nameBtn.addActionListener(e -> {
            if (Main.isNotConnected()) return;
            Pattern pattern = Pattern.compile("^[a-zA-Z_一-龥]{2,6}$");
            if (!pattern.matcher(nameField.getText()).matches()) {
                nameField.setText("名称不规范，由2~6位中文或英文组成");
                return;
            }
            Main.getConfig().getData().set("name", nameField.getText());
            Main.getConfig().send();
        });
        namePanel.add(Box.createHorizontalStrut(10));
        namePanel.add(nameBtn);
        frame.add(namePanel);

        Box buttonBox = Box.createHorizontalBox();

        muteBtn = new JToggleButton("静音");
        muteBtn.setSelected(Main.getConfig().getData().getBool("mute", false));
        muteBtn.addActionListener(e -> {
            if (Main.isNotConnected()) return;
            if (Main.getConfig().getData().getBool("mute", false)) {
                Main.getConfig().getData().set("mute", false);
            } else {
                Main.getConfig().getData().set("mute", true);
            }
            Main.getConfig().send();
        });


        micBtn = new JToggleButton("闭麦");
        micBtn.setSelected(!Main.getConfig().getData().getBool("mic", false));
        micBtn.addActionListener(e -> {
            if (Main.isNotConnected()) return;
            if (Main.getConfig().getData().getBool("mic", true)) {
                Main.getConfig().getData().set("mic", false);
            } else {
                Main.getConfig().getData().set("mic", true);
            }
            Main.getConfig().send();
        });
        buttonBox.add(muteBtn);
        buttonBox.add(Box.createHorizontalStrut(10));
        buttonBox.add(micBtn);
        frame.add(buttonBox);

        JPanel micPanel = new JPanel();
        micPanel.add(new JLabel("麦克风"));
        progressBar = new JProgressBar();
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);
        progressBar.setValue(0);
        progressBar.setStringPainted(false);
        micPanel.add(progressBar);
        frame.add(micPanel);

        JPanel volPanel = new JPanel();
        volLabel = new JLabel("音量 " + Main.getConfig().getData().getInt("vol", 100) + "%");
        volPanel.add(volLabel);
        volSlider = new JSlider(JSlider.HORIZONTAL, 0, 100,
                Main.getConfig().getData().getInt("vol", 100));
        volSlider.setMajorTickSpacing(10);
        volSlider.setMinorTickSpacing(1);
        volSlider.addChangeListener(e -> {
            int value = volSlider.getValue();
            volLabel.setText("音量 " + value + "%");
            Main.getConfig().getData().set("vol", value);
            Main.getConfig().save();
        });
        volSlider.setPreferredSize(new Dimension(140, 20));
        volPanel.add(volSlider);
        frame.add(volPanel);

        JPanel antiNoisePanel = new JPanel();
        antiNoiseLabel = new JLabel("降噪 " + Main.getConfig().getData().getInt("antiNoise", 10) + "%");
        antiNoisePanel.add(antiNoiseLabel);
        antiNoiseSlider = new JSlider(JSlider.HORIZONTAL, 0, 100,
                Main.getConfig().getData().getInt("antiNoise", 10));
        antiNoiseSlider.setMajorTickSpacing(10);
        antiNoiseSlider.setMinorTickSpacing(1);
        antiNoiseSlider.addChangeListener(e -> {
            int value = antiNoiseSlider.getValue();
            antiNoiseLabel.setText("降噪 " + value + "%");
            Main.getConfig().getData().set("antiNoise", value);
            Main.getConfig().save();
        });
        antiNoiseSlider.setPreferredSize(new Dimension(140, 20));
        antiNoisePanel.add(antiNoiseSlider);
        frame.add(antiNoisePanel);

        JPanel listPanel = new JPanel();
        listTextArea = new JTextArea("在线列表 (0)");
        listTextArea.setSize(240, 150);
        listTextArea.setLineWrap(true);
        listPanel.add(listTextArea);
        listPanel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        frame.add(listPanel);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.add(new JLabel("By阿然"));
        textPanel.add(new JLabel("版本号 " + Main.VERSION));
        textPanel.add(new JLabel("https://www.ranmc.cc/"));
        connectLabel = new JLabel("正在连接服务器...");
        textPanel.add(connectLabel);
        frame.add(textPanel);
    }
}
