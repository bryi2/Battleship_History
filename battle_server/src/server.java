import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.scene.control.ListView;

public class server {
    int count = 1; // clientID
    boolean gameReady = false;
    ArrayList<ClientThread> clients = new ArrayList<ClientThread>(); // list of clients
    ArrayList<String> parsedString = new ArrayList<String>();
    volatile ArrayList<String> playerName = new ArrayList<String>();
    TheServer server;
    private Consumer<Serializable> callback, callback2, callback3;

    // constructor
    server(Consumer<Serializable> call, Consumer<Serializable> call2, Consumer<Serializable> call3) {
        // 3 separate list views = call backs
        // player connected list
        callback = call;
        callback2 = call2;
        callback3 = call3;
        server = new TheServer();
        server.start();
    }

    public class TheServer extends Thread {


        public void run() {
            try (ServerSocket mysocket = new ServerSocket(5555)) {
                System.out.println("Server is waiting for a client!");
                while (true) {
                    ClientThread c = new ClientThread(mysocket.accept(), count);
                    callback.accept("client has connected to server: " + "client #" + count);
                    clients.add(c);
                    // check size of arraylist to determine if client connected
                    //					System.out.println("The size of the list is: " + playerName.size());
                    c.start();
                    count++;
                }
            } // end of try
            catch (Exception e) {
                callback.accept("Server socket did not launch");
            }
        }// end of while
    }
    class ClientThread extends Thread {
        Socket connection;
        int count;
        DataInputStream dis;
        DataOutputStream dos;
        boolean flag = false;
        // constructor per client/thread
        ClientThread(Socket s, int count) {
            this.connection = s;
            this.count = count;
        }
        public void gameWaitingClients() {
            for(int i = 0; i < clients.size(); i++) {
                ClientThread t = clients.get(i);
                try {
                    t.dos.writeUTF("Waiting on another player to join");
                }
                catch(Exception e) {}
            }
        }
        public void updateClients(String message) {
            for(int i = 0; i < clients.size(); i++) {
                ClientThread t = clients.get(i);
                try {
                    t.dos.writeUTF(message);
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public void updateOtherPlayer(boolean hit, int row, int column) {
            System.out.println("In updateOtherPlayer()");
            for (int i = 0; i < clients.size(); i++) {
                ClientThread t = clients.get(i);
                if (count != t.count) {
                    System.out.println("Updating client " + t.count);
                    if (hit) {
                        try {
                            String out = "#H " + row + " " + column;
                            t.dos.writeUTF(out);
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            String out = "#M " + row + " " + column;
                            t.dos.writeUTF(out);
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        public void updateSelf(boolean hit, int row, int column) {
            System.out.println("In updateSelf()");
            for (int i = 0; i < clients.size(); i++) {
                ClientThread t = clients.get(i);
                if (count == t.count) {
                    System.out.println("Updating client " + t.count);
                    if (hit) {
                        try {
                            String out = "#SH " + row + " " + column;
                            t.dos.writeUTF(out);
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            String out = "#SM " + row + " " + column;
                            t.dos.writeUTF(out);
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                }
            }
        }
        public void parseString(String msg) {
            String parts[] = msg.split("/");
            for (String t: parts) {
                System.out.print(t + " ");
                parsedString.add(t);
            }
        }
        public void run() {
            String received;
            try {
                dis = new DataInputStream(connection.getInputStream());
                dos = new DataOutputStream(connection.getOutputStream());
                connection.setTcpNoDelay(true);
            } catch (Exception e) {
                System.out.println("Streams not open");
            }
            while (true) {
                try {
                    // incoming string from client
                    received = dis.readUTF();
                    // split string using split method(delimiter)
                    parseString(received);
                    // beginning of switch
                    switch (parsedString.get(0)) {
                        // attack mode from gameplay
                        case "#A":
                            String row = parsedString.get(2);
                            String column = parsedString.get(1);
                            parsedString.clear();
                            int r = Integer.parseInt(row);
                            int c = Integer.parseInt(column);
                            boolean hit = serverLogic.checkHitMiss(r, c, count, flag);
                            if(hit) {
                                callback2.accept("Yes this is a hit!");
                                updateOtherPlayer(true, r, c);
                                updateSelf(true, r, c);
                            } else {
                                updateOtherPlayer(false, r, c);
                                updateSelf(false, r, c);
                            }
                            break;
                        // add positions of players to their board
                        case "#B":
                            // get row
                            String row2 = parsedString.get(2);
                            // get column
                            String column2 = parsedString.get(1);
                            parsedString.clear();
                            // convert to int
                            int r0 = Integer.parseInt(row2);
                            int c0 = Integer.parseInt(column2);
                            // send to board from serverLogic
                            System.out.println("Before adding to Board: row= "+r0 +" and column: "+c0);
                            serverLogic.addToBoard1(r0,c0, count);
                            serverLogic.printGameBoard1();
                            break;
                        // add player name and check game ready
                        case "#R":
                            playerName.add(parsedString.get(1));
                            // increase player count in server whenever a new client connection is made
                            serverLogic.addPlayer();
                            //System.out.println("inside 107 server");
                            if(serverLogic.gameReady()) {
                                updateClients("We have two Players, game is ready to play. Please Click Join Game");
                                parsedString.clear();
                                continue;
                            }
                            if(serverLogic.numPlayers < 2) {
                                dos.writeUTF("Waiting on another player to join");
                                parsedString.clear();
                                continue;
                            }
                            break;
                        // chatroom messaging
                        default:
                            //
                            // Warning
                            // It does not work, if one of the player has sent something before the other
                            // player join
                            //
                            // In order to work this, the both players has to be joined, then you can chat
                            // each other
                            String clientSaid = (playerName.get( count -1 ) + ": " + received);
                            callback2.accept(clientSaid);
                            updateClients(clientSaid);
                    }
                } catch (Exception e) {
                    callback.accept(
                            "OOOOPPs...Something wrong with the socket from client: " + count + "....closing down!");
                    clients.remove(this);
                    e.printStackTrace();
                    break;
                } // end of catch
            } // end of while loop
        }// end of run
    }// end of client thread
}