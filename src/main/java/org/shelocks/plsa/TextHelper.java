package org.shelocks.plsa;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

import com.google.common.base.Charsets;
import com.google.common.io.Closeables;
import com.google.common.io.Files;

/**
 * 
 * This is a text helper to extract file to a document
 * 
 * @author happyshelocks@gmail.com
 * 
 */
public final class TextHelper {

    private static final Version LUCENE_VERSION = Version.LUCENE_36;

    private final Analyzer analyzer = new StandardAnalyzer(LUCENE_VERSION);

    private static Set<String> STOPWORDS = new HashSet<String>();

    static {
        loadStopWords();
    }
    /**
     * 
     * load stop words.
     * 
     */
    private static void loadStopWords() {
        BufferedReader reader;
        try {
            File f = new File(TextHelper.class.getClassLoader().getResource("stopwords.txt")
                    .toURI());
            reader = Files.newReader(f, Charsets.UTF_8);
            String line = reader.readLine();
            while (line != null) {
                String stopWord = line.trim();
                if (!stopWord.equals("")) {
                    STOPWORDS.add(stopWord);
                }
                line = reader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 
     * Extract words from this file
     * 
     * @param file
     * @return
     * @throws IOException
     */
    public List<String> extractWords(File file) throws IOException {
        List<String> words = new LinkedList<String>();
        BufferedReader reader = Files.newReader(file, Charsets.UTF_8);
        try {
            String line = reader.readLine();
            while (line != null) {
                if (!line.contains(":")) {
                    StringReader in = new StringReader(line);
                    countWords(analyzer, words, in);
                }

                line = reader.readLine();
            }
        } finally {
            Closeables.closeQuietly(reader);
        }

        return words;
    }
    
    /**
     * 
     * Extract words from this input
     * 
     * @param line
     * @return
     * @throws IOException
     */
    public List<String> extractWords(String line) throws IOException {
        List<String> words = new LinkedList<String>();
        StringReader in = new StringReader(line);
        countWords(analyzer, words, in);

        return words;
    }
    
    /**
     * 
     * Extract words from this input
     * 
     * @param analyzer
     * @param words
     * @param in
     * @throws IOException
     */
    public static void countWords(Analyzer analyzer, Collection<String> words, Reader in)
            throws IOException {
        TokenStream ts = analyzer.reusableTokenStream("text", in);
        ts.addAttribute(CharTermAttribute.class);
        ts.reset();
        while (ts.incrementToken()) {
            String s = ts.getAttribute(CharTermAttribute.class).toString().trim();
            if (!needFilter(s)) {
                words.add(s);
            }
        }
        in.close();
    }
    
    /**
     * 
     * Dump filter rules
     * 
     * @param word
     * @return
     */
    private static boolean needFilter(String word) {
        if (STOPWORDS.contains(word) || word.length() == 1 || word.equals("") || word.contains("'")
                || word.contains(",") || word.contains(".") || isNumber(word)) {
            return true;
        }
        return false;
    }

    public static boolean isNumber(String str) {
        Pattern pattern = Pattern.compile("[0-9]+");
        Matcher match = pattern.matcher(str);
        if (match.matches() == false) {
            return false;
        } else {
            return true;
        }
    }
    
    /**
     * 
     * Return the index of the max value in this array
     * 
     * @param array
     * @return
     */
    public static int getMaxValueIndex(double[] array) {
        int maxIndex = 0;
        double maxValue = array[0];

        for (int i = 0; i < array.length; i++) {
            if (array[i] > maxValue) {
                maxIndex = i;
                maxValue = array[i];
            }
        }
        return maxIndex;
    }

}
