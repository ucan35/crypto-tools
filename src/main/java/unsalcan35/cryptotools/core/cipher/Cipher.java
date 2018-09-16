package unsalcan35.cryptotools.core.cipher;

import unsalcan35.cryptotools.core.alphabet.Alphabet;
import unsalcan35.cryptotools.core.matrix.Matrix;
import unsalcan35.cryptotools.core.matrix.MatrixException;
import unsalcan35.cryptotools.core.text.Text;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.CharBuffer;

public class Cipher {

    private static final int steganographyOffset = 32;    // Length is encoded in images' first 32 bit
    private static final int fillCh = 0;    // First letter of the alphabet

    // Encode image
    public static BufferedImage steganography(String message, BufferedImage image) throws SteganographyException {
        return steganography(message, image, steganographyOffset);
    }

    private static BufferedImage steganography(String message, BufferedImage image, int offset) throws SteganographyException {
        int width = image.getWidth();
        int height = image.getHeight();
        if (message.length() > Math.pow(2, offset)) {
            throw new SteganographyException(String.format("Insufficient offset size in image, offset: %d, message length: %d",
                    offset, message.length()));
        }
        // Encode message
        boolean[] bits = encodeMessage(message, steganographyOffset);
        if (bits.length > width * height) {
            throw new SteganographyException(String.format("Insufficient image in pixels, bit length: %d, image: %d px",
                    bits.length, width * height));
        }
        BufferedImage copy = copyImage(image);
        for (int i = 0; i < bits.length; i++) {
            int x = i % width;    // X coordinate of pixel
            int y = i / height;    // Y coordinate of pixel
            int rgb = copy.getRGB(x, y);    // Get argb code of specified pixel
            rgb = (rgb & 0xFFFFFFFE) | (bits[i] ? 1 : 0);
            copy.setRGB(x, y, rgb);
        }
        return copy;
    }

    private static boolean[] encodeMessage(String message, int offset) {
        byte[] data = message.getBytes();
        boolean bits[] = new boolean[offset + data.length * 8];
        String binary = Integer.toBinaryString(data.length);
        while (binary.length() < offset) {
            binary = '0' + binary;    // Leading zeros
        }
        for (int i = 0; i < offset; i++) {
            bits[i] = binary.charAt(i) == '1';
        }
        for (int i = 0; i < data.length; i++) {    // Loop through bytes
            byte b = data[i];
            for (int j = 0; j < 8; j++) {    // Loop through bits
                bits[offset + i * 8 + j] = ((b >> (7 - j)) & 1) == 1;
            }
        }
        return bits;
    }

    public static BufferedImage copyImage(BufferedImage image) {
        BufferedImage copy = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        Graphics g = copy.getGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return copy;
    }

    // Decode image
    public static String steganography(BufferedImage image) {
        return steganography(image, steganographyOffset);
    }

    private static String steganography(BufferedImage image, int offset) {
        int width = image.getWidth();
        int height = image.getHeight();
        int length = 0;
        // Find out length
        for (int i = 0; i < offset; i++) {
            boolean bit = Integer.toBinaryString(image.getRGB(i % width, i / height)).charAt(31) == '1';
            if (bit) {
                length |= (1 << (offset - 1 - i));
            }
        }
        byte[] data = new byte[length];
        for (int i = 0; i < length * 8; i++) {
            boolean bit = Integer.toBinaryString(image.getRGB((offset + i) % width, (offset + i) / height)).charAt(31) == '1';
            if (bit) {
                data[i / 8] |= (1 << (7 - (i % 8)));
            }
        }
        return new String(data);
    }

    public static String hill(Text text, Matrix key, boolean encrypt) throws MatrixException {
        if (!key.checkSquareMatrix()) {
            throw new MatrixException("Non-square matrix key");
        }
        double det = key.determinant();
        if (det != (int) det) {
            // Key values must be integer
            throw new MatrixException("Invalid key matrix");
        }
        int alphabetLength = text.getAlphabet().length();
        if (!encrypt) {
            // Find inverse key
            key = key.inverse().multiply(det).round();
            det = modulo((int) det, alphabetLength);
            key = key.multiply(modInverse((int) det, alphabetLength)).modulo(alphabetLength);
        }
        int keyLength = key.length()[0];
        int[] encodedText = text.getEncodedText();
        int[] newEncodedText = new int[(int) Math.ceil(encodedText.length / (double) keyLength) * keyLength];    // Length with padding
        // Create partial arrays to multiply with the key matrix
        for (int i = 0; i < newEncodedText.length; i += keyLength) {
            double[][] partialArray = new double[1][keyLength];
            for (int j = 0; j < keyLength; j++) {
                partialArray[0][j] = i + j < encodedText.length ? encodedText[i + j] : fillCh;
            }
            Matrix tmp = new Matrix(partialArray).multiply(key).modulo(alphabetLength);
            for (int j = 0; j < keyLength; j++) {
                newEncodedText[i + j] = (int) tmp.toArray()[0][j];
            }
        }
        return Text.buildTextFromEncoded(text.getAlphabet(), newEncodedText).toString();
    }

    public static String caesar(Text text, boolean encrypt) {
        return shift(text, 3, encrypt);
    }

    public static String shift(Text text, int k, boolean encrypt) {
        Alphabet alphabet = text.getAlphabet();
        int[] encodedText = text.getEncodedText();
        // Shift characters according to key
        CharBuffer cb = CharBuffer.allocate(encodedText.length);
        for (int i = 0; i < encodedText.length; i++) {
            char c = shiftChar(encodedText[i], alphabet, encrypt ? k : -k);
            cb.append(c);
        }
        cb.rewind();
        return cb.toString();
    }

    public static String vigenere(Text text, String key, boolean encrypt) {
        Alphabet alphabet = text.getAlphabet();
        int[] encodedText = text.getEncodedText();
        int[] encodedKey = new Text(alphabet, key).getEncodedText();
        // Shift characters according to key
        CharBuffer cb = CharBuffer.allocate(encodedText.length);
        for (int i = 0; i < encodedText.length; i++) {
            int k = encodedKey[i % encodedKey.length];
            char c = shiftChar(encodedText[i], alphabet, encrypt ? k : -k);
            cb.append(c);
        }
        cb.rewind();
        return cb.toString();
    }

    private static char shiftChar(int ch, Alphabet alphabet, int k) {
        return alphabet.charAt(modulo(ch + k, alphabet.length()));
    }

    // Positive modulo
    private static int modulo(int n, int m) {
        return ((n % m) + m) % m;
    }

    // Returns negative 1 if not found
    private static int modInverse(int a, int m) {
        a %= m;
        for (int x = 1; x < m; x++) {
            if ((a * x) % m == 1) return x;
        }
        return -1;
    }
}