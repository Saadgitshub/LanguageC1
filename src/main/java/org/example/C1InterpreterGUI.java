package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class C1InterpreterGUI {
    private final C1Interpreter interpreter = new C1Interpreter();

    public void createAndShowGUI() {
        // Create the frame
        JFrame frame = new JFrame("C1 Interpreter");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        // Create the input area
        JTextArea inputArea = new JTextArea(10, 40);
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputArea.setText("Enter your C1 program here...");
        JScrollPane inputScroll = new JScrollPane(inputArea);

        // Create the output area
        JTextArea outputArea = new JTextArea(10, 40);
        outputArea.setEditable(false);
        outputArea.setText("Output will appear here...");
        JScrollPane outputScroll = new JScrollPane(outputArea);

        // Create the run button
        JButton runButton = new JButton("Run Program");

        // Add action listener for the run button
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String program = inputArea.getText();
                try {
                    interpreter.interpretWithOutput(program, outputArea); // Pass outputArea to display results
                } catch (RuntimeException ex) {
                    outputArea.setText("Error: " + ex.getMessage());
                }
            }
        });

        // Layout for the GUI
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("C1 Program:"));
        panel.add(inputScroll);
        panel.add(runButton);
        panel.add(new JLabel("Output:"));
        panel.add(outputScroll);

        // Set the content pane and make the frame visible
        frame.getContentPane().add(panel);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        // Invoke the GUI creation on the Event Dispatch Thread for thread safety
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new C1InterpreterGUI().createAndShowGUI();
            }
        });
    }
}
