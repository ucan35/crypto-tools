package unsalcan35.cryptotools;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import org.apache.commons.lang.math.NumberUtils;
import unsalcan35.cryptotools.core.alphabet.Alphabet;
import unsalcan35.cryptotools.core.alphabet.AlphabetException;
import unsalcan35.cryptotools.core.cipher.Cipher;
import unsalcan35.cryptotools.core.cipher.SteganographyException;
import unsalcan35.cryptotools.core.matrix.Matrix;
import unsalcan35.cryptotools.core.matrix.MatrixException;
import unsalcan35.cryptotools.core.matrix.NoCharInAlphabetException;
import unsalcan35.cryptotools.core.text.Text;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class MainAppController {

    private static final int PAGE_MAIN = 0;
    private static final int PAGE_SHIFT = 1;
    private static final int PAGE_HILL = 2;
    private static final int PAGE_CAESAR = 3;
    private static final int PAGE_STENOGRAPHY = 4;

    private static final String ALPHABET_EN = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String ALPHABET_TR = "ABCÇDEFGĞHİIJKLMNOÖPRSŞTUÜVYZ";

    private static boolean isEncrypted = true;
    private static boolean isEncryptedStenography = true;
    private static File imageFile = null;
    private static int currentPage = 0;

    @FXML
    Button mainMenuButton;
    @FXML
    StackPane stackPane;
    @FXML
    Label menuLabel;
    // Shift
    @FXML
    TextField alphabetTextField;
    @FXML
    ChoiceBox alphabetChoiceBox;
    @FXML
    TextField textTextField;
    @FXML
    TextField keyTextField;
    @FXML
    ChoiceBox choiceBox;
    @FXML
    Button applyButton;
    @FXML
    TextField resultTextField;
    // Stenography
    @FXML
    TextField imageTextField;
    @FXML
    TextField messageTextField;
    @FXML
    ChoiceBox choiceBox2;
    @FXML
    TextField resultStegTextField;

    @FXML
    public void initialize() {
        initAlphabetChoiceBox();
        initChoiceBox();
        initializeChoiceBox2();

    }

    private void initializeChoiceBox2() {
        choiceBox2.setItems(FXCollections.observableArrayList("Encrypt", "Decrypt"));
        choiceBox2.getSelectionModel().select(0);
        choiceBox2.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            isEncryptedStenography = newValue.intValue() == 0;
        });
    }

    private void initAlphabetChoiceBox() {
        alphabetChoiceBox.setItems(FXCollections.observableArrayList("English", "Turkish", "User Defined"));
        alphabetChoiceBox.getSelectionModel().select(2);
        alphabetChoiceBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            switch (newValue.intValue()) {
                case 0:
                    alphabetTextField.setText(ALPHABET_EN);
                    break;
                case 1:
                    alphabetTextField.setText(ALPHABET_TR);
                    break;
                default:
                    alphabetTextField.setText("");
            }
        });
    }

    private void initChoiceBox() {
        choiceBox.setItems(FXCollections.observableArrayList("Encrypt", "Decrypt"));
        choiceBox.getSelectionModel().selectFirst();
        choiceBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            isEncrypted = newValue.intValue() == 0;
        });
    }

    @FXML
    private void popStenographyWindow(ActionEvent event) {
        switchPage(currentPage, PAGE_STENOGRAPHY);
        menuLabel.setText("current menu, stenography");
    }

    @FXML
    private void popShiftWindow(ActionEvent event) {
        switchPage(currentPage, PAGE_SHIFT);
        menuLabel.setText("current menu, shift cipher");
    }

    @FXML
    private void popHillWindow(ActionEvent event) {
        switchPage(currentPage, PAGE_HILL);
        menuLabel.setText("current menu, hill cipher");
    }

    @FXML
    private void popCaesarWindow(ActionEvent event) {
        switchPage(currentPage, PAGE_CAESAR);
        menuLabel.setText("current menu, caesar cipher");
    }

    @FXML
    private void popMainMenu(ActionEvent event) {
        switchPage(currentPage, PAGE_MAIN);
        menuLabel.setText("current menu, main menu");
    }

    @FXML
    private void onImageSelect(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open image file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG", "*.png"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPEG", "*.jpg", "*.jpeg"));
        imageFile = fileChooser.showOpenDialog(applyButton.getScene().getWindow());
        if (imageFile != null) {
            imageTextField.setText(imageFile.getAbsolutePath());
        }
    }

    @FXML
    private void onStenographyApplyButton(ActionEvent event) {
        String msg = messageTextField.getText();
        String imageText = imageTextField.getText();
        if (isEncryptedStenography) {
            if (msg.length() == 0 || imageText.length() == 0) {
                resultStegTextField.setText("Error: please fill every required field");
                return;
            }

            try {
                ImageIO.write(Cipher.steganography(msg, ImageIO.read(imageFile)), "png", imageFile);
                resultStegTextField.setText("Done, new image is overwritten");
            } catch (SteganographyException | IOException e) {
                resultStegTextField.setText("Error: " + e.getMessage());
            }
        } else {
            if (imageText.length() == 0) {
                resultStegTextField.setText("Error: please fill every required field");
                return;
            }
            try {
                resultStegTextField.setText(Cipher.steganography(ImageIO.read(imageFile)));
            } catch (IOException e) {
                resultStegTextField.setText("Error: " + e.getMessage());
            } catch (RuntimeException e) {
                resultStegTextField.setText("Error: no hidden message is found in the image");
            }
        }
    }

    @FXML
    private void onApplyAction(ActionEvent event) {
        String alphabet = alphabetTextField.getText().toUpperCase();
        String text = textTextField.getText().toUpperCase();
        String keyStr = keyTextField.getText().toUpperCase();
        try {
            switch (currentPage) {
                case PAGE_SHIFT: {
                    if (alphabet.length() == 0 || text.length() == 0 || keyStr.length() == 0) {
                        resultTextField.setText("Error: please fill every required field");
                        return;
                    }
                    if (!NumberUtils.isNumber(keyStr)) {
                        resultTextField.setText("Error: key must be a number");
                        return;
                    }
                    int key = Integer.valueOf(keyStr);
                    resultTextField.setText(Cipher.shift(new Text(new Alphabet(alphabet), text),
                            key, isEncrypted));
                    break;
                }
                case PAGE_HILL: {
                    if (alphabet.length() == 0 || text.length() == 0 || keyStr.length() == 0) {
                        resultTextField.setText("Error: please fill every required field");
                        return;
                    }
                    // Check keyStr is matrix or not with using regex
                    Pattern pattern = Pattern.compile("\\{\\{[0-9]+(,[0-9]+)*\\}(,\\{[0-9]+(,[0-9]+)*\\})*\\}");
                    if (!pattern.matcher(keyStr).matches()) {
                        resultTextField.setText("Error: please enter suitable key, example {{1,2},{3,4}}");
                        return;
                    }
                    int rowLength = 1;
                    int colLength = 1;
                    // Find out row length
                    for (int i = 2; keyStr.charAt(i) != '}'; i++) {
                        if (keyStr.charAt(i) == ',') {
                            rowLength++;
                        }
                    }
                    // Find out column length
                    for (int i = 2; i < keyStr.length() - 1; i++) {
                        if (keyStr.charAt(i) == ',' && keyStr.charAt(i + 1) == '{') {
                            colLength++;
                        }
                    }
                    // Build the matrix
                    double[][] matrixArray = new double[colLength][rowLength];
                    for (int i = 0; i < colLength; i++) {
                        for (int j = 0; j < rowLength; j++) {
                            matrixArray[i][j] = Character.getNumericValue(keyStr.charAt(2 + (j * 2) + (i * rowLength + 2)));
                        }
                    }
                    resultTextField.setText(Cipher.hill(new Text(new Alphabet(alphabet), text),
                            new Matrix(matrixArray), isEncrypted));
                    break;
                }
                case PAGE_CAESAR: {
                    if (alphabet.length() == 0 || text.length() == 0) {
                        resultTextField.setText("Error: please fill every required field");
                        return;
                    }
                    resultTextField.setText(Cipher.shift(new Text(new Alphabet(alphabet), text),
                            3, isEncrypted));
                    break;
                }
            }
        } catch (AlphabetException | NoCharInAlphabetException | MatrixException e) {
            resultTextField.setText("Error: " + e.getMessage());
        }

    }

    private static final List<Integer> PAGES_SHIFT = Arrays.asList(1, 2, 3);
    private static final List<Integer> PAGES_STENOGRAPHY = Arrays.asList(4);

    private void switchPage(int current, int switchTo) {

        int toPane, fromPane;
        if (PAGES_SHIFT.contains(switchTo)) {
            toPane = 1;
        } else if (PAGES_STENOGRAPHY.contains(switchTo)) {
            toPane = 2;
        } else {
            toPane = 0; // Main window
        }
        if (PAGES_SHIFT.contains(current)) {
            fromPane = 1;
        } else if (PAGES_STENOGRAPHY.contains(current)) {
            fromPane = 2;
        } else {
            fromPane = 0; // Main window
        }

        if (PAGE_CAESAR == switchTo) {
            keyTextField.setText("3");
        }
        keyTextField.setDisable(PAGE_CAESAR == switchTo);

        ObservableList<Node> panes = stackPane.getChildren();
        panes.get(fromPane).setVisible(false);
        panes.get(toPane).setVisible(true);
        mainMenuButton.setVisible(switchTo != PAGE_MAIN);
        currentPage = switchTo;
    }

}
