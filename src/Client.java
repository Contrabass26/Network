import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.Socket;

public class Client {

    private static JTextArea textArea;
    private static JTextField textField;
    private static int port;
    private static String address;
    private static String clientName = "Contrabass26";

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
            out.writeUTF(String.format("[%s] %s", clientName, text));
            client.close();
            writeText(text);
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
