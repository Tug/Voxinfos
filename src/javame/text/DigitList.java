       
        package javame.text;


        
        final class DigitList {
           
            public static final int MAX_COUNT = 19; // == Long.toString(Long.MAX_VALUE).length()
            public static final int DBL_DIG = 17;

           
            public int decimalAt = 0;
            public int count = 0;
            public char[] digits = new char[MAX_COUNT];

            /**
             * Return true if the represented number is zero.
             */
            boolean isZero() {
                for (int i = 0; i < count; ++i)
                    if (digits[i] != '0')
                        return false;
                return true;
            }

           
            public void clear() {
                decimalAt = 0;
                count = 0;
            }

           
            public void append(char digit) {
                if (count < MAX_COUNT)
                    digits[count++] = digit;
            }

          
            public final double getDouble() {
                if (count == 0)
                    return 0.0;
                StringBuffer temp = getStringBuffer();
                temp.append('.').append(digits, 0, count);
                temp.append('E');
                temp.append(decimalAt);
                return Double.parseDouble(temp.toString());
            }

           
            public final long getLong() {
                // for now, simple implementation; later, do proper IEEE native stuff

                if (count == 0)
                    return 0;

               
                if (isLongMIN_VALUE())
                    return Long.MIN_VALUE;

                StringBuffer temp = getStringBuffer();
                temp.append(digits, 0, count);
                for (int i = count; i < decimalAt; ++i) {
                    temp.append('0');
                }
                return Long.parseLong(temp.toString());
            }

         
            boolean fitsIntoLong(boolean isPositive, boolean ignoreNegativeZero) {
                while (count > 0 && digits[count - 1] == '0')
                    --count;

                if (count == 0) {
                    // Positive zero fits into a long, but negative zero can only
                    // be represented as a double. - bug 4162852
                    return isPositive || ignoreNegativeZero;
                }

                if (decimalAt < count || decimalAt > MAX_COUNT)
                    return false;

                if (decimalAt < MAX_COUNT)
                    return true;

              
                for (int i = 0; i < count; ++i) {
                    char dig = digits[i], max = LONG_MIN_REP[i];
                    if (dig > max)
                        return false;
                    if (dig < max)
                        return true;
                }

     
                if (count < decimalAt)
                    return true;

                return !isPositive;
            }

          
            public final void set(double source, int maximumFractionDigits) {
                set(source, maximumFractionDigits, true);
            }

           
            final void set(double source, int maximumDigits, boolean fixedPoint) {
                if (source == 0)
                    source = 0;
                // Generate a representation of the form DDDDD, DDDDD.DDDDD, or
                // DDDDDE+/-DDDDD.
                char[] rep = Double.toString(source).toCharArray();

                decimalAt = -1;
                count = 0;
                int exponent = 0;
                // Number of zeros between decimal point and first non-zero digit after
                // decimal point, for numbers < 1.
                int leadingZerosAfterDecimal = 0;
                boolean nonZeroDigitSeen = false;

                for (int i = 0; i < rep.length;) {
                    char c = rep[i++];
                    if (c == '.') {
                        decimalAt = count;
                    } else if (c == 'e' || c == 'E') {
                        exponent = parseInt(rep, i);
                        break;
                    } else if (count < MAX_COUNT) {
                        if (!nonZeroDigitSeen) {
                            nonZeroDigitSeen = (c != '0');
                            if (!nonZeroDigitSeen && decimalAt != -1)
                                ++leadingZerosAfterDecimal;
                        }
                        if (nonZeroDigitSeen)
                            digits[count++] = c;
                    }
                }
                if (decimalAt == -1)
                    decimalAt = count;
                if (nonZeroDigitSeen) {
                    decimalAt += exponent - leadingZerosAfterDecimal;
                }

                if (fixedPoint) {
                  
                    if (-decimalAt > maximumDigits) {
                        // Handle an underflow to zero when we round something like
                        // 0.0009 to 2 fractional digits.
                        count = 0;
                        return;
                    } else if (-decimalAt == maximumDigits) {
                        // If we round 0.0009 to 3 fractional digits, then we have to
                        // create a new one digit in the least significant location.
                        if (shouldRoundUp(0)) {
                            count = 1;
                            ++decimalAt;
                            digits[0] = '1';
                        } else {
                            count = 0;
                        }
                        return;
                    }
                    // else fall through
                }

                // Eliminate trailing zeros.
                while (count > 1 && digits[count - 1] == '0')
                    --count;

                // Eliminate digits beyond maximum digits to be displayed.
                // Round up if appropriate.
                round(fixedPoint ? (maximumDigits + decimalAt) : maximumDigits);
            }

           
            private final void round(int maximumDigits) {
                // Eliminate digits beyond maximum digits to be displayed.
                // Round up if appropriate.
                if (maximumDigits >= 0 && maximumDigits < count) {
                    if (shouldRoundUp(maximumDigits)) {
                        // Rounding up involved incrementing digits from LSD to MSD.
                        // In most cases this is simple, but in a worst case situation
                        // (9999..99) we have to adjust the decimalAt value.
                        for (;;) {
                            --maximumDigits;
                            if (maximumDigits < 0) {
                                // We have all 9's, so we increment to a single digit
                                // of one and adjust the exponent.
                                digits[0] = '1';
                                ++decimalAt;
                                maximumDigits = 0; // Adjust the count
                                break;
                            }

                            ++digits[maximumDigits];
                            if (digits[maximumDigits] <= '9')
                                break;
                            // digits[maximumDigits] = '0'; // Unnecessary since we'll truncate this
                        }
                        ++maximumDigits; // Increment for use as count
                    }
                    count = maximumDigits;

                    // Eliminate trailing zeros.
                    while (count > 1 && digits[count - 1] == '0') {
                        --count;
                    }
                }
            }

           
            private boolean shouldRoundUp(int maximumDigits) {
                boolean increment = false;
                // Implement IEEE half-even rounding
                if (maximumDigits < count) {
                    if (digits[maximumDigits] > '5') {
                        return true;
                    } else if (digits[maximumDigits] == '5') {
                        for (int i = maximumDigits + 1; i < count; ++i) {
                            if (digits[i] != '0') {
                                return true;
                            }
                        }
                        return maximumDigits > 0
                                && (digits[maximumDigits - 1] % 2 != 0);
                    }
                }
                return false;
            }

            /**
             * Utility routine to set the value of the digit list from a long
             */
            public final void set(long source) {
                set(source, 0);
            }

           
            public final void set(long source, int maximumDigits) {
             
                if (source <= 0) {
                    if (source == Long.MIN_VALUE) {
                        decimalAt = count = MAX_COUNT;
                        System.arraycopy(LONG_MIN_REP, 0, digits, 0, count);
                    } else {
                        decimalAt = count = 0; // Values <= 0 format as zero
                    }
                } else {
                    // Rewritten to improve performance.  I used to call
                    // Long.toString(), which was about 4x slower than this code.
                    int left = MAX_COUNT;
                    int right;
                    while (source > 0) {
                        digits[--left] = (char) ('0' + (source % 10));
                        source /= 10;
                    }
                    decimalAt = MAX_COUNT - left;
                    // Don't copy trailing zeros.  We are guaranteed that there is at
                    // least one non-zero digit, so we don't have to check lower bounds.
                    for (right = MAX_COUNT - 1; digits[right] == '0'; --right)
                        ;
                    count = right - left + 1;
                    System.arraycopy(digits, left, digits, 0, count);
                }
                if (maximumDigits > 0)
                    round(maximumDigits);
            }

            /**
             * equality test between two digit lists.
             */
            public boolean equals(Object obj) {
                if (this  == obj) // quick check
                    return true;
                if (!(obj instanceof  DigitList)) // (1) same object?
                    return false;
                DigitList other = (DigitList) obj;
                if (count != other.count || decimalAt != other.decimalAt)
                    return false;
                for (int i = 0; i < count; i++)
                    if (digits[i] != other.digits[i])
                        return false;
                return true;
            }

            /**
             * Generates the hash code for the digit list.
             */
            public int hashCode() {
                int hashcode = decimalAt;

                for (int i = 0; i < count; i++)
                    hashcode = hashcode * 37 + digits[i];

                return hashcode;
            }

            /**
             * Returns true if this DigitList represents Long.MIN_VALUE;
             * false, otherwise.  This is required so that getLong() works.
             */
            private boolean isLongMIN_VALUE() {
                if (decimalAt != count || count != MAX_COUNT)
                    return false;

                for (int i = 0; i < count; ++i) {
                    if (digits[i] != LONG_MIN_REP[i])
                        return false;
                }

                return true;
            }

            private static final int parseInt(char[] str, int offset) {
                char c;
                boolean positive = true;
                if ((c = str[offset]) == '-') {
                    positive = false;
                    offset++;
                } else if (c == '+') {
                    offset++;
                }

                int value = 0;
                while (offset < str.length) {
                    c = str[offset++];
                    if (c >= '0' && c <= '9') {
                        value = value * 10 + (c - '0');
                    } else {
                        break;
                    }
                }
                return positive ? value : -value;
            }

            // The digit part of -9223372036854775808L
            private static final char[] LONG_MIN_REP = "9223372036854775808"
                    .toCharArray();

            public String toString() {
                if (isZero())
                    return "0";
                StringBuffer buf = getStringBuffer();
                buf.append("0.").append(digits, 0, count);
                buf.append("x10^");
                buf.append(decimalAt);
                return buf.toString();
            }

            private StringBuffer tempBuffer;

            private StringBuffer getStringBuffer() {
                if (tempBuffer == null) {
                    tempBuffer = new StringBuffer(MAX_COUNT);
                } else {
                    tempBuffer.setLength(0);
                }
                return tempBuffer;
            }
        }
