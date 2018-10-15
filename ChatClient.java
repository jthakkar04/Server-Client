import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

final class ChatClient {
    private ObjectInputStream sInput;
    private ObjectOutputStream sOutput;
    private Socket socket;

    private final String server;
    private final String username;
    private final int port;

    /* ChatClient constructor
     * @param server - the ip address of the server as a string
     * @param port - the port number the server is hosted on
     * @param username - the username of the user connecting
     */

    private ChatClient(String server, int port, String username) {
        this.server = server;
        this.port = port;
        this.username = username;
    }

    public ChatClient(String username, int port) {
        this.username = username;
        this.port = port;
        this.server = "localhost";
    }

    public ChatClient(String username) {
        this.username = username;
        this.port = 1500;
        this.server = "localhost";
    }

    public ChatClient() {
        this.username = "CS180 Student";
        this.port = 1500;
        this.server = "localhost";
    }

    /**
     * Attempts to establish a connection with the server
     * @return boolean - false if any errors occur in startup, true if successful
     */
    private boolean start() {
        // Create a socket
        try {
            socket = new Socket(server, port);
        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("Could not connect. Server is not on!");
            return false;
        }

        // Attempt to create output stream
        try {
            sOutput = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        // Attempt to create input stream
        try {
            sInput = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        // Create client thread to listen from the server for incoming messages
        Runnable r = new ListenFromServer();
        Thread t = new Thread(r);
        t.start();

        // After starting, send the clients username to the server.
        try {
            sOutput.writeObject(username);
        } catch (IOException e) {
            e.printStackTrace();

        }

        return true;
    }


    /*
     * Sends a string to the server
     * @param msg - the message to be sent
     */
    private void sendMessage(ChatMessage msg) {
        try {
            sOutput.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /*
     * To start the Client use one of the following command
     * > java ChatClient
     * > java ChatClient username
     * > java ChatClient username portNumber
     * > java ChatClient username portNumber serverAddress
     *
     * If the portNumber is not specified 1500 should be used
     * If the serverAddress is not specified "localHost" should be used
     * If the username is not specified "Anonymous" should be used
     */

    public void close() {
        try {
            socket.close();
            sInput.close();
            sOutput.close();
            sInput = null;
            socket = null;
        } catch (IOException e) {
            System.out.println("Logged Out.");
        }
    }

    public static void main(String[] args) {
        // Get proper arguments and override defaults

        System.out.println("Hi. This is a greeting.");
        Scanner scanner = new Scanner(System.in);

        String name = "";
        String address = "localhost";
        int portNum = 1500;

        if (args.length == 1)
            name = args[0];
        else {
            System.out.print("Please type a username. ");
            name = scanner.nextLine();
        }
        if (args.length == 2){
            name = args[0];
            try {
                portNum = Integer.parseInt(args[1]);
            } catch (Exception e) {
                System.out.println("Invalid port number!");
                return;
            }
        }
        if (args.length == 3){
            address = args[2];
            try {
                portNum = Integer.parseInt(args[1]);
            } catch (Exception e) {
                System.out.println("Invalid port number!");
                return;
            }
            name = args[0];
        }


        // Create your client and start it
        ChatClient client = new ChatClient(address, portNum, name);
        if (!client.start()) {
            return;
        }


        // Send an empty message to the server

        while (true) {
            String message = scanner.nextLine();
            message = message.toLowerCase();
            if (message.contains("/ttt")) {
                String[] messageArray = message.split(" ");
                if (messageArray.length <= 1) {
                    System.out.println("Dude do this right!!!");
                    continue;
                }
                if(messageArray[1].equals(name)) {
                    System.out.println("Error.");
                    continue;
                }
                client.sendMessage(new ChatMessage(ChatMessage.TICTACTOE,message, messageArray[1]));


                    ///     /tt <recipent> <placement>


            } else if (message.equals("/logout")){
                client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, " " + name + " disconnected", name));
                client.close();
                break;

            }  else if (message.equals("/list")) {
//                System.out.println(name + "requested a list of users.");
                client.sendMessage(new ChatMessage(ChatMessage.LIST, " ",name));

            }  else if (message.contains("/msg")){
                String[] messageArray = message.split(" ");
                if(messageArray[1].equals(name)) {
                    System.out.println("Error. You cannot slide into your own DM.");
                }

                else {
                    int index = message.indexOf(messageArray[2]);
                    client.sendMessage(new ChatMessage(ChatMessage.DM, message.substring(index), messageArray[1]));
                }
            } else {
                client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, message, name));
            }
        }
    }


    /*
     * This is a private class inside of the ChatClient
     * It will be responsible for listening for messages from the ChatServer.
     * ie: When other clients send messages, the server will relay it to the client.
     */
    private final class ListenFromServer implements Runnable {
        public void run() {
            while (true) {
                try {
                    String msg = (String) sInput.readObject();
                    System.out.print(msg);
                } catch (Exception e) {
                    System.out.println("The client or server was disconnected.");
                    System.exit(0);
                } /*catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                } */
            }

        }
    }
}
