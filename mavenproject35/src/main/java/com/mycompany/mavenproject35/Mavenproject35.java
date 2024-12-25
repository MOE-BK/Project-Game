package com.mycompany.mavenproject35;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

// Constants Class
class Constants {
    public static final int CODE_LENGTH = 4;
    public static final int MAX_ATTEMPTS = 10;
    public static final String[] COLORS = {"Red", "Gray", "Blue", "Yellow", "Orange", "Pink"};
}

// CodeGenerator Class
class CodeGenerator {
    public ArrayList<String> generateCode() {
        Random random = new Random();
        ArrayList<String> code = new ArrayList<>();
        for (int i = 0; i < Constants.CODE_LENGTH; i++) {
            code.add(Constants.COLORS[random.nextInt(Constants.COLORS.length)]);
        }
        return code;
    }
}

// GuessEvaluator Class
class GuessEvaluator {
    public Feedback evaluateGuess(ArrayList<String> secretCode, ArrayList<String> playerGuess) {
        int blackPegs = 0;
        int whitePegs = 0;

        HashMap<String, Integer> colorCounts = new HashMap<>();
        for (String color : secretCode) {
            colorCounts.put(color, colorCounts.getOrDefault(color, 0) + 1);
        }

        // Count black pegs
        for (int i = 0; i < secretCode.size(); i++) {
            if (secretCode.get(i).equals(playerGuess.get(i))) {
                blackPegs++;
                colorCounts.put(secretCode.get(i), colorCounts.get(secretCode.get(i)) - 1);
            }
        }

        // Count white pegs
        for (int i = 0; i < playerGuess.size(); i++) {
            if (!secretCode.get(i).equals(playerGuess.get(i)) && colorCounts.getOrDefault(playerGuess.get(i), 0) > 0) {
                whitePegs++;
                colorCounts.put(playerGuess.get(i), colorCounts.get(playerGuess.get(i)) - 1);
            }
        }

        return new Feedback(blackPegs, whitePegs);
    }
}

// Feedback Class
class Feedback {
    private int blackPegs;
    private int whitePegs;

    public Feedback(int blackPegs, int whitePegs) {
        this.blackPegs = blackPegs;
        this.whitePegs = whitePegs;
    }

    @Override
    public String toString() {
        return "Black Pegs: " + blackPegs + ", White Pegs: " + whitePegs;
    }
}

// MastermindGameGUI Class
public class Mavenproject35 {
    private CodeGenerator codeGenerator;
    private GuessEvaluator guessEvaluator;
    private ArrayList<String> secretCode;
    private int attempts;
    private boolean isWon;

    // GUI Components
    private JFrame frame;
    private JTextArea feedbackArea;
    private JLabel statusLabel, hintLabel;
    private JButton submitButton, restartButton;
    private ArrayList<JButton> colorButtons;
    private ArrayList<String> currentGuess;

    public Mavenproject35() {
        this.codeGenerator = new CodeGenerator();
        this.guessEvaluator = new GuessEvaluator();
        this.secretCode = codeGenerator.generateCode();
        this.attempts = 0;
        this.isWon = false;

        currentGuess = new ArrayList<>();
        initializeGUI();
    }

    private void initializeGUI() {
        // Frame setup
        frame = new JFrame("Mastermind Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        frame.setLayout(new BorderLayout());

        // Input Panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 1));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        hintLabel = new JLabel("Click on the colors to form your guess", JLabel.CENTER);
        hintLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        hintLabel.setForeground(Color.DARK_GRAY);

        JPanel colorPanel = new JPanel(new FlowLayout());
        colorButtons = new ArrayList<>();
        for (String color : Constants.COLORS) {
            JButton colorButton = new JButton();
            colorButton.setBackground(getColorFromName(color));
            colorButton.setPreferredSize(new Dimension(60, 60));
            colorButton.addActionListener(e -> addColorToGuess(color));
            colorButtons.add(colorButton);
            colorPanel.add(colorButton);
        }

        JPanel guessPanel = new JPanel(new FlowLayout());
        submitButton = new JButton("Submit Guess");
        submitButton.setEnabled(false);
        submitButton.addActionListener(e -> submitGuess());

        JButton clearButton = new JButton("Clear Guess");
        clearButton.addActionListener(e -> clearGuess());

        guessPanel.add(new JLabel("Your Guess: "));
        guessPanel.add(submitButton);
        guessPanel.add(clearButton);

        inputPanel.add(hintLabel);
        inputPanel.add(colorPanel);
        inputPanel.add(guessPanel);

        // Feedback Area
        feedbackArea = new JTextArea(15, 40);
        feedbackArea.setEditable(false);
        feedbackArea.setFont(new Font("Courier New", Font.PLAIN, 14));
        feedbackArea.setBorder(BorderFactory.createTitledBorder("Game Feedback"));
        JScrollPane feedbackScroll = new JScrollPane(feedbackArea);

        // Status Panel
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        statusLabel = new JLabel("Attempts: 0/" + Constants.MAX_ATTEMPTS, JLabel.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusLabel.setForeground(Color.BLUE);

        restartButton = new JButton("Restart Game");
        restartButton.setEnabled(false);
        restartButton.addActionListener(e -> restartGame());

        statusPanel.add(statusLabel, BorderLayout.NORTH);
        statusPanel.add(restartButton, BorderLayout.SOUTH);

        // Add components to the frame
        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(feedbackScroll, BorderLayout.CENTER);
        frame.add(statusPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void addColorToGuess(String color) {
        if (currentGuess.size() < Constants.CODE_LENGTH) {
            currentGuess.add(color);
            hintLabel.setText("Current Guess: " + String.join(", ", currentGuess));
            if (currentGuess.size() == Constants.CODE_LENGTH) {
                submitButton.setEnabled(true);
            }
        }
    }

    private void clearGuess() {
        currentGuess.clear();
        hintLabel.setText("Click on the colors to form your guess");
        submitButton.setEnabled(false);
    }

    private void submitGuess() {
        processGuess(currentGuess);
        clearGuess();
    }

    private void processGuess(ArrayList<String> guess) {
        Feedback feedback = guessEvaluator.evaluateGuess(secretCode, guess);
        feedbackArea.append("Guess: " + String.join(", ", guess) + " -> " + feedback + "\n");

        attempts++;
        statusLabel.setText("Attempts: " + attempts + "/" + Constants.MAX_ATTEMPTS);

        if (feedback.toString().contains("Black Pegs: " + Constants.CODE_LENGTH)) {
            isWon = true;
            endGame("Congratulations! You cracked the code!");
        } else if (attempts >= Constants.MAX_ATTEMPTS) {
            endGame("Game over! The secret code was: " + String.join(", ", secretCode));
        }
    }

    private void endGame(String message) {
        feedbackArea.append(message + "\n");
        submitButton.setEnabled(false);
        restartButton.setEnabled(true);
        statusLabel.setText(message);
    }

    private void restartGame() {
        this.secretCode = codeGenerator.generateCode();
        this.attempts = 0;
        this.isWon = false;

        feedbackArea.setText("");
        statusLabel.setText("Attempts: 0/" + Constants.MAX_ATTEMPTS);
        clearGuess();
        submitButton.setEnabled(false);
        restartButton.setEnabled(false);
    }

    private Color getColorFromName(String colorName) {
        switch (colorName) {
            case "Red":
                return Color.RED;
            case "Gray":
                return Color.GRAY;
            case "Blue":
                return Color.BLUE;
            case "Yellow":
                return Color.YELLOW;
            case "Orange":
                return Color.ORANGE;
            case "Pink":
                return Color.PINK;
            default:
                return Color.BLACK;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Mavenproject35::new);
    }
}

