        
        package javame.text;


        
        public abstract class Format{

            private static final long serialVersionUID = -299282585814624189L;

           
            public final String format(Object obj) {
                return format(obj, new StringBuffer(), new FieldPosition(0))
                        .toString();
            }

            
            public abstract StringBuffer format(Object obj,
                    StringBuffer toAppendTo, FieldPosition pos);

            
            public AttributedCharacterIterator formatToCharacterIterator(
                    Object obj) {
                return createAttributedCharacterIterator(format(obj));
            }

            
            AttributedCharacterIterator createAttributedCharacterIterator(
                    String s) {
                AttributedString as = new AttributedString(s);

                return as.getIterator();
            }

           
            AttributedCharacterIterator createAttributedCharacterIterator(
                    AttributedCharacterIterator[] iterators) {
                AttributedString as = new AttributedString(iterators);

                return as.getIterator();
            }

            
            AttributedCharacterIterator createAttributedCharacterIterator(
                    String string, Attribute key,
                    Object value) {
                AttributedString as = new AttributedString(string);

                as.addAttribute(key, value);
                return as.getIterator();
            }

           
            AttributedCharacterIterator createAttributedCharacterIterator(
                    AttributedCharacterIterator iterator,
                    Attribute key, Object value) {
                AttributedString as = new AttributedString(iterator);

                as.addAttribute(key, value);
                return as.getIterator();
            }

          
            public static class Field extends
                    Attribute {
                /**
                 * Creates a Field with the specified name.
                 *
                 * @param name Name of the attribute
                 */
                protected Field(String name) {
                    super (name);
                }
            }

           
            interface FieldDelegate {
              
                public void formatted(Format.Field attr, Object value,
                        int start, int end, StringBuffer buffer);

              
                public void formatted(int fieldID, Format.Field attr,
                        Object value, int start, int end, StringBuffer buffer);
            }
        }
