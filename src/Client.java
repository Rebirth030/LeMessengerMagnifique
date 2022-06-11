import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class Client {

    private Socket socket;
    private BufferedWriter writer;
    private BufferedReader reader;
    private String username;
    static Gui gui;

    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
            this.username = username;
        } catch (IOException e) {
            closeEverything(socket, reader, writer);
        }
    }

    public static void main(String[] args) throws IOException {
        gui = new Gui("Client");
        gui.addKeyPanel();
        gui.setVisible(true);
        Socket socket = new Socket("localhost", 4473);
        Client client = new Client(socket, gui.getUserName());
        client.sendUsername();
        client.listenForMessage();
        gui.addWritingArea(client);
        gui.repaint();
    }

    public void sendUsername() {
        try {
            writer.write(username);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            closeEverything(socket, reader, writer);
        }

    }

    public void sendMessage(String messageToSend) {
        try {
            String encryption = gui.getEncryption();
            String key = gui.getKey();

            if (!Objects.equals(encryption, "no encryption")) {
                Cryption cryption = new Cryption(gui.getKey(), username + ": " + messageToSend, encryption);
                writer.write(cryption.encrypt());
            } else {
                writer.write(username + ": " + messageToSend);
            }
            writer.newLine();
            writer.flush();
            gui.setEditorPanelText(messageToSend);

        } catch (IOException e) {
            closeEverything(socket, reader, writer);
        }
    }

    public void listenForMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromChat;
                while (socket.isConnected()) {
                    try {
                        msgFromChat = reader.readLine();
                        if (msgFromChat.contains("SERVER: ")) gui.setEditorPanelText(msgFromChat);
                        else {
                            String encryption = gui.getEncryption();

                            if (!Objects.equals(encryption, "no encryption")) {
                                Cryption cryption = new Cryption(gui.getKey(), msgFromChat, encryption);
                                msgFromChat = cryption.decrypt();
                            }
                            gui.setEditorPanelText(msgFromChat);
                        }
                    } catch (IOException e) {
                        closeEverything(socket, reader, writer);
                    }
                }

            }
        }).start();
    }


    public void closeEverything(Socket socket, BufferedReader reader, BufferedWriter writer) {
        try {
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
