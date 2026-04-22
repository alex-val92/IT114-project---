import java.io.*;
import java.net.*;
import java.util.*;
public class AuctionServer{ 
    private static final int Port = 5000;
    public static int currBid = 0;
    private static String currBidder = "No one";
    public static List<AuctionClientHandler> clients = new ArrayList<>();

    public static void main(String[] args) {
        try(ServerSocket server = new ServerSocket(Port)){ //try-with-resources to ensure the server socket is closed properly
            System.out.println("The Deal: Server Live on Port" + Port);

            while (true) /*handle concurrent connections using a thread per client model*/ {
                Socket clientSocket = server.accept();
                AuctionClientHandler handler = new AuctionClientHandler(clientSocket);
                clients.add(handler);// Add the new client handler to the list of clients
                new Thread(handler).start();
            }
        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
        }//Try block throws IOException if there is an error starting the server or accepting a client connection
    }


    public static synchronized String placeBid (String bidderName, int biggestBid) {//synchronized to ensure thread safety when multiple clients place bids simultaneously
        if (biggestBid > currBid) {
            currBid = biggestBid;
            currBidder = bidderName;
            String highestBidUp = "Current Highest Bid" + currBid + ": by " + currBidder;
            broadcast(highestBidUp);
            return "You're the top bidder @ $%.2f " + biggestBid;
        }else {
            return "Better luck next time bid too low highestbid is $%.2f " + currBid;
        }
    }

    public static void broadcast (String message) /*pushes updates to every active socket */{  
        for (AuctionClientHandler client : clients){
            client.sendMessage(message);
        }
    }

    public static void removeClient (AuctionClientHandler handler){
        clients.remove(handler);
        System.out.println("Inactive client removed. Active Bidders: " + clients.size());
    }
}