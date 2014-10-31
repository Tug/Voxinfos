package javame.text;

import java.util.Hashtable;

public class Attribute {

	
	private String name;

	private static final Hashtable instanceMap = new Hashtable(7);

	protected Attribute(String name) {
		this.name = name;
		if (this.getClass() == Attribute.class) {
			instanceMap.put(name, this);
		}
	}

	
	public final boolean equals(Object obj) {
		return super.equals(obj);
	}

	public final int hashCode() {
		return super.hashCode();
	}

	
	public String toString() {
		return getClass().getName() + "(" + name + ")";
	}

	protected String getName() {
		return name;
	}

	
	public static final Attribute LANGUAGE = new Attribute("language");

	
	public static final Attribute READING = new Attribute("reading");

	
	public static final Attribute INPUT_METHOD_SEGMENT = new Attribute(
			"input_method_segment");

	
	private static final long serialVersionUID = -9142742483513960612L;

};