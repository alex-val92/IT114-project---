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
    private JTextField nameField;
    private JLabel timerLabel;
    private Timer timer;
    private JLabel statusLabel;
    private int timeLeft = 60; 
    private String playerId;
    private JTextField bidField;

    public TheDealGui(String host, int port) {
        this.playerId = String.format("Player-%06d", new Random().nextInt(999999));
        setupUI();
        connectToServer(host, port, playerId);
    }

    private void setupUI() {
        setTitle("THE DEAL - " + playerId);
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new GridLayout(3,1,5,5));
        timerLabel = new JLabel("Time Left: 60s", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        statusLabel = new JLabel("Enter your name:", SwingConstants.CENTER);

        JPanel nameInputPanel = new JPanel(new BorderLayout(5, 5));
        nameField = new JTextField(playerId);
        JButton connectBtn = new JButton("Connect");
        nameInputPanel.add(new JLabel ("Name:"), BorderLayout.WEST);
        nameInputPanel.add(nameField, BorderLayout.CENTER);
        nameInputPanel.add(connectBtn, BorderLayout.EAST);


        topPanel.add(timerLabel);
        topPanel.add(statusLabel);
        topPanel.add(nameInputPanel);
        add(topPanel, BorderLayout.NORTH);

        leaderboardModel = new DefaultListModel<>();
        JList<String> leaderboardList = new JList<>(leaderboardModel);
        add(new JScrollPane(leaderboardList), BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout(5,5));
        bidField = new JTextField();
        bidField.setEnabled(false);
        JButton bidButton = new JButton("Place Bid");
        bidButton.setEnabled(false);

        footer.add(new JLabel("Your Bid: "), BorderLayout.WEST);
        footer.add(bidField, BorderLayout.CENTER);
        footer.add(bidButton, BorderLayout.EAST);
        add(footer, BorderLayout.SOUTH);

        connectBtn.addActionListener(e -> {
            String chosenName = nameField.getText().trim();
            if (!chosenName.isEmpty()) {
                playerId = chosenName;
                connectToServer("localhost", 5000, chosenName);
                nameField.setEnabled(false);
                connectBtn.setEnabled(false);
                bidField.setEnabled(true);
                bidButton.setEnabled(true);
                statusLabel.setText("Connected as: " + playerId);
            } else {
                JOptionPane.showMessageDialog(this, "Please enter a valid name.");
            }

        
        });

        bidButton.addActionListener(e -> sendInBid());
        bidField.addActionListener(e -> sendInBid());

        countDownInit();
    }

    private void connectToServer(String host, int port, String playerId) {
        this.playerId = playerId;
        setTitle("THE DEAL - " + playerId);

        new Thread(() -> {
            try {
                socket = new Socket(host, port);
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                out.println(playerId); // Send ID to server

                String message;
                while ((message = in.readLine()) != null) {
                    String finalMessage = message;
                    SwingUtilities.invokeLater(() -> serverMessageHandle(finalMessage));
                }
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> statusLabel.setText("Disconnected from server."));
            }
        }).start();
    }

    private void serverMessageHandle(String msg) {
        leaderboardModel.insertElementAt(msg, 0);
        if (msg.contains("The Highest Bid:") || msg.contains("You are the highest bidder!")) {
            resetTime();
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