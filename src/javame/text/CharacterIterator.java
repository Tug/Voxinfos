

        package javame.text;

       

        public interface CharacterIterator{

           
            public static final char DONE = '\uFFFF';

          
            public char first();

            
            public char last();

          
            public char current();

           
            public char next();

           
            public char previous();

          
            public char setIndex(int position);

           
            public int getBeginIndex();

            public int getEndIndex();

            
            public int getIndex();
        }
