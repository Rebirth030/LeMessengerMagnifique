import java.util.Arrays;
import java.util.Scanner;

import java.util.Scanner;
public class test {
        public static Scanner scanner = new Scanner(System.in);
        static char[] alphabet = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '!', '?', '.'};
        static char[] textChar;
        static char[] result;
        static int[] key;
        static String text;
        static String keyString;

        public static void main(String[] args) {

            System.out.println("Bitte den Text der ver- oder entschlüsselt werden soll eingeben(Die Ausgabe ist ohne Leerzeichen):");
            text = scanner.nextLine().toLowerCase();
            System.out.println("Verschiebungs KEY bitte eingeben (nur Buchstaben):");
            keyString = scanner.nextLine().toLowerCase();
            key = new int[keyString.length()];


            for (int o = 0; o < keyString.length() ; o++) {
                for (int i = 1; i < alphabet.length; i++) {
                    if (alphabet[i] == keyString.charAt(o)) {
                        key[o] = i+1;
                    }
                }
            }

            textChar = text.toCharArray();
            result = new char[textChar.length];


            System.out.println("Willst du etwas verschlüsseln ('e' eingeben) oder entschlüsseln ('d' eingeben)");
            String input = scanner.next();

            switch (input) {
                case "e" -> cryption();
                case "d" -> {
                    for (int i = 0; i <= key.length - 1; i++) {
                        key[i] *= -1;
                    }
                    cryption();
                }
                default -> System.out.println("Dann halt nicht!!!!!");
            }
            for (int i = 0; i < textChar.length; i++) {
                System.out.print(result[i]);
            }
        }

        public static void cryption() {
            int r = 0;
            for (int o = 0; o < textChar.length; o++) {
                for (int i = 0; i < alphabet.length; i++) {
                    if (alphabet[i] == textChar[o]) {
                        result[o] = alphabet[((i + key[r % key.length] % alphabet.length) + alphabet.length) % alphabet.length];
                        r++;
                    }
                }
            }
        }
    }
