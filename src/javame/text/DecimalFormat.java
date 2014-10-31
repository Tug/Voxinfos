

package javame.text;

import java.util.Vector;


public class DecimalFormat extends NumberFormat {

	private String mPattern = "";

	
	public DecimalFormat() {
		// Always applyPattern after the symbols are set
		this.symbols = new DecimalFormatSymbols();
		applyPattern(mPattern, false);
	}

	
	public DecimalFormat(String pattern) {
		// Always applyPattern after the symbols are set
		this.symbols = new DecimalFormatSymbols();
		applyPattern(pattern, false);
	}

	
	public DecimalFormat(String pattern, DecimalFormatSymbols symbols) {
		// Always applyPattern after the symbols are set
		this.symbols = (DecimalFormatSymbols) symbols.clone();
		applyPattern(pattern, false);
	}

	
	public StringBuffer format(double number, StringBuffer result,
			FieldPosition fieldPosition) {
		fieldPosition.setBeginIndex(0);
		fieldPosition.setEndIndex(0);
		return format(number, result, fieldPosition.getFieldDelegate());
	}

	
	private StringBuffer format(double number, StringBuffer result,
			FieldDelegate delegate) {
		if (Double.isNaN(number)) {
			int iFieldStart = result.length();

			result.append(symbols.getNaN());

			delegate.formatted(INTEGER_FIELD, Field.INTEGER, Field.INTEGER,
					iFieldStart, result.length(), result);
			return result;
		}

		
		boolean isNegative = (number < 0.0)
				|| (number == 0.0 && 1 / number < 0.0);
		if (isNegative)
			number = -number;

		// Do this BEFORE checking to see if value is infinite!
		if (multiplier != 1)
			number *= multiplier;

		if (Double.isInfinite(number)) {
			if (isNegative) {
				append(result, negativePrefix, delegate,
						getNegativePrefixFieldPositions(), Field.SIGN);
			} else {
				append(result, positivePrefix, delegate,
						getPositivePrefixFieldPositions(), Field.SIGN);
			}
			int iFieldStart = result.length();

			result.append(symbols.getInfinity());

			delegate.formatted(INTEGER_FIELD, Field.INTEGER, Field.INTEGER,
					iFieldStart, result.length(), result);

			if (isNegative) {
				append(result, negativeSuffix, delegate,
						getNegativeSuffixFieldPositions(), Field.SIGN);
			} else {
				append(result, positiveSuffix, delegate,
						getPositiveSuffixFieldPositions(), Field.SIGN);
			}
			return result;
		}

		// At this point we are guaranteed a nonnegative finite
		// number.
		synchronized (digitList) {
			digitList.set(number,
					useExponentialNotation ? getMaximumIntegerDigits()
							+ getMaximumFractionDigits()
							: getMaximumFractionDigits(),
					!useExponentialNotation);

			return subformat(result, delegate, isNegative, false);
		}
	}

	
	public StringBuffer format(long number, StringBuffer result,
			FieldPosition fieldPosition) {
		fieldPosition.setBeginIndex(0);
		fieldPosition.setEndIndex(0);

		return format(number, result, fieldPosition.getFieldDelegate());
	}

	
	private StringBuffer format(long number, StringBuffer result,
			FieldDelegate delegate) {
		boolean isNegative = (number < 0);
		if (isNegative)
			number = -number;

		
		if (multiplier != 1 && multiplier != 0) {
			boolean useDouble = false;

			if (number < 0) // This can only happen if number == Long.MIN_VALUE
			{
				long cutoff = Long.MIN_VALUE / multiplier;
				useDouble = (number < cutoff);
			} else {
				long cutoff = Long.MAX_VALUE / multiplier;
				useDouble = (number > cutoff);
			}

			if (useDouble) {
				double dnumber = (double) (isNegative ? -number : number);
				return format(dnumber, result, delegate);
			}
		}

		number *= multiplier;
		synchronized (digitList) {
			digitList.set(number,
					useExponentialNotation ? getMaximumIntegerDigits()
							+ getMaximumFractionDigits() : 0);

			return subformat(result, delegate, isNegative, true);
		}
	}

	
	public AttributedCharacterIterator formatToCharacterIterator(Object obj) {
		CharacterIteratorFieldDelegate delegate = new CharacterIteratorFieldDelegate();
		StringBuffer sb = new StringBuffer();

		if (obj instanceof Long) {
			format(((Long) obj).longValue(), sb, delegate);
		} else if (obj == null) {
			throw new NullPointerException(
					"formatToCharacterIterator must be passed non-null object");
		} else if (obj instanceof Double) {
			format(((Double) obj).doubleValue(), sb, delegate);
		} else {
			throw new IllegalArgumentException(
					"Cannot format given Object as a Number");
		}
		return delegate.getIterator(sb.toString());
	}

	
	private StringBuffer subformat(StringBuffer result, FieldDelegate delegate,
			boolean isNegative, boolean isInteger) {
		

		char zero = symbols.getZeroDigit();
		int zeroDelta = zero - '0'; // '0' is the DigitList representation of
		// zero
		char grouping = symbols.getGroupingSeparator();
		char decimal = isCurrencyFormat ? symbols.getMonetaryDecimalSeparator()
				: symbols.getDecimalSeparator();

		
		if (digitList.isZero()) {
			digitList.decimalAt = 0; // Normalize
		}

		int fieldStart = result.length();

		if (isNegative) {
			append(result, negativePrefix, delegate,
					getNegativePrefixFieldPositions(), Field.SIGN);
		} else {
			append(result, positivePrefix, delegate,
					getPositivePrefixFieldPositions(), Field.SIGN);
		}

		if (useExponentialNotation) {
			int iFieldStart = result.length();
			int iFieldEnd = -1;
			int fFieldStart = -1;

			
			int exponent = digitList.decimalAt;
			int repeat = getMaximumIntegerDigits();
			int minimumIntegerDigits = getMinimumIntegerDigits();
			if (repeat > 1 && repeat > minimumIntegerDigits) {
				
				if (exponent >= 1) {
					exponent = ((exponent - 1) / repeat) * repeat;
				} else {
					// integer division rounds towards 0
					exponent = ((exponent - repeat) / repeat) * repeat;
				}
				minimumIntegerDigits = 1;
			} else {
				// No repeating range is defined; use minimum integer digits.
				exponent -= minimumIntegerDigits;
			}

			
			int minimumDigits = getMinimumIntegerDigits()
					+ getMinimumFractionDigits();
			// The number of integer digits is handled specially if the number
			// is zero, since then there may be no digits.
			int integerDigits = digitList.isZero() ? minimumIntegerDigits
					: digitList.decimalAt - exponent;
			if (minimumDigits < integerDigits) {
				minimumDigits = integerDigits;
			}
			int totalDigits = digitList.count;
			if (minimumDigits > totalDigits)
				totalDigits = minimumDigits;
			boolean addedDecimalSeparator = false;

			for (int i = 0; i < totalDigits; ++i) {
				if (i == integerDigits) {
					// Record field information for caller.
					iFieldEnd = result.length();

					result.append(decimal);
					addedDecimalSeparator = true;

					// Record field information for caller.
					fFieldStart = result.length();

				}
				result
						.append((i < digitList.count) ? (char) (digitList.digits[i] + zeroDelta)
								: zero);
			}

			// Record field information
			if (iFieldEnd == -1) {
				iFieldEnd = result.length();
			}
			delegate.formatted(INTEGER_FIELD, Field.INTEGER, Field.INTEGER,
					iFieldStart, iFieldEnd, result);
			if (addedDecimalSeparator) {
				delegate
						.formatted(Field.DECIMAL_SEPARATOR,
								Field.DECIMAL_SEPARATOR, iFieldEnd,
								fFieldStart, result);
			}
			if (fFieldStart == -1) {
				fFieldStart = result.length();
			}
			delegate.formatted(FRACTION_FIELD, Field.FRACTION, Field.FRACTION,
					fFieldStart, result.length(), result);

			
			fieldStart = result.length();

			result.append(symbols.getExponentialSymbol());

			delegate.formatted(Field.EXPONENT_SYMBOL, Field.EXPONENT_SYMBOL,
					fieldStart, result.length(), result);

			
			if (digitList.isZero())
				exponent = 0;

			boolean negativeExponent = exponent < 0;
			if (negativeExponent) {
				exponent = -exponent;
				append(result, negativePrefix, delegate,
						getNegativePrefixFieldPositions(), Field.EXPONENT_SIGN);
			} else {
				append(result, positivePrefix, delegate,
						getPositivePrefixFieldPositions(), Field.EXPONENT_SIGN);
			}
			digitList.set(exponent);

			int eFieldStart = result.length();

			for (int i = digitList.decimalAt; i < minExponentDigits; ++i)
				result.append(zero);
			for (int i = 0; i < digitList.decimalAt; ++i) {
				result
						.append((i < digitList.count) ? (char) (digitList.digits[i] + zeroDelta)
								: zero);
			}
			delegate.formatted(Field.EXPONENT, Field.EXPONENT, eFieldStart,
					result.length(), result);
			fieldStart = result.length();
			if (negativeExponent) {
				append(result, negativeSuffix, delegate,
						getNegativeSuffixFieldPositions(), Field.EXPONENT_SIGN);
			} else {
				append(result, positiveSuffix, delegate,
						getPositiveSuffixFieldPositions(), Field.EXPONENT_SIGN);
			}
		} else {
			int iFieldStart = result.length();

			
			int count = getMinimumIntegerDigits();
			int digitIndex = 0; // Index into digitList.fDigits[]
			if (digitList.decimalAt > 0 && count < digitList.decimalAt)
				count = digitList.decimalAt;

			

			if (count > getMaximumIntegerDigits()) {
				count = getMaximumIntegerDigits();
				digitIndex = digitList.decimalAt - count;
			}

			int sizeBeforeIntegerPart = result.length();
			for (int i = count - 1; i >= 0; --i) {
				if (i < digitList.decimalAt && digitIndex < digitList.count) {
					// Output a real digit
					result
							.append((char) (digitList.digits[digitIndex++] + zeroDelta));
				} else {
					// Output a leading zero
					result.append(zero);
				}

				
				if (isGroupingUsed() && i > 0 && (groupingSize != 0)
						&& (i % groupingSize == 0)) {
					int gStart = result.length();
					result.append(grouping);
					delegate.formatted(Field.GROUPING_SEPARATOR,
							Field.GROUPING_SEPARATOR, gStart, result.length(),
							result);
				}
			}

			
			boolean fractionPresent = (getMinimumFractionDigits() > 0)
					|| (!isInteger && digitIndex < digitList.count);

			
			if (!fractionPresent && result.length() == sizeBeforeIntegerPart) {
				result.append(zero);
			}

			delegate.formatted(INTEGER_FIELD, Field.INTEGER, Field.INTEGER,
					iFieldStart, result.length(), result);

			
			int sStart = result.length();
			if (decimalSeparatorAlwaysShown || fractionPresent)
				result.append(decimal);

			if (sStart != result.length()) {
				delegate.formatted(Field.DECIMAL_SEPARATOR,
						Field.DECIMAL_SEPARATOR, sStart, result.length(),
						result);
			}
			int fFieldStart = result.length();

			for (int i = 0; i < getMaximumFractionDigits(); ++i) {
				
				if (i >= getMinimumFractionDigits()
						&& (isInteger || digitIndex >= digitList.count))
					break;

				
				if (-1 - i > (digitList.decimalAt - 1)) {
					result.append(zero);
					continue;
				}

				
				if (!isInteger && digitIndex < digitList.count) {
					result
							.append((char) (digitList.digits[digitIndex++] + zeroDelta));
				} else {
					result.append(zero);
				}
			}

			
			delegate.formatted(FRACTION_FIELD, Field.FRACTION, Field.FRACTION,
					fFieldStart, result.length(), result);
		}

		if (isNegative) {
			append(result, negativeSuffix, delegate,
					getNegativeSuffixFieldPositions(), Field.SIGN);
		} else {
			append(result, positiveSuffix, delegate,
					getPositiveSuffixFieldPositions(), Field.SIGN);
		}

		return result;
	}

	
	private void append(StringBuffer result, String string,
			FieldDelegate delegate, FieldPosition[] positions,
			Format.Field signAttribute) {
		int start = result.length();

		if (string.length() > 0) {
			result.append(string);
			for (int counter = 0, max = positions.length; counter < max; counter++) {
				FieldPosition fp = positions[counter];
				Format.Field attribute = fp.getFieldAttribute();

				if (attribute == Field.SIGN) {
					attribute = signAttribute;
				}
				delegate.formatted(attribute, attribute, start
						+ fp.getBeginIndex(), start + fp.getEndIndex(), result);
			}
		}
	}

	
	private static final int STATUS_INFINITE = 0;
	private static final int STATUS_POSITIVE = 1;
	private static final int STATUS_LENGTH = 2;

	
	private final boolean subparse(String text, ParsePosition parsePosition,
			DigitList digits, boolean isExponent, boolean status[]) {
		int position = parsePosition.index;
		int oldStart = parsePosition.index;
		int backup;

		
		String str1 = text.substring(position, position
				+ positivePrefix.length());
		String str2 = positivePrefix.substring(0, 0 + positivePrefix.length());
		boolean gotPositive = str1.equalsIgnoreCase(str2);

		str1 = text.substring(position, position + negativePrefix.length());
		str2 = positivePrefix.substring(0, 0 + negativePrefix.length());
		boolean gotNegative = str1.equalsIgnoreCase(str2);

		if (gotPositive && gotNegative) {
			if (positivePrefix.length() > negativePrefix.length())
				gotNegative = false;
			else if (positivePrefix.length() < negativePrefix.length())
				gotPositive = false;
		}
		if (gotPositive) {
			position += positivePrefix.length();
		} else if (gotNegative) {
			position += negativePrefix.length();
		} else {
			parsePosition.errorIndex = position;
			return false;
		}
		// process digits or Inf, find decimal position
		status[STATUS_INFINITE] = false;
		str1 = text.substring(position, position
				+ symbols.getInfinity().length());
		str2 = positivePrefix.substring(0, 0 + symbols.getInfinity().length());
		boolean gotInfinity = str1.equalsIgnoreCase(str2);
		if (!isExponent && gotInfinity) {
			position += symbols.getInfinity().length();
			status[STATUS_INFINITE] = true;
		} else {
			

			digits.decimalAt = digits.count = 0;
			char zero = symbols.getZeroDigit();
			char decimal = isCurrencyFormat ? symbols
					.getMonetaryDecimalSeparator() : symbols
					.getDecimalSeparator();
			char grouping = symbols.getGroupingSeparator();
			char exponentChar = symbols.getExponentialSymbol();
			boolean sawDecimal = false;
			boolean sawExponent = false;
			boolean sawDigit = false;
			int exponent = 0; // Set to the exponent value, if any

			// We have to track digitCount ourselves, because digits.count will
			// pin when the maximum allowable digits is reached.
			int digitCount = 0;

			backup = -1;
			for (; position < text.length(); ++position) {
				char ch = text.charAt(position);

				
				int digit = ch - zero;
				if (digit < 0 || digit > 9)
					digit = Character.digit(ch, 10);

				if (digit == 0) {
					// Cancel out backup setting (see grouping handler below)
					backup = -1; // Do this BEFORE continue statement below!!!
					sawDigit = true;

					// Handle leading zeros
					if (digits.count == 0) {
						// Ignore leading zeros in integer part of number.
						if (!sawDecimal)
							continue;

						
						--digits.decimalAt;
					} else {
						++digitCount;
						digits.append((char) (digit + '0'));
					}
				} else if (digit > 0 && digit <= 9) // [sic] digit==0 handled
				// above
				{
					sawDigit = true;
					++digitCount;
					digits.append((char) (digit + '0'));

					// Cancel out backup setting (see grouping handler below)
					backup = -1;
				} else if (!isExponent && ch == decimal) {
					// If we're only parsing integers, or if we ALREADY saw the
					// decimal, then don't parse this one.
					if (isParseIntegerOnly() || sawDecimal)
						break;
					digits.decimalAt = digitCount; // Not digits.count!
					sawDecimal = true;
				} else if (!isExponent && ch == grouping && isGroupingUsed()) {
					if (sawDecimal) {
						break;
					}
					
					backup = position;
				} else if (!isExponent && ch == exponentChar && !sawExponent) {
					// Process the exponent by recursively calling this method.
					ParsePosition pos = new ParsePosition(position + 1);
					boolean[] stat = new boolean[STATUS_LENGTH];
					DigitList exponentDigits = new DigitList();

					if (subparse(text, pos, exponentDigits, true, stat)
							&& exponentDigits.fitsIntoLong(
									stat[STATUS_POSITIVE], true)) {
						position = pos.index; // Advance past the exponent
						exponent = (int) exponentDigits.getLong();
						if (!stat[STATUS_POSITIVE])
							exponent = -exponent;
						sawExponent = true;
					}
					break; // Whether we fail or succeed, we exit this loop
				} else
					break;
			}

			if (backup != -1)
				position = backup;

			// If there was no decimal point we have an integer
			if (!sawDecimal)
				digits.decimalAt = digitCount; // Not digits.count!

			// Adjust for exponent, if any
			digits.decimalAt += exponent;

			
			if (!sawDigit && digitCount == 0) {
				parsePosition.index = oldStart;
				parsePosition.errorIndex = oldStart;
				return false;
			}
		}

		// check for positiveSuffix
		if (gotPositive) {
			str1 = text.substring(position, position + positivePrefix.length());
			str2 = positivePrefix.substring(0, 0 + positivePrefix.length());
			gotPositive = str1.equalsIgnoreCase(str2);
		}
		if (gotNegative) {
			str1 = text.substring(position, position + negativeSuffix.length());
			str2 = positivePrefix.substring(0, 0 + negativeSuffix.length());
			gotPositive = str1.equalsIgnoreCase(str2);
		}

		// if both match, take longest
		if (gotPositive && gotNegative) {
			if (positiveSuffix.length() > negativeSuffix.length())
				gotNegative = false;
			else if (positiveSuffix.length() < negativeSuffix.length())
				gotPositive = false;
		}

		// fail if neither or both
		if (gotPositive == gotNegative) {
			parsePosition.errorIndex = position;
			return false;
		}

		parsePosition.index = position
				+ (gotPositive ? positiveSuffix.length() : negativeSuffix
						.length()); // mark success!

		status[STATUS_POSITIVE] = gotPositive;
		if (parsePosition.index == oldStart) {
			parsePosition.errorIndex = position;
			return false;
		}
		return true;
	}

	
	public DecimalFormatSymbols getDecimalFormatSymbols() {
		try {
			// don't allow multiple references
			return (DecimalFormatSymbols) symbols.clone();
		} catch (Exception foo) {
			return null; // should never happen
		}
	}

	
	public void setDecimalFormatSymbols(DecimalFormatSymbols newSymbols) {
		try {
			// don't allow multiple references
			symbols = (DecimalFormatSymbols) newSymbols.clone();
			expandAffixes();
		} catch (Exception foo) {
			// should never happen
		}
	}

	
	public String getPositivePrefix() {
		return positivePrefix;
	}

	
	public void setPositivePrefix(String newValue) {
		positivePrefix = newValue;
		posPrefixPattern = null;
		positivePrefixFieldPositions = null;
	}

	
	private FieldPosition[] getPositivePrefixFieldPositions() {
		if (positivePrefixFieldPositions == null) {
			if (posPrefixPattern != null) {
				positivePrefixFieldPositions = expandAffix(posPrefixPattern);
			} else {
				positivePrefixFieldPositions = EmptyFieldPositionArray;
			}
		}
		return positivePrefixFieldPositions;
	}

	
	public String getNegativePrefix() {
		return negativePrefix;
	}

	
	public void setNegativePrefix(String newValue) {
		negativePrefix = newValue;
		negPrefixPattern = null;
	}

	
	private FieldPosition[] getNegativePrefixFieldPositions() {
		if (negativePrefixFieldPositions == null) {
			if (negPrefixPattern != null) {
				negativePrefixFieldPositions = expandAffix(negPrefixPattern);
			} else {
				negativePrefixFieldPositions = EmptyFieldPositionArray;
			}
		}
		return negativePrefixFieldPositions;
	}

	
	public String getPositiveSuffix() {
		return positiveSuffix;
	}

	
	public void setPositiveSuffix(String newValue) {
		positiveSuffix = newValue;
		posSuffixPattern = null;
	}

	
	private FieldPosition[] getPositiveSuffixFieldPositions() {
		if (positiveSuffixFieldPositions == null) {
			if (posSuffixPattern != null) {
				positiveSuffixFieldPositions = expandAffix(posSuffixPattern);
			} else {
				positiveSuffixFieldPositions = EmptyFieldPositionArray;
			}
		}
		return positiveSuffixFieldPositions;
	}

	
	public String getNegativeSuffix() {
		return negativeSuffix;
	}

	
	public void setNegativeSuffix(String newValue) {
		negativeSuffix = newValue;
		negSuffixPattern = null;
	}

	
	private FieldPosition[] getNegativeSuffixFieldPositions() {
		if (negativeSuffixFieldPositions == null) {
			if (negSuffixPattern != null) {
				negativeSuffixFieldPositions = expandAffix(negSuffixPattern);
			} else {
				negativeSuffixFieldPositions = EmptyFieldPositionArray;
			}
		}
		return negativeSuffixFieldPositions;
	}

	
	public int getMultiplier() {
		return multiplier;
	}

	
	public void setMultiplier(int newValue) {
		multiplier = newValue;
	}

	
	public int getGroupingSize() {
		return groupingSize;
	}

	
	public void setGroupingSize(int newValue) {
		groupingSize = (byte) newValue;
	}

	
	public boolean isDecimalSeparatorAlwaysShown() {
		return decimalSeparatorAlwaysShown;
	}

	
	public void setDecimalSeparatorAlwaysShown(boolean newValue) {
		decimalSeparatorAlwaysShown = newValue;
	}

	
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (!super.equals(obj))
			return false; // super does class check
		DecimalFormat other = (DecimalFormat) obj;
		return ((posPrefixPattern == other.posPrefixPattern && positivePrefix
				.equals(other.positivePrefix)) || (posPrefixPattern != null && posPrefixPattern
				.equals(other.posPrefixPattern)))
				&& ((posSuffixPattern == other.posSuffixPattern && positiveSuffix
						.equals(other.positiveSuffix)) || (posSuffixPattern != null && posSuffixPattern
						.equals(other.posSuffixPattern)))
				&& ((negPrefixPattern == other.negPrefixPattern && negativePrefix
						.equals(other.negativePrefix)) || (negPrefixPattern != null && negPrefixPattern
						.equals(other.negPrefixPattern)))
				&& ((negSuffixPattern == other.negSuffixPattern && negativeSuffix
						.equals(other.negativeSuffix)) || (negSuffixPattern != null && negSuffixPattern
						.equals(other.negSuffixPattern)))
				&& multiplier == other.multiplier
				&& groupingSize == other.groupingSize
				&& decimalSeparatorAlwaysShown == other.decimalSeparatorAlwaysShown
				&& useExponentialNotation == other.useExponentialNotation
				&& (!useExponentialNotation || minExponentDigits == other.minExponentDigits)
				&& symbols.equals(other.symbols);
	}

	
	public int hashCode() {
		return super.hashCode() * 37 + positivePrefix.hashCode();
		// just enough fields for a reasonable distribution
	}

	
	public String toPattern() {
		return toPattern(false);
	}

	
	public String toLocalizedPattern() {
		return toPattern(true);
	}

	
	private void expandAffixes() {
		// Reuse one StringBuffer for better performance
		StringBuffer buffer = new StringBuffer();
		if (posPrefixPattern != null) {
			positivePrefix = expandAffix(posPrefixPattern, buffer);
			positivePrefixFieldPositions = null;
		}
		if (posSuffixPattern != null) {
			positiveSuffix = expandAffix(posSuffixPattern, buffer);
			positiveSuffixFieldPositions = null;
		}
		if (negPrefixPattern != null) {
			negativePrefix = expandAffix(negPrefixPattern, buffer);
			negativePrefixFieldPositions = null;
		}
		if (negSuffixPattern != null) {
			negativeSuffix = expandAffix(negSuffixPattern, buffer);
			negativeSuffixFieldPositions = null;
		}
	}

	
	private String expandAffix(String pattern, StringBuffer buffer) {
		buffer.setLength(0);
		for (int i = 0; i < pattern.length();) {
			char c = pattern.charAt(i++);
			if (c == QUOTE) {
				c = pattern.charAt(i++);
				switch (c) {
				case CURRENCY_SIGN:
					buffer.append(symbols.getCurrencySymbol());
					continue;
				case PATTERN_PERCENT:
					c = symbols.getPercent();
					break;
				case PATTERN_PER_MILLE:
					c = symbols.getPerMill();
					break;
				case PATTERN_MINUS:
					c = symbols.getMinusSign();
					break;
				}
			}
			buffer.append(c);
		}
		return buffer.toString();
	}

	
	private FieldPosition[] expandAffix(String pattern) {
		Vector positions = null;
		int stringIndex = 0;
		for (int i = 0; i < pattern.length();) {
			char c = pattern.charAt(i++);
			if (c == QUOTE) {
				int field = -1;
				Format.Field fieldID = null;
				c = pattern.charAt(i++);
				switch (c) {
				case CURRENCY_SIGN:
					String string = symbols.getCurrencySymbol();
					if (string.length() > 0) {
						if (positions == null) {
							positions = new Vector(2);
						}
						FieldPosition fp = new FieldPosition(Field.CURRENCY);
						fp.setBeginIndex(stringIndex);
						fp.setEndIndex(stringIndex + string.length());
						positions.addElement(fp);
						stringIndex += string.length();
					}
					continue;
				case PATTERN_PERCENT:
					c = symbols.getPercent();
					field = -1;
					fieldID = Field.PERCENT;
					break;
				case PATTERN_PER_MILLE:
					c = symbols.getPerMill();
					field = -1;
					fieldID = Field.PERMILLE;
					break;
				case PATTERN_MINUS:
					c = symbols.getMinusSign();
					field = -1;
					fieldID = Field.SIGN;
					break;
				}
				if (fieldID != null) {
					if (positions == null) {
						positions = new Vector(2);
					}
					FieldPosition fp = new FieldPosition(fieldID, field);
					fp.setBeginIndex(stringIndex);
					fp.setEndIndex(stringIndex + 1);
					positions.addElement(fp);
				}
			}
			stringIndex++;
		}
		if (positions != null) {
			EmptyFieldPositionArray = new FieldPosition[positions.size()];
			positions.copyInto(EmptyFieldPositionArray);
		}
		return EmptyFieldPositionArray;
	}

	
	private void appendAffix(StringBuffer buffer, String affixPattern,
			String expAffix, boolean localized) {
		if (affixPattern == null) {
			appendAffix(buffer, expAffix, localized);
		} else {
			int i;
			for (int pos = 0; pos < affixPattern.length(); pos = i) {
				i = affixPattern.indexOf(QUOTE, pos);
				if (i < 0) {
					appendAffix(buffer, affixPattern.substring(pos), localized);
					break;
				}
				if (i > pos) {
					appendAffix(buffer, affixPattern.substring(pos, i),
							localized);
				}
				char c = affixPattern.charAt(++i);
				++i;
				if (c == QUOTE) {
					buffer.append(c);
					// Fall through and append another QUOTE below
				} else if (c == CURRENCY_SIGN && i < affixPattern.length()
						&& affixPattern.charAt(i) == CURRENCY_SIGN) {
					++i;
					buffer.append(c);
					// Fall through and append another CURRENCY_SIGN below
				} else if (localized) {
					switch (c) {
					case PATTERN_PERCENT:
						c = symbols.getPercent();
						break;
					case PATTERN_PER_MILLE:
						c = symbols.getPerMill();
						break;
					case PATTERN_MINUS:
						c = symbols.getMinusSign();
						break;
					}
				}
				buffer.append(c);
			}
		}
	}

	
	private void appendAffix(StringBuffer buffer, String affix,
			boolean localized) {
		boolean needQuote;
		if (localized) {
			needQuote = affix.indexOf(symbols.getZeroDigit()) >= 0
					|| affix.indexOf(symbols.getGroupingSeparator()) >= 0
					|| affix.indexOf(symbols.getDecimalSeparator()) >= 0
					|| affix.indexOf(symbols.getPercent()) >= 0
					|| affix.indexOf(symbols.getPerMill()) >= 0
					|| affix.indexOf(symbols.getDigit()) >= 0
					|| affix.indexOf(symbols.getPatternSeparator()) >= 0
					|| affix.indexOf(symbols.getMinusSign()) >= 0
					|| affix.indexOf(CURRENCY_SIGN) >= 0;
		} else {
			needQuote = affix.indexOf(PATTERN_ZERO_DIGIT) >= 0
					|| affix.indexOf(PATTERN_GROUPING_SEPARATOR) >= 0
					|| affix.indexOf(PATTERN_DECIMAL_SEPARATOR) >= 0
					|| affix.indexOf(PATTERN_PERCENT) >= 0
					|| affix.indexOf(PATTERN_PER_MILLE) >= 0
					|| affix.indexOf(PATTERN_DIGIT) >= 0
					|| affix.indexOf(PATTERN_SEPARATOR) >= 0
					|| affix.indexOf(PATTERN_MINUS) >= 0
					|| affix.indexOf(CURRENCY_SIGN) >= 0;
		}
		if (needQuote)
			buffer.append('\'');
		if (affix.indexOf('\'') < 0)
			buffer.append(affix);
		else {
			for (int j = 0; j < affix.length(); ++j) {
				char c = affix.charAt(j);
				buffer.append(c);
				if (c == '\'')
					buffer.append(c);
			}
		}
		if (needQuote)
			buffer.append('\'');
	}

	/**
	 * Does the real work of generating a pattern.
	 */
	private String toPattern(boolean localized) {
		StringBuffer result = new StringBuffer();
		for (int j = 1; j >= 0; --j) {
			if (j == 1)
				appendAffix(result, posPrefixPattern, positivePrefix, localized);
			else
				appendAffix(result, negPrefixPattern, negativePrefix, localized);
			int i;
			int digitCount = useExponentialNotation ? getMaximumIntegerDigits()
					: Math.max(groupingSize, getMinimumIntegerDigits()) + 1;
			for (i = digitCount; i > 0; --i) {
				if (i != digitCount && isGroupingUsed() && groupingSize != 0
						&& i % groupingSize == 0) {
					result.append(localized ? symbols.getGroupingSeparator()
							: PATTERN_GROUPING_SEPARATOR);
				}
				result
						.append(i <= getMinimumIntegerDigits() ? (localized ? symbols
								.getZeroDigit()
								: PATTERN_ZERO_DIGIT)
								: (localized ? symbols.getDigit()
										: PATTERN_DIGIT));
			}
			if (getMaximumFractionDigits() > 0 || decimalSeparatorAlwaysShown)
				result.append(localized ? symbols.getDecimalSeparator()
						: PATTERN_DECIMAL_SEPARATOR);
			for (i = 0; i < getMaximumFractionDigits(); ++i) {
				if (i < getMinimumFractionDigits()) {
					result.append(localized ? symbols.getZeroDigit()
							: PATTERN_ZERO_DIGIT);
				} else {
					result.append(localized ? symbols.getDigit()
							: PATTERN_DIGIT);
				}
			}
			if (useExponentialNotation) {
				result.append(localized ? symbols.getExponentialSymbol()
						: PATTERN_EXPONENT);
				for (i = 0; i < minExponentDigits; ++i)
					result.append(localized ? symbols.getZeroDigit()
							: PATTERN_ZERO_DIGIT);
			}
			if (j == 1) {
				appendAffix(result, posSuffixPattern, positiveSuffix, localized);
				if ((negSuffixPattern == posSuffixPattern && // n == p == null
						negativeSuffix.equals(positiveSuffix))
						|| (negSuffixPattern != null && negSuffixPattern
								.equals(posSuffixPattern))) {
					if ((negPrefixPattern != null && posPrefixPattern != null && negPrefixPattern
							.equals("'-" + posPrefixPattern))
							|| (negPrefixPattern == posPrefixPattern && // n ==
							// p ==
							// null
							negativePrefix.equals(symbols.getMinusSign()
									+ positivePrefix)))
						break;
				}
				result.append(localized ? symbols.getPatternSeparator()
						: PATTERN_SEPARATOR);
			} else
				appendAffix(result, negSuffixPattern, negativeSuffix, localized);
		}
		return result.toString();
	}

	
	public void applyPattern(String pattern) {
		applyPattern(pattern, false);
	}

	
	public void applyLocalizedPattern(String pattern) {
		applyPattern(pattern, true);
	}

	/**
	 * Does the real work of applying a pattern.
	 */
	private void applyPattern(String pattern, boolean localized) {
		char zeroDigit = PATTERN_ZERO_DIGIT;
		char groupingSeparator = PATTERN_GROUPING_SEPARATOR;
		char decimalSeparator = PATTERN_DECIMAL_SEPARATOR;
		char percent = PATTERN_PERCENT;
		char perMill = PATTERN_PER_MILLE;
		char digit = PATTERN_DIGIT;
		char separator = PATTERN_SEPARATOR;
		char exponent = PATTERN_EXPONENT;
		char minus = PATTERN_MINUS;
		if (localized) {
			zeroDigit = symbols.getZeroDigit();
			groupingSeparator = symbols.getGroupingSeparator();
			decimalSeparator = symbols.getDecimalSeparator();
			percent = symbols.getPercent();
			perMill = symbols.getPerMill();
			digit = symbols.getDigit();
			separator = symbols.getPatternSeparator();
			exponent = symbols.getExponentialSymbol();
			minus = symbols.getMinusSign();
		}
		boolean gotNegative = false;

		decimalSeparatorAlwaysShown = false;
		isCurrencyFormat = false;
		useExponentialNotation = false;

		int phaseOneStart = 0;
		int phaseOneLength = 0;
		

		int start = 0;
		for (int j = 1; j >= 0 && start < pattern.length(); --j) {
			boolean inQuote = false;
			StringBuffer prefix = new StringBuffer();
			StringBuffer suffix = new StringBuffer();
			int decimalPos = -1;
			int multiplier = 1;
			int digitLeftCount = 0, zeroDigitCount = 0, digitRightCount = 0;
			byte groupingCount = -1;

			int phase = 0;

			// The affix is either the prefix or the suffix.
			StringBuffer affix = prefix;

			for (int pos = start; pos < pattern.length(); ++pos) {
				char ch = pattern.charAt(pos);
				switch (phase) {
				case 0:
				case 2:
					if (inQuote) {
						
						if (ch == QUOTE) {
							if ((pos + 1) < pattern.length()
									&& pattern.charAt(pos + 1) == QUOTE) {
								++pos;
								affix.append("''"); // 'don''t'
							} else {
								inQuote = false; // 'do'
							}
							continue;
						}
					} else {
						
						if (ch == digit || ch == zeroDigit
								|| ch == groupingSeparator
								|| ch == decimalSeparator) {
							
							phase = 1;
							if (j == 1)
								phaseOneStart = pos;
							--pos; // Reprocess this character
							continue;
						} else if (ch == CURRENCY_SIGN) {
							
							boolean doubled = (pos + 1) < pattern.length()
									&& pattern.charAt(pos + 1) == CURRENCY_SIGN;
							if (doubled)
								++pos; // Skip over the doubled character
							isCurrencyFormat = true;
							affix.append(doubled ? "'\u00A4\u00A4" : "'\u00A4");
							continue;
						} else if (ch == QUOTE) {
							
							if (ch == QUOTE) {
								if ((pos + 1) < pattern.length()
										&& pattern.charAt(pos + 1) == QUOTE) {
									++pos;
									affix.append("''"); // o''clock
								} else {
									inQuote = true; // 'do'
								}
								continue;
							}
						} else if (ch == separator) {
							
							if (phase == 0 || j == 0)
								throw new IllegalArgumentException(
										"Unquoted special character '" + ch
												+ "' in pattern \"" + pattern
												+ '"');
							start = pos + 1;
							pos = pattern.length();
							continue;
						}

						// Next handle characters which are appended directly.
						else if (ch == percent) {
							if (multiplier != 1)
								throw new IllegalArgumentException(
										"Too many percent/permille characters in pattern \""
												+ pattern + '"');
							multiplier = 100;
							affix.append("'%");
							continue;
						} else if (ch == perMill) {
							if (multiplier != 1)
								throw new IllegalArgumentException(
										"Too many percent/permille characters in pattern \""
												+ pattern + '"');
							multiplier = 1000;
							affix.append("'\u2030");
							continue;
						} else if (ch == minus) {
							affix.append("'-");
							continue;
						}
					}
					
					affix.append(ch);
					break;
				case 1:
					
					if (j == 1)
						++phaseOneLength;
					else {
						
						if (--phaseOneLength == 0) {
							phase = 2;
							affix = suffix;
						}
						continue;
					}

					
					if (ch == digit) {
						if (zeroDigitCount > 0)
							++digitRightCount;
						else
							++digitLeftCount;
						if (groupingCount >= 0 && decimalPos < 0)
							++groupingCount;
					} else if (ch == zeroDigit) {
						if (digitRightCount > 0)
							throw new IllegalArgumentException(
									"Unexpected '0' in pattern \"" + pattern
											+ '"');
						++zeroDigitCount;
						if (groupingCount >= 0 && decimalPos < 0)
							++groupingCount;
					} else if (ch == groupingSeparator) {
						groupingCount = 0;
					} else if (ch == decimalSeparator) {
						if (decimalPos >= 0)
							throw new IllegalArgumentException(
									"Multiple decimal separators in pattern \""
											+ pattern + '"');
						decimalPos = digitLeftCount + zeroDigitCount
								+ digitRightCount;
					} else if (ch == exponent) {
						if (useExponentialNotation)
							throw new IllegalArgumentException(
									"Multiple exponential "
											+ "symbols in pattern \"" + pattern
											+ '"');
						useExponentialNotation = true;
						minExponentDigits = 0;

						
						while (++pos < pattern.length()
								&& pattern.charAt(pos) == zeroDigit) {
							++minExponentDigits;
							++phaseOneLength;
						}

						if ((digitLeftCount + zeroDigitCount) < 1
								|| minExponentDigits < 1)
							throw new IllegalArgumentException(
									"Malformed exponential " + "pattern \""
											+ pattern + '"');

						// Transition to phase 2
						phase = 2;
						affix = suffix;
						--pos;
						continue;
					} else {
						phase = 2;
						affix = suffix;
						--pos;
						--phaseOneLength;
						continue;
					}
					break;
				}
			}
			
			if (zeroDigitCount == 0 && digitLeftCount > 0 && decimalPos >= 0) {
				// Handle "###.###" and "###." and ".###"
				int n = decimalPos;
				if (n == 0)
					++n; // Handle ".###"
				digitRightCount = digitLeftCount - n;
				digitLeftCount = n - 1;
				zeroDigitCount = 1;
			}

			// Do syntax checking on the digits.
			if ((decimalPos < 0 && digitRightCount > 0)
					|| (decimalPos >= 0 && (decimalPos < digitLeftCount || decimalPos > (digitLeftCount + zeroDigitCount)))
					|| groupingCount == 0 || inQuote)
				throw new IllegalArgumentException("Malformed pattern \""
						+ pattern + '"');

			if (j == 1) {
				posPrefixPattern = prefix.toString();
				posSuffixPattern = suffix.toString();
				negPrefixPattern = posPrefixPattern; // assume these for now
				negSuffixPattern = posSuffixPattern;
				int digitTotalCount = digitLeftCount + zeroDigitCount
						+ digitRightCount;
				
				int effectiveDecimalPos = decimalPos >= 0 ? decimalPos
						: digitTotalCount;
				setMinimumIntegerDigits(effectiveDecimalPos - digitLeftCount);
				setMaximumIntegerDigits(useExponentialNotation ? digitLeftCount
						+ getMinimumIntegerDigits() : DOUBLE_INTEGER_DIGITS);
				setMaximumFractionDigits(decimalPos >= 0 ? (digitTotalCount - decimalPos)
						: 0);
				setMinimumFractionDigits(decimalPos >= 0 ? (digitLeftCount
						+ zeroDigitCount - decimalPos) : 0);
				setGroupingUsed(groupingCount > 0);
				this.groupingSize = (groupingCount > 0) ? groupingCount : 0;
				this.multiplier = multiplier;
				setDecimalSeparatorAlwaysShown(decimalPos == 0
						|| decimalPos == digitTotalCount);
			} else {
				negPrefixPattern = prefix.toString();
				negSuffixPattern = suffix.toString();
				gotNegative = true;
			}
		}

		if (pattern.length() == 0) {
			posPrefixPattern = posSuffixPattern = "";
			setMinimumIntegerDigits(0);
			setMaximumIntegerDigits(DOUBLE_INTEGER_DIGITS);
			setMinimumFractionDigits(0);
			setMaximumFractionDigits(DOUBLE_FRACTION_DIGITS);
		}

		
		if (!gotNegative
				|| (negPrefixPattern.equals(posPrefixPattern) && negSuffixPattern
						.equals(posSuffixPattern))) {
			negSuffixPattern = posSuffixPattern;
			negPrefixPattern = "'-" + posPrefixPattern;
		}

		expandAffixes();
	}

	
	public void setMaximumIntegerDigits(int newValue) {
		super
				.setMaximumIntegerDigits(Math.min(newValue,
						DOUBLE_INTEGER_DIGITS));
	}

	
	public void setMinimumIntegerDigits(int newValue) {
		super
				.setMinimumIntegerDigits(Math.min(newValue,
						DOUBLE_INTEGER_DIGITS));
	}

	
	public void setMaximumFractionDigits(int newValue) {
		super.setMaximumFractionDigits(Math.min(newValue,
				DOUBLE_FRACTION_DIGITS));
	}

	
	public void setMinimumFractionDigits(int newValue) {
		super.setMinimumFractionDigits(Math.min(newValue,
				DOUBLE_FRACTION_DIGITS));
	}




	private transient DigitList digitList = new DigitList();

	private String positivePrefix = "";

	
	private String positiveSuffix = "";

	
	private String negativePrefix = "-";

	
	private String negativeSuffix = "";

	
	private String posPrefixPattern;

	
	private String posSuffixPattern;

	
	private String negPrefixPattern;

	
	private String negSuffixPattern;

	
	private int multiplier = 1;

	
	private byte groupingSize = 3; // invariant, > 0 if useThousands

	
	private boolean decimalSeparatorAlwaysShown = false;

	
	private transient boolean isCurrencyFormat = false;

	
	private DecimalFormatSymbols symbols = null; // LIU new
	
	private boolean useExponentialNotation; // Newly persistent in the Java 2
	
	private transient FieldPosition[] positivePrefixFieldPositions;

	private transient FieldPosition[] positiveSuffixFieldPositions;

	
	private transient FieldPosition[] negativePrefixFieldPositions;

	
	private transient FieldPosition[] negativeSuffixFieldPositions;

	
	private byte minExponentDigits; // Newly persistent in the Java 2 platform

	
	static final int currentSerialVersion = 2;

	
	private int serialVersionOnStream = currentSerialVersion;

	private static final char PATTERN_ZERO_DIGIT = '0';
	private static final char PATTERN_GROUPING_SEPARATOR = ',';
	private static final char PATTERN_DECIMAL_SEPARATOR = '.';
	private static final char PATTERN_PER_MILLE = '\u2030';
	private static final char PATTERN_PERCENT = '%';
	private static final char PATTERN_DIGIT = '#';
	private static final char PATTERN_SEPARATOR = ';';
	private static final char PATTERN_EXPONENT = 'E';
	private static final char PATTERN_MINUS = '-';

	private static final char CURRENCY_SIGN = '\u00A4';

	private static final char QUOTE = '\'';

	private static FieldPosition[] EmptyFieldPositionArray = new FieldPosition[0];

	// Upper limit on integer and fraction digits for a Java double
	static final int DOUBLE_INTEGER_DIGITS = 309;
	static final int DOUBLE_FRACTION_DIGITS = 340;

}
