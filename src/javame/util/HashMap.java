package javame.util;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import javame.io.Serializable;
import javame.io.Serializer;

/**
 * Hash table implementation of the Map.
 */
public class HashMap extends MapImpl
{
    /**
     * Construct an empty HashMap.
     */
    public HashMap( )
    {
        super( new HashSet( ) );
    }
    
    /**
     * Construct a HashMap with same key/value pairs as another map.
     * @param other the other map.
     */
    public HashMap( Map other )
    {
        super( other );
    }
    
    protected Map.Entry makePair( Object key, Object value )
    {
        return new Pair( key, value );
    }
    
    protected Set makeEmptyKeySet( )
    {
        return new HashSet( );
    }
    
    protected Set clonePairSet( Set pairSet )
    {
        return new HashSet( pairSet );
    }

    public void serialize(DataOutput output) throws IOException {
        throw new RuntimeException("Not implemented yet");
    }

    public void deserialize(DataInput input) throws IOException {
        throw new RuntimeException("Not implemented yet");
    }
    
    private static final class Pair implements Map.Entry
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
        
        public int hashCode( )
        {
            return key.hashCode( );
        }
        
        public boolean equals( Object other )
        {
            if( other instanceof Map.Entry )
                return getKey( ).equals( ((Map.Entry) other).getKey( ) );
            else
                return false;    
        }

        public void serialize(DataOutput output) throws IOException {
            Serializer.serializeClass((Serializable) key);
            Serializer.serializeClass((Serializable) value);
        }

        public void deserialize(DataInput input) throws IOException {
            this.key = (Serializable) Serializer.deserializeClass(input);
            this.value = (Serializable) Serializer.deserializeClass(input);
        }
        
        private Object key;
        private Object value;
    }
}
