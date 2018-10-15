import javax.swing.border.TitledBorder;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

final class ChatServer {
    private static int uniqueId = 0;
    // Data structure to hold all of the connected clients
    private final List<ClientThread> clients = new ArrayList<>();
    private final int port;			// port the server is hosted on
    private SimpleDateFormat sdf;
    private ArrayList<TikTakToe> boards = new ArrayList<>();
    private HashMap<String, ClientThread> hashMap = new HashMap<>();


    /**
     * ChatServer constructor
     * @param port - the port the server is being hosted on
     */
    private ChatServer(int port) {
        this.port = port;
        this.sdf = new SimpleDateFormat("HH:mm:ss");

    }

    public ChatServer() {
        this.port = 1500;
    }

    /*
     * This is what starts the ChatServer.
     * Right now it just creates the socketServer and adds a new ClientThread to a list to be handled
     */
    private void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                Socket socket = serverSocket.accept();
                Runnable r = new ClientThread(socket, uniqueId++);
                Thread t = new Thread(r);
                ClientThread clientThread = (ClientThread) r;

                if (hashMap.containsKey(clientThread.username)) {
                    clientThread.writeMessage("This Username is already taken! :) \n");
                    clientThread.close();
                } else {
                    hashMap.put(clientThread.username, clientThread);
                    t.start();
                }

//                clients.add((ClientThread) r);
//                t.start();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void broadcast(String message){
        String newMessage = sdf.format(new Date()) + " " + message + "\n";
        System.out.print(newMessage);

        for (String user : hashMap.keySet()) {
            if (!hashMap.get(user).writeMessage(newMessage)) {
                hashMap.remove(user);
                System.out.println(user + " Disconnected");
            }
        }
//        for(int i = clients.size() - 1; i >= 0; i--) {
//            ClientThread clientThread = clients.get(i);
//            if(!clientThread.writeMessage(newMessage)) {
//                System.out.println("Client Disconnected");
//                clients.remove(i);
//            }
//        }
    }

    private synchronized void remove (int id) {
        for (String user : hashMap.keySet()) {
            if (hashMap.get(user).id == id) {
                hashMap.remove(user);
                return;
            }
        }
//        for (int i = 0; i < clients.size(); i++) {
//            ClientThread clientThread = clients.get(i);
//            if (clientThread.id == id){
//                clients.remove(i);
//                return;
//            }
//
//        }
    }




    /*
     *  > java ChatServer
     *  > java ChatServer portNumber
     *  If the port number is not specified 1500 is used
     */
    public static void main(String[] args) {
        ChatServer server = new ChatServer(1500);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        System.out.println(sdf.format(new Date()) + " Server waiting for clients on port " + server.port);
        server.start();
    }


    /*
     * This is a private class inside of the ChatServer
     * A new thread will be created to run this every time a new client connects.
     */
    private final class ClientThread implements Runnable {
        Socket socket;                  // The socket the client is connected to
        ObjectInputStream sInput;       // Input stream to the server from the client
        ObjectOutputStream sOutput;     // Output stream to the client from the server
        String username;                // Username of the connected client
        ChatMessage cm;                 // Helper variable to manage messages
        int id;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

        /*
         * socket - the socket the client is connected to
         * id - id of the connection
         */
        private ClientThread(Socket socket, int id) {
            this.id = id;
            this.socket = socket;
            try {
                sOutput = new ObjectOutputStream(socket.getOutputStream());
                sInput = new ObjectInputStream(socket.getInputStream());
                username = (String) sInput.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        private void close() {
            try {
                if (sInput != null)
                    sInput.close();
                if (sOutput != null)
                    sOutput.close();
                if (socket != null)
                    socket.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        private boolean writeMessage(String msg){
            System.out.println("Printing a message to : " + username + "\n" + msg);
            if(!socket.isConnected()) {
                System.out.println("Connection is closed");
                close();
                return false;
            }
            try {
                sOutput.writeObject(msg);
            } catch (IOException e) {
                System.out.println(username + " has been disconnected.");
                return false;
            }
            return true;
        }

        /**
         *	Sample code to use as a reference for Tic Tac Toe
         *
         * directMessage - sends a message to a specific username, if connected
         * @param message - the string to be sent
         * @param recepient_username - the user the message will be sent to
         */
        private synchronized void directMessage(String message, String recepient_username) {
            String newMessage = sdf.format(new Date()) + " " + message + "\n";
            System.out.print(newMessage);

            if (hashMap.containsKey(recepient_username)) {
                hashMap.get(recepient_username).writeMessage(newMessage);
                hashMap.get(this.username).writeMessage(newMessage);
            } else {
                hashMap.get(this.username).writeMessage("Username does not exist!!");
            }

//            for (ClientThread clientThread : clients) {
//                if (clientThread.username.equalsIgnoreCase(username)) {
//                    clientThread.writeMessage(newMessage);
//                }
//                if (clientThread.username.equalsIgnoreCase(this.username)) {
//                    clientThread.writeMessage(newMessage);
//                }
//            }
        }

        /*
         * This is what the client thread actually runs.
         */
        @Override
        public void run() {
            // Read the username sent to you by client
            System.out.println(sdf.format(new Date()) + username + " Connected!");
            System.out.println(sdf.format(new Date()) + " Server waiting for clients on port " + port);

            while (true) {
                try {
                    cm = (ChatMessage) sInput.readObject();
                } catch (IOException | ClassNotFoundException e) {
//                    e.printStackTrace();
                }
//            System.out.println(username + ": Ping");
                if ( cm != null) {
                    String message = cm.getMessage();
                    String recipient = cm.getRecipient();

                    if (cm.getType() == ChatMessage.MESSAGE) {
                        broadcast( username + ": " + message);
                    } else if (cm.getType() == ChatMessage.LOGOUT) {
                        broadcast( username + ": " + message);
                        try {
                            socket.close();
                        } catch (IOException e) {
                            System.out.println("Error");
                        }
                        break;
                    } else if (cm.getType() == ChatMessage.LIST) {
                        System.out.println("There was a request for a list of users.");
                        writeMessage("Users @ " + sdf.format(new Date()) + "\n");
                        int i = 1;
                        for (String username : hashMap.keySet()) {
                            writeMessage((i++) + ": " + username + "\n");
                        }
//                        int j = 1;
//                        for (int i = 0; i < clients.size(); ++i) {
//                            ClientThread clientThread = clients.get(i);
//                            writeMessage(j + ": " + clientThread.username + "\n");
//                            j++;
//                        }
                    } else if (cm.getType() == ChatMessage.DM) {
                        directMessage(username + " -> " + recipient + ": " + message, recipient);

                    } else if (cm.getType() == ChatMessage.TICTACTOE) {
                        if (!hashMap.containsKey(recipient)) {
                            writeMessage("User does not exist!!");
                            continue;
                        }

                        System.out.println(username + " : " + recipient);

                        String[] messages = message.split(" ");
                        if (messages.length > 2) {
                            // find game
                            int box = 0;
                            try {
                                box = Integer.parseInt(messages[2]);
                                if (box < 0 || box > 8) {
                                    throw new Exception();
                                }
                            } catch (Exception e) {
                                writeMessage("Enter a vaild number");
                                continue;
                            }

                            boolean found = false;

                            for (TikTakToe board : boards) {
                                if (board.checkUser(username, recipient)) {
                                    System.out.println("Found a board!!!");
                                    board.insert(username, box);
                                    writeMessage(board.getBoard());
                                    hashMap.get(recipient).writeMessage(board.getBoard());
                                    found = true;
                                }
                            }

                            if (!found)
                                writeMessage("Game not started yet!");
                        } else {
                            boolean found = false;
                            for (TikTakToe board : boards) {
                                if (board.checkUser(username, recipient)) {
                                    writeMessage("Fuck you!!");
                                    found = true;
                                    break;
                                }
                            }

                            if (!found) {
                                System.out.println("Started TicTacToe with " + recipient + ".");
                                TikTakToe tikTakToe = new TikTakToe(this, hashMap.get(recipient));
                                boards.add(tikTakToe);
                                this.writeMessage(tikTakToe.getBoard());
                                hashMap.get(recipient).writeMessage(tikTakToe.getBoard());
                            }
                        }

//                        /ttt

//                        /ttt jagat index asdasd asdas


//                            String playerX = username;
//                            String playerO = cm.getRecipient();
//
//                            TicTacToeGame game = new TicTacToeGame(playerO);
////                            game.printScreen();




                    }
                }
                else
                    break;
                // Send message back to the client
//                try {
//                    sOutput.writeObject("Pong");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }
            remove(id);
            close();
        }
    }

    public class TikTakToe {
        private ClientThread client1;
        private char symbol_user1;
        private ClientThread client2;
        private char symmbol_user2;
        private char[][] board;
        char currentPlayer = 'X';

        public TikTakToe(ClientThread client1, ClientThread client2) {
            this.client1 = client1;
            this.symbol_user1 = 'X';
            this.client2 = client2;
            this.symmbol_user2 = 'O';
            this.board = new char[3][3];

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    board[i][j] = ' ';
                }
            }
        }

        public boolean checkUser(String username_1, String username_2) {
            if ((client1.username.equals(username_1) || client2.username.equals(username_1)) &&
                    (client1.username.equals(username_2) || client2.username.equals(username_2))) {
                return true;
            }

            return false;
        }

        public void insert(String username, int value) {
            if (!client1.username.equals(username) && !client2.username.equals(username)) {
                return;
            }

            if (username.equals(client2.username) && currentPlayer != symmbol_user2) {
                client2.writeMessage("Not your turn.");
                return;
            }

            if (username.equals(client1.username) && currentPlayer != symbol_user1) {
                client1.writeMessage("Not your turn");
                return;
            }

            if (value < 0 || value > 8) {
                return;
            }

            if (value == 0) {
                board[0][0] = currentPlayer;
            } else if (value == 1) {
                board[0][1] = currentPlayer;
            } else if (value == 2) {
                board[0][2] = currentPlayer;
            } else if (value == 3) {
                board[1][0] = currentPlayer;
            } else if (value == 4) {
                board[1][1] = currentPlayer;
            } else if (value == 5) {
                board[1][2] = currentPlayer;
            } else if (value == 6) {
                board[2][0] = currentPlayer;
            } else if (value == 7) {
                board[2][1] = currentPlayer;
            } else {
                board[2][2] = currentPlayer;
            }

            if (currentPlayer == symbol_user1) {
                currentPlayer = symmbol_user2;
            } else {
                currentPlayer = symbol_user1;
            }
        }

        public String getBoard() {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
//                    System.out.println(board[i][j]);
                    stringBuilder.append(" " + board[i][j] + " ");
                    if (j < 2)
                        stringBuilder.append('|');
                }
                stringBuilder.append("\n-----------\n");
            }

            return stringBuilder.toString();
        }
    }
}