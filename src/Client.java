import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

public class Client {

    private Socket socket;
    private BufferedWriter writer;
    private BufferedReader reader;
    private String username;
    static Gui gui;
    private int number;
    public static ArrayList<Client> clients = new ArrayList<>();

    public Client(Socket socket, String username, int number) {
        try {
            this.socket = socket;
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
            this.username = username;
            this.number = number;
        } catch (IOException e) {
            closeEverything(socket, reader, writer);
        }
    }

    public static void main(String[] args) throws IOException {
        gui = new Gui("Client");
        gui.setTabbedPane();
        gui.addClientGui(Gui.jPanels.get(0));
        gui.setVisible(true);
        Socket socket = new Socket("localhost", 4473);
        Client client = new Client(socket, gui.getUserName(), 0);
        clients.add(client);
        client.sendUsername();
        client.listenForMessage(client.getNumber());
        gui.addWritingArea(client);
    }

    public static void createNewConnection(String IP, String username, int port, int number) {
        Socket socket = null;
        try {
            socket = new Socket(IP, port);
            Client client = new Client(socket, username, number);
            clients.add(client);
            gui.addClientGui(Gui.jPanels.get(client.getNumber()));
            client.sendUsername();
            client.listenForMessage(client.getNumber());
            gui.addWritingArea(client);
        } catch (IOException e) {
            throw new RuntimeException(e);

        }
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

    public void sendMessage(String messageToSend, int clientIndex) {
        try {
            String encryption = gui.getEncryption(number);
            String key = gui.getKey(number);

            if (!Objects.equals(encryption, "no encryption")) {
                Cryption cryption = new Cryption(key, username + ": " + messageToSend, encryption);
                writer.write(cryption.encrypt());
            } else {
                writer.write(username + ": " + messageToSend);
            }
            writer.newLine();
            writer.flush();
            gui.setEditorPanelText(messageToSend, clientIndex);

        } catch (IOException e) {
            closeEverything(socket, reader, writer);
        }
    }

    public void listenForMessage(int number) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromChat;
                while (socket.isConnected()) {
                    try {
                        msgFromChat = reader.readLine();
                        if (msgFromChat.contains("SERVER: ")) gui.setEditorPanelText(msgFromChat, number);
                        else {
                            String encryption = gui.getEncryption(number);
                            String key = gui.getKey(number);

                            if (!Objects.equals(encryption, "no encryption")) {
                                Cryption cryption = new Cryption(key, msgFromChat, encryption);
                                msgFromChat = cryption.decrypt();
                            }
                            gui.setEditorPanelText(msgFromChat, number);
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

    public int getNumber() {
        return number;
    }
}
