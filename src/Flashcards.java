import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Flashcards extends JFrame implements ActionListener {
    private JTabbedPane mainPane;
    private ArrayList<Card> cards = new ArrayList<Card>();
    private int index;
    private JTextField word;
    private JTextArea def;
    private JButton addCardButton;
    private JPanel addPanel;
    private JPanel viewPanel;
    private JButton prev;
    private JButton next;
    private JPanel currentCard;
    private JButton uploadFileButton;
    private JPanel quizPanel;
    private JPanel quizSection;
    private JButton newQuestionButton;
    private JLabel feedback;

    public Flashcards() {
        setContentPane(mainPane);
        setTitle("Flashcards");
        setSize(500, 400);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        addCardButton.addActionListener(this);
        prev.addActionListener(this);
        next.addActionListener(this);
        uploadFileButton.addActionListener(this);
        newQuestionButton.addActionListener(this);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source instanceof JButton) {
            JButton button = (JButton) source;

            try {
                if (button.equals(addCardButton)) {
                    if (word.getText().length() == 0 || def.getText().length() == 0) {
                        return;
                    }
                    if (contains(word.getText())) {
                        feedback.setText(word.getText() + " is already in the list.");
                    } else {
                        cards.add(new Card(word.getText(), def.getText()));
                        feedback.setText(word.getText() + " has been added to the list.");
                        index = cards.size() - 1;
                        updateView();
                    }
                    word.setText("");
                    def.setText("");
                } else if (button.equals(prev)) {
                    if (cards.size() == 0) return;
                    index = index - 1 < 0 ? cards.size() - 1 : index - 1;
                    updateView();
                } else if (button.equals(next)) {
                    if (cards.size() == 0) return;
                    index = index + 1 > cards.size() - 1 ? 0 : index + 1;
                    updateView();
                } else if (button.equals(uploadFileButton)) {
                    feedback.setText("Make sure to import a csv file with the format [word, definition].");

                    JFileChooser chooser = new JFileChooser();
                    chooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));
                    if (chooser.showOpenDialog(mainPane) == JFileChooser.APPROVE_OPTION) {
                        File selectedFile = chooser.getSelectedFile();
                        int count = 0;
                        try (Scanner scanner = new Scanner(selectedFile)) {
                            while (scanner.hasNextLine()) {
                                String line = scanner.nextLine();
                                String[] data = line.split(",");
                                if (!contains(data[0])) {
                                    cards.add(new Card(data[0], data[1]));
                                    count++;
                                }
                            }
                        } catch (FileNotFoundException er) {
                            er.printStackTrace();
                            JOptionPane.showMessageDialog(this, "An error occurred.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                        index = cards.size() - 1;
                        updateView();
                        feedback.setText("Added " + count + " new words to the list.");
                    }
                } else if (button.equals(newQuestionButton)) {
                    if (cards.size() < 4) {
                        JOptionPane.showMessageDialog(this, "You need 4 or more words!", "Error", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                    updateQuiz();
                    newQuestionButton.setBackground(new Color(187, 187, 187));
                    newQuestionButton.setEnabled(false);
                }
            } catch (Exception er) {
                JOptionPane.showMessageDialog(this, "An error occurred.", "Error", JOptionPane.ERROR_MESSAGE);
                throw er;
            }
        }
    }

    private void updateView() {
        currentCard.removeAll();
        if (cards.size() >= 4) {
            updateQuiz();
        }
        if (!cards.isEmpty()) {
            Card card = cards.get(index);

            JButton delete = new JButton("Delete Word");
            delete.setFont(prev.getFont());
            delete.setBackground(new Color(227, 74, 74));
            delete.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cards.remove(index);
                    index = 0;
                    updateView();
                }
            });

            currentCard.setLayout(new BoxLayout(currentCard, BoxLayout.Y_AXIS));
            currentCard.setBackground(Color.LIGHT_GRAY);
            currentCard.add(new JLabel("<html><font size=+4><i>" + card.getWord() + "</i></font><br>____________________________________________________________________________<br><font size=+2>Definition:<br>" + card.getDefinition() + "</font><html>"));
            currentCard.add(new JLabel(" "));
            currentCard.add(delete);
        }

        revalidate();
        repaint();

        if (cards.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nothing in list! Add something new in the Add Card tab.", "Information", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void updateQuiz() {
        quizSection.removeAll();

        ArrayList<Card> randomCards = new ArrayList<>();
        ArrayList<Card> temp = new ArrayList<>(cards);
        for (int i = 0; i < 4; i++) {
            int randomIndex = (int) (Math.random() * temp.size());
            Card randomCard = temp.get(randomIndex);
            randomCards.add(randomCard);
            temp.remove(randomIndex);
        }

        Card card = randomCards.get(0);

        java.util.Collections.shuffle(randomCards);

        JRadioButton[] options = new JRadioButton[4];
        ButtonGroup buttonGroup = new ButtonGroup();
        for (int i = 0; i < 4; i++) {
            options[i] = new JRadioButton(randomCards.get(i).getWord());
            options[i].setFont(new Font("", Font.PLAIN, 18));
            options[i].setBackground(Color.LIGHT_GRAY);
            buttonGroup.add(options[i]);
        }

        JButton submitButton = new JButton("Submit");
        submitButton.setFont(prev.getFont());
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < 4; i++) {
                    if (options[i].isSelected()) {
                        if (randomCards.get(i).equals(card)) {
                            options[i].setBackground(Color.GREEN);
                            for (int j = 0; j < 4; j++) {
                                options[j].setEnabled(false);
                            }
                            submitButton.setEnabled(false);
                            JOptionPane.showMessageDialog(Flashcards.this, "Correct answer!", "Result", JOptionPane.INFORMATION_MESSAGE);

                            newQuestionButton.setBackground(new Color(48, 148, 48));
                            newQuestionButton.setEnabled(true);
                        } else {
                            options[i].setEnabled(false);
                            options[i].setBackground(Color.RED);
                            JOptionPane.showMessageDialog(Flashcards.this, "Wrong answer!", "Result", JOptionPane.INFORMATION_MESSAGE);
                        }
                        break;
                    }
                }
            }
        });

        quizSection.setLayout(new BoxLayout(quizSection, BoxLayout.Y_AXIS));
        quizSection.setBackground(Color.LIGHT_GRAY);
        quizSection.add(new JLabel("<html><font size=+2>Definition:</font><br><font size=+1>" + card.getDefinition() + "</font><br>____________________________________________________________________________<br><html>"));
        quizSection.add(new JLabel(" "));

        for (int i = 0; i < 4; i++) {
            quizSection.add(options[i]);
        }
        quizSection.add(submitButton);

        revalidate();
        repaint();
    }

    private boolean contains(String word) {
        for (Card card : cards) {
            if (card.getWord().equals(word)) {
                return true;
            }
        }
        return false;
    }
}
