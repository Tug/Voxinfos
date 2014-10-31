package util;

import java.util.Hashtable;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

/**
 * Easy J2ME Internationalization
 * Copyright (C) Florian Hülsmann 2010 <flohuels@gmail.com>
 * Licensed under GNU LGPL v. 3.0
 * 
 * HOW TO USE:
 * 1. copy this class to your project's source code directory, preferrable
 *      to /i18n/i18n.java
 * 2. import it anywhere you would like to use it
 * 3. when initializing your application (maybe within startApp()), call
 *      i18n.init(...) giving a string beginning with the ISO 693-1 
 *      language code (i.e. 'de-AT', 'en-US', 'english' ...) and the path
 *      of the folder where your .tit ("TIny Translation") files can be
 *      found if it isn't '/i18n'
 * 4. if you would like to use a language string, use the returned one of
 *      i18n.s(...) giving the key either as an integer value or a string
 *      EXAMPLE:
 *      message = "SCORE: " + score.toString() + " " + i18n.s("points");
 * 
 * TINY TRANSLATION FORMAT SPECIFICATION:
 *      Please put your translation files to the folder you used in init()
 *      call. The files are named with the ISO 683-1 two-letter code of
 *      the used language, with ".tit" extension, i.e. "fr.tit" and
 *      encoded with UTF-8
 *      Those files are plain text files with following syntax:
 *      $key{Text!}
 *      all characters between the $ and { (whitespaces before and after
 *      are ignored!) describe the key, either as a number
 * 
 *      $34{Previous Level}
 *      $35{Next Level}
 * 
 *      or as a (short) text string
 * 
 *      $main_menu_1{Start new game}
 *      $main_menu_2{Options}
 * 
 *      should be unique and the same in all languages, i.e.
 *      en.tit:
 *          $55{Quit Game}
 *      de.tit:
 *          $55{Spiel Beenden}
 * 
 *      The language specific text (all text between { and } ) can contain
 *      every UTF-8 character except the closing brace }
 *      Whitespaces before and after the text are ignored, but you can
 *      (and probably will) use line breaks anywhere inside the text, i.e.
 * 
 *      $bag{
 *      1. key
 *      2. knife
 *      3. [nothing]
 *      ...some comments...
 *      }
 * 
 * Have fun with it! :-)
 * 
 */

public class i18n {
    private static String folder; // resource folder with *.tit files
    private static Hashtable strings; // for all language strings
    
    public static void init(String lang) {
        if(folder == null) {
            folder = "/i18n"; // default folder
        }
        init(lang, folder);
    }
    
    public static void init(String lang, String folder) {
        strings = new Hashtable();
        StringBuffer key = null;
        StringBuffer text = null;
        String file = (new StringBuffer(folder))
            .append("/")
            .append(lang.substring(0, 2))
            .append(".tit")
            .toString();
        InputStream res = i18n.class.getResourceAsStream(file);
        if(res != null) {
            try {
                InputStreamReader r = new InputStreamReader(res, "UTF-8");
                int c; // currently read character
                while((c = r.read()) != -1) {
                    if((char)c == '$') {
                        key = new StringBuffer();
                        text = new StringBuffer(128);
                        while((c = r.read()) != -1 && (char)c != '{') {
                            key.append((char)c);
                        }
                        while((c = r.read()) != -1 && (char)c != '}') {
                            text.append((char)c);
                        }
                        if(key.length() != 0 && text.length() != 0) {
                            String k = key.toString().trim();
                            String t = text.toString().trim();
                            try { // use either Integer or String as key
                                strings.put(new Integer(Integer.parseInt(k)), new String(t));
                            } catch(NumberFormatException e) {
                                strings.put(new String(k), new String(t));
                            }
                        }
                        key = null;
                        text = null;
                    }
                }
                r.close();
                res.close();
            } catch(IOException e) {}
        }
    }
    
    public static String s(int id) {
        Object o; // check if the selected string exists
        if((o = strings.get(new Integer(id))) == null) return "???";
        return o.toString();
    }
    
    public static String s(String id) {
        Object o;
        if((o = strings.get(new String(id))) == null) return "???";
        return o.toString();
    }
}
