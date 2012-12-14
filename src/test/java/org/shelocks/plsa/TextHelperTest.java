package org.shelocks.plsa;

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;
import org.junit.Assert;
import org.junit.Test;

public class TextHelperTest {
    
    @Test
    public void testAnalyze() throws IOException{
        Analyzer analyzer=new StandardAnalyzer(Version.LUCENE_36);
        
        List<String> words=new LinkedList<String>();
        StringReader in=new StringReader("Aeronautical Research Laboratory");
        TextHelper.countWords(analyzer, words, in);
        
        //filter stop words
        Assert.assertEquals(3, words.size());
    }
    
    @Test
    public void testGetMaxValueIndex(){
        double[] array={0.1,0.2,1.3,0.4};
        
        Assert.assertEquals(2, TextHelper.getMaxValueIndex(array));
        
        double[] array1={0.1,0.2,1.3,0.4,6.0};
        Assert.assertEquals(4, TextHelper.getMaxValueIndex(array1));
        
        double[] array2={0.1};
        Assert.assertEquals(0, TextHelper.getMaxValueIndex(array2));
    }

}
