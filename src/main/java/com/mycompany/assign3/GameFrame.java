package com.mycompany.assign3;

import java.awt.Component;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author emaoits
 */
public class GameFrame extends JFrame{
    private static final String DB_URL = "jdbc:mysql://localhost:3306/snake_game";
    private static final String USER = "root";
    private static final String PASS = "masato0907";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
        }
    }
    GameFrame() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        menuBar.add(menu);

        JMenuItem newGameItem = new JMenuItem("New Game");
        JMenuItem highScoresItem = new JMenuItem("High Scores");
        JMenuItem exitItem = new JMenuItem("Exit");

        menu.add(newGameItem);
        menu.add(highScoresItem);
        menu.addSeparator();
        menu.add(exitItem);

        this.setJMenuBar(menuBar);

        newGameItem.addActionListener(e -> promptLevelAndStartGame());
        highScoresItem.addActionListener(e -> showHighScores());
        exitItem.addActionListener(e -> System.exit(0));
        
        this.setTitle("Snake Game");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
    }

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
        }
    }
    
    private void promptLevelAndStartGame() {
        String[] options = {"Level 1", "Level 2", "Level 3"};
        int level = JOptionPane.showOptionDialog(this, "Choose the level", "Level Selection", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
        if (level != JOptionPane.CLOSED_OPTION) {
            startNewGame(level + 1);
        }
    }

    public void saveHighScore(String playerName, int score) {
        String query = "INSERT INTO high_scores (player_name, score) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, playerName);
            pstmt.setInt(2, score);
            pstmt.executeUpdate();
        } catch (SQLException e) {
        }
    }

    List<String> getTopHighScores() {
        List<String> scores = new ArrayList<>();
        String query = "SELECT player_name, score FROM high_scores ORDER BY score DESC, date ASC LIMIT 10";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String name = rs.getString("player_name");
                int score = rs.getInt("score");
                scores.add(name + ": " + score);
            }
        } catch (SQLException e) {
        }
        return scores;
    }


    
    private void startNewGame(int level) {
        getContentPane().removeAll();

        GamePanel newGamePanel = new GamePanel(this, level);
        this.add(newGamePanel);
        this.pack();
        newGamePanel.startGame();
        newGamePanel.requestFocusInWindow();
        this.revalidate();
        this.repaint();
    }
    
    private void showHighScores() {
        List<String> topScores = getTopHighScores();
        String highScoresStr = String.join("\n", topScores);

        JOptionPane.showMessageDialog(this, highScoresStr, "Top 10 High Scores", JOptionPane.INFORMATION_MESSAGE);
    }
}