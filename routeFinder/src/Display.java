import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.Buffer;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import static javax.swing.UIManager.*;

@SuppressWarnings("serial")
public class Display extends JFrame {

    private JPanel contentPane;
    private String edgeFile = "src//cityFiles//Majorcitypairs.txt";
    private String vertexFile = "src//cityFiles//Majorcityxy.txt";
    private GraphPanel panel;
    private JComboBox<String> comboBoxStartCity;
    private JComboBox<String> comboBoxEndCity;
    private Dijkstra dijkstra;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {

            for (LookAndFeelInfo info : getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Display.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Display.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Display.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Display.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
//                    SplashScreen screen = new SplashScreen();
//                    screen.setVisible(true);
//                    screen.jProgressBar.setValue(0);
//                    for(int i=0; screen.jProgressBar.getValue() <= 100000; i++ ) {
//                        screen.jProgressBar.setValue(i);
//                    }
                    Display frame = new Display();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public Display() throws IOException {
        setTitle("Route Finder");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(925,720);
        setMinimumSize(new Dimension(925, 720));
        setResizable(false);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setBackground(Color.white);
        setContentPane(contentPane);
        GridBagLayout gbl_contentPane = new GridBagLayout();
//        gbl_contentPane.columnWidths = new int[] { 0, 0, 0, 0, 0, 0};
//        gbl_contentPane.rowHeights = new int[] { 0, 0, 0, 0, 0, 0};
//        gbl_contentPane.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 1.0};
//        gbl_contentPane.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 1.0};
        contentPane.setLayout(gbl_contentPane);

        dijkstra = readGraph(vertexFile, edgeFile);

        panel = new GraphPanel(dijkstra);
        Insets insets = new Insets(5, 10, 0, 5);
        addComponent(panel, 0, 6, 0,11, insets, 1,1, GridBagConstraints.BOTH, GridBagConstraints.CENTER);

        JSeparator separator = new JSeparator();
        separator.setBackground(Color.WHITE);
        separator.setForeground(Color.BLACK);
        separator.setOrientation(1);
        addComponent(separator, 0, 5, 0, 11, new Insets(0,5,0,5), 0, 0, GridBagConstraints.BOTH, GridBagConstraints.CENTER);

        JLabel jLabelDijkstraHeading = new JLabel("Dijkstra's Algorithm");
        jLabelDijkstraHeading.setFont(new Font("Cookie", Font.ITALIC + Font.BOLD, 30));
        addComponent(jLabelDijkstraHeading, 0, 0, 2, 1, new Insets(25,5,50,5), 0, 0, GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER);

        JLabel jLabelStart = new JLabel("Start:       ");
        jLabelStart.setFont(new Font("Tahoma", Font.BOLD, 12));
        addComponent(jLabelStart, 1, 0, 1, 1, new Insets(10,5,5,5), 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST);

        comboBoxStartCity = new JComboBox<>();
        comboBoxStartCity.setFont(new Font("Tahoma", Font.BOLD, 12));
        addComponent(comboBoxStartCity, 1, 1, 1, 1, new Insets(10, 5, 5, 5), 0 , 0, GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER);

        JLabel jLabelEnd = new JLabel("End:");
        jLabelEnd.setFont(new Font("Tahoma", Font.BOLD, 12));
        addComponent(jLabelEnd, 2, 0, 1, 1, new Insets(10, 5, 5, 5), 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST);

        comboBoxEndCity = new JComboBox<>();
        comboBoxEndCity.setFont(new Font("Tahoma", Font.BOLD, 12));
        addComponent(comboBoxEndCity, 2, 1, 1, 1, new Insets(10,5,5,5), 0, 0, GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER);

        JSeparator separator2 = new JSeparator();
        separator2.setBackground(Color.WHITE);
        separator2.setForeground(Color.BLACK);
        addComponent(separator2, 3, 0, 2, 1, new Insets(10,5,5,5), 0, 0, GridBagConstraints.BOTH, GridBagConstraints.CENTER);

        JLabel jLabelSelectCity = new JLabel("Select Cities to Display:");
        jLabelSelectCity.setFont(new Font("Tahoma", Font.PLAIN, 12));
        addComponent(jLabelSelectCity, 4, 0, 2, 1, new Insets(10, 5, 5, 5), 0, 0, GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER);

        JRadioButton radioMajorCities = new JRadioButton();
        JRadioButton radioSmallCities = new JRadioButton();
        JRadioButton radioMediumCities = new JRadioButton();

        ButtonGroup radioGroup = new ButtonGroup();
        radioGroup.add(radioMajorCities);
        radioGroup.add(radioMediumCities);
        radioGroup.add(radioSmallCities);

        radioMajorCities.setText("Major Cities");
        radioMajorCities.setSelected(true);
        addComponent(radioMajorCities,5,0,2,1,new Insets(10,5,5,5),0,0,GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER);
        radioMajorCities.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                vertexFile = "src//cityFiles//Majorcityxy.txt";
                edgeFile = "src//cityFiles//Majorcitypairs.txt";
                setSize(925, 720);
                updateGraphPanel();
                repaint();
            }
        });
        radioSmallCities.setText("Small Cities");
        addComponent(radioSmallCities,6,0,2,1,new Insets(10,5,5,5),0,0,GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER);
        radioSmallCities.addItemListener(new ItemListener() {
                 @Override
                 public void itemStateChanged(ItemEvent e) {
                     repaint();
                 }
             }
        );

        radioMediumCities.setText("Double Circle Cities");
        addComponent(radioMediumCities,7,0,2,1,new Insets(10,5,5,5),0,0,GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER);
        radioMediumCities.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                edgeFile = "src//cityFiles//DoubleCirclecitypairs.txt";
                vertexFile = "src//cityFiles//DoubleCirclecityxy.txt";
                setSize(960,720);
                updateGraphPanel();
                repaint();
            }
        });

        JSeparator separator3 = new JSeparator();
        separator3.setBackground(Color.WHITE);
        separator3.setForeground(Color.BLACK);
        addComponent(separator3, 8, 0, 2, 1, new Insets(10,5,5,5), 0, 0, GridBagConstraints.BOTH, GridBagConstraints.CENTER);

        JButton btnDrawWeightedShortest = new JButton("Draw Dijkstra's Path");
        btnDrawWeightedShortest.setFont(new Font("Tahoma", Font.BOLD, 12));
        addComponent(btnDrawWeightedShortest, 9, 0, 2, 1, new Insets(10,5,5,5), 0, 0, GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH);

        btnDrawWeightedShortest.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                String startCity = comboBoxStartCity.getItemAt(comboBoxStartCity.getSelectedIndex());
                String endCity = comboBoxEndCity.getItemAt(comboBoxEndCity.getSelectedIndex());
                System.out.println("Calculating shortest weighted path for " + startCity + " to " + endCity);
                List<Edge> weightedPath = dijkstra.getDijkstraPath(startCity, endCity);
                panel.overlayEdges.put("weighted", weightedPath);
                repaint();
            }
        });

        JButton btnReloadGraph = new JButton("Load / Reset");
        btnReloadGraph.setFont(new Font("Tahoma", Font.BOLD, 12));
        addComponent(btnReloadGraph, 10, 0, 2, 1, new Insets(10,5,5,5), 0, 0, GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH);

        btnReloadGraph.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                // update JPanel
                updateGraphPanel();
            }
        });

        updateGraphPanel();
    }

    public static Dijkstra readGraph(String vertexFile, String edgeFile) {

        Dijkstra dijkstra = new Dijkstra();
        try {
            String line;
            // Read in the vertices
            BufferedReader vertexFileBr = new BufferedReader(new FileReader(vertexFile));
            while ((line = vertexFileBr.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 3) {
                    vertexFileBr.close();
                    throw new IOException("Invalid line in vertex file " + line);
                }
                String cityname = parts[0];
                int x = Integer.valueOf(parts[1]);
                int y = Integer.valueOf(parts[2]);
                Vertex vertex = new Vertex(cityname, x, y);
                dijkstra.addVertex(vertex);
            }
            vertexFileBr.close();
            // Now read in the edges
            BufferedReader edgeFileBr = new BufferedReader(new FileReader(edgeFile));
            while ((line = edgeFileBr.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 3) {
                    edgeFileBr.close();
                    throw new IOException("Invalid line in edge file " + line);
                }
                dijkstra.addUndirectedEdge(parts[0], parts[1], Double.parseDouble(parts[2]));
            }
            edgeFileBr.close();
        } catch (IOException e) {
            System.err.println("Could not read the dijkstra: " + e);
            return null;
        }
        return dijkstra;
    }

    private void updateGraphPanel() {
        dijkstra = readGraph(vertexFile, edgeFile);
        panel.dijkstra = dijkstra;
        System.out.println("Constructing new file from " + vertexFile + " and " + edgeFile);
        System.out.println("Data read: " + panel.dijkstra.getVertices());

        List<String> cityNameList = new ArrayList<>();
        for (Vertex v : dijkstra.getVertices())
            cityNameList.add(v.name);
        Collections.sort(cityNameList);
        String[] cityNames = cityNameList.toArray(new String[cityNameList.size()]);
        comboBoxStartCity.setModel(new DefaultComboBoxModel<>(cityNames));
        comboBoxEndCity.setModel(new DefaultComboBoxModel<>(cityNames));

        panel.overlayEdges.put("weighted", new LinkedList<Edge>());
        panel.overlayEdges.put("unweighted", new LinkedList<Edge>());
        panel.overlayEdges.put("mst", new LinkedList<Edge>());

        repaint();
    }
    private void addComponent(Component component, int row, int column, int width, int height,
                              Insets insets, double weightx, double weighty, int fill, int anchor){
        GridBagConstraints constraints = new GridBagConstraints();

        constraints.gridy = row;     //row to be placed in
        constraints.gridx = column;     //column to be placed in
        constraints.gridwidth = width;
        constraints.gridheight = height;
        constraints.insets = insets;
        constraints.weightx = weightx;
        constraints.weighty = weighty;
        constraints.fill = fill;
        constraints.anchor = anchor;

        contentPane.add(component, constraints);
    }
}
