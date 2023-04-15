import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

class GraUseInt extends JFrame implements ActionListener {
    private JRadioButton encOpt, decOpt;
    private JPasswordField pasFie;
    private JTextField selFolFilPat;
    private JButton selCha, sub, can;
    private File selFolFil;
    private JCheckBox shoPas;
    private String pasPat = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[`~!@#$%^&*()\\-_=+\\[{\\]}\\\\|;:'\",<.>/?]).{8,}$";

    GraUseInt() {
        setTitle("Encryption & Decryption");
        setSize(500, 220);
        setResizable(false);
        setLocationRelativeTo(this);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 1));
        getContentPane().setBackground(new Color(28, 37, 46));

        JPanel optPan = new JPanel(new FlowLayout());
        encOpt = new JRadioButton("Encrypt");
        encOpt.setFont(new Font("Cascadia Code", Font.PLAIN, 18));
        encOpt.setForeground(Color.WHITE);
        encOpt.setBackground(new Color(28, 37, 46));
        decOpt = new JRadioButton("Decrypt");
        decOpt.setFont(new Font("Cascadia Code", Font.PLAIN, 18));
        decOpt.setForeground(Color.WHITE);
        decOpt.setBackground(new Color(28, 37, 46));
        ButtonGroup optGro = new ButtonGroup();
        optGro.add(encOpt);
        optGro.add(decOpt);
        optPan.add(encOpt);
        optPan.add(decOpt);
        optPan.setBackground(new Color(28, 37, 46));
        add(optPan);

        JPanel pasPan = new JPanel(new FlowLayout());
        JLabel pasLab = new JLabel("Password ");
        pasLab.setFont(new Font("Cascadia Code", Font.PLAIN, 18));
        pasLab.setForeground(Color.WHITE);
        pasFie = new JPasswordField(20);
        pasFie.setBackground(new Color(204, 238, 241));
        pasFie.setEchoChar('●');
        pasPan.add(pasLab);
        pasPan.add(pasFie);
        shoPas = new JCheckBox("Show Password");
        shoPas.setFont(new Font("Arial", Font.PLAIN, 18));
        shoPas.setForeground(Color.WHITE);
        shoPas.setBackground(new Color(28, 37, 46));
        shoPas.addActionListener(this);
        pasPan.add(shoPas);
        pasPan.setBackground(new Color(28, 37, 46));
        add(pasPan);

        JPanel filPan = new JPanel(new FlowLayout());
        JLabel filLab = new JLabel("File ");
        filLab.setFont(new Font("Cascadia Code", Font.PLAIN, 18));
        filLab.setForeground(Color.WHITE);
        selFolFilPat = new JTextField(20);
        selFolFilPat.setBackground(new Color(204, 238, 241));
        selFolFilPat.setEditable(false);
        selCha = new JButton("Select");
        selCha.setFont(new Font("Arial", Font.PLAIN, 18));
        selCha.addActionListener(this);
        filPan.add(filLab);
        filPan.add(selFolFilPat);
        filPan.add(selCha);
        filPan.setBackground(new Color(28, 37, 46));
        add(filPan);

        JPanel subCanPan = new JPanel(new FlowLayout());
        sub = new JButton("Submit");
        sub.setFont(new Font("Arial", Font.PLAIN, 18));
        sub.addActionListener(this);
        can = new JButton("Cancel");
        can.setFont(new Font("Arial", Font.PLAIN, 18));
        can.addActionListener(this);
        subCanPan.add(sub);
        subCanPan.add(can);
        subCanPan.setBackground(new Color(28, 37, 46));
        add(subCanPan);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == selCha) {
            JFileChooser folFilCho = new JFileChooser();
            folFilCho.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            folFilCho.setDialogTitle("Select a Folder or File");
            int retVal = folFilCho.showOpenDialog(this);
            if (retVal == JFileChooser.APPROVE_OPTION) {
                selFolFil = folFilCho.getSelectedFile();
                selFolFilPat.setText(selFolFil.getAbsolutePath());
                selCha.setText("Change");
            } else if (retVal != JFileChooser.APPROVE_OPTION && selFolFil == null) {
                JLabel mes = new JLabel("Folder or File Not Selected");
                mes.setFont(new Font("Arial", Font.BOLD, 16));
                JOptionPane.showMessageDialog(this, mes, "Message", JOptionPane.WARNING_MESSAGE);
            }
        } else if (e.getSource() == sub) {
            if (selFolFil != null) {
                if (!(new String(pasFie.getPassword())).isEmpty()) {
                    if ((new String(pasFie.getPassword())).matches(pasPat)) {
                        if (encOpt.isSelected() || decOpt.isSelected()) {
                            if (encOpt.isSelected()) {
                                try {
                                    if (selFolFil.isDirectory()) {
                                        selFolFil = ZipUnzDel.zipDel(selFolFil);
                                    }
                                    EncDec.encFil(selFolFil, new String(pasFie.getPassword()));
                                    JLabel mes = new JLabel("Encryption done!");
                                    mes.setFont(new Font("Arial", Font.BOLD, 16));
                                    JOptionPane.showMessageDialog(this, mes);
                                } catch (Exception ex) {
                                    JLabel mes = new JLabel("Error! Try Again");
                                    mes.setFont(new Font("Arial", Font.BOLD, 16));
                                    JOptionPane.showMessageDialog(this, mes, "Message", JOptionPane.ERROR_MESSAGE);
                                }
                            } else if (decOpt.isSelected()) {
                                try {
                                    EncDec.decFil(selFolFil, new String(pasFie.getPassword()));
                                    if (selFolFil.isFile() && selFolFil.getName().endsWith(".zip")) {
                                        ZipUnzDel.unzDel(selFolFil);
                                    }
                                    JLabel mes = new JLabel("Decryption done!");
                                    mes.setFont(new Font("Arial", Font.BOLD, 16));
                                    JOptionPane.showMessageDialog(this, mes);
                                } catch (Exception ex) {
                                    JLabel mes = new JLabel("Error! Try Again");
                                    mes.setFont(new Font("Arial", Font.BOLD, 16));
                                    JOptionPane.showMessageDialog(this, mes, "Message", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        } else {
                            JLabel mes = new JLabel("Please Select an Option (Encrypt or Decrypt)");
                            mes.setFont(new Font("Arial", Font.BOLD, 16));
                            JOptionPane.showMessageDialog(this, mes);
                        }
                    } else {
                        JLabel mes = new JLabel(
                                "<html>Password must meet following requirements<br>&emsp;&emsp;1. At least one capital letter<br>&emsp;&emsp;2. At least one small letter<br>&emsp;&emsp;3. At least one number<br>&emsp;&emsp;4. At least one special character<br>&emsp;&emsp;5. At least 8 characters long</html>");
                        mes.setFont(new Font("Arial", Font.BOLD, 16));
                        JOptionPane.showMessageDialog(this, mes, "Password Requirements", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    JLabel mes = new JLabel("Please Enter Password");
                    mes.setFont(new Font("Arial", Font.BOLD, 16));
                    JOptionPane.showMessageDialog(this, mes);
                }
            } else {
                JLabel mes = new JLabel("Please Select a File");
                mes.setFont(new Font("Arial", Font.BOLD, 16));
                JOptionPane.showMessageDialog(this, mes);
            }
        } else if (e.getSource() == shoPas) {
            pasFie.setEchoChar(shoPas.isSelected() ? '\u0000' : '●');
        } else if (e.getSource() == can) {
            dispose();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        new GraUseInt();
    }
}