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
            System.out.println("Successfully updated data file.");
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

    public static int loadCSV() {
        int count = 0;

        File file = new File("data.csv");
        try {
            if (file.createNewFile()) {
                System.out.println("Data file created: " + file.getName());
            } else {
                System.out.println("Data file found.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

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
        return count;
    }
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

    public static boolean contains(String word) {
        for (Card card : Flashcards.getCards()) {
            if (card.getWord().equals(word)) {
                return true;
            }
        }
        return false;
    }
}
