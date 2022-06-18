import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Locale;
import java.util.Objects;

public class Cryption {
    private static final char[] ALPHABET = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'ß', ' ', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '!', '.', '?', '&', '%', '$', '§', '=', '/', '{', '}', '[', ']', '(', ')', '*', '#', '+', '~', '-', ':', ';', '_', '@', '€', 'ü', 'ä', 'ö', '<', '>', '|', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    private String keyWord, text, encryption;
    private int[] key;
    private byte[] AESKey;
    private Key secretKey;
    private static BigInteger n, p, a;
    private static int bitLength = 128;
    static SecureRandom random = new SecureRandom();


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
        return "error: could not find decryption";
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
        } catch (Exception e) {
            return "error: no decryption possible";
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

    protected static String generatePrime() {
        System.out.println(BigInteger.probablePrime(bitLength, random));
        return BigInteger.probablePrime(bitLength, random).toString();
    }

    protected static String generatePublicKey(BigInteger n, BigInteger p) {
        Cryption.n = n;
        Cryption.p = p;
        a = new BigInteger(Integer.toString((int) (Math.random() * bitLength)));
        BigInteger A = n.modPow(a, p);
        return A.toString();
    }

    protected static String generateKey(BigInteger B) {
        return B.modPow(a, p).toString();
    }

    public static String getKey(String password, String filepath, int position) throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException, UnrecoverableEntryException, InvalidKeySpecException {
        KeyStore keyStore = KeyStore.getInstance("JCEKS");
        keyStore.load(null, password.toCharArray());
        KeyStore.PasswordProtection keyStorePP = new KeyStore.PasswordProtection(password.toCharArray());

        FileInputStream fIn = new FileInputStream(filepath);

        keyStore.load(fIn, password.toCharArray());

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBE");

        KeyStore.SecretKeyEntry ske =
                (KeyStore.SecretKeyEntry) keyStore.getEntry(String.valueOf(position), keyStorePP);

        PBEKeySpec keySpec = (PBEKeySpec) factory.getKeySpec(
                ske.getSecretKey(),
                PBEKeySpec.class);
        fIn.close();

        return new String(keySpec.getPassword());

    }

    public static void addToKeyStore(String Key, String password, String filepath) throws Exception {

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBE");
        SecretKey generatedSecret = factory.generateSecret(new PBEKeySpec(Key.toCharArray()));

        File file = new File(filepath);
        KeyStore keyStore = KeyStore.getInstance("JCEKS");

        FileInputStream fIn = new FileInputStream(filepath);
        keyStore.load(fIn, password.toCharArray());
        fIn.close();

        KeyStore.PasswordProtection keyStorePP = new KeyStore.PasswordProtection(password.toCharArray());

        keyStore.setEntry(String.valueOf(keyStore.size()), new KeyStore.SecretKeyEntry(generatedSecret), keyStorePP);

        FileOutputStream fos = new java.io.FileOutputStream(filepath);
        keyStore.store(fos, password.toCharArray());
        fos.close();
    }

    public static int getKeyStoreSize(String filepath, String password) throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException {
        FileInputStream fIn = new FileInputStream(filepath);
        KeyStore keyStore = KeyStore.getInstance("JCEKS");
        keyStore.load(fIn, password.toCharArray());
        fIn.close();
        return keyStore.size();
    }
}