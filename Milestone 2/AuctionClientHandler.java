import java.net.*;
import java.io.*;
public class AuctionClientHandler implements Runnable {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private String bidderName;

        public AuctionClientHandler (Socket socket) {
            this.clientSocket = socket;
        }
        @Override
        public void run() {
            try{
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));// Set up input and output streams for communication with the client
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                out.println("Welcome to The Deal! Please enter your name:");
                this.bidderName = in.readLine();
                out.println("Hello " + bidderName + "! Current highest bid is $" + AuctionServer.currBid);
                String inputLine;
                while ((inputLine = in.readLine()) !=null) {// Read client input in a loop until the client disconnects
                    try {// Attempt to parse the input as a double representing the bid amount
                        double bidAmount = Double.parseDouble(inputLine);
                        String response = AuctionServer.placeBid(bidderName, bidAmount);
                        out.println(response);
                    } catch (NumberFormatException e) {
                        out.println("Invalid input. Please enter a valid bid amount.");
                    }
                }
            } catch (IOException e) {// Handle any IO exceptions that occur during communication with the client
                System.out.println ("Connection error with client: " + bidderName);
            } finally {
                cleanUp();// Ensure resources are cleaned up when the client disconnects or an error occurs
        }
    }

    public void sendMessage (String message) { // Method to send a message to the client
        if(out != null) {
            out.println(message);
        }
    }
    private void cleanUp() {// Method to clean up resources when the client disconnects
        try {
            AuctionServer.removeClient(this);
            if (clientSocket != null) clientSocket.close();
        } catch (IOException e) {
            System.err.println("Error cleaning up client connection: " + e.getMessage());
        }

    }
}