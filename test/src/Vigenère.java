import java.util.Arrays;

public class Vigenère {
    private static final char[] ALPHABET = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', ' ', '1','2','3','4', '5','6','7','8','9', '0','!', '.', '?', '&','%', '$','§','=', '/','{', '}', '[', ']','(', ')','*','#', '+', '~', '-',':', ';', '_','@','€','ü', 'ä','ö','<', '>', '|'};
    private String keyWord;
    private String text;
    private int[] key;


    public Vigenère(String keyWord, String text) {
        this.text = text;
        this.keyWord = keyWord;
    }

    public static void main(String[] args) {
        Vigenère vigenère = new Vigenère("hallo", "was geht alter");
        vigenère.encryptKeyWord(vigenère.keyWord);
        System.out.println(vigenère.encrypt());
        vigenère.setText(vigenère.encrypt());
        System.out.println(vigenère.decrypt().toString());
        //Vigenère vigenère1 = new Vigenère("hallo",vigenère.encrypt());
        //vigenère1.encryptKeyWord(vigenère1.keyWord);
        //System.out.println(vigenère1.decrypt());
    }

    public void encryptKeyWord(String keyWord) {
        int[] key = new int[keyWord.length()];
        for (int o = 0; o < keyWord.length(); o++) {
            for (int i = 1; i < ALPHABET.length; i++) {
                if (ALPHABET[i] == keyWord.charAt(o)) {
                    System.out.println(ALPHABET[i] + " "+ i + "" +keyWord.charAt(o) + " "+ o);
                    key[o] = i + 1;
                }
            }
        }
        this.key = key;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String decrypt(){
        for (int i = 0; i <= this.key.length - 1; i++) {
            this.key[i] *= -1;
        }
        return encrypt();
    }


    public String encrypt() {
        int r = 0;
        char[] textChar = this.text.toCharArray();
        System.out.println(Arrays.toString(textChar));
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
