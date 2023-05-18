import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Flashcards extends JFrame implements ActionListener {
    private ArrayList<Card> cards = new ArrayList<Card>();
    private JTextField word;
    private JTextArea def;
    private JButton addCardButton;

    public Flashcards() {
        addCardButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source instanceof JButton) {
            JButton button = (JButton) source;

            try {
                if (button.equals(addCardButton)) {
                    if (cards.stream().map(card -> card.getWord().equals(word)).count() == 0) {
                        //add
                    }
                }
            } catch (Exception er) {
                System.out.println("error because input is bad");
                throw er;
            }
        }
    }
}
