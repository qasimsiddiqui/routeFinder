import javax.swing.*;
import java.awt.*;

public class SplashScreen extends JFrame {
    public JProgressBar jProgressBar;
    private JPanel jPanel;
    private ImageIcon image;

    public SplashScreen(){
        this.image = new ImageIcon("src\\imageBackground.png");
        setUndecorated(true);
        setSize(new Dimension(400,400));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(50, 50, 400, 400);
        setMinimumSize(new Dimension(400, 400));
        setResizable(false);
        jPanel = new JPanel();
        setContentPane(jPanel);
        JLabel label = new JLabel();
        label.setIcon(image);
        jPanel.add(label);
        jPanel.setBackground(Color.white);
        jProgressBar = new JProgressBar();
        jPanel.add(jProgressBar);
        jProgressBar.setForeground(Color.CYAN);
    }
}
