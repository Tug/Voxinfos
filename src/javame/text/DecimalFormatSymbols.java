      

        package javame.text;

        
        

        final public class DecimalFormatSymbols {

            public DecimalFormatSymbols() {
            }


            /**
             * Gets the character used for zero. Different for Arabic, etc.
             */
            public char getZeroDigit() {
                return zeroDigit;
            }

            /**
             * Sets the character used for zero. Different for Arabic, etc.
             */
            public void setZeroDigit(char zeroDigit) {
                this .zeroDigit = zeroDigit;
            }

            /**
             * Gets the character used for thousands separator. Different for French, etc.
             */
            public char getGroupingSeparator() {
                return groupingSeparator;
            }

            /**
             * Sets the character used for thousands separator. Different for French, etc.
             */
            public void setGroupingSeparator(char groupingSeparator) {
                this .groupingSeparator = groupingSeparator;
            }

            /**
             * Gets the character used for decimal sign. Different for French, etc.
             */
            public char getDecimalSeparator() {
                return decimalSeparator;
            }

            /**
             * Sets the character used for decimal sign. Different for French, etc.
             */
            public void setDecimalSeparator(char decimalSeparator) {
                this .decimalSeparator = decimalSeparator;
            }

            /**
             * Gets the character used for mille percent sign. Different for Arabic, etc.
             */
            public char getPerMill() {
                return perMill;
            }

            /**
             * Sets the character used for mille percent sign. Different for Arabic, etc.
             */
            public void setPerMill(char perMill) {
                this .perMill = perMill;
            }

            /**
             * Gets the character used for percent sign. Different for Arabic, etc.
             */
            public char getPercent() {
                return percent;
            }

            /**
             * Sets the character used for percent sign. Different for Arabic, etc.
             */
            public void setPercent(char percent) {
                this .percent = percent;
            }

            /**
             * Gets the character used for a digit in a pattern.
             */
            public char getDigit() {
                return digit;
            }

            /**
             * Sets the character used for a digit in a pattern.
             */
            public void setDigit(char digit) {
                this .digit = digit;
            }

            /**
             * Gets the character used to separate positive and negative subpatterns
             * in a pattern.
             */
            public char getPatternSeparator() {
                return patternSeparator;
            }

            /**
             * Sets the character used to separate positive and negative subpatterns
             * in a pattern.
             */
            public void setPatternSeparator(char patternSeparator) {
                this .patternSeparator = patternSeparator;
            }

            /**
             * Gets the string used to represent infinity. Almost always left
             * unchanged.
             */
            public String getInfinity() {
                return infinity;
            }

            /**
             * Sets the string used to represent infinity. Almost always left
             * unchanged.
             */
            public void setInfinity(String infinity) {
                this .infinity = infinity;
            }

            /**
             * Gets the string used to represent "not a number". Almost always left
             * unchanged.
             */
            public String getNaN() {
                return NaN;
            }

            /**
             * Sets the string used to represent "not a number". Almost always left
             * unchanged.
             */
            public void setNaN(String NaN) {
                this .NaN = NaN;
            }

            /**
             * Gets the character used to represent minus sign. If no explicit
             * negative format is specified, one is formed by prefixing
             * minusSign to the positive format.
             */
            public char getMinusSign() {
                return minusSign;
            }

            /**
             * Sets the character used to represent minus sign. If no explicit
             * negative format is specified, one is formed by prefixing
             * minusSign to the positive format.
             */
            public void setMinusSign(char minusSign) {
                this .minusSign = minusSign;
            }

            /**
             * Returns the currency symbol for the currency of these
             * DecimalFormatSymbols in their locale.
             * @since 1.2
             */
            public String getCurrencySymbol() {
                return currencySymbol;
            }

            /**
             * Sets the currency symbol for the currency of these
             * DecimalFormatSymbols in their locale.
             * @since 1.2
             */
            public void setCurrencySymbol(String currency) {
                currencySymbol = currency;
            }


            /**
             * Returns the monetary decimal separator.
             * @since 1.2
             */
            public char getMonetaryDecimalSeparator() {
                return monetarySeparator;
            }

            /**
             * Sets the monetary decimal separator.
             * @since 1.2
             */
            public void setMonetaryDecimalSeparator(char sep) {
                monetarySeparator = sep;
            }

            //------------------------------------------------------------
            // BEGIN   Package Private methods ... to be made public later
            //------------------------------------------------------------

            /**
             * Returns the character used to separate the mantissa from the exponent.
             */
            char getExponentialSymbol() {
                return exponential;
            }

            /**
             * Sets the character used to separate the mantissa from the exponent.
             */
            void setExponentialSymbol(char exp) {
                exponential = exp;
            }

            //------------------------------------------------------------
            // END     Package Private methods ... to be made public later
            //------------------------------------------------------------


            /**
             * Override equals.
             */
            public boolean equals(Object obj) {
                if (obj == null)
                    return false;
                if (this  == obj)
                    return true;
                if (getClass() != obj.getClass())
                    return false;
                DecimalFormatSymbols other = (DecimalFormatSymbols) obj;
                return (zeroDigit == other.zeroDigit
                        && groupingSeparator == other.groupingSeparator
                        && decimalSeparator == other.decimalSeparator
                        && percent == other.percent && perMill == other.perMill
                        && digit == other.digit && minusSign == other.minusSign
                        && patternSeparator == other.patternSeparator
                        && infinity.equals(other.infinity)
                        && NaN.equals(other.NaN)
                        && currencySymbol.equals(other.currencySymbol)
                        && monetarySeparator == other.monetarySeparator);
            }

            /**
             * Override hashCode.
             */
            public int hashCode() {
                int result = zeroDigit;
                result = result * 37 + groupingSeparator;
                result = result * 37 + decimalSeparator;
                return result;
            }

 

            /**
             * Character used for zero.
             *
             * @serial
             * @see #getZeroDigit
             */
            private char zeroDigit = '0';

            /**
             * Character used for thousands separator.
             *
             * @serial
             * @see #getGroupingSeparator
             */
            private char groupingSeparator = ',';

            /**
             * Character used for decimal sign.
             *
             * @serial
             * @see #getDecimalSeparator
             */
            private char decimalSeparator = '.';

            /**
             * Character used for mille percent sign.
             *
             * @serial
             * @see #getPerMill
             */
            private char perMill = '\u2030';

            /**
             * Character used for percent sign.
             * @serial
             * @see #getPercent
             */
            private char percent = '%';

            /**
             * Character used for a digit in a pattern.
             *
             * @serial
             * @see #getDigit
             */
            private char digit = '#';

            /**
             * Character used to separate positive and negative subpatterns
             * in a pattern.
             *
             * @serial
             * @see #getPatternSeparator
             */
            private char patternSeparator = 160;

            /**
             * String used to represent infinity.
             * @serial
             * @see #getInfinity
             */
            private String infinity = "?";

            /**
             * String used to represent "not a number".
             * @serial
             * @see #getNaN
             */
            private String NaN = "NaN";

            /**
             * Character used to represent minus sign.
             * @serial
             * @see #getMinusSign
             */
            private char minusSign = '-';

            /**
             * String denoting the local currency, e.g. "$".
             * @serial
             * @see #getCurrencySymbol
             */
            private String currencySymbol = "$";


            /**
             * The decimal separator used when formatting currency values.
             * @serial
             * @since JDK 1.1.6
             * @see #getMonetaryDecimalSeparator
             */
            private char monetarySeparator = ','; // Field new in JDK 1.1.6

           
            private char exponential = 'E'; // Field new in JDK 1.1.6

			public DecimalFormatSymbols clone() {
				DecimalFormatSymbols result = new DecimalFormatSymbols();
				result.currencySymbol = currencySymbol;
				result.decimalSeparator = decimalSeparator;
				result.digit = digit;
				result.exponential = exponential;
				result.groupingSeparator = groupingSeparator;
				result.infinity = infinity;
				result.minusSign = minusSign;
				result.monetarySeparator = monetarySeparator;
				result.NaN = NaN;
				result.patternSeparator = patternSeparator;
				result.percent = percent;
				result.perMill = perMill;
				result.zeroDigit = zeroDigit;				
				return result;
			}
        }
