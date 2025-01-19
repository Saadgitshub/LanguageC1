package org.example;

import javax.swing.*;
import java.util.*;

public class C2Interpreter {
    private final Map<String, Object> variables = new HashMap<>(); // Store both numbers and arrays/strings

    // Interpret a given program
    public void interpret(String program, JTextArea outputArea) {
        String[] instructions = program.split(";");
        for (String instruction : instructions) {
            instruction = instruction.trim();
            if (!instruction.isEmpty()) {
                execute(instruction, outputArea);
            }
        }
    }

    // Execute an individual instruction
    private void execute(String instruction, JTextArea outputArea) {
        if (instruction.startsWith("afficher")) {
            String expression = instruction.substring("afficher".length()).trim();
            expression = expression.substring(1, expression.length() - 1); // Remove parentheses
            String result = evaluateStringExpression(expression);
            afficher(result, outputArea);
        } else if (instruction.contains(":=")) {
            String[] parts = instruction.split(":=");
            String variable = parts[0].trim();
            String expression = parts[1].trim();
            Object value = evaluateExpression(expression);
            variables.put(variable, value);
        } else if (instruction.startsWith("if")) {
            handleIfStatement(instruction, outputArea); // Delegate to handleIfStatement
        } else if (instruction.startsWith("while")) {
            handleWhileLoop(instruction, outputArea); // Delegate to handleWhileLoop
        } else {
            throw new RuntimeException("Unknown instruction: " + instruction);
        }
    }

    // Evaluate an expression (supports numbers, arrays, and strings)
    private Object evaluateExpression(String expression) {
        expression = expression.trim();

        // Handle arrays
        if (expression.startsWith("[") && expression.endsWith("]")) {
            String[] elements = expression.substring(1, expression.length() - 1).split(",");
            List<Double> array = new ArrayList<>();
            for (String element : elements) {
                array.add(Double.parseDouble(element.trim()));
            }
            return array;
        }

        // Handle array access (e.g., A[1])
        if (expression.matches("[a-zA-Z]+\\[\\d+\\]")) {
            String variable = expression.substring(0, expression.indexOf("["));
            int index = Integer.parseInt(expression.substring(expression.indexOf("[") + 1, expression.indexOf("]")));
            Object value = variables.get(variable);
            if (value instanceof List) {
                return ((List<?>) value).get(index);
            } else {
                throw new RuntimeException("Variable " + variable + " is not an array.");
            }
        }

        // Handle string operations
        if (expression.startsWith("\"") && expression.endsWith("\"")) {
            return expression.substring(1, expression.length() - 1); // Return string literal
        }

        // Handle math functions (sin, cos, tan, sqrt)
        if (expression.startsWith("sin")) {
            return Math.sin(parseMathFunctionArgument(expression, "sin"));
        } else if (expression.startsWith("cos")) {
            return Math.cos(parseMathFunctionArgument(expression, "cos"));
        } else if (expression.startsWith("tan")) {
            return Math.tan(parseMathFunctionArgument(expression, "tan"));
        } else if (expression.startsWith("sqrt")) {
            return Math.sqrt(parseMathFunctionArgument(expression, "sqrt"));
        }

        // Handle math functions and variables
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            if (entry.getValue() instanceof Double) {
                expression = expression.replace(entry.getKey(), entry.getValue().toString());
            }
        }

        // Concatenate strings (e.g., "Hello" + " World")
        if (expression.contains("+")) {
            String[] parts = expression.split("\\+");
            StringBuilder result = new StringBuilder();
            for (String part : parts) {
                Object evaluated = evaluateExpression(part.trim());
                result.append(evaluated.toString());
            }
            return result.toString();
        }

        // Evaluate numerical expression
        try {
            return Double.parseDouble(expression);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Failed to evaluate expression: " + expression, e);
        }
    }

    // Helper method to extract the argument inside parentheses for math functions
    private double parseMathFunctionArgument(String expression, String functionName) {
        int start = expression.indexOf("(") + 1;
        int end = expression.indexOf(")");
        String argument = expression.substring(start, end).trim();
        return (double) evaluateExpression(argument); // Recursively evaluate the argument
    }

    // Handle conditional statements (if/else)
    private void handleIfStatement(String instruction, JTextArea outputArea) {
        try {
            // Step 1: Extract the condition inside parentheses (e.g., "X > 5")
            String condition = instruction.substring(instruction.indexOf("(") + 1, instruction.indexOf(")")).trim();

            // Step 2: Evaluate the condition (boolean result)
            boolean conditionResult = evaluateCondition(condition);

            // Step 3: Extract the block inside braces after the condition
            String remainingInstruction = instruction.substring(instruction.indexOf(")") + 1).trim();
            String trueBlock = extractBlock(remainingInstruction);  // Extract the true block

            // Ensure the remaining instruction is handled correctly (after true block)
            remainingInstruction = remainingInstruction.substring(trueBlock.length()).trim();

            // Step 4: If condition is true, execute the true block
            if (conditionResult) {
                interpret(trueBlock, outputArea);
            }

            // Step 5: Check if there is an "else" block
            if (remainingInstruction.contains("else")) {
                String elseBlock = remainingInstruction.substring(remainingInstruction.indexOf("else") + 4).trim();
                elseBlock = extractBlock(elseBlock);  // Extract else block
                if (!conditionResult) {
                    interpret(elseBlock, outputArea);
                }
            }
        } catch (Exception e) {
            // Handle errors and provide feedback
            throw new RuntimeException("Invalid if block: missing braces or incorrect format", e);
        }
    }

    private String extractBlock(String instruction) {
        // This method extracts the block inside curly braces (e.g., "{ ... }")
        if (instruction.startsWith("{") && instruction.endsWith("}")) {
            return instruction.substring(1, instruction.length() - 1).trim();  // Remove braces
        } else {
            // If braces are missing or format is incorrect, return an empty string
            return "";
        }
    }

    private void handleWhileLoop(String instruction, JTextArea outputArea) {
        int openParenthesis = instruction.indexOf("(");
        int closeParenthesis = instruction.indexOf(")");

        if (openParenthesis == -1 || closeParenthesis == -1) {
            throw new RuntimeException("Invalid while loop: missing parentheses.");
        }

        String condition = instruction.substring(openParenthesis + 1, closeParenthesis).trim();

        int openBrace = instruction.indexOf("{");
        int closeBrace = instruction.lastIndexOf("}");

        if (openBrace == -1 || closeBrace == -1) {
            throw new RuntimeException("Invalid while loop block: missing braces.");
        }

        String block = instruction.substring(openBrace + 1, closeBrace).trim();

        while (evaluateCondition(condition)) {
            interpret(block, outputArea);
        }
    }

    // Evaluate a condition (returns boolean)
    private boolean evaluateCondition(String condition) {
        String[] operators = {"<=", ">=", "<", ">", "==", "!="};
        for (String operator : operators) {
            if (condition.contains(operator)) {
                String[] parts = condition.split(operator);
                double left = (double) evaluateExpression(parts[0].trim());
                double right = (double) evaluateExpression(parts[1].trim());
                return switch (operator) {
                    case "<=" -> left <= right;
                    case ">=" -> left >= right;
                    case "<" -> left < right;
                    case ">" -> left > right;
                    case "==" -> left == right;
                    case "!=" -> left != right;
                    default -> false;
                };
            }
        }
        throw new RuntimeException("Invalid condition: " + condition);
    }

    private String evaluateStringExpression(String expression) {
        // Check for quoted strings and replace variables in the string
        if (expression.startsWith("\"") && expression.endsWith("\"")) {
            return expression.substring(1, expression.length() - 1); // Return string without quotes
        } else if (expression.contains("+")) {
            // Handle string concatenation or mixed expressions
            String[] parts = expression.split("\\+");
            StringBuilder result = new StringBuilder();
            for (String part : parts) {
                part = part.trim();
                if (part.startsWith("\"") && part.endsWith("\"")) {
                    result.append(part.substring(1, part.length() - 1)); // Add string part
                } else {
                    result.append(evaluateExpression(part)); // Add numeric part
                }
            }
            return result.toString();
        } else {
            // Assume it's a numeric expression
            return String.valueOf(evaluateExpression(expression));
        }
    }

    // Output results
    public void afficher(Object result, JTextArea outputArea) {
        outputArea.append("Result: " + result + "\n");
    }

    // Main interpreter entry point
    public void interpretWithOutput(String program, JTextArea outputArea) {
        interpret(program, outputArea);
    }
}
