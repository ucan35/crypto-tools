package unsalcan35.cryptotools.core.alphabet;

import unsalcan35.cryptotools.core.matrix.NoCharInAlphabetException;

import java.util.Arrays;

public class Alphabet {

    private CharSequence alphabetCs;
    private int[] charIndex = new int[Character.MAX_VALUE];

    public Alphabet(CharSequence alphabetCs) throws AlphabetException {
        int i;
        if ((i = checkAlphabet(alphabetCs)) > 0) {
            throw new AlphabetException(String.format("Duplicate character '%c'", alphabetCs.charAt(i)));
        }
        initCharIndex(alphabetCs);
        this.alphabetCs = alphabetCs;
    }

    private int checkAlphabet(CharSequence alphabetCs) {
        int length = alphabetCs.length();
        boolean[] unicode = new boolean[Character.MAX_VALUE];
        // Check alphabetCs contains no dublicate characters
        for (int i = 0; i < length; i++) {
            char c = alphabetCs.charAt(i);
            if (unicode[c]) {
                return i;
            }
            unicode[c] = true;
        }
        return 0;
    }

    private void initCharIndex(CharSequence alphabetCs) {
        Arrays.fill(charIndex, -1);
        int length = alphabetCs.length();
        // Assign the index values for the specified alphabetCs
        for (int i = 0; i < length; i++) {
            charIndex[alphabetCs.charAt(i)] = i;
        }
    }

    public int charIndex(char c) {
        int index;
        if ((index = charIndex[c]) == -1) {
            throw new NoCharInAlphabetException(String.format("Char '%c' is not found in the alphabet", c));
        }
        return index;
    }

    public CharSequence getAlphabet() {
        return alphabetCs;
    }

    public int length() {
        return alphabetCs.length();
    }

    public char charAt(int i) {
        return alphabetCs.charAt(i);
    }
}