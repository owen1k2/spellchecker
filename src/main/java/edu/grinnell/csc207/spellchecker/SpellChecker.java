package edu.grinnell.csc207.spellchecker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * A spellchecker maintains an efficient representation of a dictionary for
 * the purposes of checking spelling and provided suggested corrections.
 */
public class SpellChecker {
    /** The number of letters in the alphabet. */
    private static final int NUM_LETTERS = 26;

    /** The path to the dictionary file. */
    private static final String DICT_PATH = "words_alpha.txt";

    /**
     * @param filename the path to the dictionary file
     * @return a SpellChecker over the words found in the given file.
     */
    public static SpellChecker fromFile(String filename) throws IOException {
        return new SpellChecker(Files.readAllLines(Paths.get(filename)));
    }

    /** A Node of the SpellChecker structure. */
    private class Node {
        ArrayList<Node> nodeArray;
        ArrayList<Character> charArray;
        boolean endsWord;
        char curChar;

        public Node(char curChar) {
            nodeArray = new ArrayList<>();
            charArray = new ArrayList<>();
            this.curChar = curChar;
            endsWord = false;
        }
    }

    /** The root of the SpellChecker */
    private Node root;

    public SpellChecker(List<String> dict) {
        root = new Node('.');
        for(int i = 0; i < dict.size(); i++) {
            add(dict.get(i));
        }
    }

    public void add(String word) {
        Node cur = root;
        for(int i =0; i < word.length(); i++) {
            if(cur.charArray.contains(word.charAt(i))) {
                cur = cur.nodeArray.get(cur.charArray.indexOf(word.charAt(i)));
            } else {
                Node n = new Node(word.charAt(i));
                cur.charArray.add(word.charAt(i));
                cur.nodeArray.add(n);
                cur = n;
            }
        }
        cur.endsWord = true;
    }

    public boolean isWord(String word) {
        int i = 0;
        Node cur = root;
        while (i < word.length() && cur.charArray.contains(word.charAt(i))) {
            cur = cur.nodeArray.get(cur.charArray.indexOf(word.charAt(i)));
            i++;
        }
        if (i < word.length()) {
            return false; 
        } else {
            return true;
        }
    }

    public List<String> getOneCharCompletions(String word) {
        ArrayList<String> output = new ArrayList<>();
        int i = 0;
        Node cur = root;
        while (i < word.length() && cur.charArray.contains(word.charAt(i))) {
            cur = cur.nodeArray.get(cur.charArray.indexOf(word.charAt(i)));
            i++;
        }
       for(int j = 0; j < cur.nodeArray.size(); j++) {
            if(cur.nodeArray.get(j).endsWord) {
                output.add(word + cur.charArray.get(j));
            }
       }
       return output;
    }

    public List<String> getOneCharEndCorrections(String word) {
        ArrayList<String> output = new ArrayList<>();
        int i = 0;
        Node cur = root;
        while (i < word.length() - 1 && cur.charArray.contains(word.charAt(i))) {
            cur = cur.nodeArray.get(cur.charArray.indexOf(word.charAt(i)));
            i++;
        }
       for(int j = 0; j < cur.nodeArray.size(); j++) {
            if(cur.nodeArray.get(j).endsWord) {
                output.add(word.substring(0, word.length() - 1) + cur.charArray.get(j));
            }
       }
       return output;
    }

    public List<String> getOneCharCorrections(String word) {
        ArrayList<String> output = new ArrayList<>();
        int i = 0;
        Node cur = root;
        while (i < word.length() && cur.charArray.contains(word.charAt(i))) {
            cur = cur.nodeArray.get(cur.charArray.indexOf(word.charAt(i)));
            i++;
        }
       for(int j = 0; j < cur.nodeArray.size(); j++) {
            if(cur.nodeArray.get(j).endsWord) {
                output.add(word.substring(0, i) + cur.charArray.get(j) + word.substring(i + 1, word.length()));
            }
       }
       return output;
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println("Usage: java SpellChecker <command> <word>");
            System.exit(1);
        } else {
            String command = args[0];
            String word = args[1];
            SpellChecker checker = SpellChecker.fromFile(DICT_PATH);
            switch (command) {
                case "check": {
                    System.out.println(checker.isWord(word) ? "correct" : "incorrect");
                    System.exit(0);
                }

                case "complete": {
                    List<String> completions = checker.getOneCharCompletions(word);
                    for (String completion : completions) {
                        System.out.println(completion);
                    }
                    System.exit(0);
                }

                case "correct": {
                    List<String> corrections = checker.getOneCharCorrections(word);
                    for (String correction : corrections) {
                        System.out.println(correction);
                    }
                    System.exit(0);
                }

                default: {
                    System.err.println("Unknown command: " + command);
                    System.exit(1);
                }
            }
        }
    }
}
