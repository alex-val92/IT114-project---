import java.io.*;
import java.net.*;
import java.util.*;

public class AuctionServer {
    private static final int Port = 5000;
    public static int currBid = 0;
    private static String currBidder = "No one";
    public static List<AuctionClientHandler> clients = new ArrayList<>();
    
    // Add these to track the current image index
    private static int currImageIndex = 0;
    private static Random random = new Random();

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(Port)) {
            System.out.println("The Deal: Server Live on Port " + Port);
            
            // Pick the first random image when the server starts
            currImageIndex = random.nextInt(3); 

            while (true) {
                Socket clientSocket = server.accept();
                AuctionClientHandler handler = new AuctionClientHandler(clientSocket);
                clients.add(handler);
                new Thread(handler).start();
                
                // CRITICAL: Tell the NEW client which image is currently active
                handler.sendMessage("IMAGE:" + currImageIndex);
            }
        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
        }
    }

    public static synchronized String placeBid(String bidderName, int biggestBid) {
        if (biggestBid > currBid) {
            currBid = biggestBid;
            currBidder = bidderName;
            String highestBidUp = "Current Highest Bid " + currBid + ": by " + currBidder;
            broadcast(highestBidUp);
            return "You're the top bidder @ $ " + biggestBid;
        } else {
            return "Better luck next time bid too low highestbid is $ " + currBid;
        }
    }

    public static void nextItem() {
        currBid = 0;
        currBidder = "No one";
        currImageIndex = random.nextInt(2); // Pick a new random image (0, 1, or 2)
        broadcast("IMAGE:" + currImageIndex);
        broadcast("--- NEW ITEM UP FOR BID ---");
    }

    public static void broadcast(String message) {
        for (AuctionClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public static void removeClient(AuctionClientHandler handler) {
        clients.remove(handler);
        System.out.println("Inactive client removed. Active Bidders: " + clients.size());
    }
}