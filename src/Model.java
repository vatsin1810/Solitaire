import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class Model {
    LinkedList<Card>[] tableau;
    LinkedList<Card>[] foundations;
    ArrayList<Card> waste;
    Card wasteUsable;
    int wastePointer;
    int wasteUsablePointer;
    boolean wasteVisible;
    
    Model() {
        
    }
    
//    public boolean moveCardsTableau(int pileFrom, int pileTo, int cardFrom) {
//        Card from = tableau[pileFrom].get(cardFrom);
//        Card to = tableau[pileTo].peek();
//        if (from.getColor() != to.getColor() && to.getRank() == from.getRank() + 1) {
//            int count = 0;
//            LinkedList<Card> tempPile = new LinkedList<>();
//            while (count++ <= cardFrom) {
//                Card temp = tableau[pileFrom].poll();
//                tempPile.add(temp);
//            }
//            count = 0;
//            while (count++ <= cardFrom) {
//                Card temp = tempPile.pollLast();
//                tableau[pileTo].push(temp);
//            }
//            return true;
//        }
//        return false;
//    }
    
//    public boolean moveCardTableauFoundation(int pileFrom, int pileTo) {
//        Card from = tableau[pileFrom].peek();
//        Card to = foundations[pileTo];
//        if (from.getSuit() == to.getSuit() && to.getRank() == from.getRank() + 1) {
//            tableau[pileFrom].poll();
//            foundations[pileTo] = from;
//            return true;
//        }
//        return false;
//    }
//    
//    public boolean moveCardFoundationTableau(int pileFrom, int pileTo) {
//        Card from = foundations[pileFrom];
//        Card to = tableau[pileTo].peek();
//        if (from.getColor() != to.getColor() && to.getRank() == from.getRank() + 1) {
//            if (to.getRank() == 1) {
//                foundations[pileFrom] = null;
//            } else {
//                Card c = new Card(from.getSuit(), from.getRank() - 1, true);
//                foundations[pileFrom] = c;
//            }
//            tableau[pileTo].push(from);
//            return true;
//        }
//        return false;
//    }
//    
//    public boolean moveCardWasteTableau(int pileTo) {
//        Card from = wasteUsable.getLast();
//        Card to = tableau[pileTo].peek();
//        if (from.getColor() != to.getColor() && to.getRank() == from.getRank() + 1) {
//            tableau[pileTo].push(from);
//            Card del = wasteUsable.removeLast();
//            waste.remove(del);
//            return true;
//        }
//        return false;
//    }
    
    public boolean resetWaste() {
        if (!wasteVisible) {
            if (waste.size() > 0) {
                wasteVisible = true;
                wastePointer = 0;
                wasteUsablePointer = Math.min(2, waste.size() - 1);
                wasteUsable = waste.get(wasteUsablePointer);
            } else {
                wastePointer = -1;
                wasteUsablePointer = -1;
            }
        } else if (wasteVisible && wasteUsablePointer < waste.size() - 1) {
//            wasteUsablePointer = wastePointer + 3;
//            if (wasteUsablePointer >= waste.size()) {
//                
//            }
            if (wasteUsablePointer + 3 < waste.size()) {
                wastePointer = wasteUsablePointer + 1;
                wasteUsablePointer += 3;
                wasteUsable = waste.get(wasteUsablePointer);
            } else {
                System.out.println("Here");
                wastePointer += waste.size() - 1 - wasteUsablePointer;
                wasteUsablePointer = waste.size() - 1;
                wasteUsable = waste.get(wasteUsablePointer);
            }
        } else {
            wasteUsable = null;
            wasteVisible = false;
        }
        //System.out.println(wastePointer + " " + wasteUsablePointer);
        return true;
    }
    
    public List<Card> cardsShown() {
        LinkedList<Card> l = new LinkedList<>();
        if (wasteVisible) {
            for (int i = wastePointer; i <= wasteUsablePointer; i++) {
                l.add(waste.get(i));
            }
        }
        return l;
    }
    
    public void removeCurrent() {
        if (wastePointer == 0) {
            waste.remove(wasteUsablePointer);
            wasteUsablePointer--;
            if (wasteUsablePointer == -1) {
                wastePointer = -1;
                wasteUsable = null;
                wasteVisible = false;
            } else {
                wasteUsable = waste.get(wasteUsablePointer);
            }
        } else {
            wastePointer--;
            waste.remove(wasteUsablePointer);
            wasteUsablePointer--;
            wasteUsable = waste.get(wasteUsablePointer);
        }
        
    }
    
    public void reset(List<Card> perm) {
        tableau = new LinkedList[7];
        for (int i = 0; i < 7; i++) {
            tableau[i] = new LinkedList<>();
        }
        foundations = new LinkedList[4];
        for (int i = 0; i < 4; i++) {
            foundations[i] = new LinkedList<>();
        }
        waste = new ArrayList<>();
        wasteUsable = null;
        wastePointer = 0;
        wasteVisible = false;
        Iterator<Card> it = perm.iterator();
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j <= i; j++) {
                tableau[i].add(it.next());
            }
        }
        while (it.hasNext()) {
            waste.add(it.next());
        }
    }
}
