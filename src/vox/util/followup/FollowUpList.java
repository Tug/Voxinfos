/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vox.util.followup;

import javame.util.Collection;
import java.util.Vector;
import util.PersistentList;
import javame.util.Comparator;
import javame.util.Iterator;

/**
 *
 * @author Tug
 */
public class FollowUpList extends PersistentList {

    public static final Comparator fuComparator = new Comparator() {
        public int compare(Object o1, Object o2) {
            FollowUp fu1 = (FollowUp) o1;
            FollowUp fu2 = (FollowUp) o2;
            long t1 = fu1.getStart().getTime();
            long t2 = fu2.getStart().getTime();
            long epsi = 5 * 60 * 1000L; // 5 min
            if(Math.abs(t1-t2) < epsi) return 0;
            else if(t1 < t2) return -1;
            else return 1;
        }
    };
    
    public FollowUpList() {
        super(fuComparator);
    }

    public void add(FollowUp fu1)
    {
        FollowUp fu2;
        if((fu2 = (FollowUp) get(fu1)) != null) {
            int duration = fu1.getDuration();
            if(duration == 0) remove(fu2);
            else if(duration != fu2.getDuration()) {
                fu2.setDuration(duration);
                notifyObservers(fu2);
            }
        } else {
            super.add(fu1);
        }
    }

    public void cleanOld()
    {
        while(size() > 0) {
            FollowUp fu = (FollowUp) first();
            if(fu.startsIn() < 0) {
                remove(fu);
            } else {
                break;
            }
        }
    }

}
