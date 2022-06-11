import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.Socket;
import java.util.Objects;


public class Gui extends JFrame {

    private JEditorPane editorPanel;
    private int rightLocation;
    private String usernameString = "";
    private JComboBox verschluesselung;
    private JComboBox encryptionKey;

    public Gui(String user) {
        setSize(600, 600);
        rightLocation = getWidth() / 2 + 30;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //setIconImage(InitSpriteAnimation.getIcon());
        setLayout(null);
        setLocationRelativeTo(null);
        setTitle(user);
        setResizable(true);
        setBackground(Color.darkGray);

        editorPanel = new JEditorPane();
        editorPanel.setEditable(false);

        JScrollPane editorScrollPane = new JScrollPane(editorPanel);
        editorScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        editorScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        editorScrollPane.setBounds(0, 0, getWidth() / 2, getHeight() * 2 / 3);
        add(editorScrollPane);
    }

    public void setEditorPanelText(String text) {
        if (editorPanel.getText() != null) editorPanel.setText(editorPanel.getText() + "\n" + text);
        else editorPanel.setText(text);
    }

    public void addKeyPanel() {
        String[] Verschlüsselungen = { "no encryption", "Vigenère", "AES"};


        encryptionKey = new JComboBox();
        encryptionKey.addItem("Hatschi");
        encryptionKey.setBounds(rightLocation, 30, 200, 30);
        JLabel label1 = new JLabel("Chose Key or set a new:");
        label1.setBounds(rightLocation, 60, 250, 30);
        add(encryptionKey);
        add(label1);

        JTextField keyEingabe = new JTextField();
        keyEingabe.setBounds(rightLocation, 90, 200, 30);
        keyEingabe.setToolTipText("Enter a new key for the encryption:");
        keyEingabe.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!keyEingabe.getText().isEmpty()) {
                    encryptionKey.addItem(keyEingabe.getText());
                    encryptionKey.setSelectedItem(keyEingabe.getText());
                    keyEingabe.setText("");
                }
            }
        });
        add(keyEingabe);
        JLabel label2 = new JLabel("Chose encryption:");
        label2.setBounds(rightLocation, 160, 200, 30);
        add(label2);
        verschluesselung = new JComboBox(Verschlüsselungen);
        verschluesselung.setBounds(rightLocation, 190, 200, 30);
        add(verschluesselung);

    }

    public void addWritingArea(Client client) {
        int quadrat = getWidth() / 16;
        JTextField eingabe = new JTextField();
        eingabe.setToolTipText("Here you can write something...");
        eingabe.setFont(new Font(eingabe.getFont().getFontName(), eingabe.getFont().getStyle(), 14));
        eingabe.setBounds(0, getHeight() * 2 / 3, getWidth() / 2 - quadrat, quadrat);
        eingabe.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (eingabe.getText() != null) {
                    client.sendMessage(eingabe.getText());
                    eingabe.setText("");
                }
            }
        });
        add(eingabe);

        JButton button = new JButton();
        button.setBounds(getWidth() / 2 - quadrat, getHeight() * 2 / 3, quadrat - 1, quadrat - 1);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (eingabe.getText() != null) {
                    client.sendMessage(eingabe.getText());
                    eingabe.setText("");
                }
            }
        });
        add(button);
    }

    public String getUserName() {
        JFrame popUpUsername = new JFrame();
        String getMessage = JOptionPane.showInputDialog(popUpUsername, "Enter your username:");

        JOptionPane.showMessageDialog(popUpUsername, "Your Username: " + getMessage);

        return getMessage;
    }

    public String getEncryption(){
        return Objects.requireNonNull(verschluesselung.getSelectedItem()).toString();
    }

    public String getKey(){
        return Objects.requireNonNull(encryptionKey.getSelectedItem()).toString();
    }
}

