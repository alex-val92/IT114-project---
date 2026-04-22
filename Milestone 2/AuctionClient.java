import java.io.*;
import java.net.*;
import java.util.*;
public class AuctionClient {
    public static void main(String[] args) {
        String hostname = "localhost";
        int port = 5000;
        try (Socket socket = new Socket(hostname, port)) {//try-with-resources to ensure the socket is closed properly
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);// Set up output stream to send messages to the server
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));// Set up input stream to receive messages from the server
            Scanner scan = new Scanner(System.in);// Scanner for reading user input from the console


            new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = in.readLine()) != null) {// Thread to listen for messages from the server and print them to the console
                        System.out.println("[Server]: " + serverMessage);
                        System.out.print("Enter your name: ");
                    }
                }catch (IOException e) {
                    System.err.println("Error reading from server: Disconnected" );
                }
            }).start();

            System.out.println("Connected to The Deal Auction Server!");// Main thread handles user input and sends it to the server
            String name = scan.nextLine();
            out.println(name);

            while(scan.hasNextLine()) {
                String bid = scan.nextLine();
                out.println(bid);
            }
            
        }catch (UnknownHostException ex) {// Handle case where the server hostname is unknown
            System.err.println("Unknown host: " + hostname);
        } catch (IOException ex) {// Handle I/O errors that occur when trying to connect to the server or during communication
            System.err.println("I/O error: " + ex.getMessage());
        }
    } 
}