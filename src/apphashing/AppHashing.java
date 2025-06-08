/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package apphashing;

/**
 *
 * @author HUSAIN
 */
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.swing.*;
import org.mindrot.jbcrypt.BCrypt;
import com.lambdaworks.crypto.SCryptUtil;

public class AppHashing extends JFrame {
    private JTextArea inputArea;
    private JTextArea outputArea;
    private JComboBox<String> algorithmBox;
    private JButton hashButton, fileButton;

    public AppHashing() {
        setTitle("Hashing App (PBKDF2, bcrypt, scrypt)");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        JLabel label = new JLabel("Masukkan Teks:");
        label.setBounds(20, 10, 200, 20);
        add(label);

        inputArea = new JTextArea();
        JScrollPane scroll1 = new JScrollPane(inputArea);
        scroll1.setBounds(20, 30, 440, 80);
        add(scroll1);

        algorithmBox = new JComboBox<>(new String[]{"PBKDF2", "bcrypt", "scrypt"});
        algorithmBox.setBounds(20, 120, 120, 25);
        add(algorithmBox);

        fileButton = new JButton("Pilih File");
        fileButton.setBounds(150, 120, 100, 25);
        add(fileButton);

        hashButton = new JButton("Hash");
        hashButton.setBounds(270, 120, 100, 25);
        add(hashButton);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scroll2 = new JScrollPane(outputArea);
        scroll2.setBounds(20, 160, 440, 180);
        add(scroll2);

        // Aksi
        fileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                int returnVal = chooser.showOpenDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    try {
                        inputArea.setText(readFile(file));
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "Gagal membaca file");
                    }
                }
            }
        });

        hashButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String input = inputArea.getText();
                String algorithm = (String) algorithmBox.getSelectedItem();
                String result = "";
                try {
                    switch (algorithm) {
                        case "PBKDF2":
                            result = hashPBKDF2(input);
                            break;
                        case "bcrypt":
                            result = hashBcrypt(input);
                            break;
                        case "scrypt":
                            result = hashScrypt(input);
                            break;
                    }
                } catch (Exception ex) {
                    result = "Error: " + ex.getMessage();
                }
                outputArea.setText(result);
            }
        });
    }

    private String readFile(File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        StringBuilder sb = new StringBuilder();
        String line;
        while((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }
        br.close();
        return sb.toString();
    }

    // PBKDF2
    private String hashPBKDF2(String input) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] salt = "staticSalt".getBytes(); // Jangan gunakan salt statis di produksi
        PBEKeySpec spec = new PBEKeySpec(input.toCharArray(), salt, 65536, 128);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = factory.generateSecret(spec).getEncoded();
        return Base64.getEncoder().encodeToString(hash);
    }

    // bcrypt
    private String hashBcrypt(String input) {
        return BCrypt.hashpw(input, BCrypt.gensalt());
    }

    // scrypt
    private String hashScrypt(String input) {
        return SCryptUtil.scrypt(input, 16384, 8, 1);
    }

    public static void main(String[] args) {
        new AppHashing().setVisible(true);
    }
}
