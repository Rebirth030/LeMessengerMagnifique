import com.sun.tools.javac.Main;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

   private Socket socket;
   private BufferedWriter writer;
   private BufferedReader reader;
   private String username;

   public Client(Socket socket, String username){
      try {
         this.socket = socket;
         this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
         this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
         this.username = username;
      } catch (IOException e) {
         closeEverything(socket, reader, writer);
      }
   }

   public static void main(String[] args) throws IOException {

      Scanner scanner = new Scanner(System.in);
      System.out.println("Enter your username for the group chat: ");
      String username = scanner.nextLine();
      Socket socket = new Socket("localhost",4473);
      Client client = new Client(socket, username);
      client.listenForMessage();
      client.sendMessage();
   }

   public void sendMessage() {
      try {
         writer.write(username);
         writer.newLine();
         writer.flush();

         Scanner scanner = new Scanner(System.in);
         while (socket.isConnected()) {
            String messageToSend = scanner.nextLine();
            //decrypt
            writer.write(username + ": " + messageToSend);
            writer.newLine();
            writer.flush();
         }
      } catch (IOException e) {
         closeEverything(socket, reader, writer);
      }
   }

   public void listenForMessage(){
      new Thread(new Runnable() {
         @Override
         public void run() {
            String msgFromChat;

            while (socket.isConnected()) {
               try {
                  msgFromChat = reader.readLine();
                  System.out.println(msgFromChat);
               } catch (IOException e) {
                  closeEverything(socket, reader, writer);
               }
            }

         }
      }).start();
   }

   public void closeEverything(Socket socket, BufferedReader reader, BufferedWriter writer) {
      try{
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
