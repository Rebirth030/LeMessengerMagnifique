import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Locale;
import java.util.Objects;

public class Cryption {
    private static final char[] ALPHABET = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'ß', ' ', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '!', '.', '?', '&', '%', '$', '§', '=', '/', '{', '}', '[', ']', '(', ')', '*', '#', '+', '~', '-', ':', ';', '_', '@', '€', 'ü', 'ä', 'ö', '<', '>', '|', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    private String keyWord;
    private String text;
    private final String encryption;
    private int[] key;
    private byte[] AESKey;
    private Key secretKey;


    public Cryption(String keyWord, String text, String encryption) {
        this.text = text;
        this.keyWord = keyWord;
        this.encryption = encryption;
        if (Objects.equals(this.encryption, "Vigenère")) encryptKeyWordVigenère();
        if (Objects.equals(this.encryption, "AES")) encryptKeyWordAES();
    }

    public String encrypt() {
        if (Objects.equals(encryption, "Vigenère")) return encryptVigenère();
        if (Objects.equals(encryption, "AES")) return encryptAES();
        return null;
    }

    public String decrypt() {
        if (Objects.equals(encryption, "Vigenère")) return decryptVigenère();
        if (Objects.equals(encryption, "AES")) return decryptAES();
        return null;
    }

    private void encryptKeyWordAES() {
        try {
            AESKey = keyWord.getBytes(StandardCharsets.UTF_8);
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            AESKey = sha.digest(AESKey);
            secretKey = new SecretKeySpec(AESKey, "AES");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


    private String encryptAES() {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(text.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    private String decryptAES() {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(text)));
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException |
                 IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        }
    }



    private void encryptKeyWordVigenère() {
        int[] key = new int[keyWord.length()];
        for (int o = 0; o < keyWord.length(); o++) {
            for (int i = 1; i < ALPHABET.length; i++) {
                if (ALPHABET[i] == keyWord.charAt(o)) {
                    key[o] = i + 1;
                }
            }
        }
        this.key = key;
    }

    private String decryptVigenère() {
        for (int i = 0; i <= this.key.length - 1; i++) {
            this.key[i] *= -1;
        }
        return encryptVigenère();
    }


    private String encryptVigenère() {
        int r = 0;
        char[] textChar = this.text.toCharArray();
        char[] result = new char[textChar.length];
        for (int o = 0; o < textChar.length; o++) {
            for (int i = 0; i < ALPHABET.length; i++) {
                if (ALPHABET[i] == textChar[o]) {
                    result[o] = ALPHABET[((i + key[r % key.length] % ALPHABET.length) + ALPHABET.length) % ALPHABET.length];
                    r++;
                }
            }
        }
        return new String(result);
    }
}