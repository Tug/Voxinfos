package javame.util;

/**
 * AbstractCollection provides default implementations for
 * some of the easy methods in the Collection interface.
 */
public abstract class AbstractCollection implements Collection
{
    /**
     * Tests if this collection is empty.
     * @return true if the size of this collection is zero.
     */
    public boolean isEmpty( )
    {
        return size( ) == 0;
    }
    
    /**
     * Change the size of this collection to zero.
     */
    public void clear( )
    {
        Iterator itr = iterator( );
        while( itr.hasNext( ) )
        {
            itr.next( );
            itr.remove( );
        }
    }    
    
    /**
     * Obtains a primitive array view of the collection.
     * @return the primitive array view.
     */
    public Object [ ] toArray( )
    {
        Object [ ] copy = new Object[ size( ) ];
        
        Iterator itr = iterator( );
        int i = 0;
        
        while( itr.hasNext( ) )
            copy[ i++ ] = itr.next( );
        
        return copy;    
    }
    
    /**
     * Returns true if this collection contains x.
     * If x is null, returns false.
     * (This behavior may not always be appropriate.)
     * @param x the item to search for.
     * @return true if x is not null and is found in
     * this collection.
     */
    public boolean contains( Object x )
    {
        if( x == null )
            return false;
            
        Iterator itr = iterator( );
        while( itr.hasNext( ) )
            if( x.equals( itr.next( ) ) )
                return true;
            
        return false;        
    }
    
    /**
     * Removes non-null x from this collection.
     * (This behavior may not always be appropriate.)
     * @param x the item to remove.
     * @return true if remove succeeds.
     */
    public boolean remove( Object x )
    {
        if( x == null )
            return false;
            
        Iterator itr = iterator( );
        while( itr.hasNext( ) )
            if( x.equals( itr.next( ) ) )
            {
                itr.remove( );
                return true;
            }
            
        return false;
    }
    
    /**
     * Return true if items in other collection
     * are equal to items in this collection
     * (same order, and same according to equals).
     */
    public final boolean equals( Object other )
    {
        if( other == this )
            return true;
            
        if( ! ( other instanceof Collection ) )
            return false;
        
        Collection rhs = (Collection) other;
        if( size( ) != rhs.size( ) )
            return false;
        
        Iterator lhsItr = this.iterator( );
        Iterator rhsItr = rhs.iterator( );
        
        while( lhsItr.hasNext( ) )
            if( !isEqual( lhsItr.next( ), rhsItr.next( ) ) )
                return false;
                
        return true;            
    }
    
    /**
     * Return the hashCode.
     */
    public final int hashCode( )
    {
       int hashVal = 1;
       Iterator itr = iterator( );
       
       while( itr.hasNext( ) )
       {
           Object obj = itr.next( );
           hashVal = 31 * hashVal + ( obj == null ? 0 : obj.hashCode( ) );
       }
       
       return hashVal;
    }
    
    
    /**
     * Return true if two objects are equal; works
     * if objects can be null.
     */
    private boolean isEqual( Object lhs, Object rhs )
    {
        if( lhs == null )
            return rhs == null;
        return lhs.equals( rhs );    
    }
}