import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private static JEditorPane ep;
    static Gui gui;
    /**
     * Der Socket des Servers, der der zum Verbinden zwischen Client und Server nötig ist.
     */
    private ServerSocket serverSocket;

    /**
     * Konstruktor weist dem Server-Objekt den übergebenen Serversocket zu
     * @param serverSocket
     */
    public Server(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }

    /**
     * neues ServerSocket-Objekt, serversocket mit bestimmtem Port instanziiert.
     * neues Server-Objekt mit zuvor erstelltem Socket instanziiert.
     * auf dem Server-Objekt wird die Startfunktion aufgerufen. (Endlosschleife)
     * @param args
     * @throws IOException (Wenn man mit Sockets arbeitet, kann es immer Input/Output Exceptions geben)
     */
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(4473);
        gui = new Gui("Server");
        gui.setVisible(true);
        Server server = new Server(serverSocket);
        server.start();
    }

    /**
     * Methode start die in einer theoretischen endlosschleife solange der Server läuft.
     * Jeder Socket der sich versucht zu verbinden wird angenommen.
     * gibt aus: "A new client has connected".
     * erstellt einen neuen ClientHandler, dem der Socket übergeben wird.
     * In einem neuen Thread wird die Start-Methode des ClientHandlers aufgerufen.
     *
     * wenn es einen Fehler gibt, wird die Methode close everything aufgerufen
     */
    public void start() {
        try{
            while (!serverSocket.isClosed()){
                Socket socket = serverSocket.accept();
                gui.setEditorPanelText("A new Client has Connected!", 0);
                ClientHandler clientHandler = new ClientHandler(socket);
                Thread client = new Thread(clientHandler);
                client.start();
            }
        } catch (IOException e) {
            closeServerSocket();
        }
    }

    /**
     * versucht den ServerSocket zu schließen, wenn er vorhanden ist.
     */
    public void closeServerSocket() {
        try {
            if (serverSocket != null){
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

//TODO: nachgucken warum es Input/output exceptions gibt bei sockets
// Versuchen das nicht jeder socket der sich versucht zu verbinden angenommen wird
