/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import vox.Config;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import javame.util.Collection;
import javame.util.Comparator;
import javame.util.Iterator;
import javame.util.Observable;
import javame.util.TreeSet;
import javame.io.Serializable;

public abstract class PersistentList extends Observable implements Serializable {

    protected TreeSet data;

    public PersistentList(Comparator comparator) {
        super();
        this.data = new TreeSet(comparator);
    }

    public void add(Collection newData)
    {
        for(Iterator newDataIter = newData.iterator(); newDataIter.hasNext(); ) {
            add(newDataIter.next());
        }
    }

    public synchronized void add(Object element)
    {
        if(data.add(element)) {
            notifyObservers(element);
        }
    }

    public synchronized void remove(Object element)
    {
        data.remove(element);
    }

    public Object first()
    {
        if(data.size() == 0) return null;
        return data.first();
    }

    public Object last()
    {
        if(data.size() == 0) return null;
        return data.last();
    }

    public Object get(Object object)
    {
        return data.getMatch(object);
    }

    public int size()
    {
        return data.size();
    }

    public Iterator iterator() {
        return data.iterator();
    }

    public void serialize(DataOutput output) throws IOException {
        data.serialize(output);
    }

    public void deserialize(DataInput input) throws IOException {
        data.deserialize(input);
    }
    
}
