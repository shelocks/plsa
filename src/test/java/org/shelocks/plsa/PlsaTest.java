package org.shelocks.plsa;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class PlsaTest {

    private Plsa plsa;

    private TextHelper textHelper = new TextHelper();

    @Before
    public void before() {
        plsa = new Plsa(-1);
    }

    @Test
    public void testRandomProbilities() {
        Random r = new Random();
        for (int i = 0; i < 10000; i++) {
            double[] array1 = plsa.randomProbilities(r.nextInt(10000) + 1);
            double sum = 0.0;
            for (double d : array1) {
                sum += d;
            }
            assertTrue(1.0 - 0.0001 < sum && sum < 1.0 + 0.0001);
        }
    }

    @Test
    public void testPlsaSimple() throws IOException {
        List<Document> docs = new LinkedList<Document>();

        String doc1 = "tech web iphone";
        docs.add(getDoc("doc1", doc1));

        String doc2 = "iphone web tech";
        docs.add(getDoc("doc2", doc2));

        String doc3 = "funny gif cat";
        docs.add(getDoc("doc3", doc3));

        String doc4 = "cat gif funny";
        docs.add(getDoc("doc4", doc4));

        Plsa plsa = new Plsa(2);
        plsa.train(docs, 50);

        double[][] docTopicPros = plsa.getDocTopics();

        assertTrue(docTopicPros[0][0] == docTopicPros[1][0]);
        assertTrue(docTopicPros[2][0] == docTopicPros[3][0]);
        assertTrue(docTopicPros[0][0] != docTopicPros[2][0]);
    }
    
    /**
     * 
     * This test cost will cost long time:(,you could choose
     * to skip it :)
     * 
     */
    @Test
    @Ignore
    public void testPlsaText() {
        try {
            File root = new File(Plsa.class.getClassLoader().getResource("data").toURI());
            List<File> files = Arrays.asList(root.listFiles());

            List<Document> docs = new LinkedList<Document>();

            for (File file : files) {
                docs.add(new Document(textHelper.extractWords(file), file.getName()));
            }

            Plsa plsa = new Plsa(3);
            plsa.train(docs, 50);

            /**
             * those are documents' name prefix
             * 
             */
            List<String> prefixes = new LinkedList<String>();
            prefixes.add("10");
            prefixes.add("54");
            prefixes.add("51");
            
            System.out.println("####documents topics statistics####");
            printStatisticsData(plsa, docs, prefixes);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    /**
     * 
     * Print model statistics data
     * 
     * @param plsa
     * @param docs
     * @param prefixes
     */
    private void printStatisticsData(Plsa plsa, List<Document> docs, List<String> prefixes) {
        double[][] docTopicPros = plsa.getDocTopics();

        double[][] docTopicsStatistics = new double[plsa.getTopicNum()][plsa.getTopicNum()];
        for (int i = 0; i < docs.size(); i++) {
            String fileName = docs.get(i).getFileName();
            int index = -1;
            for (String prefix : prefixes) {
                if (fileName.startsWith(prefix)) {
                    index = prefixes.indexOf(prefix);
                    break;
                }
            }
            if (index == -1) {
                throw new RuntimeException("The index is -1!");
            }
            int maxValueIndex = TextHelper.getMaxValueIndex(docTopicPros[i]);
            docTopicsStatistics[index][maxValueIndex] = docTopicsStatistics[index][maxValueIndex] + 1;
        }
        String header = "  ";
        for (int i = 0; i < plsa.getTopicNum(); i++) {
            header += "topic" + i + " ";
        }
        System.out.println(header);

        for (int i = 0; i < docTopicsStatistics.length; i++) {
            System.out.println(prefixes.get(i) + " " + docTopicsStatistics[i][0] + " "
                    + docTopicsStatistics[i][1] + " " + docTopicsStatistics[i][2]);
        }
    }

    private Document getDoc(String docName, String line) throws IOException {
        List<String> words = textHelper.extractWords(line);
        return new Document(words, docName);
    }

}
