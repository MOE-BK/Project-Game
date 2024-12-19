package com.mycompany.mavenproject32;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;


class Constants {
    public static final int CODE_LENGTH = 4;
    public static final int MAX_ATTEMPTS = 10;
    public static final String[] COLORS = {"Red", "Gray", "Blue", "Yellow", "Orange", "Pink"};
}


interface GameAction {
    // Method to perform an action  evaluating a guess)
    void performAction();// as  a public static final
}


class CodeGenerator implements GameAction {
    public ArrayList<String> generateCode() {
        Random random = new Random();
        ArrayList<String> code = new ArrayList<>();
        for (int i = 0; i < Constants.CODE_LENGTH; i++) {
            code.add(Constants.COLORS[random.nextInt(Constants.COLORS.length)]);
        }
        return code;
    }

    @Override
    public void performAction() {
        generateCode();
    }
}


class GuessEvaluator implements GameAction {
    public Feedback evaluateGuess(ArrayList<String> secretCode, ArrayList<String> playerGuess) {
        int blackPegs = 0;
        int whitePegs = 0;

        HashMap<String, Integer> colorCounts = new HashMap<>();
        for (String color : secretCode) {
            colorCounts.put(color, colorCounts.getOrDefault(color, 0) + 1);
        }

        // Count black pegs in gauss
        for (int i = 0; i < secretCode.size(); i++) {
            if (secretCode.get(i).equals(playerGuess.get(i))) {
                blackPegs++;
                colorCounts.put(secretCode.get(i), colorCounts.get(secretCode.get(i)) - 1);
            }
        }

        // Count white pegs nin gauss
        for (int i = 0; i < playerGuess.size(); i++) {
            if (!secretCode.get(i).equals(playerGuess.get(i)) && colorCounts.getOrDefault(playerGuess.get(i), 0) > 0) {
                whitePegs++;
                colorCounts.put(playerGuess.get(i), colorCounts.get(playerGuess.get(i)) - 1);
            }
        }

        return new Feedback(blackPegs, whitePegs);
    }

    @Override
    public void performAction() {
        
    }
}

    // Feedback Class
class Feedback {
    private final int blackPegs;
    private final int whitePegs;

    public Feedback(int blackPegs, int whitePegs) {
        this.blackPegs = blackPegs;
        this.whitePegs = whitePegs;
    }

    public int getBlackPegs() {
        return blackPegs;
    }

    public int getWhitePegs() {
        return whitePegs;
    }


    @Override
    public String toString() {
        return "Black Pegs: " + blackPegs + ", White Pegs: " + whitePegs;
    }
}


public class Mavenproject32 {
    private GameAction codeGenerator;
    private GameAction guessEvaluator;
    private ArrayList<String> secretCode;
    private int attempts;
    private boolean isWon;

    
    private JFrame frame;
    private JTextField guessInputField;
    private JTextArea feedbackArea;
    private JLabel statusLabel;
    private JButton submitButton, restartButton;

    public Mavenproject32() {
        this.codeGenerator = new CodeGenerator();
        this.guessEvaluator = new GuessEvaluator();
        this.secretCode = ((CodeGenerator) codeGenerator).generateCode(); // Casting to access specific method
        this.attempts = 0;
        this.isWon = false;

        initializeGUI();
    }

   private void initializeGUI() {
    // Frame Game in project
    frame = new JFrame("Mastermind Game");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(600, 450); 
    frame.setLayout(new BorderLayout(10, 10)); 

    
    JPanel inputPanel = new JPanel(new BorderLayout());
    inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    inputPanel.setBackground(Color.LIGHT_GRAY);

    JLabel hintLabel = new JLabel("Enter your guess (as Same Syntax of color: Red Blue Yellow Pink):", JLabel.CENTER);
    hintLabel.setFont(new Font("Arial", Font.BOLD, 14));
    hintLabel.setForeground(Color.DARK_GRAY);
    inputPanel.add(hintLabel, BorderLayout.NORTH);

    guessInputField = new JTextField();
    guessInputField.setFont(new Font("Arial", Font.PLAIN, 14));
    guessInputField.setHorizontalAlignment(JTextField.CENTER);
    guessInputField.setToolTipText("Type 4 colors separated by spaces (e.g., Red Blue Yellow Pink)");
    inputPanel.add(guessInputField, BorderLayout.CENTER);

    submitButton = new JButton("Submit Guess");
    submitButton.setBackground(Color.white);
    submitButton.setForeground(Color.BLACK);
    submitButton.setFont(new Font("Arial", Font.BOLD, 14));
    submitButton.addActionListener(e -> processGuess());
    inputPanel.add(submitButton, BorderLayout.SOUTH);

    // Feedback  in broject Area
    feedbackArea = new JTextArea(10, 40);
    feedbackArea.setEditable(false);
    feedbackArea.setFont(new Font("Courier New", Font.PLAIN, 12));
    feedbackArea.setBorder(BorderFactory.createTitledBorder("Feedback"));
    JScrollPane feedbackScroll = new JScrollPane(feedbackArea);

        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        statusPanel.setBackground(Color.LIGHT_GRAY);

        statusLabel = new JLabel("Attempts: 0/" + Constants.MAX_ATTEMPTS+"  "+"This Project Make By Student EELU", JLabel.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setForeground(Color.BLUE);

        restartButton = new JButton("Restart");
        restartButton.setEnabled(false);
        restartButton.setBackground(Color.BLACK);
        restartButton.setForeground(Color.WHITE);
        restartButton.addActionListener(e -> restartGame());

        statusPanel.add(statusLabel, BorderLayout.NORTH);
        statusPanel.add(restartButton, BorderLayout.SOUTH);

frame.add(statusPanel, BorderLayout.SOUTH);

  
    
    frame.add(inputPanel, BorderLayout.NORTH);
    frame.add(feedbackScroll, BorderLayout.CENTER);
    frame.add(statusPanel, BorderLayout.SOUTH);

    frame.setVisible(true);
}

    private void processGuess() {
        String guessText = guessInputField.getText().trim();
        ArrayList<String> playerGuess = new ArrayList<>(Arrays.asList(guessText.split(" ")));


        if (playerGuess.size() != Constants.CODE_LENGTH) {
            feedbackArea.append("Invalid guess! Please enter " + Constants.CODE_LENGTH + " colors.\n");
            return;
        }

        Feedback feedback = ((GuessEvaluator) guessEvaluator).evaluateGuess(secretCode, playerGuess); // Casting to access specific method
        feedbackArea.append("Guess: " + String.join(" ", playerGuess) + " -> " + feedback + "\n");

        attempts++;
        statusLabel.setText("Attempts: " + attempts + "/" + Constants.MAX_ATTEMPTS);

        if (feedback.toString().contains("Black Pegs: " + Constants.CODE_LENGTH)) {
            isWon = true;
            endGame("Congratulations! You cracked the code!");
        } else if (attempts >= Constants.MAX_ATTEMPTS) {
            endGame("Game over! The secret code was: " + String.join(" ", secretCode));
        }

        guessInputField.setText("");
    }

    private void endGame(String message) {
        feedbackArea.append(message + "\n");
        submitButton.setEnabled(false);
        restartButton.setEnabled(true);
        statusLabel.setText(message);
    }

    private void restartGame() {
        this.secretCode = ((CodeGenerator) codeGenerator).generateCode();
        this.attempts = 0;
        this.isWon = false;

        feedbackArea.setText("");
        statusLabel.setText("Attempts: 0/" + Constants.MAX_ATTEMPTS);
        guessInputField.setText("");
        submitButton.setEnabled(true);
        restartButton.setEnabled(false);
    }

    public static void main(String[] args) {
    
       Mavenproject32 game = new Mavenproject32();
        
    }
}
