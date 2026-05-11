import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Random;
import java.net.*;

public class TheDealGui extends JFrame {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private DefaultListModel<String> leaderboardModel;
    private JTextField bidField;
    private JLabel timerLabel;
    private Timer timer;
    private JLabel statusLabel;
    private JLabel itemImageLabel;
    private String[] itemImages = new String[0];
    private int timeLeft = 60; 
    private String playerId;

    public TheDealGui(String host, int port) {
        // Generates the random ID immediately
        this.playerId = "Player-" + (100000 + new Random().nextInt(900000));
        setupUI();
        connectToServer(host, port);
    }

    private void setupUI() {
        setTitle("THE DEAL - " + playerId);
        setSize(400, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        timerLabel = new JLabel("Time Left: 60s", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        statusLabel = new JLabel("Connected as " + playerId, SwingConstants.CENTER);
        topPanel.add(timerLabel);
        topPanel.add(statusLabel);
        add(topPanel, BorderLayout.NORTH);

        leaderboardModel = new DefaultListModel<>();
        JList<String> leaderboardList = new JList<>(leaderboardModel);

        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        itemImageLabel = new JLabel("Item Image", SwingConstants.CENTER);
        itemImageLabel.setPreferredSize(new Dimension(200, 200));
        centerPanel.add(itemImageLabel, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(leaderboardList), BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout(5, 5));
        bidField = new JTextField();
        JButton bidButton = new JButton("Place Bid");
        footer.add(new JLabel(" Your Bid: "), BorderLayout.WEST);
        footer.add(bidField, BorderLayout.CENTER);
        footer.add(bidButton, BorderLayout.EAST);
        add(footer, BorderLayout.SOUTH);

        bidButton.addActionListener(e -> sendInBid());
        bidField.addActionListener(e -> sendInBid());

        countDownInit();
    }

    private void connectToServer(String host, int port) {
        new Thread(() -> {
            try {
                socket = new Socket(host, port);
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Sends the random ID to the server immediately
                out.println(playerId); 

                String message;
                while ((message = in.readLine()) != null) {
                    String finalMessage = message;
                    SwingUtilities.invokeLater(() -> serverMessageHandle(finalMessage));
                }
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> statusLabel.setText("Connection Failed."));
            }
        }).start();
    }

   private void serverMessageHandle(String msg) {

    if (msg.startsWith("IMAGE:")) {
        try {
            int index = Integer.parseInt(msg.substring(6).trim());
            updateImageById(index);
        } catch (Exception e) {
            itemImageLabel.setText("Image Load Error");
        }
        return; 
    }
    String lower = msg.toLowerCase();
    
  
    if (msg.startsWith("IMAGE:")) {
        try {
            int imageIndex = Integer.parseInt(msg.substring(6).trim());
            updateImageById(imageIndex);
        } catch (Exception e) {
            System.out.println("Error parsing image index");
        }
        return; 
    }

    // 2. Existing filters
    if (lower.contains("please enter your name") || lower.contains("welcome to the deal")) {
        return;
    }

    leaderboardModel.insertElementAt(msg, 0);
    
    if (msg.contains("Highest Bid") || msg.contains("highest bidder")) {
        resetTime();
    }
}

private void updateImageById(int index) {
    if (index >= 0 && index < itemImages.length) {
        String imageName = itemImages[index];
        ImageIcon icon = new ImageIcon(imageName);
        Image img = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        itemImageLabel.setIcon(new ImageIcon(img));
        itemImageLabel.setText(""); 
    }
}

    private void sendInBid() {
        String amount = bidField.getText().trim();
        if (!amount.isEmpty() && out != null) {
            out.println(amount);
            bidField.setText("");
        }
    }

    private void countDownInit() {
        timer = new Timer(1000, e -> {
            if (timeLeft > 0) {
                timeLeft--;
                timerLabel.setText(String.format("Time Left: %ds", timeLeft));
                if (timeLeft <= 10) timerLabel.setForeground(Color.RED);
            } else {
                timer.stop();
                timerLabel.setText("!!!SOLD!!!");
                bidField.setEnabled(false);
                JOptionPane.showMessageDialog(this, "Auction Ended!");
            }
        });
        timer.start();
    }

    private void resetTime() {
        timeLeft = 60;
        timerLabel.setForeground(Color.MAGENTA);
        timerLabel.setText("Time Left: 60s");
        if (timer != null && !timer.isRunning()) {
            timer.start();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TheDealGui("localhost", 5000).setVisible(true));
    }
}