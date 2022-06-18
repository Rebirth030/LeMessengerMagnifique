import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Objects;


public class Gui extends JFrame {

    private JTabbedPane tabbedPane;
    private JEditorPane editorPanel;
    private final int rightLocation;
    private int tab = 1;
    private JComboBox encryption;
    private JComboBox encryptionKey;
    private static boolean darkM = false;
    static ArrayList<JPanel> jPanels = new ArrayList();
    static ArrayList<JEditorPane> editorPanes = new ArrayList<>();
    static ArrayList<JComboBox> keys = new ArrayList<>();
    static ArrayList<JComboBox> encryptions = new ArrayList<>();
    private static final String password = "Vx3nzk#f05lBG4BE@Zhcz0Q7zCiyka!9@ui9AWt2";
    private static final String keyFilepath = "file/pass.jks";


    public Gui(String user) {
        setSize(600, 600);
        rightLocation = getWidth() / 2 + 30;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //setIconImage(InitSpriteAnimation.getIcon());
        setLayout(null);
        setLocationRelativeTo(null);
        setTitle(user);
        setResizable(true);
        if (Objects.equals(user, "Server")) setPanel();
    }

    public void setTabbedPane() {
        tabbedPane = new JTabbedPane();
        setPanel();
        tabbedPane.addTab("LocalHost", jPanels.get(0));
        tabbedPane.addTab(" + ", new Panel());
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (tabbedPane.getSelectedIndex() == tabbedPane.indexOfTab(" + ")) {
                    createTab();
                }
            }
        });
        tabbedPane.setBounds(0, 0, getWidth(), getHeight());
        add(tabbedPane, BorderLayout.CENTER);
    }

    private void createTab() {
        tabbedPane.removeTabAt(tabbedPane.indexOfTab(" + "));
        setPanel();
        tabbedPane.addTab("New Tab", jPanels.get(tab));
        tabbedPane.addTab(" + ", new Panel());
        tabbedPane.setSelectedIndex(tab);
        String username = getUserName();
        String ip = getIP();
        int port = getPort();
        Client.createNewConnection(ip, username, port, tab);
        tabbedPane.setTitleAt(tab, ip);
        tab++;
    }

    public void setPanel() {
        JPanel panel = new JPanel();
        jPanels.add(panel);
        panel.setBounds(0, 0, getWidth(), getHeight());
        panel.setLayout(null);
        add(panel);


        editorPanel = new JEditorPane();
        editorPanel.setEditable(false);
        editorPanes.add(editorPanel);

        JScrollPane editorScrollPane = new JScrollPane(editorPanel);
        editorScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        editorScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        editorScrollPane.setBounds(0, 0, getWidth() / 2, getHeight() * 2 / 3);
        panel.add(editorScrollPane);
    }

    public void addClientGui(JPanel panel) {
        String[] encryptionStrings = {"no encryption", "Vigenère", "AES", "Diffie-Hellman"};


        encryptionKey = new JComboBox();
        encryptionKey.addItem("Hatschi");
        encryptionKey.setBounds(rightLocation, 30, 200, 30);
        JLabel label1 = new JLabel("Chose Key or set a new:");
        label1.setBounds(rightLocation, 60, 250, 30);
        keys.add(encryptionKey);
        try {
            createKeyFile();
            getPasswords(jPanels.indexOf(panel));
        } catch (CertificateException | IOException | KeyStoreException | NoSuchAlgorithmException |
                 UnrecoverableEntryException | InvalidKeySpecException e) {
            setEditorPanelText("could not get File", jPanels.indexOf(panel));
        }
        panel.add(encryptionKey);
        panel.add(label1);

        JTextField keyEingabe = new JTextField();
        keyEingabe.setBounds(rightLocation, 90, 200, 30);
        keyEingabe.setToolTipText("Enter a new key for the encryption:");
        keyEingabe.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!keyEingabe.getText().isEmpty()) {
                    encryptionKey.addItem(keyEingabe.getText());
                    encryptionKey.setSelectedItem(keyEingabe.getText());
                    try {
                        Cryption.addToKeyStore(keyEingabe.getText(),password, keyFilepath);
                    } catch (Exception ex) {
                        setEditorPanelText("Could not add Key to Storage", jPanels.indexOf(panel));
                    }
                    keyEingabe.setText("");
                }
            }
        });
        panel.add(keyEingabe);
        JLabel label2 = new JLabel("Chose encryption:");
        label2.setBounds(rightLocation, 160, 200, 30);
        panel.add(label2);
        encryption = new JComboBox(encryptionStrings);
        encryption.setBounds(rightLocation, 190, 200, 30);
        encryption.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(encryption.getSelectedItem() == "Diffie-Hellman") addDHPanel(jPanels.indexOf(panel));
            }
        });
        encryptions.add(encryption);
        panel.add(encryption);
    }

    public void setEditorPanelText(String text, int number) {
        if (!Objects.equals(editorPanes.get(number).getText(), "")) editorPanes.get(number).setText(editorPanes.get(number).getText() + "\n" + text);
        else editorPanes.get(number).setText(text);
    }

    public void addWritingArea(Client client) {
        JPanel panel = jPanels.get(tabbedPane.indexOfTab(" + ") - 1);
        int quadrat = getWidth() / 16;
        JTextField eingabe = new JTextField();
        eingabe.setToolTipText("Here you can write something...");
        eingabe.setFont(new Font(eingabe.getFont().getFontName(), eingabe.getFont().getStyle(), 14));
        eingabe.setBounds(0, getHeight() * 2 / 3, getWidth() / 2 - quadrat, quadrat);
        eingabe.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                send(client, eingabe);
            }
        });
        panel.add(eingabe);

        JButton button = new JButton();
        button.setBounds(getWidth() / 2 - quadrat, getHeight() * 2 / 3, quadrat - 1, quadrat - 1);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                send(client, eingabe);
            }
        });
        panel.add(button);
        panel.repaint();
    }

    private void send(Client client, JTextField eingabe) {
        if (!Objects.equals(eingabe.getText(), "")) {
            client.sendMessage(eingabe.getText(), client.getNumber());
            eingabe.setText("");
        }
    }

    private void addDHPanel(int number) {
        JLabel label3 = new JLabel("Enter two public prime numbers you share:");
        label3.setBounds(rightLocation, 220, 200, 30);
        jPanels.get(number).add(label3);

        JTextField n = new JTextField();
        n.setBounds(rightLocation, 250, 200, 30);
        n.setToolTipText("Enter the first prime number you both share");
        jPanels.get(number).add(n);

        JTextField p = new JTextField();
        p.setBounds(rightLocation, 280, 200, 30);
        p.setToolTipText("Enter the second prime number you both share");
        jPanels.get(number).add(p);

        JButton button2 = new JButton("Get number");
        button2.setToolTipText("generates one public random prime number (use twice for two numbers)");
        button2.setBounds(rightLocation + 75, 310, 125, 30);
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Client.clients.get(number).sendMessage(Cryption.generatePrime(), number);
            }
        });
        jPanels.get(number).add(button2);

        JButton button1 = new JButton("send");
        button1.setBounds(rightLocation, 310, 75, 30);
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Client.clients.get(number).sendMessage((Cryption.generatePublicKey(new BigInteger(n.getText()), new BigInteger(p.getText()))).toString(), number);
                n.setText("");
                p.setText("");
                jPanels.get(number).remove(n);
                jPanels.get(number).remove(p);
                jPanels.get(number).remove(label3);
                jPanels.get(number).remove(button1);
                jPanels.get(number).remove(button2);
                jPanels.get(number).repaint();
                addPublicKeyPanel(number);
            }
        });
        jPanels.get(number).add(button1);
        jPanels.get(number).repaint();
    }

    private void addPublicKeyPanel(int number) {
        JLabel label4 = new JLabel("Enter the public key the other Client send:");
        label4.setBounds(rightLocation, 220, 200, 30);
        jPanels.get(number).add(label4);

        JTextField pKey = new JTextField();
        pKey.setBounds(rightLocation, 250, 200, 30);
        pKey.setToolTipText("Enter the public key the other Client send:");
        pKey.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                keys.get(number).addItem(Cryption.generateKey(new BigInteger(pKey.getText())));
                keys.get(number).setSelectedItem(Cryption.generateKey(new BigInteger(pKey.getText())));
                jPanels.get(number).remove(label4);
                jPanels.get(number).remove(pKey);
                jPanels.get(number).repaint();
            }
        });
        jPanels.get(number).add(pKey);


    }

    public void getPasswords(int number) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableEntryException, InvalidKeySpecException {
        for (int i = 0; i < Cryption.getKeyStoreSize(keyFilepath, password); i++) {
            keys.get(number).addItem(Cryption.getKey(password, keyFilepath,i));
        }
    }

    public void createKeyFile() throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
            File file = new File(keyFilepath);
        KeyStore keyStore = KeyStore.getInstance("JCEKS");
        if (!file.exists()) {
            keyStore.load(null, password.toCharArray());
            FileOutputStream fos = new java.io.FileOutputStream(keyFilepath);
            keyStore.store(fos, password.toCharArray());
            fos.close();
        }

    }


    public String getUserName() {
        JFrame popUpUsername = new JFrame();
        String getMessage = JOptionPane.showInputDialog(popUpUsername, "Enter your username:");

        JOptionPane.showMessageDialog(popUpUsername, "Your Username: " + getMessage);

        //if (Objects.equals(getMessage, "Kellermann")) darkM = true; // TODO: Darkmode

        return getMessage;
    }

    public String getIP() {
        JFrame popUpUsername = new JFrame();
        String getIP = JOptionPane.showInputDialog(popUpUsername, "Enter ServerIP:");

        return getIP;
    }

    public int getPort() {
        JFrame popUpUsername = new JFrame();
        String getPort = JOptionPane.showInputDialog(popUpUsername, "Enter ServerPort:");

        return Integer.parseInt(getPort);
    }


    public String getEncryption(int number) {
        return Objects.requireNonNull(encryptions.get(number).getSelectedItem()).toString();
    }

    public String getKey(int number) {
        return Objects.requireNonNull(keys.get(number).getSelectedItem()).toString();
    }
}

