package javame.util;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import javame.io.Serializable;

/**
 * Stack class. Unlike java.util.Stack, this is not
 * extended from Vector. This is the minimum respectable
 * set of operations.
 */
public class Stack implements Serializable
{
    /**
     * Constructs an empty stack.
     */
    public Stack( )
    {
        items = new ArrayList( );
    }
    
    /**
     * Adds an item to the top of the stack.
     * @param x the item to add.
     * @return the item added.
     */
    public Object push( Object x )
    {
        items.add( x );
        return x;
    }
    
    /**
     * Removes and returns item from the top of the stack.
     * @return the former top item.
     * @throws EmptyStackException if stack is empty.
     */
    public Object pop( )
    {
        if( isEmpty( ) )
            throw new EmptyStackException( );
            
        return items.remove( items.size( ) - 1 );
    }
    
    /**
     * Returns item from the top of the stack.
     * @return the top item.
     * @throws EmptyStackException if stack is empty.
     */
    public Object peek( )
    {
        if( isEmpty( ) )
            throw new EmptyStackException( );
            
        return items.get( items.size( ) - 1 );
    }
    
    /**
     * Tests if stack is empty.
     * @return true if the stack is empty; false otherwise.
     */
    public boolean isEmpty( )
    {
        return size( ) == 0;
    }
    
    /**
     * Returns the size of the stack.
     * @return the size of the stack.
     */
    public int size( )
    {
        return items.size( );
    }
    
    public void clear( )
    {
        items.clear( );
    }
    
    public String toString( )
    {
        StringBuffer result = new StringBuffer( );
        for( int i = size( ) - 1; i >= 0; i-- )
            result.append( items.get( i ) );
        return result.toString( );    
    }
    
    private ArrayList items;

    public void serialize(DataOutput output) throws IOException {
        throw new RuntimeException("Not implemented yet");
    }

    public void deserialize(DataInput input) throws IOException {
        throw new RuntimeException("Not implemented yet");
    }
}
