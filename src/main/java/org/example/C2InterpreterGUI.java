package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class C2InterpreterGUI {
    private final C2Interpreter interpreter = new C2Interpreter();

    public void createAndShowGUI() {
        // Create the frame
        JFrame frame = new JFrame("C2 Interpreter");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        // Create the input area
        JTextArea inputArea = new JTextArea(10, 40);
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);

        // Create the output area
        JTextArea outputArea = new JTextArea(10, 40);
        outputArea.setEditable(false);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);

        // Create the button to execute
        JButton executeButton = new JButton("Execute");

        // Button action listener
        executeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String program = inputArea.getText();
                interpreter.interpretWithOutput(program, outputArea);
            }
        });

        // Add components to the frame
        frame.getContentPane().add(new JScrollPane(inputArea), BorderLayout.NORTH);
        frame.getContentPane().add(new JScrollPane(outputArea), BorderLayout.CENTER);
        frame.getContentPane().add(executeButton, BorderLayout.SOUTH);

        // Show the frame
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            C2InterpreterGUI gui = new C2InterpreterGUI();
            gui.createAndShowGUI();
        });
    }
}
