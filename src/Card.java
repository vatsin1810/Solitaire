import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class Card extends JLabel {
    public enum suit {
        HEARTS(0),
        DIAMONDS(1),
        SPADES(2),
        CLUBS(3);
        
        private final int value;

        suit(int value) {
            this.value = value;
        }

        public int getCode() {
            return value;
        }
    }
    private boolean color;
    private suit suit;
    private int rank;
    boolean visible;
    
    Card (suit suit, int rank, boolean visible) {
        this.suit = suit;
        this.rank = rank;
        this.visible = visible;
        if (suit == suit.SPADES || suit == suit.CLUBS) {
            color = true;
        } else {
            color = false;
        }
    }
    
    public suit getSuit() {
        return this.suit;
    }
    
    public int getRank() {
        return this.rank;
    }
    
    public boolean getColor() {
        return this.color;
    }
    
    public boolean getVisible() {
        return this.visible;
    }
    
    public Card copy() {
        return new Card(this.suit, this.rank, this.visible);
    }
    
    private BufferedImage readImage (String name) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(name));
        } catch (IOException e) {}
        return img;
    }
    
    @Override
    protected void paintComponent (Graphics g) {
        g.drawRect(0, 0, 69, 105);
        String r, s;
        if ((this.rank > 10 || this.rank == 1)) {
            if (this.rank == 1) {
                r = "A";
            } else if (this.rank == 11) {
                r = "J";
            } else if (this.rank == 12) {
                r = "Q";
            } else {
                r = "K";
            }
        } else {
            r = Integer.toString(this.rank);
        }
        if (this.suit == suit.CLUBS) {
            s = "C";
        } else if (this.suit == suit.HEARTS) {
            s = "H";
        } else if (this.suit == suit.SPADES) {
            s = "S";
        } else {
            s = "D";
        }
        if (this.visible) {
            g.drawImage(readImage("files/" + r + s + ".jpg"), 0, 0, null);
        } else {
            g.drawImage(readImage("files/blue_back.jpg"), 0, 0, null);
        }   
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(69, 105);
    }

}
