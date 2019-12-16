import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class GraphPanel extends JPanel  implements MouseWheelListener, MouseListener, MouseMotionListener{

    // dijkstra layout parameters
    public static final int VERTEX_RADIUS = 8;
    public static final int DEFAULT_THICKNESS = 1;
    private Image img;
    public Dijkstra dijkstra;
    public HashMap<String, List<Edge>> overlayEdges;



    //private final BufferedImage image;
    private double zoomFactor = 1;
    private double prevZoomFactor = 1;
    private boolean zoomer;
    private boolean dragger;
    private boolean released;
    private double xOffset = 0;
    private double yOffset = 0;
    private int xDiff;
    private int yDiff;
    private Point startPoint;



    public GraphPanel(Dijkstra dijkstra) throws IOException {
        this.dijkstra = dijkstra;
        img = ImageIO.read(new File("src//images//pkout.png"));
       // Image image = img.getScaledInstance(692,673,Image.SCALE_SMOOTH);
       // this.img = image;
        addMouseWheelListener(this);
        addMouseMotionListener(this);
        addMouseListener(this);
        setBackground(Color.white);
        overlayEdges = new HashMap<>();
        overlayEdges.put("weighted", new LinkedList<Edge>());
    }

    public void paintComponent(Graphics g) {
        //this.img = new ImageIcon("src//images//pkout.png").getImage();

        // make everything smooth like butter
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        super.paintComponent(g2); // paint the panel
        try {
          //  g.drawImage(img,0,0,null);
            //paintGraph(g2);// paint the dijkstra
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D g2 = (Graphics2D) g;

        if (zoomer) {
            AffineTransform at = new AffineTransform();

            double xRel = MouseInfo.getPointerInfo().getLocation().getX() - getLocationOnScreen().getX();
            double yRel = MouseInfo.getPointerInfo().getLocation().getY() - getLocationOnScreen().getY();

            double zoomDiv = zoomFactor / prevZoomFactor;

            xOffset = (zoomDiv) * (xOffset) + (1 - zoomDiv) * xRel;
            yOffset = (zoomDiv) * (yOffset) + (1 - zoomDiv) * yRel;

            at.translate(xOffset, yOffset);
            at.scale(zoomFactor, zoomFactor);
            prevZoomFactor = zoomFactor;
            g2.transform(at);
            zoomer = false;
        }
        if (dragger) {
            AffineTransform at = new AffineTransform();
            at.translate(xOffset + xDiff, yOffset + yDiff);
            at.scale(zoomFactor, zoomFactor);
            g2.transform(at);

            if (released) {
                xOffset += xDiff;
                yOffset += yDiff;
                dragger = false;
            }

        }
        // All drawings go here

        g2.drawImage(img, 0, 0, this);
        paintGraph(g2);
    }
    public void paintGraph(Graphics2D g) {
        for (Vertex v : dijkstra.getVertices()) {
            for (Edge edge : v.adjacentEdges) {
                paintEdge(g, edge.source, edge.target, edge.distance, Color.LIGHT_GRAY, DEFAULT_THICKNESS, 255);
            }
            paintVertex(g,v);
        }

        for (String overlayType : overlayEdges.keySet()) {
            if (overlayType.equals("weighted")) {
                for (Edge edge : overlayEdges.get(overlayType)) {
                    paintEdge(g, edge.source, edge.target, edge.distance, Color.RED, 8, 100);
                }
            }
        }
    }

    public void paintVertex(Graphics2D g, Vertex v) {
        //g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int x = Math.round((float) v.x );
        int y = Math.round((float) v.y );
        g.setColor(Color.BLUE);
        Stroke oldStroke = g.getStroke();
//        g.setStroke(new BasicStroke(5));
//        g.drawOval(x - VERTEX_RADIUS / 2, y - VERTEX_RADIUS / 2, VERTEX_RADIUS, VERTEX_RADIUS);
        g.setStroke(oldStroke);
        g.setColor(Color.cyan);
        g.fillOval(x - VERTEX_RADIUS / 2, y - VERTEX_RADIUS / 2, VERTEX_RADIUS, VERTEX_RADIUS);
        g.setFont(new Font("Helvetica", Font.PLAIN, 15));
        g.setColor(Color.BLACK);
        g.drawString(v.name, x - v.name.length() * 8 / 2, y + VERTEX_RADIUS / 2);
    }

    public void paintEdge(Graphics2D g, Vertex u, Vertex v, double weight, Color color, int thickness, int alpha) {
        //g =image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int x1 = Math.round((float) u.x );
        int y1 = Math.round((float) u.y );
        int x2 = Math.round((float) v.x );
        int y2 = Math.round((float) v.y );
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
        Stroke oldStroke = g.getStroke();
        g.setStroke(new BasicStroke(thickness));
        g.drawLine(x1, y1, x2, y2);
        g.setStroke(oldStroke);
        if (true) {
            Font oldFont = g.getFont();
            g.setFont(new Font("Helvetica", Font.PLAIN, 8));
            g.setColor(Color.GRAY);
            g.drawString(String.format("%.1f", weight), (x1 + x2) / 2, (y1 + y2) / 2);
            g.setFont(oldFont);
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {

        zoomer = true;

        //Zoom in
        if (e.getWheelRotation() < 0) {
            zoomFactor *= 1.1;
            repaint();
        }
        //Zoom out
        if (e.getWheelRotation() > 0) {
            zoomFactor /= 1.1;
            repaint();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        released = false;
        startPoint = MouseInfo.getPointerInfo().getLocation();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        released = true;
        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Point curPoint = e.getLocationOnScreen();
        xDiff = curPoint.x - startPoint.x;
        yDiff = curPoint.y - startPoint.y;

        dragger = true;
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}