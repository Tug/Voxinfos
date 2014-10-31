package javame.util;

import javame.util.Comparable;
import javame.util.Comparator;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import javame.io.Serializable;
import javame.io.Serializer;

/**
 * Balanced search tree implementation of the Map.
 */
public class TreeMap extends MapImpl
{
    /**
     * Construct an empty TreeMap with default comparator.
     */
    public TreeMap( )
    {
        super( new TreeSet( ) );
    }
    
    /**
     * Construct a TreeMap using comparator.
     * @param cmp the comparator.
     */
    public TreeMap( Comparator comparator )
    {
        super( new TreeSet( ) );
        cmp = comparator;
    }
        
    /**
     * Construct a TreeMap with same key/value pairs
     * and comparator as another map..
     * @param other the other map.
     */
    public TreeMap( Map other )
    {
        super( other );
    }
    
    /**
     * Gets the comparator; returns null if default.
     * @return the comparator or if null if default is used.
     */
    public Comparator comparator( )
    {
        if( cmp == Collections.DEFAULT_COMPARATOR )
            return null;
        else
            return cmp;    
    }
    
    protected Map.Entry makePair( Object key, Object value )
    {
        return new Pair( key, value );
    }
    
    protected Set makeEmptyKeySet( )
    {
        return new TreeSet( ((TreeSet)getSet( ) ).comparator( ) );
    }
    
    protected Set clonePairSet( Set pairSet )
    {
        return new TreeSet( pairSet );
    }
    
    private final class Pair implements Map.Entry, Comparable
    {
        public Pair( Object k, Object v )
        {
            key = k;
            value = v;
        }
        
        public Object getKey( )
        {
            return key;
        }
        
        public Object getValue( )
        {
            return value;
        }
        
        public Object setValue( Object newValue )
        {
            Object old = value;
            value = newValue;
            return old;
        }
               
        public int compareTo( Object other )
        {
            return cmp.compare( getKey( ), ((Map.Entry) other).getKey( ) );
        }

        public void serialize(DataOutput output) throws IOException {
            Serializer.serializeClass((Serializable) key, output);
            Serializer.serializeClass((Serializable) value, output);
        }

        public void deserialize(DataInput input) throws IOException {
            this.key = (Serializable) Serializer.deserializeClass(input);
            this.value = (Serializable) Serializer.deserializeClass(input);
        }
        
        private Object key;
        private Object value;
    }
    
    private Comparator cmp = Collections.DEFAULT_COMPARATOR;
}
