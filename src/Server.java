import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class Server extends Thread {

    private JFrame frame;
    private JTextArea textArea;
    private final ServerSocket serverSocket;

    public Server() throws IOException {
        serverSocket = new ServerSocket(0);
        createWindow();
        log("Server hosted on port " + serverSocket.getLocalPort());
    }

    public void run() {
        while (true) {
            try {
                Socket server = serverSocket.accept();
                DataInputStream in = new DataInputStream(server.getInputStream());
                String message = in.readUTF();
                log(message);
                server.close();
            } catch (SocketTimeoutException e) {
                System.out.println("Socket timed out");
                break;
            } catch (SocketException e) {
                System.out.println("Closing server");
                break;
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public static void main(String[] args) {
        try {
            Thread thread = new Server();
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createWindow() {
        frame = new JFrame();
        frame.setSize(800, 600);
        frame.setTitle("Network server");
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(windowCloseListener);
        frame.setLayout(new GridLayout(1, 1));
        // Components
        textArea = new JTextArea();
        frame.add(textArea);
        // Finalise
        frame.setVisible(true);
    }

    private void log(String message) {
        if (!message.equals("")) {
            textArea.append(message + "\n");
        }
    }

    private final WindowAdapter windowCloseListener = new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
            try {
                serverSocket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            frame.dispose();
            System.exit(0);
        }
    };
}
