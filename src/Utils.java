import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Utils {
    public static void writeToFile(String data) {
        try {
            FileWriter writer = new FileWriter("data.csv");
            writer.write(data);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String cardsToCSV(ArrayList<Card> cards) {
        String output = "";
        for (Card c : cards) {
            output += c.getWord() + "," + c.getDefinition() + "\n";
        }
        return output;
    }

    /**
     * Used to load data.csv file on start.
     * @return A string that is used in the feedback field on program start.
     */
    public static String loadCSV() {
        int count = 0;

        File file = new File("data.csv");
        try {
            if (file.createNewFile()) {
                System.out.println("Data file created: " + file.getName());
                return "<html>Data file not found. Created a new file.</html>";
            } else {
                System.out.println("Data file found.");
                try (Scanner scanner = new Scanner(file)) {
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        String[] data = line.split(",");
                        if (!contains(data[0].trim())) {
                            Flashcards.getCards().add(new Card(data[0].trim(), data[1].trim()));
                            count++;
                        }
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "<html>Loaded <font color=blue>" + count + "</font> saved words.</html>";
    }

    /**
     * Used to load files uploaded when using "Upload .csv File"
     * @param file The file reference
     * @return A number that represents the number of new words added to the list.
     */
    public static int loadCSV(File file) {
        int count = 0;
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] data = line.split(",");
                if (!contains(data[0].trim())) {
                    Flashcards.getCards().add(new Card(data[0].trim(), data[1].trim()));
                    count++;
                }
            }
        } catch (FileNotFoundException er) {
            er.printStackTrace();
        }
        return count;
    }

    /**
     * Helper function to check if word is in the list.
     * @param word The string to check
     * @return Whether it is in the list or not.
     */
    public static boolean contains(String word) {
        for (Card card : Flashcards.getCards()) {
            if (card.getWord().equals(word)) {
                return true;
            }
        }
        return false;
    }
}
