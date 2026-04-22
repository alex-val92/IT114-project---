import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class AuctionServer {
    private static final int Port = 5000;
    private static double currBid = 0.0;
    private static String currBidder = "No one";
    public static List<AuctionClientHandler> clients = new ArrayList<>();

    public static void main(String[] args) {
        try(ServerSocket server = new ServerSocket(Port))//listen for incoming client connections on dedicated port{
            System.out.println("The Deal: Server Live on Port" + Port);

            while (true) /*handle concurrent connections using a thread per client model*/ {
                Socket clientSocket = server.accept();
                AuctionHandler handler = new AuctionHandler(clientSocket);
                clients.add(handler);// Add the new client handler to the list of clients
                new Thread(handler).start();
            }
        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
        }//Try block throws IOException if there is an error starting the server or accepting a client connection
    }


    public static synchrosnized String placeBid (String bidderName, double biggestBid) /* thread safe method using synchronized to prevent race conditions during high volume bidding  */ {
        if (bidAmount > currBid) {
            currBid = biggestBid;
            currBidder = bidderName;
            String highestBidUp = "Current Highest Bid" + currBid + ": by " + currBidder;
            broadcast(highestBidUp);
            return "You're the top bidder @ $ " + biggestBid;
        }else {
            return "Better luck next time bid too low highet bid is $ " + currBid;
        }
    }

    public static void broadcast (String message) /*pushes updates to every active socket */{  
        for (AuctionHandler client : clients){
            client.sendMessage(message);
        }
    }

    public static void removeClient (AuctionHandler handler){
        clients.remove(handler);
        System.out.println("Inactive client removed. Active Bidders: " + clients.size());
    }
}