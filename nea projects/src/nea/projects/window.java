/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nea.projects;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.Timer;

/**
 *
 * @author 15choasi
 */
public class window extends JComponent implements MouseWheelListener, MouseListener, MouseMotionListener, ActionListener {

    private final int Max_iter = 100;
    private final float Scale = 250;
   // private final double zoom = 250;
    private double zoomfactor = 1230;
    private double prevzoomfactor = 1;
    private BufferedImage Image;
   // private double zx, zy, cX, cY, tmp;
   // private final int colour = 20;
    int mf = 35;
    public static int WIDTH = 800;
    public static int HEIGHT = 600;
    private double xOffset;
    private double yOffset;
    private int xDiff;
    private int yDiff;
    private Point startPoint;
    private float hueoffset = 0;
    private Timer timer;
    
    private boolean zoomer , dragger, released;

    public window() {
       /* super("Mandelbrot Set");
        setBounds(485, 100, 800, 600);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);*/
       
        Image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        JLabel label = new JLabel(new ImageIcon(Image));
       
        timer = new Timer(1, this);
        
        
        JButton J = new JButton("show Pallete editor");
        J.setActionCommand("editor");
        ActionListener l = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                String action = ae.getActionCommand();
                if (action.equals("editor")) {
                    new editor().setVisible(true);
                }
            }
        };
        J.addActionListener(l);
        
       
        JFrame frame = new JFrame("mandelbrot set");
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.getContentPane().add(this);
        frame.add(J);
        frame.add(label);
        frame.pack();
        frame.setResizable(true);
        frame.setVisible(true);
        
        
        

        

       // Image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
       /* for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                zx = zy = 0;
                cX = (x - 500) / zoom;
                cY = (y - 300) / zoom;
                int iter = Max_iter;
                while (zx * zx + zy * zy < 4 && iter > 0) {
                    tmp = zx * zx - zy * zy + cX;
                    zy = 2.0 * zx * zy + cY;
                    zx = tmp;
                    iter--;
                    
                }
                Image.setRGB(x, y, iter | (iter << mf));
            } 
        }*/
        
      
       
        
    }
    
    @Override
    public void addNotify(){
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        timer.start();
    }
    
    public void renderMandelbrotset(){
        
        for (int x = 0; x < WIDTH; x++) 
            for (int y = 0; y < HEIGHT; y++){
                int colour = calculatePoint((x - WIDTH/2f)/Scale , (y - HEIGHT/2f)/Scale);
                
                Image.setRGB(x, y, colour);
            }
    }
    
    public int calculatePoint(float x , float y){
    
        float cx = x;
        float cy = y;
        
        int i = 0;
        
        for(;i<Max_iter;i++){
            
            float nx = x*x - y*y +cx;
            float ny = 2 * x* y +cy;
            
            x = nx;
            y = ny;
            
            if(x*x + y*y > 4) break;
        }
        
        if(i == Max_iter) return 0x000000000;
        return Color.HSBtoRGB(((float)i / Max_iter + hueoffset)%1f, 0.7f, 1);
        
    }
    

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
    if (zoomer) {
        AffineTransform at = new AffineTransform();
        
        double xRel = MouseInfo.getPointerInfo().getLocation().getX() - getLocationOnScreen().getX();
        double yRel = MouseInfo.getPointerInfo().getLocation().getY() - getLocationOnScreen().getY();

        double zoomDiv = zoomfactor / prevzoomfactor;

        xOffset = (zoomDiv) * (xOffset) + (1 - zoomDiv) * xRel;
        yOffset = (zoomDiv) * (yOffset) + (1 - zoomDiv) * yRel;
        
        
        at.scale(zoomfactor, zoomfactor);
        prevzoomfactor = zoomfactor;
        g2.transform(at);
        zoomer = false;
    }
    if(dragger){
        AffineTransform at = new AffineTransform();
            at.translate(xOffset + xDiff, yOffset + yDiff);
            at.scale(zoomfactor, zoomfactor);
            g2.transform(at);

            if (released) {
                xOffset += xDiff;
                yOffset += yDiff;
                dragger = false;
            }

        } 
        
        g.drawImage(Image, 0, 0, null);
        
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        zoomer = true;
        
        //Zoom in
        if (e.getWheelRotation() < 0) {
            zoomfactor *= 1.1;
            repaint();
        }
        if (e.getWheelRotation() > 0) {
            zoomfactor /= 1.1;
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

    @Override
    public void actionPerformed(ActionEvent e) {
        
        hueoffset += 0.1f;
        renderMandelbrotset();
        repaint();
        
    }

}
