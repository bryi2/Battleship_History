import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.Scanner;
import java.util.function.Consumer;

public class client extends Thread {

    Socket socketClient;
    Scanner scanner;
    private Consumer<Serializable> callback;
    DataInputStream dis;
    DataOutputStream dos;
    //constructor
    client(Consumer<Serializable> call){
        callback = call;
    }
    public void run() {
        try {
            socketClient = new Socket("127.0.0.1", 5555);
            // obtaining input and out streams
            dis = new DataInputStream(socketClient.getInputStream());
            dos = new DataOutputStream(socketClient.getOutputStream());
            socketClient.setTcpNoDelay(true);
            System.out.println("Client connected");
        } catch (Exception e) {
            System.out.println("Client didnt connect!");
        }
        // this is where client is receiving data from server
        while (true) {
            try {
                // read message sent to this client
                String msg = dis.readUTF();
                //now lets check the information sent from server
                callback.accept(msg);
            } catch (Exception e) {
                System.out.println("Read Chat error! " + "client: 41");
                e.printStackTrace();
            }
        }// end of while
    }// end of run
    // client sending chat
    public void send(String message) {
        try {
            dos.writeUTF(message);
        } catch (IOException e) {
            System.out.println("Send Chat Error! " + "client: 51");
            e.printStackTrace();
        }
    }
}