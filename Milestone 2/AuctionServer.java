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
        try(ServerSocket server = new ServerSocket(Port)){
            System.out.println("The Deal: Server Live on Port" + Port);

            while (true) {
                Socket clientSocket = server.accept();
                AuctionHandler handler = new AuctionHandler(clientSocket);
                clients.add(handler);// Add the new client handler to the list of clients
                new Thread(handler).start();
            }
        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
        }//Try block throws IOException if there is an error starting the server or accepting a client connection
    }
}

public static synchrosnized String placeBid (String bidderName, double biggestBid) {
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