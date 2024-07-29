/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package chatapa_ei;

/**
 *
 * @author KESHINI B
 */
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

// Entry point for the chat application
public class ChatApplication {

    public static void main(String[] args) {
        try {
            int port = 12346; // Port number (change if needed)
            ChatServer server = new ChatServer(port);
            server.start();
        } catch (IOException e) {
            System.err.println("Server failed to start: " + e.getMessage());
        }
    }
}

// Singleton class to manage chat rooms
class ChatRoomManager {
    private static ChatRoomManager instance;
    private final Map<String, ChatRoom> chatRooms = new ConcurrentHashMap<>();

    private ChatRoomManager() {}

    public static synchronized ChatRoomManager getInstance() {
        if (instance == null) {
            instance = new ChatRoomManager();
        }
        return instance;
    }

    public ChatRoom getChatRoom(String roomId) {
        return chatRooms.computeIfAbsent(roomId, ChatRoom::new);
    }
}

// Chat room class
class ChatRoom {
    private final String roomId;
    private final List<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private final List<String> messageHistory = new CopyOnWriteArrayList<>();

    public ChatRoom(String roomId) {
        this.roomId = roomId;
    }

    public void addClient(ClientHandler client) {
        clients.add(client);
        client.sendMessage("Welcome to chat room: " + roomId);
        sendMessageToClient(client, messageHistory);
        broadcastMessage(client.getUsername() + " has joined the chat room.");
    }

    public void removeClient(ClientHandler client) {
        clients.remove(client);
        broadcastMessage(client.getUsername() + " has left the chat room.");
    }

    public void broadcastMessage(String message) {
        messageHistory.add(message);
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public void sendMessageToClient(ClientHandler client, List<String> messages) {
        for (String message : messages) {
            client.sendMessage(message);
        }
    }

    public List<String> getActiveUsers() {
        List<String> usernames = new ArrayList<>();
        for (ClientHandler client : clients) {
            usernames.add(client.getUsername());
        }
        return usernames;
    }

    public String getRoomId() {
        return roomId;
    }
}

// Server class to handle incoming client connections
class ChatServer {
    private final int port;

    public ChatServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Chat server started on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
            throw e;
        }
    }
}

// Handler class to manage individual client connections
class ClientHandler extends Thread {
    private final Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private String userName;
    private ChatRoom currentChatRoom;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    public String getUsername() {
        return userName;
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            out.println("Enter your username:");
            userName = in.readLine();

            out.println("Enter chat room ID to join:");
            String roomId = in.readLine();
            currentChatRoom = ChatRoomManager.getInstance().getChatRoom(roomId);
            currentChatRoom.addClient(this);

            String message;
            while ((message = in.readLine()) != null) {
                currentChatRoom.broadcastMessage(userName + ": " + message);
            }
        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            try {
                if (currentChatRoom != null) {
                    currentChatRoom.removeClient(this);
                }
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage());
            }
        }
    }
}


