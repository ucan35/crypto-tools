package unsalcan35.cryptotools.core.text;

import unsalcan35.cryptotools.core.alphabet.Alphabet;

public class Text {

    private Alphabet alphabet;
    private String text;
    private int[] encodedText;

    public Text(Alphabet alphabet, String text) {
        this.alphabet = alphabet;
        this.text = text;
        char[] charText = text.toCharArray();
        encodedText = new int[text.length()];
        // Assign index value of each character according to the alphabet
        for (int i = 0; i < charText.length; i++) {
            encodedText[i] = alphabet.charIndex(charText[i]);
        }
    }

    private Text(Alphabet alphabet, String text, int[] encodedText) {
        this.text = text;
        this.alphabet = alphabet;
        this.encodedText = encodedText;
    }

    public static Text buildTextFromEncoded(Alphabet alphabet, int[] encodedText) {
        // Build text from the encoded text
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < encodedText.length; i++) {
            sb.append(alphabet.charAt(encodedText[i]));
        }
        return new Text(alphabet, sb.toString(), encodedText);
    }

    public Alphabet getAlphabet() {
        return alphabet;
    }

    public int[] getEncodedText() {
        return encodedText;
    }

    public String toString() {
        return text;
    }
}
