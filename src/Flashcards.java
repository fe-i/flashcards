import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

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
    private JPanel quizPanel;

    public Flashcards() {
        setContentPane(mainPane);
        setTitle("Flashcards");
        setSize(500, 400);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        addCardButton.addActionListener(this);
        prev.addActionListener(this);
        next.addActionListener(this);
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
                        JOptionPane.showMessageDialog(this, "The word is already in the list.", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        cards.add(new Card(word.getText(), def.getText()));
                        JOptionPane.showMessageDialog(this, "The word has been added to the list.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        index = cards.size() - 1;
                        updateView();
                    }
                } else if (button.equals(prev)) {
                    if (cards.size() == 0) return;
                    index = index - 1 < 0 ? cards.size() - 1: index - 1;
                    updateView();
                } else if (button.equals(next)) {
                    if (cards.size() == 0) return;
                    index = index + 1 > cards.size() - 1 ? 0 : index + 1;
                    updateView();
                }
            } catch (Exception er) {
                JOptionPane.showMessageDialog(this, "An error occurred.", "Error", JOptionPane.ERROR_MESSAGE);
                throw er;
            }
        }
    }

    private void updateView() {
        currentCard.removeAll();
        if (!cards.isEmpty()) {
            Card card = cards.get(index);

            JLabel wordLabel = new JLabel(card.getWord());
            wordLabel.setFont(new Font("Times New Roman", Font.BOLD, 36));

            JLabel defLabel = new JLabel(card.getDefinition());
            defLabel.setFont(new Font("Times New Roman", Font.PLAIN, 20));

            JButton delete = new JButton("Delete Word From List");
            delete.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cards.remove(index);
                    index = 0;
                    updateView();
                }
            });

            currentCard.setLayout(new BoxLayout(currentCard, BoxLayout.Y_AXIS));
            currentCard.setBackground(Color.ORANGE);
            currentCard.add(wordLabel);
            currentCard.add(new JLabel("________________________________________________________________________________________"));
            currentCard.add(new JLabel("\n"));
            currentCard.add(defLabel);
            currentCard.add(delete);
        }

        revalidate();
        repaint();

        if (cards.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nothing in list! Add something new in the Add Card tab.", "Information", JOptionPane.INFORMATION_MESSAGE);
        }
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
