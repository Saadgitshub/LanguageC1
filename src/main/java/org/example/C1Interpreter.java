package org.example;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class C1Interpreter {
    private final Map<String, Double> variables = new HashMap<>();

    public void interpret(String program) {
        String[] instructions = program.split(";");
        for (String instruction : instructions) {
            instruction = instruction.trim();
            if (!instruction.isEmpty()) {
                execute(instruction);
            }
        }
    }

    private void execute(String instruction) {
        if (instruction.startsWith("afficher")) {
            // Handle "afficher" instruction
            String expression = instruction.substring("afficher".length()).trim();
            expression = expression.substring(1, expression.length() - 1); // Remove parentheses
            double result = evaluateExpression(expression);
            System.out.println(result);  // Output to console (optional, for testing)
        } else if (instruction.contains(":=")) {
            // Handle variable assignment
            String[] parts = instruction.split(":=");
            String variable = parts[0].trim();
            String expression = parts[1].trim();
            double value = evaluateExpression(expression);
            variables.put(variable, value);
        } else {
            throw new RuntimeException("Unknown instruction: " + instruction);
        }
    }

    private double evaluateExpression(String expression) {
        // Replace variables with their values
        for (Map.Entry<String, Double> entry : variables.entrySet()) {
            expression = expression.replace(entry.getKey(), entry.getValue().toString());
        }
        // Evaluate the expression (basic implementation using ScriptEngine)
        try {
            return new net.objecthunter.exp4j.ExpressionBuilder(expression).build().evaluate();
        } catch (Exception e) {
            throw new RuntimeException("Failed to evaluate expression: " + expression, e);
        }
    }

    public void interpretWithOutput(String program, JTextArea outputArea) {
        try {
            StringBuilder outputBuilder = new StringBuilder(); // Use StringBuilder to store all results
            String[] instructions = program.split(";");
            for (String instruction : instructions) {
                instruction = instruction.trim();
                if (!instruction.isEmpty()) {
                    if (instruction.startsWith("afficher")) {
                        // Handle "afficher" instruction
                        String expression = instruction.substring("afficher".length()).trim();
                        expression = expression.substring(1, expression.length() - 1); // Remove parentheses
                        double result = evaluateExpression(expression);
                        outputBuilder.append("Result: ").append(result).append("\n"); // Append result to output
                    } else if (instruction.contains(":=")) {
                        // Handle variable assignment
                        String[] parts = instruction.split(":=");
                        String variable = parts[0].trim();
                        String expression = parts[1].trim();
                        double value = evaluateExpression(expression);
                        variables.put(variable, value);
                    }
                }
            }
            outputArea.setText(outputBuilder.toString()); // Set all results to JTextArea
        } catch (Exception e) {
            outputArea.setText("Error: " + e.getMessage());
        }
    }


    public static void main(String[] args) {
        C1Interpreter interpreter = new C1Interpreter();
        String program = """
                X:=350;
                y:=X*3-25/5;
                afficher(y+5);
                """;
        interpreter.interpret(program);
    }}