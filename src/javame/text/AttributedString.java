

package javame.text;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;



public class AttributedString {

	
	private static final int ARRAY_SIZE_INCREMENT = 10;

	String text;

	
	int runArraySize; // current size of the arrays
	int runCount; // actual number of runs, <= runArraySize
	int runStarts[]; // start index for each run
	Vector runAttributes[]; // vector of attribute keys for each run
	Vector runAttributeValues[]; // parallel vector of attribute values for each

	
	AttributedString(AttributedCharacterIterator[] iterators) {
		if (iterators == null) {
			throw new NullPointerException("Iterators must not be null");
		}
		if (iterators.length == 0) {
			text = "";
		} else {
			// Build the String contents
			StringBuffer buffer = new StringBuffer();
			for (int counter = 0; counter < iterators.length; counter++) {
				appendContents(buffer, iterators[counter]);
			}

			text = buffer.toString();

			if (text.length() > 0) {
				// Determine the runs, creating a new run when the attributes
				// differ.
				int offset = 0;
				Hashtable last = null;

				for (int counter = 0; counter < iterators.length; counter++) {
					AttributedCharacterIterator iterator = iterators[counter];
					int start = iterator.getBeginIndex();
					int end = iterator.getEndIndex();
					int index = start;

					while (index < end) {
						iterator.setIndex(index);

						Hashtable attrs = iterator.getAttributes();

						if (mapsDiffer(last, attrs)) {
							setAttributes(attrs, index - start + offset);
						}
						last = attrs;
						index = iterator.getRunLimit();
					}
					offset += (end - start);
				}
			}
		}
	}

	
	public AttributedString(String text) {
		if (text == null) {
			throw new NullPointerException();
		}
		this.text = text;
	}

	
	public AttributedString(String text, Hashtable attributes) {
		if (text == null || attributes == null) {
			throw new NullPointerException();
		}
		this.text = text;

		if (text.length() == 0) {
			if (attributes.isEmpty())
				return;
			throw new IllegalArgumentException(
					"Can't add attribute to 0-length text");
		}

		int attributeCount = attributes.size();
		if (attributeCount > 0) {
			createRunAttributeDataVectors();
			Vector newRunAttributes = new Vector(attributeCount);
			Vector newRunAttributeValues = new Vector(attributeCount);
			runAttributes[0] = newRunAttributes;
			runAttributeValues[0] = newRunAttributeValues;
			Enumeration iterator = attributes.keys();
			while (iterator.hasMoreElements()) {
				Object key = iterator.nextElement();
				Object value = attributes.get(key);
				newRunAttributes.addElement(key);
				newRunAttributeValues.addElement(value);
			}
		}
	}

	public AttributedString(AttributedCharacterIterator text) {
		
		this(text, text.getBeginIndex(), text.getEndIndex(), null);
	}

	
	public AttributedString(AttributedCharacterIterator text, int beginIndex,
			int endIndex) {
		this(text, beginIndex, endIndex, null);
	}

	
	public AttributedString(AttributedCharacterIterator text, int beginIndex,
			int endIndex, Attribute[] attributes) {
		if (text == null) {
			throw new NullPointerException();
		}

		// Validate the given subrange
		int textBeginIndex = text.getBeginIndex();
		int textEndIndex = text.getEndIndex();
		if (beginIndex < textBeginIndex || endIndex > textEndIndex
				|| beginIndex > endIndex)
			throw new IllegalArgumentException("Invalid substring range");

		// Copy the given string
		StringBuffer textBuffer = new StringBuffer();
		text.setIndex(beginIndex);
		for (char c = text.current(); text.getIndex() < endIndex; c = text
				.next())
			textBuffer.append(c);
		this.text = textBuffer.toString();

		if (beginIndex == endIndex)
			return;

		// Select attribute keys to be taken care of
		Vector keys = new Vector();
		Vector attrKeys = text.getAllAttributeKeys();
		if (attributes == null) {
			keys = attrKeys;
		} else {
			for (int i = 0; i < attributes.length; i++)
				keys.addElement(attributes[i]);
			Enumeration iterator = keys.elements();

			// retain all keys by attrKeys
			//TODO add to ArrayUtils
			while (iterator.hasMoreElements()) {
				Object elem = iterator.nextElement();
				if (!attrKeys.contains(elem))
					keys.removeElement(elem);
			}

		}
		if (keys.isEmpty())
			return;

	
		Enumeration itr = keys.elements();
		while (itr.hasMoreElements()) {
			Attribute attributeKey = (Attribute) itr.nextElement();
			text.setIndex(textBeginIndex);
			while (text.getIndex() < endIndex) {
				int start = text.getRunStart(attributeKey);
				int limit = text.getRunLimit(attributeKey);
				Object value = text.getAttribute(attributeKey);

				if (value != null) {
					if (value instanceof Annotation) {
						if (start >= beginIndex && limit <= endIndex) {
							addAttribute(attributeKey, value, start
									- beginIndex, limit - beginIndex);
						} else {
							if (limit > endIndex)
								break;
						}
					} else {
						// if the run is beyond the given (subset) range, we
						// don't need to process further.
						if (start >= endIndex)
							break;
						if (limit > beginIndex) {
							// attribute is applied to any subrange
							if (start < beginIndex)
								start = beginIndex;
							if (limit > endIndex)
								limit = endIndex;
							if (start != limit) {
								addAttribute(attributeKey, value, start
										- beginIndex, limit - beginIndex);
							}
						}
					}
				}
				text.setIndex(limit);
			}
		}
	}


	public void addAttribute(Attribute attribute, Object value) {

		if (attribute == null) {
			throw new NullPointerException();
		}

		int len = length();
		if (len == 0) {
			throw new IllegalArgumentException(
					"Can't add attribute to 0-length text");
		}

		addAttributeImpl(attribute, value, 0, len);
	}

	
	public void addAttribute(Attribute attribute, Object value, int beginIndex,
			int endIndex) {

		if (attribute == null) {
			throw new NullPointerException();
		}

		if (beginIndex < 0 || endIndex > length() || beginIndex >= endIndex) {
			throw new IllegalArgumentException("Invalid substring range");
		}

		addAttributeImpl(attribute, value, beginIndex, endIndex);
	}

	
	public void addAttributes(Hashtable attributes, int beginIndex, int endIndex) {
		if (attributes == null) {
			throw new NullPointerException();
		}

		if (beginIndex < 0 || endIndex > length() || beginIndex > endIndex) {
			throw new IllegalArgumentException("Invalid substring range");
		}
		if (beginIndex == endIndex) {
			if (attributes.isEmpty())
				return;
			throw new IllegalArgumentException(
					"Can't add attribute to 0-length text");
		}

		// make sure we have run attribute data vectors
		if (runCount == 0) {
			createRunAttributeDataVectors();
		}

		// break up runs if necessary
		int beginRunIndex = ensureRunBreak(beginIndex);
		int endRunIndex = ensureRunBreak(endIndex);

		Enumeration iterator = attributes.keys();
		while (iterator.hasMoreElements()) {
			Object key = iterator.nextElement();
			Object value = attributes.get(key);
			addAttributeRunData((Attribute) key, value, beginRunIndex,
					endRunIndex);
		}
	}

	private synchronized void addAttributeImpl(Attribute attribute,
			Object value, int beginIndex, int endIndex) {

		// make sure we have run attribute data vectors
		if (runCount == 0) {
			createRunAttributeDataVectors();
		}

		// break up runs if necessary
		int beginRunIndex = ensureRunBreak(beginIndex);
		int endRunIndex = ensureRunBreak(endIndex);

		addAttributeRunData(attribute, value, beginRunIndex, endRunIndex);
	}

	private final void createRunAttributeDataVectors() {
		// use temporary variables so things remain consistent in case of an
		// exception
		int newRunStarts[] = new int[ARRAY_SIZE_INCREMENT];
		Vector newRunAttributes[] = new Vector[ARRAY_SIZE_INCREMENT];
		Vector newRunAttributeValues[] = new Vector[ARRAY_SIZE_INCREMENT];
		runStarts = newRunStarts;
		runAttributes = newRunAttributes;
		runAttributeValues = newRunAttributeValues;
		runArraySize = ARRAY_SIZE_INCREMENT;
		runCount = 1; // assume initial run starting at index 0
	}

	// ensure there's a run break at offset, return the index of the run
	private final int ensureRunBreak(int offset) {
		return ensureRunBreak(offset, true);
	}

	
	private final int ensureRunBreak(int offset, boolean copyAttrs) {
		if (offset == length()) {
			return runCount;
		}

		// search for the run index where this offset should be
		int runIndex = 0;
		while (runIndex < runCount && runStarts[runIndex] < offset) {
			runIndex++;
		}

		// if the offset is at a run start already, we're done
		if (runIndex < runCount && runStarts[runIndex] == offset) {
			return runIndex;
		}

		// we'll have to break up a run
		// first, make sure we have enough space in our arrays
		if (runCount == runArraySize) {
			int newArraySize = runArraySize + ARRAY_SIZE_INCREMENT;
			int newRunStarts[] = new int[newArraySize];
			Vector newRunAttributes[] = new Vector[newArraySize];
			Vector newRunAttributeValues[] = new Vector[newArraySize];
			for (int i = 0; i < runArraySize; i++) {
				newRunStarts[i] = runStarts[i];
				newRunAttributes[i] = runAttributes[i];
				newRunAttributeValues[i] = runAttributeValues[i];
			}
			runStarts = newRunStarts;
			runAttributes = newRunAttributes;
			runAttributeValues = newRunAttributeValues;
			runArraySize = newArraySize;
		}

		
		Vector newRunAttributes = null;
		Vector newRunAttributeValues = null;

		if (copyAttrs) {
			Vector oldRunAttributes = runAttributes[runIndex - 1];
			Vector oldRunAttributeValues = runAttributeValues[runIndex - 1];
			if (oldRunAttributes != null) {
				// clone Vector
				// TODO move to ArrayUtils
				newRunAttributes = new Vector(oldRunAttributes.size());
				Enumeration iterator = oldRunAttributes.elements();
				while (iterator.hasMoreElements()) {
					newRunAttributes.insertElementAt(iterator.nextElement(), 0);
				}
			}
			if (oldRunAttributeValues != null) {
				newRunAttributeValues = new Vector(oldRunAttributeValues.size());
				Enumeration iterator = oldRunAttributeValues.elements();
				while (iterator.hasMoreElements()) {
					newRunAttributeValues.insertElementAt(iterator
							.nextElement(), 0);
				}
			}
		}

		// now actually break up the run
		runCount++;
		for (int i = runCount - 1; i > runIndex; i--) {
			runStarts[i] = runStarts[i - 1];
			runAttributes[i] = runAttributes[i - 1];
			runAttributeValues[i] = runAttributeValues[i - 1];
		}
		runStarts[runIndex] = offset;
		runAttributes[runIndex] = newRunAttributes;
		runAttributeValues[runIndex] = newRunAttributeValues;

		return runIndex;
	}

	
	private void addAttributeRunData(Attribute attribute, Object value,
			int beginRunIndex, int endRunIndex) {

		for (int i = beginRunIndex; i < endRunIndex; i++) {
			int keyValueIndex = -1; // index of key and value in our vectors;
			// assume we don't have an entry yet
			if (runAttributes[i] == null) {
				Vector newRunAttributes = new Vector();
				Vector newRunAttributeValues = new Vector();
				runAttributes[i] = newRunAttributes;
				runAttributeValues[i] = newRunAttributeValues;
			} else {
				// check whether we have an entry already
				keyValueIndex = runAttributes[i].indexOf(attribute);
			}

			if (keyValueIndex == -1) {
				// create new entry
				int oldSize = runAttributes[i].size();
				runAttributes[i].addElement(attribute);
				try {
					runAttributeValues[i].addElement(value);
				} catch (Exception e) {
					runAttributes[i].setSize(oldSize);
					runAttributeValues[i].setSize(oldSize);
				}
			} else {
				// update existing entry
				runAttributeValues[i].setElementAt(value, keyValueIndex);
			}
		}
	}

	
	public AttributedCharacterIterator getIterator() {
		return getIterator(null, 0, length());
	}


	public AttributedCharacterIterator getIterator(Attribute[] attributes) {
		return getIterator(attributes, 0, length());
	}

	
	public AttributedCharacterIterator getIterator(Attribute[] attributes,
			int beginIndex, int endIndex) {
		return new AttributedStringIterator(attributes, beginIndex, endIndex);
	}

	
	int length() {
		return text.length();
	}

	private char charAt(int index) {
		return text.charAt(index);
	}

	private synchronized Object getAttribute(Attribute attribute, int runIndex) {
		Vector currentRunAttributes = runAttributes[runIndex];
		Vector currentRunAttributeValues = runAttributeValues[runIndex];
		if (currentRunAttributes == null) {
			return null;
		}
		int attributeIndex = currentRunAttributes.indexOf(attribute);
		if (attributeIndex != -1) {
			return currentRunAttributeValues.elementAt(attributeIndex);
		} else {
			return null;
		}
	}

	
	private Object getAttributeCheckRange(Attribute attribute, int runIndex,
			int beginIndex, int endIndex) {
		Object value = getAttribute(attribute, runIndex);
		if (value instanceof Annotation) {
			// need to check whether the annotation's range extends outside the
			// iterator's range
			if (beginIndex > 0) {
				int currIndex = runIndex;
				int runStart = runStarts[currIndex];
				while (runStart >= beginIndex
						&& valuesMatch(value, getAttribute(attribute,
								currIndex - 1))) {
					currIndex--;
					runStart = runStarts[currIndex];
				}
				if (runStart < beginIndex) {
					// annotation's range starts before iterator's range
					return null;
				}
			}
			int textLength = length();
			if (endIndex < textLength) {
				int currIndex = runIndex;
				int runLimit = (currIndex < runCount - 1) ? runStarts[currIndex + 1]
						: textLength;
				while (runLimit <= endIndex
						&& valuesMatch(value, getAttribute(attribute,
								currIndex + 1))) {
					currIndex++;
					runLimit = (currIndex < runCount - 1) ? runStarts[currIndex + 1]
							: textLength;
				}
				if (runLimit > endIndex) {
					// annotation's range ends after iterator's range
					return null;
				}
			}
			// annotation's range is subrange of iterator's range,
			// so we can return the value
		}
		return value;
	}

	
	private boolean attributeValuesMatch(Vector attributes, int runIndex1,
			int runIndex2) {
		Enumeration iterator = attributes.elements();
		while (iterator.hasMoreElements()) {
			Attribute key = (Attribute) iterator.nextElement();
			if (!valuesMatch(getAttribute(key, runIndex1), getAttribute(key,
					runIndex2))) {
				return false;
			}
		}
		return true;
	}

	// returns whether the two objects are either both null or equal
	private final static boolean valuesMatch(Object value1, Object value2) {
		if (value1 == null) {
			return value2 == null;
		} else {
			return value1.equals(value2);
		}
	}

	
	private final void appendContents(StringBuffer buf,
			CharacterIterator iterator) {
		int index = iterator.getBeginIndex();
		int end = iterator.getEndIndex();

		while (index < end) {
			iterator.setIndex(index++);
			buf.append(iterator.current());
		}
	}

	
	private void setAttributes(Hashtable attrs, int offset) {
		if (runCount == 0) {
			createRunAttributeDataVectors();
		}

		int index = ensureRunBreak(offset, false);
		int size;

		if (attrs != null && (size = attrs.size()) > 0) {
			Vector runAttrs = new Vector(size);
			Vector runValues = new Vector(size);

			Enumeration iterator = attrs.keys();
			while (iterator.hasMoreElements()) {
				Object key = iterator.nextElement();
				Object value = attrs.get(key);
				runAttrs.addElement(key);
				runValues.addElement(value);
			}
			runAttributes[index] = runAttrs;
			runAttributeValues[index] = runValues;
		}
	}

	
	private static boolean mapsDiffer(Hashtable last, Hashtable attrs) {
		if (last == null) {
			return (attrs != null && attrs.size() > 0);
		}
		return (!last.equals(attrs));
	}

	// the iterator class associated with this string class

	final private class AttributedStringIterator implements
			AttributedCharacterIterator {

		private int beginIndex;
		private int endIndex;

		// attributes that our client is interested in
		private Attribute[] relevantAttributes;

		// the current index for our iteration
		// invariant: beginIndex <= currentIndex <= endIndex
		private int currentIndex;

		// information about the run that includes currentIndex
		private int currentRunIndex;
		private int currentRunStart;
		private int currentRunLimit;

		// constructor
		AttributedStringIterator(Attribute[] attributes, int beginIndex,
				int endIndex) {

			if (beginIndex < 0 || beginIndex > endIndex || endIndex > length()) {
				throw new IllegalArgumentException("Invalid substring range");
			}

			this.beginIndex = beginIndex;
			this.endIndex = endIndex;
			this.currentIndex = beginIndex;
			updateRunInfo();
			if (attributes != null) {
				//array clone
				//TODO add to ArrayUtils
				relevantAttributes = new Attribute[attributes.length];
				for (int i = 0; i < attributes.length; i++)
					relevantAttributes[i] = attributes[i];
			}
		}

		// Object methods. See documentation in that class.

		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!(obj instanceof AttributedStringIterator)) {
				return false;
			}

			AttributedStringIterator that = (AttributedStringIterator) obj;

			if (AttributedString.this != that.getString())
				return false;
			if (currentIndex != that.currentIndex
					|| beginIndex != that.beginIndex
					|| endIndex != that.endIndex)
				return false;
			return true;
		}

		public int hashCode() {
			return text.hashCode() ^ currentIndex ^ beginIndex ^ endIndex;
		}


		// CharacterIterator methods. See documentation in that interface.

		public char first() {
			return internalSetIndex(beginIndex);
		}

		public char last() {
			if (endIndex == beginIndex) {
				return internalSetIndex(endIndex);
			} else {
				return internalSetIndex(endIndex - 1);
			}
		}

		public char current() {
			if (currentIndex == endIndex) {
				return DONE;
			} else {
				return charAt(currentIndex);
			}
		}

		public char next() {
			if (currentIndex < endIndex) {
				return internalSetIndex(currentIndex + 1);
			} else {
				return DONE;
			}
		}

		public char previous() {
			if (currentIndex > beginIndex) {
				return internalSetIndex(currentIndex - 1);
			} else {
				return DONE;
			}
		}

		public char setIndex(int position) {
			if (position < beginIndex || position > endIndex)
				throw new IllegalArgumentException("Invalid index");
			return internalSetIndex(position);
		}

		public int getBeginIndex() {
			return beginIndex;
		}

		public int getEndIndex() {
			return endIndex;
		}

		public int getIndex() {
			return currentIndex;
		}

		

		public int getRunStart() {
			return currentRunStart;
		}

		public int getRunStart(Attribute attribute) {
			if (currentRunStart == beginIndex || currentRunIndex == -1) {
				return currentRunStart;
			} else {
				Object value = getAttribute(attribute);
				int runStart = currentRunStart;
				int runIndex = currentRunIndex;
				while (runStart > beginIndex
						&& valuesMatch(value, AttributedString.this
								.getAttribute(attribute, runIndex - 1))) {
					runIndex--;
					runStart = runStarts[runIndex];
				}
				if (runStart < beginIndex) {
					runStart = beginIndex;
				}
				return runStart;
			}
		}

		public int getRunStart(Vector attributes) {
			if (currentRunStart == beginIndex || currentRunIndex == -1) {
				return currentRunStart;
			} else {
				int runStart = currentRunStart;
				int runIndex = currentRunIndex;
				while (runStart > beginIndex
						&& AttributedString.this.attributeValuesMatch(
								attributes, currentRunIndex, runIndex - 1)) {
					runIndex--;
					runStart = runStarts[runIndex];
				}
				if (runStart < beginIndex) {
					runStart = beginIndex;
				}
				return runStart;
			}
		}

		public int getRunLimit() {
			return currentRunLimit;
		}

		public int getRunLimit(Attribute attribute) {
			if (currentRunLimit == endIndex || currentRunIndex == -1) {
				return currentRunLimit;
			} else {
				Object value = getAttribute(attribute);
				int runLimit = currentRunLimit;
				int runIndex = currentRunIndex;
				while (runLimit < endIndex
						&& valuesMatch(value, AttributedString.this
								.getAttribute(attribute, runIndex + 1))) {
					runIndex++;
					runLimit = runIndex < runCount - 1 ? runStarts[runIndex + 1]
							: endIndex;
				}
				if (runLimit > endIndex) {
					runLimit = endIndex;
				}
				return runLimit;
			}
		}

		public int getRunLimit(Vector attributes) {
			if (currentRunLimit == endIndex || currentRunIndex == -1) {
				return currentRunLimit;
			} else {
				int runLimit = currentRunLimit;
				int runIndex = currentRunIndex;
				while (runLimit < endIndex
						&& AttributedString.this.attributeValuesMatch(
								attributes, currentRunIndex, runIndex + 1)) {
					runIndex++;
					runLimit = runIndex < runCount - 1 ? runStarts[runIndex + 1]
							: endIndex;
				}
				if (runLimit > endIndex) {
					runLimit = endIndex;
				}
				return runLimit;
			}
		}

		public Hashtable getAttributes() {
			if (runAttributes == null || currentRunIndex == -1
					|| runAttributes[currentRunIndex] == null) {
				
				return new Hashtable();				
			}
			return new AttributeMap(currentRunIndex, beginIndex, endIndex);
		}

		public Vector getAllAttributeKeys() {
		
			if (runAttributes == null) {
				// ??? would be nice to return null, but current spec doesn't
				// allow it
				// returning HashSet saves us from dealing with emptiness
				return new Vector();
			}
			synchronized (AttributedString.this) {
			
				Vector keys = new Vector();
				int i = 0;
				while (i < runCount) {
					if (runStarts[i] < endIndex
							&& (i == runCount - 1 || runStarts[i + 1] > beginIndex)) {
						Vector currentRunAttributes = runAttributes[i];
						if (currentRunAttributes != null) {
							int j = currentRunAttributes.size();
							while (j-- > 0) {
								keys.addElement(currentRunAttributes.elementAt(j));
							}
						}
					}
					i++;
				}
				return keys;
			}
		}

		public Object getAttribute(Attribute attribute) {
			int runIndex = currentRunIndex;
			if (runIndex < 0) {
				return null;
			}
			return AttributedString.this.getAttributeCheckRange(attribute,
					runIndex, beginIndex, endIndex);
		}

	

		private AttributedString getString() {
			return AttributedString.this;
		}

		
		private char internalSetIndex(int position) {
			currentIndex = position;
			if (position < currentRunStart || position >= currentRunLimit) {
				updateRunInfo();
			}
			if (currentIndex == endIndex) {
				return DONE;
			} else {
				return charAt(position);
			}
		}

		// update the information about the current run
		private void updateRunInfo() {
			if (currentIndex == endIndex) {
				currentRunStart = currentRunLimit = endIndex;
				currentRunIndex = -1;
			} else {
				synchronized (AttributedString.this) {
					int runIndex = -1;
					while (runIndex < runCount - 1
							&& runStarts[runIndex + 1] <= currentIndex)
						runIndex++;
					currentRunIndex = runIndex;
					if (runIndex >= 0) {
						currentRunStart = runStarts[runIndex];
						if (currentRunStart < beginIndex)
							currentRunStart = beginIndex;
					} else {
						currentRunStart = beginIndex;
					}
					if (runIndex < runCount - 1) {
						currentRunLimit = runStarts[runIndex + 1];
						if (currentRunLimit > endIndex)
							currentRunLimit = endIndex;
					} else {
						currentRunLimit = endIndex;
					}
				}
			}
		}

	}

	

	final private class AttributeMap extends Hashtable {

		int runIndex;
		int beginIndex;
		int endIndex;

		AttributeMap(int runIndex, int beginIndex, int endIndex) {
			this.runIndex = runIndex;
			this.beginIndex = beginIndex;
			this.endIndex = endIndex;
		}

		public Enumeration keys() {
			Vector result = new Vector();
			synchronized (AttributedString.this) {
				int size = runAttributes[runIndex].size();
				for (int i = 0; i < size; i++) {
					Attribute key = (Attribute) runAttributes[runIndex].elementAt(i);
					Object value = runAttributeValues[runIndex].elementAt(i);
					if (value instanceof Annotation) {
						value = AttributedString.this.getAttributeCheckRange(
								key, runIndex, beginIndex, endIndex);
						if (value == null) {
							continue;
						}
					}
					result.addElement(key);
				}
			}
			return result.elements();
		}
		
		public Enumeration elements() {
			Vector result = new Vector();
			synchronized (AttributedString.this) {
				int size = runAttributes[runIndex].size();
				for (int i = 0; i < size; i++) {
					Attribute key = (Attribute) runAttributes[runIndex].elementAt(i);
					Object value = runAttributeValues[runIndex].elementAt(i);
					if (value instanceof Annotation) {
						value = AttributedString.this.getAttributeCheckRange(
								key, runIndex, beginIndex, endIndex);
						if (value == null) {
							continue;
						}
					}
					result.addElement(value);
				}
			}
			return result.elements();
		}

		public Object get(Object key) {
			return AttributedString.this.getAttributeCheckRange(
					(Attribute) key, runIndex, beginIndex, endIndex);
		}
	}
}

