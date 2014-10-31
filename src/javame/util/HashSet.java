package javame.util;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import javame.io.Serializable;
import javame.io.Serializer;


/**
 * HashSet implementation.
 * Matches are based on equals; and hashCode must be consistently defined.
 */
public class HashSet extends AbstractCollection implements Set
{
    /**
     * Construct an empty HashSet.
     */
    public HashSet( )
    {
        allocateArray( DEFAULT_TABLE_SIZE );
        clear( );
    }
    
    /**
     * Construct a HashSet from any collection.
     */
    public HashSet( Collection other )
    {
        allocateArray( nextPrime( other.size( ) * 2 ) );
        clear( );
        
        Iterator itr = other.iterator( );
        while( itr.hasNext( ) )
            add( itr.next( ) );    
    }
    
    /**
     * Returns the number of items in this collection.
     * @return the number of items in this collection.
     */
    public int size( )
    {
        return currentSize;
    }
    
    
    /**
     * This method is not part of standard Java 1.2.
     * Like contains, it checks if x is in the set.
     * If it is, it returns the reference to the matching
     * object; otherwise it returns null.
     * @param x the object to search for.
     * @return if contains(x) is false, the return value is null;
     * otherwise, the return value is the object that causes
     * contains(x) to return true.
     */
    public Object getMatch( Object x )
    {
        int currentPos = findPos( x );

        if( isActive( array, currentPos ) )
            return array[ currentPos ].element;
        return null;
    }
    
    /**
     * Tests if some item is in this collection.
     * @param x any object.
     * @return true if this collection contains an item equal to x.
     */
    public boolean contains( Object x )
    {
        return isActive( array, findPos( x ) );
    }
    
    /**
     * Tests if item in pos is active.
     * @param pos a position in the hash table.
     * @param arr the HashEntry array (can be oldArray during rehash).
     * @return true if this position is active.
     */
    private static boolean isActive( HashEntry [ ] arr, int pos )
    {
        return arr[ pos ] != null && arr[ pos ].isActive;
    }
    
    /**
     * Adds an item to this collection.
     * @param x any object.
     * @return true if this item was added to the collection.
     */
    public boolean add( Object x )
    {
        int currentPos = findPos( x );
        if( isActive( array, currentPos ) )
            return false;
        
        array[ currentPos ] = new HashEntry( x, true );
        currentSize++;
        occupied++;
        modCount++;
        
        if( occupied > array.length / 2 )        
            rehash( );
                
        return true;                   
    }
    
    /**
     * Private routine to perform rehashing.
     * Can be called by both add and remove.
     */
    private void rehash( )
    {
        HashEntry [ ] oldArray = array;
        
            // Create a new, empty table
        allocateArray( nextPrime( 4 * size( ) ) );
        currentSize = 0;
        occupied = 0;
        
            // Copy table over
        for( int i = 0; i < oldArray.length; i++ )
            if( isActive( oldArray, i ) ) 
                add( oldArray[ i ].element );
    }
    
    /**
     * Removes an item from this collection.
     * @param x any object.
     * @return true if this item was removed from the collection.
     */
    public boolean remove( Object x )
    {
        int currentPos = findPos( x );
        if( !isActive( array, currentPos ) )
            return false;
        
        array[ currentPos ].isActive = false;
        currentSize--;
        modCount++;    
        
        if( currentSize < array.length / 8 )
            rehash( );
    
        return true;
    }
    
    /**
     * Change the size of this collection to zero.
     */
    public void clear( )
    {
        currentSize = occupied = 0;
        modCount++;
        for( int i = 0; i < array.length; i++ )
            array[ i ] = null;
    }
    
    /**
     * Obtains an Iterator object used to traverse the collection.
     * @return an iterator positioned prior to the first element.
     */
    public Iterator iterator( )
    {
        return new HashSetIterator( );
    }

    public void serialize(DataOutput output) throws IOException {
        output.writeInt(currentSize);
        for(int i=0; i<currentSize; i++) {
            Serializer.serializeClass((Serializable) array[i], output);
        }
    }

    public void deserialize(DataInput input) throws IOException {
        this.currentSize = input.readInt();
        this.array = new HashEntry[currentSize];
        for(int i=0; i<currentSize; i++) {
            array[i] = (HashEntry) Serializer.deserializeClass(input);
        }
        this.occupied = currentSize;
        this.modCount = currentSize;
    }
    
    /**
     * This is the implementation of the HashSetIterator.
     * It maintains a notion of a current position and of
     * course the implicit reference to the HashSet.
     */
    private class HashSetIterator implements Iterator
    {
        private int expectedModCount = modCount;
        private int currentPos = -1;
        private int visited = 0;       
        
        public boolean hasNext( )
        {
            if( expectedModCount != modCount )
                throw new ConcurrentModificationException( );
            
            return visited != size( );    
        }
        
        public Object next( )
        {
            if( !hasNext( ) )
                throw new NoSuchElementException( );
                          
            do
            {
                currentPos++;
            } while( currentPos < array.length && !isActive( array, currentPos ) );
                            
            visited++;
            return array[ currentPos ].element;    
        }
        
        public void remove( )
        {
            if( expectedModCount != modCount )
                throw new ConcurrentModificationException( );              
            if( currentPos == -1 || !isActive( array, currentPos ) )
                throw new IllegalStateException( );
    
            array[ currentPos ].isActive = false;
            currentSize--;
            visited--;
            modCount++;
            expectedModCount++;
        }
    }
    
    private static class HashEntry implements Serializable
    {
        public Object  element;   // the element
        public boolean isActive;  // false if marked deleted

        public HashEntry( Object e )
        {
            this( e, true );
        }

        public HashEntry( Object e, boolean i )
        {
            element  = e;
            isActive = i;
        }

        public void serialize(DataOutput output) throws IOException {
            if(element instanceof Serializable) {
                Serializable sObj = (Serializable) element;
                Serializer.serializeClass(sObj, output);
            } else {
                throw new IOException();
            }
            output.writeBoolean(isActive);
        }

        public void deserialize(DataInput input) throws IOException {
            this.element = Serializer.deserializeClass(input);
            this.isActive = input.readBoolean();
        }
    }
    
    
    /**
     * Method that performs quadratic probing resolution.
     * @param x the item to search for.
     * @return the position where the search terminates.
     */
    private int findPos( Object x )
    {
        int collisionNum = 0;
        int currentPos = ( x == null ) ? 0 : Math.abs( x.hashCode( ) % array.length );

        while( array[ currentPos ] != null )
        {
            if( x == null )
            {
                if( array[ currentPos ].element == null )
                    break;
            }
            else if( x.equals( array[ currentPos ].element ) )   
                break; 
            
            currentPos += 2 * ++collisionNum - 1;  // Compute ith probe
            if( currentPos >= array.length )       // Implement the mod
                currentPos -= array.length;
        }

        return currentPos;
    }
    
    
    /**
     * Internal method to allocate array.
     * @param arraySize the size of the array.
     */
    private void allocateArray( int arraySize )
    {
        array = new HashEntry[ nextPrime( arraySize ) ];
    }

    /**
     * Internal method to find a prime number at least as large as n.
     * @param n the starting number (must be positive).
     * @return a prime number larger than or equal to n.
     */
    private static int nextPrime( int n )
    {
        if( n % 2 == 0 )
            n++;

        for( ; !isPrime( n ); n += 2 )
            ;

        return n;
    }

    /**
     * Internal method to test if a number is prime.
     * Not an efficient algorithm.
     * @param n the number to test.
     * @return the result of the test.
     */
    private static boolean isPrime( int n )
    {
        if( n == 2 || n == 3 )
            return true;

        if( n == 1 || n % 2 == 0 )
            return false;

        for( int i = 3; i * i <= n; i += 2 )
            if( n % i == 0 )
                return false;

        return true;
    }
    
    private static final int DEFAULT_TABLE_SIZE = 101;
    
    private int currentSize = 0;
    private int occupied = 0;
    private int modCount = 0;
    private HashEntry [ ] array;
}
