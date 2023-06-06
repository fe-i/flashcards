import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Flashcards extends JFrame implements ActionListener {
    private static ArrayList<Card> cards;
    private int index;
    private JTabbedPane mainPane;
    private JPanel addPanel;
    private JPanel viewPanel;
    private JPanel quizPanel;
    private JTextField wordField;
    private JTextArea defField;
    private JLabel feedback;
    private JButton addCardButton;
    private JButton uploadFileButton;
    private JPanel currentCard;
    private JButton prev;
    private JButton next;
    private JPanel quizSection;
    private JButton newQuestionButton;

    public Flashcards() {
        cards = new ArrayList<Card>(); // initialize arraylist
        feedback.setText(Utils.loadCSV()); // load data.csv if it exists OR create new data.csv
        setContentPane(mainPane);
        setTitle("Flashcards");
        setSize(500, 400);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        addCardButton.addActionListener(this);
        uploadFileButton.addActionListener(this);
        prev.addActionListener(this);
        next.addActionListener(this);
        newQuestionButton.addActionListener(this);
        setVisible(true);
        if (cards.size() > 0)
            setView(cards.size() - 1); // if there are cards, update the view card panel with the last card
    }

    public static ArrayList<Card> getCards() {
        return cards;
    }

    public void setView(int i) {
        index = i;
        updateView();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source instanceof JButton button) {
            try {
                if (button.equals(addCardButton)) {
                    String wordInput = wordField.getText().trim();
                    String defInput = defField.getText().trim();

                    if (wordInput.length() == 0) {
                        feedback.setText("I'm literally at a loss for words. Perhaps you could add one?");
                        return;
                    }
                    if (defInput.length() == 0) {
                        feedback.setText("<html>What does <font color=blue>" + wordInput + "</font> mean?</html>");
                        return;
                    }
                    if (Utils.contains(wordInput)) {
                        feedback.setText("<html><font color=blue>" + wordInput + "</font> is already in the list.</html>");
                    } else {
                        cards.add(new Card(wordInput, defInput));
                        setView(cards.size() - 1);
                        Utils.writeToFile(Utils.cardsToCSV(cards));
                        feedback.setText("<html><font color=blue>" + wordInput + "</font> has been added to the list.</html>");
                    }
                    wordField.setText("");
                    defField.setText("");
                } else if (button.equals(prev)) {
                    if (cards.size() == 0) return;
                    setView(index - 1 < 0 ? cards.size() - 1 : index - 1);
                } else if (button.equals(next)) {
                    if (cards.size() == 0) return;
                    setView(index + 1 > cards.size() - 1 ? 0 : index + 1);
                } else if (button.equals(uploadFileButton)) {
                    feedback.setText("Make sure to import a csv file with the format [wordField,definition]");
                    JFileChooser chooser = new JFileChooser();
                    chooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));
                    if (chooser.showOpenDialog(mainPane) == JFileChooser.APPROVE_OPTION) {
                        int count = Utils.loadCSV(chooser.getSelectedFile());
                        setView(cards.size() - 1);
                        Utils.writeToFile(Utils.cardsToCSV(cards));
                        feedback.setText("<html>Added <font color=blue>" + count + "</font> new words to the list.</html>");
                    }
                } else if (button.equals(newQuestionButton)) {
                    updateQuiz();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "An error occurred.", "Error", JOptionPane.ERROR_MESSAGE);
                throw ex;
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

            currentCard.setLayout(new BoxLayout(currentCard, BoxLayout.Y_AXIS));
            currentCard.setBackground(Color.LIGHT_GRAY);
            currentCard.add(new JLabel("<html><font size=+4><i> " + card.getWord() + "</i></font><br>____________________________________________________________________________<br><font size=+2>Definition:<br>" + card.getDefinition() + "</font><html>"));
            currentCard.add(new JLabel(" "));

            JButton modify = new JButton("Modify Word");
            modify.setFont(prev.getFont());
            modify.setBackground(new Color(155, 175, 137));
            modify.addActionListener(e -> {
                JPanel modifyPanel = new JPanel();
                modifyPanel.setLayout(new BoxLayout(modifyPanel, BoxLayout.Y_AXIS));
                modifyPanel.add(new JLabel("New Word"));
                JTextField newWordField = new JTextField(card.getWord());
                modifyPanel.add(newWordField);
                modifyPanel.add(new JLabel("New Definition"));
                JTextField newDefField = new JTextField(card.getDefinition());
                modifyPanel.add(newDefField);

                int result = JOptionPane.showConfirmDialog(null, modifyPanel, "Modify Current Word", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (result == JOptionPane.OK_OPTION) {
                    String newWord = newWordField.getText().trim();
                    String newDef = newDefField.getText().trim();
                    if (newWord.length() < 1 || newDef.length() < 1) {
                        JOptionPane.showMessageDialog(this, "I think you forgot to type something in one the input fields.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (!newWord.equals(card.getWord())) {
                        if (Utils.contains(newWord)) {
                            JOptionPane.showMessageDialog(this, "That word already exists somewhere else in the list.", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        card.setWord(newWord);
                    }
                    card.setDefinition(newDef);
                    updateView();
                    Utils.writeToFile(Utils.cardsToCSV(cards));
                }
            });
            currentCard.add(modify);

            currentCard.add(new JLabel(" "));

            JButton delete = new JButton("Delete Word");
            delete.setFont(prev.getFont());
            delete.setBackground(new Color(227, 74, 74));
            delete.addActionListener(e -> {
                cards.remove(index);
                setView(cards.size() - 1);
                Utils.writeToFile(Utils.cardsToCSV(cards));
            });
            currentCard.add(delete);
        }
        revalidate();
        repaint();
        if (cards.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nothing in list! Add something new in the New Card tab.", "Information", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void updateQuiz() {
        quizSection.removeAll();
        newQuestionButton.setBackground(new Color(187, 187, 187));
        newQuestionButton.setEnabled(false);
        if (cards.size() < 4) return;

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

        quizSection.setLayout(new BoxLayout(quizSection, BoxLayout.Y_AXIS));
        quizSection.setBackground(Color.LIGHT_GRAY);
        quizSection.add(new JLabel("<html><font size=+2>Definition:</font><br><font size=+1>" + card.getDefinition() + "</font><br>____________________________________________________________________________<br><html>"));
        quizSection.add(new JLabel(" "));

        JRadioButton[] options = new JRadioButton[4];
        ButtonGroup buttonGroup = new ButtonGroup();
        for (int i = 0; i < 4; i++) {
            options[i] = new JRadioButton(randomCards.get(i).getWord());
            options[i].setFont(new Font("", Font.PLAIN, 18));
            options[i].setBackground(Color.LIGHT_GRAY);
            buttonGroup.add(options[i]);
            quizSection.add(options[i]);
        }

        JButton submitButton = new JButton("Submit");
        submitButton.setFont(prev.getFont());
        submitButton.addActionListener(e -> {
            for (int i = 0; i < 4; i++) {
                if (options[i].isSelected()) {
                    if (randomCards.get(i).equals(card)) {
                        options[i].setBackground(Color.GREEN);
                        for (int j = 0; j < 4; j++) {
                            options[j].setEnabled(false);
                        }
                        submitButton.setEnabled(false);
                        if (cards.size() >= 4) {
                            newQuestionButton.setBackground(new Color(48, 148, 48));
                            newQuestionButton.setEnabled(true);
                        }
                        JOptionPane.showMessageDialog(Flashcards.this, "Correct answer!", "Result", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        options[i].setEnabled(false);
                        options[i].setBackground(Color.RED);
                        JOptionPane.showMessageDialog(Flashcards.this, "Wrong answer!", "Result", JOptionPane.INFORMATION_MESSAGE);
                    }
                    break;
                }
            }
        });
        quizSection.add(submitButton);

        revalidate();
        repaint();
    }
}
