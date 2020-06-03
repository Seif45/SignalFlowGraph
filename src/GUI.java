import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import javax.swing.*;

public class GUI extends JPanel{

    private static int n;
    private static JTextField[] t ;
    private static MasonFormula mf;

    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g1=(Graphics2D)g;
        g1.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g1.setPaint(Color.BLACK);
        for (int i = 1 ; i <= n ; i++){
            Ellipse2D node = new Ellipse2D.Double(i*getWidth()/(n+1) - 4 ,getHeight()/2 -4,8 ,8);
            g1.fill(node);
            g1.drawString(Integer.toString(i),i*getWidth()/(n+1) - 4,getHeight()/2 + 15);
        }
        int k = 0 ;
        for (int i = 0 ; i < n ; i++){
            for (int j = 0 ; j < n ; j++){
                if (!t[k].getText().equals("0") && !t[k].getText().equals("")) {
                    if (j == i){
                        g1.drawOval((i+1)*getWidth()/(n+1) - 30,getHeight()/2 - 60,60,60);
                        g1.drawString(t[k].getText(),(i+1)*getWidth()/(n+1),getHeight()/2 - 67);
                        g1.drawString(">",(i+1)*getWidth()/(n+1),getHeight()/2 - 60 + 5);

                    }
                    else if ( j == i+1){
                        g1.drawLine((i+1)*getWidth()/(n+1)-4,getHeight()/2,(j+1)*getWidth()/(n+1)-4,getHeight()/2);
                        g1.drawString(t[k].getText(),((i+1)*getWidth()/(n+1) + (j+1)*getWidth()/(n+1))/2, getHeight()/2 - 5);
                        g1.drawString(">",((i+1)*getWidth()/(n+1) + (j+1)*getWidth()/(n+1))/2, getHeight()/2 + 5);
                    }
                    else {
                        if (i<j){
                            g1.drawArc((i+1)*getWidth()/(n+1),getHeight()/2 - 42,(j-i) * getWidth()/(n+1),80,-180,180);
                            g1.drawString(t[k].getText(),((i+1)*getWidth()/(n+1)) + ((j-i) * getWidth()/(n+1) /2),getHeight()/2 + 50);
                            g1.drawString(">",((i+1)*getWidth()/(n+1)) + ((j-i) * getWidth()/(n+1) /2),getHeight()/2 + 44);
                        }
                        else {
                            g1.drawArc((j+1)*getWidth()/(n+1),getHeight()/2 - 42,(i-j) * getWidth()/(n+1),80,0,180);
                            g1.drawString(t[k].getText(),((j+1)*getWidth()/(n+1)) + ((i-j) * getWidth()/(n+1) /2),getHeight()/2 - 45);
                            g1.drawString("<",((j+1)*getWidth()/(n+1)) + ((i-j) * getWidth()/(n+1) /2),getHeight()/2 - 37);
                        }
                    }
                }
                k++;
            }
        }
    }

    public static void main(String args[]){
        JFrame frame2 = new JFrame("Nodes");
        frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        JTextField txt = new JTextField(16);
        txt.setText("");
        JButton btn = new JButton("Submit");
        JLabel lbl = new JLabel("Enter the number of nodes");
        panel.add(lbl);
        panel.add(txt);
        panel.add(btn);
        frame2.add(panel);
        frame2.setBounds(Toolkit.getDefaultToolkit().getScreenSize().width/2 - 150,Toolkit.getDefaultToolkit().getScreenSize().height/2 - 60,300,120);
        frame2.setVisible(true);
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s = e.getActionCommand();
                if (s.equals("Submit")){
                    try {
                        n = Integer.parseInt(txt.getText());
                        frame2.setVisible(false);
                        JFrame f = new JFrame("Gains");
                        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        JPanel p = new JPanel();
                        JLabel le = new JLabel("                            Enter the gain of each path                            ");
                        p.add(le);
                        t = new JTextField[n*n];
                        int k = 0;
                        for (int i = 1 ; i <= n ; i++){
                            for (int j = 1 ; j <= n ; j++){
                                JLabel l = new JLabel("Gain from node " + i + " to node " + j);
                                t[k] = new JTextField(16);
                                t[k].setText("0");
                                p.add(l);
                                p.add(t[k]);
                                k++;
                            }
                        }
                        JButton b = new JButton("Draw");
                        p.add(b);
                        f.add(p);
                        f.setBounds(Toolkit.getDefaultToolkit().getScreenSize().width/2 - 210,0,420,Toolkit.getDefaultToolkit().getScreenSize().height);
                        f.setVisible(true);
                        b.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                String s = e.getActionCommand();
                                if (s.equals("Draw")){
                                    int k = 0;
                                    Node[] nodes = new Node[n];
                                    for (int i = 0; i < nodes.length; i++) {
                                        nodes[i] = new Node();
                                    }
                                    for (int i = 0 ; i < n ; i++){
                                        for (int j = 0 ; j < n ; j++){
                                            if (!t[k].getText().equals("0") && !t[k].getText().equals("")){
                                                nodes[i].getChildren().add(nodes[j]);
                                                try {
                                                    nodes[j].getParents().put(nodes[i],Double.parseDouble(t[k].getText()));
                                                }
                                                catch (NumberFormatException e1){
                                                    nodes[j].getParents().put(nodes[i],t[k].getText());
                                                }
                                            }
                                            k++;
                                        }
                                    }
                                    mf = new MasonFormula(new SignalFlowGraph(nodes[0],nodes[nodes.length-1]));
                                    f.setVisible(false);
                                    drawMenu();
                                }
                            }
                        });
                    }
                    catch (NumberFormatException er){
                    }
                }
            }
        });
    }

    private static void drawMenu(){
        JFrame frame =new JFrame("Signal Flow Graph");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JButton btn2 = new JButton("Find TF");
        btn2.setBounds(40,40,100,80);
        frame.add(btn2);
        frame.add(new GUI());
        btn2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s = e.getActionCommand();
                if (s.equals("Find TF")){
                    JOptionPane.showMessageDialog(frame,mf.printAll());
                }
            }
        });
        frame.setBounds(0,0,Toolkit.getDefaultToolkit().getScreenSize().width/2,Toolkit.getDefaultToolkit().getScreenSize().height/2);
        frame.setVisible(true);
    }
}