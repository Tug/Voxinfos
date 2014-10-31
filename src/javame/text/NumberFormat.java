
package javame.text;

import java.util.Hashtable;


public abstract class NumberFormat extends Format {

	
	public static final int INTEGER_FIELD = 0;

	
	public static final int FRACTION_FIELD = 1;

	
	public final StringBuffer format(Object number, StringBuffer toAppendTo,
			FieldPosition pos) {
		if (number instanceof Long)
			return format(((Long) number).longValue(), toAppendTo, pos);
		else if (number instanceof Double)
			return format(((Double) number).doubleValue(), toAppendTo, pos);
		else if (number instanceof Integer)
			return format(((Integer) number).intValue(), toAppendTo, pos);
		else {
			throw new IllegalArgumentException(
					"Cannot format given Object as a Number");
		}
	}

	
	public final String format(double number) {
		return format(number, new StringBuffer(),
				DontCareFieldPosition.INSTANCE).toString();
	}

	
	public final String format(long number) {
		return format(number, new StringBuffer(),
				DontCareFieldPosition.INSTANCE).toString();
	}

	
	public abstract StringBuffer format(double number, StringBuffer toAppendTo,
			FieldPosition pos);

	
	public abstract StringBuffer format(long number, StringBuffer toAppendTo,
			FieldPosition pos);

	
	public boolean isParseIntegerOnly() {
		return parseIntegerOnly;
	}

	
	public void setParseIntegerOnly(boolean value) {
		parseIntegerOnly = value;
	}

	
	public final static NumberFormat getNumberInstance() {
		return getInstance(NUMBERSTYLE);
	}

	
	public final static NumberFormat getIntegerInstance() {
		return getInstance(INTEGERSTYLE);
	}

	
	public final static NumberFormat getCurrencyInstance() {
		return getInstance(CURRENCYSTYLE);
	}

	
	public final static NumberFormat getPercentInstance() {
		return getInstance(PERCENTSTYLE);
	}

	final static NumberFormat getScientificInstance() {
		return getInstance(SCIENTIFICSTYLE);
	}

	
	public int hashCode() {
		return maximumIntegerDigits * 37 + maxFractionDigits;
		
	}

	
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		NumberFormat other = (NumberFormat) obj;
		return (maximumIntegerDigits == other.maximumIntegerDigits
				&& minimumIntegerDigits == other.minimumIntegerDigits
				&& maximumFractionDigits == other.maximumFractionDigits
				&& minimumFractionDigits == other.minimumFractionDigits
				&& groupingUsed == other.groupingUsed && parseIntegerOnly == other.parseIntegerOnly);
	}

	
	public boolean isGroupingUsed() {
		return groupingUsed;
	}

	
	public void setGroupingUsed(boolean newValue) {
		groupingUsed = newValue;
	}

	
	public int getMaximumIntegerDigits() {
		return maximumIntegerDigits;
	}

	
	public void setMaximumIntegerDigits(int newValue) {
		maximumIntegerDigits = Math.max(0, newValue);
		if (minimumIntegerDigits > maximumIntegerDigits)
			minimumIntegerDigits = maximumIntegerDigits;
	}

	
	public int getMinimumIntegerDigits() {
		return minimumIntegerDigits;
	}

	
	public void setMinimumIntegerDigits(int newValue) {
		minimumIntegerDigits = Math.max(0, newValue);
		if (minimumIntegerDigits > maximumIntegerDigits)
			maximumIntegerDigits = minimumIntegerDigits;
	}

	
	public int getMaximumFractionDigits() {
		return maximumFractionDigits;
	}

	
	public void setMaximumFractionDigits(int newValue) {
		maximumFractionDigits = Math.max(0, newValue);
		if (maximumFractionDigits < minimumFractionDigits)
			minimumFractionDigits = maximumFractionDigits;
	}

	
	public int getMinimumFractionDigits() {
		return minimumFractionDigits;
	}

	
	public void setMinimumFractionDigits(int newValue) {
		minimumFractionDigits = Math.max(0, newValue);
		if (maximumFractionDigits < minimumFractionDigits)
			maximumFractionDigits = minimumFractionDigits;
	}


	private static NumberFormat getInstance(int choice) {
		/* try the cache first */
		String[] numberPatterns = new String[] { "", "", "", "", "" };

		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		int entry = (choice == INTEGERSTYLE) ? NUMBERSTYLE : choice;
		DecimalFormat format = new DecimalFormat(numberPatterns[entry], symbols);

		if (choice == INTEGERSTYLE) {
			format.setMaximumFractionDigits(0);
			format.setDecimalSeparatorAlwaysShown(false);
			format.setParseIntegerOnly(true);
		} else if (choice == CURRENCYSTYLE) {
			//format.adjustForCurrencyDefaultFractionDigits();
		}

		return format;
	}

	// Constants used by factory methods to specify a style of format.
	private static final int NUMBERSTYLE = 0;
	private static final int CURRENCYSTYLE = 1;
	private static final int PERCENTSTYLE = 2;
	private static final int SCIENTIFICSTYLE = 3;
	private static final int INTEGERSTYLE = 4;

	
	private boolean groupingUsed = true;

	
	private byte maxIntegerDigits = 40;

	
	private byte minIntegerDigits = 1;

	
	private byte maxFractionDigits = 3; // invariant, >= minFractionDigits

	
	private byte minFractionDigits = 0;

	
	private boolean parseIntegerOnly = false;

	
	private int maximumIntegerDigits = 40;

	
	private int minimumIntegerDigits = 1;

	
	private int maximumFractionDigits = 3; // invariant, >= minFractionDigits

	
	private int minimumFractionDigits = 0;

	static final int currentSerialVersion = 1;

	
	private int serialVersionOnStream = currentSerialVersion;

	
	static final long serialVersionUID = -2308460125733713944L;

	
	public static class Field extends Format.Field {
		
		private static final Hashtable instanceMap = new Hashtable(11);

		protected Field(String name) {
			super(name);
			if (this.getClass() == NumberFormat.Field.class) {
				instanceMap.put(name, this);
			}
		}

		
		public static final Field INTEGER = new Field("integer");

		
		public static final Field FRACTION = new Field("fraction");

		
		public static final Field EXPONENT = new Field("exponent");

		
		public static final Field DECIMAL_SEPARATOR = new Field(
				"decimal separator");

		
		public static final Field SIGN = new Field("sign");

		
		public static final Field GROUPING_SEPARATOR = new Field(
				"grouping separator");

		
		public static final Field EXPONENT_SYMBOL = new Field("exponent symbol");

		
		public static final Field PERCENT = new Field("percent");

		
		public static final Field PERMILLE = new Field("per mille");

		
		public static final Field CURRENCY = new Field("currency");

		
		public static final Field EXPONENT_SIGN = new Field("exponent sign");
	}
}
