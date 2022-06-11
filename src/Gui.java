import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Objects;


public class Gui extends JFrame {

    private JTabbedPane tabbedPane;
    private JEditorPane editorPanel;
    private int rightLocation;
    private int tab = 1;
    private JComboBox encryption;
    private JComboBox encryptionKey;
    static ArrayList<JPanel> jPanels = new ArrayList();
    static ArrayList<JEditorPane> editorPanes = new ArrayList<>();
    static ArrayList<JComboBox> keys = new ArrayList<>();
    static ArrayList<JComboBox> encryptions = new ArrayList<>();


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

    public void setTabbedPane(){
        tabbedPane = new JTabbedPane();
        setPanel();
        tabbedPane.addTab("LocalHost", jPanels.get(0));
        tabbedPane.addTab(" + ", new Panel());
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if(tabbedPane.getSelectedIndex() == tabbedPane.indexOfTab(" + ")){
                    createTab();
                }
            }
        });
        tabbedPane.setBounds(0,0,getWidth(),getHeight());
        add(tabbedPane, BorderLayout.CENTER);
    }

    private void createTab()
    {
        tabbedPane.removeTabAt(tabbedPane.indexOfTab(" + "));
        setPanel();
        tabbedPane.addTab("New Tab",jPanels.get(tab));
        tabbedPane.addTab(" + ",new Panel());
        tabbedPane.setSelectedIndex(tab);
        String username = getUserName();
        String ip = getIP();
        int port = getPort();
        Client.createNewConnection(ip, username, port, tab);
        tabbedPane.setTitleAt(tab,ip);
        tab++;
    }

    public void setPanel(){
        JPanel panel = new JPanel();
        jPanels.add(panel);
        panel.setBounds(0,0,getWidth(),getHeight());
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
        String[] encryptionStrings = { "no encryption", "Vigenère", "AES"};


        encryptionKey = new JComboBox();
        encryptionKey.addItem("Hatschi");
        encryptionKey.setBounds(rightLocation, 30, 200, 30);
        JLabel label1 = new JLabel("Chose Key or set a new:");
        label1.setBounds(rightLocation, 60, 250, 30);
        keys.add(encryptionKey);
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
        encryptions.add(encryption);
        panel.add(encryption);
    }

    public void setEditorPanelText(String text, int number) {
        if (editorPanes.get(number).getText() != null) editorPanes.get(number).setText(editorPanes.get(number).getText() + "\n" + text);
        else editorPanes.get(number).setText(text);
    }

    public void addWritingArea(Client client) {
        JPanel panel = jPanels.get(tabbedPane.indexOfTab(" + ") -1);
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

    private void send(Client client, JTextField eingabe){
        if (!Objects.equals(eingabe.getText(), "")) {
            client.sendMessage(eingabe.getText(), client.getNumber());
            eingabe.setText("");
        }
    }


    public String getUserName() {
        JFrame popUpUsername = new JFrame();
        String getMessage = JOptionPane.showInputDialog(popUpUsername, "Enter your username:");

        JOptionPane.showMessageDialog(popUpUsername, "Your Username: " + getMessage);

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



    public String getEncryption(int number){
        return Objects.requireNonNull(encryptions.get(number).getSelectedItem()).toString();
    }

    public String getKey(int number){
        return Objects.requireNonNull(keys.get(number).getSelectedItem()).toString();
    }
}

