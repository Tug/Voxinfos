
package javame.text;

import java.util.Hashtable;
import java.util.Vector;



public interface AttributedCharacterIterator extends CharacterIterator {

	
	public int getRunStart();

	
	public int getRunStart(Attribute attribute);

	
	public int getRunStart(Vector attributes);

	public int getRunLimit();

	
	public int getRunLimit(Attribute attribute);

	
	public int getRunLimit(Vector attributes);

	public Hashtable getAttributes();

	
	public Object getAttribute(Attribute attribute);

	
	public Vector getAllAttributeKeys();
};