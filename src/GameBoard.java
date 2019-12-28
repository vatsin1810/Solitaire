import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


@SuppressWarnings("serial")
public class GameBoard extends JPanel {
    Model model;
    JLayeredPane gb = new JLayeredPane();
    ArrayList<Card> perm = new ArrayList<>();
    JLayeredPane[] rows;
    
    int numCards;
    int pileRemoved;
    Card clicked;
    LinkedList<Card> removed;
    
    boolean wasteVisible = false;
    int cardOrigin;
    
    private class SolitaireMouse extends MouseAdapter {
        @Override
        public void mousePressed (MouseEvent e) {
            Point p = e.getPoint();
            if (p.x < 80) {
                if (p.y <= 105) {
                    if (model.wasteVisible) {
                        List<Card> cards = model.cardsShown();
                        for (int i = 0; i < cards.size(); i++) {
                            gb.remove(cards.get(i));
                            gb.repaint();
                        }
                    }
                    model.resetWaste();
                    List<Card> cards = model.cardsShown();
                    //System.out.println(cards.size());
                    for (int i = 0; i < cards.size(); i++) {
                        Card c = cards.get(i);
                        gb.add(c);
                        gb.setLayer(c, new Integer(i));
                        c.setBounds(0, 150 + 30 * i, 69, 105);
                        //gb.revalidate();
                    }
                    //System.out.println(wasteUse.getLast().getLocation());
                    reset();
                    gb.repaint();
                } else if (p.y >= (model.wasteUsablePointer - model.wastePointer) * 30 + 150 
                        && p.y <= (model.wasteUsablePointer - model.wastePointer) * 30 + 255) {
                    clicked = model.wasteUsable;
                    numCards = 1;
                    pileRemoved = -1;
                    removed = new LinkedList<>();
                    removed.add(clicked);
                    gb.setLayer(clicked, new Integer(100));
                    clicked.setLocation(p.x, p.y);
                    cardOrigin = 0;
                } else {
                    reset();
                }
            } else if (p.x >= 80 && p.x < 640) {
                int col = (p.x - 80)/80;
                int row = p.y/30;
                removed = new LinkedList<>();
                if (model.tableau[col].size() - row - 1 >= 0) {
                    clicked = model.tableau[col].get(model.tableau[col].size() - row - 1);
                } else {
                    reset();
                    return;
                }
                if (clicked.visible) {
                    int x = p.x;
                    int y = p.y;
                    int size = model.tableau[col].size();
                    int count = model.tableau[col].size() - row - 1;
                    numCards = model.tableau[col].size() - row;
                    pileRemoved = col;
                    cardOrigin = 1;
                    //Iterator<Card> it = model.tableau[col].iterator();
                    Card c = model.tableau[col].poll();
                    c.setLocation(x, y + 30 * count);
                    gb.setLayer(c, 100);
                    removed.add(c);
                    while (c != clicked) {
                        count--;
                        c = model.tableau[col].poll();
                        gb.setLayer(c, 100 + (model.tableau[col].size() - row - 1) - count);
                        c.setLocation(x, y + 30 * count);
                        removed.add(c);
                    }
                } else {
                    reset();
                }  
            } else {
                int row = p.y/110;
                if (row <= 3) {
                    clicked = model.foundations[row].poll();
                    numCards = 1;
                    pileRemoved = -1;
                    removed = new LinkedList<>();
                    removed.add(clicked);
                    gb.setLayer(clicked, new Integer(100));
                    clicked.setLocation(p.x, p.y);
                    cardOrigin = 2;
                } else {
                    reset();
                }
            }
        } 
        
        @Override
        public void mouseReleased(MouseEvent e) {
            if (clicked == null) {
                return;
            }
            Point p = e.getPoint();
            if (p.x < 80) {
                returnToOrigin();
            } else if (p.x >= 80 && p.x < 640) {
                int col = (p.x - 80)/80;
                int row = p.y/30;
                LinkedList<Card> l = model.tableau[col];
                if (col != pileRemoved 
                        && ((l.size() == 0 && clicked.getRank() == 13) 
                        || (l.size() > 0 && clicked.getColor() != l.peek().getColor() 
                        && clicked.getRank() + 1 == l.peek().getRank()))) {
                    int size = l.size();
                    int count = 0;
                    Iterator<Card> it = removed.descendingIterator();
                    while(it.hasNext()) {
                        Card c = it.next();
                        l.push(c);
                        c.setLocation(80 * col + 80, (size + count) * 30);
                        gb.setLayer(c, size + count);
                        count++;
                    }
                    fixOrigin();
                    repaint();
                } else {
                    returnToOrigin();
                }
            } else {
                int row = p.y/110;
                if (numCards == 1 && row <= 3) {
                    if (clicked.getSuit().getCode() == row 
                            && ((model.foundations[row].size() == 0 && clicked.getRank() == 1) 
                            || (model.foundations[row].size() > 0 
                            && clicked.getRank() == model.foundations[row].peek().getRank() + 1))) {
                        
                            clicked.setLocation(640, row * 110);
                            gb.setLayer(clicked, model.foundations[row].size());
                            model.foundations[row].push(clicked);
                            fixOrigin();
                    } else {
                        returnToOrigin();
                    }
                }
            }
            reset();
        }
        
        public void fixOrigin() {
            if (cardOrigin == 0) {
                List<Card> cards = model.cardsShown();
                for (int i = 0; i < cards.size() - 1; i++) {
                    gb.remove(cards.get(i));
                    gb.repaint();
                }
                model.removeCurrent();
                cards = model.cardsShown();
                for (int i = 0; i < cards.size(); i++) {
                    Card c = cards.get(i);
                    gb.add(c);
                    gb.setLayer(c, new Integer(i));
                    c.setBounds(0, 150 + 30 * i, 69, 105);
                }
                
            } else if (cardOrigin == 1) {
                Card c = model.tableau[pileRemoved].peek();
                if (c != null) c.visible = true;
                repaint();
            }
        }
        
        public void reset() {
            clicked = null;
            numCards = -1;
            removed = null;
            pileRemoved = -1;
            cardOrigin = -1;
        }
        
        public void returnToOrigin() {
            if (cardOrigin == 0) {
                clicked.visible = true;
                //model.wasteUsable = clicked;
                gb.setLayer(clicked, model.wasteUsablePointer - model.wastePointer);
                clicked.setLocation(0, 150 + 30 * (model.wasteUsablePointer - model.wastePointer));
                System.out.println(clicked.getLocation());
                gb.repaint();
            } else if (cardOrigin == 1) {
                LinkedList<Card> l = model.tableau[pileRemoved];
                int size = l.size();
                int count = 0;
                Iterator<Card> it = removed.descendingIterator();
                while(it.hasNext()) {
                    Card c = it.next();
                    l.push(c);
                    c.setLocation(80 * pileRemoved + 80, (size + count) * 30);
                    gb.setLayer(c, size + count);
                    count++;
                }
            } else if (cardOrigin == 2) {
                int r = clicked.getSuit().getCode();
                clicked.setLocation(640, r * 110);
                gb.setLayer(clicked, clicked.getRank());
                model.foundations[r].push(clicked);
            }
            reset();
        }
    }
    
    private class SolitaireMotionMouse extends MouseMotionAdapter {
        @Override
        public void mouseDragged (MouseEvent e) {
            if (clicked == null) {
                return;
            }
            Point p = e.getPoint();
            int count = numCards - 1;
            Iterator<Card> it = removed.iterator();
            while (it.hasNext()) {
                Card c = it.next();
                c.setLocation(p.x, p.y + 30 * count);
                count--;
            }
            repaint();
        }
    }
    
    
    GameBoard() {
        model = new Model();
        gb.setLayout(new BorderLayout());
        gb.setPreferredSize(new Dimension(800, 600));
        generatePerm();
        makeTableau();
        makeFoundations();
        makeWaste();
        //tableau.setPreferredSize(new Dimension(700, 700));
        SolitaireMouse ml = new SolitaireMouse();
        SolitaireMotionMouse mo = new SolitaireMotionMouse();
        this.add(gb);
        this.addMouseListener(ml);
        this.addMouseMotionListener(mo);
        repaint();
        
    }
    
    public void makeFoundations() {
        for (int i = 0; i < 4; i++) {
            if (model.foundations[i].size() == 0) {
                JLabel nullCard = new JLabel();
                nullCard.setBounds(640, 110 * i, 69, 105);
                gb.add(nullCard, BorderLayout.CENTER);
                gb.setLayer(nullCard, new Integer(0));
            } else {
                model.foundations[i].peek().setBounds(640, 110 * i, 69, 105);
                gb.add(model.foundations[i].peek(), BorderLayout.CENTER);
                gb.setLayer(model.foundations[i].peek(), new Integer(0));
            }
        }
        
    }
    
    public void makeWaste() {
        Card dummy = new Card(Card.suit.SPADES, 1, false);
        dummy.setBounds(0, 0, 69, 105);
        gb.add(dummy, BorderLayout.CENTER);
        gb.setLayer(dummy, new Integer(0));
    }
    
    public void makeTableau() {
        model.reset(perm);
        for (int i = 0; i < 7; i++) {
            Iterator<Card> it = model.tableau[i].descendingIterator();
            for (int j = 0; j <= i; j++) {
                Card c = it.next();
                if (j != i) {
                    c.visible = false;
                } else {
                    c.visible = true;
                }
                c.setBounds(80 * (i + 1), 30 * (j), 69, 105);
                gb.add(c, BorderLayout.CENTER);
                gb.setLayer(c, new Integer(j));
            }
        }
    }
    
    public void generatePerm() {
        Random r = new Random();
        ArrayList<Card> temp = new ArrayList<>();
        for (int i = 0; i < 52; i++) {
            if (i <= 12) {
                temp.add(new Card(Card.suit.SPADES, i + 1, true));
            } else if (i <= 25) {
                temp.add(new Card(Card.suit.CLUBS, i + 1 - 13, true));
            } else if (i <= 38) {
                temp.add(new Card(Card.suit.HEARTS, i + 1 - 26, true));
            } else {
                temp.add(new Card(Card.suit.DIAMONDS, i + 1 - 39, true));
            }
        }
        while (temp.size() > 0) {
            int n = temp.size();
            int swap = r.nextInt(n);
            Card temp2 = temp.get(swap);
            temp.set(swap, temp.get(n - 1));
            temp.remove(n - 1);
            perm.add(temp2);
        }
    }
}
