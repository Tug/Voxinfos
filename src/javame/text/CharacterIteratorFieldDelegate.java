
package javame.text;

import java.util.Vector;


class CharacterIteratorFieldDelegate implements Format.FieldDelegate {
	
	private Vector attributedStrings;
	
	private int size;

	CharacterIteratorFieldDelegate() {
		attributedStrings = new Vector();
	}

	public void formatted(Format.Field attr, Object value, int start, int end,
			StringBuffer buffer) {
		if (start != end) {
			if (start < size) {
				// Adjust attributes of existing runs
				int index = size;
				int asIndex = attributedStrings.size() - 1;

				while (start < index) {
					AttributedString as = (AttributedString) attributedStrings
							.elementAt(asIndex--);
					int newIndex = index - as.length();
					int aStart = Math.max(0, start - newIndex);

					as.addAttribute(attr, value, aStart, Math.min(end - start,
							as.length() - aStart)
							+ aStart);
					index = newIndex;
				}
			}
			if (size < start) {
				// Pad attributes
				attributedStrings.addElement(new AttributedString(buffer
						.toString().substring(size, start)));
				size = start;
			}
			if (size < end) {
				// Add new string
				int aStart = Math.max(start, size);
				AttributedString string = new AttributedString(buffer
						.toString().substring(aStart, end));

				string.addAttribute(attr, value);
				attributedStrings.addElement(string);
				size = end;
			}
		}
	}

	public void formatted(int fieldID, Format.Field attr, Object value,
			int start, int end, StringBuffer buffer) {
		formatted(attr, value, start, end, buffer);
	}

	
	public AttributedCharacterIterator getIterator(String string) {
		// Add the last AttributedCharacterIterator if necessary
		// assert(size <= string.length());
		if (string.length() > size) {
			attributedStrings.addElement(new AttributedString(string
					.substring(size)));
			size = string.length();
		}
		int iCount = attributedStrings.size();
		AttributedCharacterIterator iterators[] = new AttributedCharacterIterator[iCount];

		for (int counter = 0; counter < iCount; counter++) {
			iterators[counter] = ((AttributedString) attributedStrings
					.elementAt(counter)).getIterator();
		}
		return new AttributedString(iterators).getIterator();
	}
}
