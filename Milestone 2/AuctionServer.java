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
            System.out.println("The Deal: Server Live on Port" + Port);")
        }
    }
}