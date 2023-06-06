public class Card {
    private String word;
    private String definition;

    public Card(String w, String d) {
        word = w;
        definition = d;
    }

    public String getWord() {
        return word;
    }

    public String getDefinition() {
        return definition;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }
}
