import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class Client {

    private static final int READ_CYCLE_PERIOD = 500;

    private static JTextArea textArea;
    private static JTextField textField;
    private static int port = 0;
    private static String address = "";
    private static String clientName = System.getProperty("user.name");
    private static String lastMessage = "";

    public static void main(String[] args) {
        createWindow();
    }

    private static void createWindow() {
        JFrame frame = new JFrame();
        frame.setSize(800, 600);
        frame.setTitle("Network client");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        // Text area
        textArea = new JTextArea();
        textArea.setEditable(false);
        frame.add(textArea, BorderLayout.CENTER);
        // Text field
        textField = new JTextField();
        textField.addKeyListener(keyListener);
        frame.add(textField, BorderLayout.PAGE_END);
        // Finalise
        frame.setVisible(true);
        // Start read clock
        new Thread(() -> {
            Timer timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    try {
                        read();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            timer.scheduleAtFixedRate(task, 0, READ_CYCLE_PERIOD);
        }).start();
    }

    private static void run(String text) throws IOException {
        if (text.startsWith("/address")) {
            if (text.contains(" ")) {
                String argument = text.substring(text.indexOf(' ') + 1);
                address = argument.substring(0, argument.indexOf(':'));
                port = Integer.parseInt(argument.substring(argument.indexOf(':') + 1));
                writeText(String.format("Changed server address to %s:%s", address, port));
            } else {
                writeText(address + ":" + port);
            }
        } else if (text.startsWith("/name")) {
            if (text.contains(" ")) {
                clientName = text.substring(text.indexOf(' ') + 1);
                writeText("Changed client name to " + clientName);
            } else {
                writeText(clientName);
            }
        } else {
            Socket client = new Socket(address, port);
            DataOutputStream out = new DataOutputStream(client.getOutputStream());
            String toWrite = String.format("[%s] %s", clientName, text);
            out.writeUTF(toWrite);
            client.close();
            writeText(toWrite);
        }
    }

    private static void read() throws IOException {
        if (!(address.equals("") || port == 0)) {
            Socket client = new Socket(address, port);
            DataOutputStream out = new DataOutputStream(client.getOutputStream());
            out.writeUTF("/update " + lastMessage);
            DataInputStream in = new DataInputStream(client.getInputStream());
            int max = Integer.parseInt(in.readUTF());
            for (int i = 0; i < max; i++) {
                String message = in.readUTF();
                if (!message.startsWith("[" + clientName)) {
                writeText(message);
                }
                lastMessage = message;
            }
        }
    }

    private static void writeText(String text) {
        if (!text.equals("")) {
            textArea.append(text + "\n");
        }
    }

    private static final KeyListener keyListener = new KeyListener() {
        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {

        }

        @Override
        public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                String text = textField.getText();
                try {
                    run(text);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    writeText("Failed to run command");
                }
                textField.setText("");
            }
        }
    };
}
