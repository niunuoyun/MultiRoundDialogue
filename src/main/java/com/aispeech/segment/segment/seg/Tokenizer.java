package com.aispeech.segment.segment.seg;

import com.aispeech.segment.dictionary.newImp.AhoCorasickDoubleArrayTrie;
import com.aispeech.segment.entity.Phrase;
import com.aispeech.segment.entity.Word;
import com.aispeech.segment.loader.StopWordResourceLoader;
import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.recognition.impl.NatureRecognition;
import org.ansj.recognition.impl.StopRecognition;
import org.ansj.splitWord.analysis.DicAnalysis;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.nlpcn.commons.lang.tire.domain.Forest;
import org.nlpcn.commons.lang.tire.library.Library;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by Huihua Niu
 * on 2019/8/30 14:57
 */
@Component
public class Tokenizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(Tokenizer.class);
    @Autowired
    private StopWordResourceLoader stopWord;
  //  @Autowired
//    ResourceLoaderFactory loaderFactory;
    private AhoCorasickDoubleArrayTrie doubleArrayTrie;
    private static Forest forest;
  //  private static List<String> StopWords = new ArrayList<>();
    private static  StopRecognition stopRecognition = new StopRecognition();
    static {
        try {
            forest = Library.makeForest("C:/Users/work/segment/src/main/resources/library/library.dic");
            InputStreamReader isr = new InputStreamReader(
                    new FileInputStream("C:/Users/work/segment/src/main/resources/library/stopWord.dic"));
            BufferedReader bf = new BufferedReader(isr);

            String stopWord = null;
            while ((stopWord = bf.readLine()) != null) {
                stopWord = stopWord.trim();
             //   StopWords.add(stopWord);
                stopRecognition.insertStopWords(stopWord);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public List<Phrase> segment(String text, boolean isRemoveStopWord){
        Result result = DicAnalysis.parse(text,forest);
        if (isRemoveStopWord){
            stopRecognition.recognition(result);
        }
        List<Term> list = result.getTerms();
        List<Phrase> phrases = new ArrayList<>();
        list.forEach(val->phrases.add(new Phrase(val.getName(),val.getNatureStr(),val.getOffe())));
        return phrases;
    }
  /*  @PostConstruct
    public AhoCorasickDoubleArrayTrie getDoubleArrayTrie(){
        if (doubleArrayTrie==null){
            doubleArrayTrie = loaderFactory.getDoubleArrayTrie();
            LOGGER.info("加载分词成功：{}",doubleArrayTrie.parseText("成功"));
            return doubleArrayTrie;
        }else {
            return doubleArrayTrie;
        }
    }*/
    /**
     *
     * @param text 被分词的内容
     * @return
     */
    /* public List<Word> segTest(String text){
         AhoCorasickDoubleArrayTrie acdat = getDoubleArrayTrie();
         List<AhoCorasickDoubleArrayTrie.Hit<Word>> wordList = acdat.parseText(text);
         System.out.println(wordList);
         List<Word> list = new ArrayList<>();
         wordList.stream().forEach(val->list.add(val.value));
         return list;
    }*/
    /**
     *
     * @param text 被分词的内容
     * @return
     */
 /*   public Word segResult(String text){
        AhoCorasickDoubleArrayTrie<Word> acdat = getDoubleArrayTrie();
       AhoCorasickDoubleArrayTrie.Hit<Word> hit =  acdat.findFirst(text);
        return hit.value;
    }*/

    /**
     *
     * @param text
     * @param isRemoveStopWord
     * @return
     */
  /*  public List<Phrase> segSentence(String text,boolean isRemoveStopWord)
    {

        char[] sentence = text.toCharArray();
        final int[] wordNet = new int[sentence.length];
        String[] typeSet = new String[sentence.length];
        double[] frequence =  new double[sentence.length];
        Arrays.fill(wordNet, 1);
        getDoubleArrayTrie().parseText(sentence, new AhoCorasickDoubleArrayTrie.IHit<Word>()
        {
            @Override
            public void hit(int begin, int end, Word value)
            {
                int length = end - begin;
                if (length > wordNet[begin])
                {
                    wordNet[begin] = length;
                }
                typeSet[begin] = value.getTypeSet();
                frequence[begin] = value.getFrequence();
            }
        });
        LinkedList<Phrase> termList = new LinkedList<>();
        for (int i = 0; i < wordNet.length; )
        {
            if (isRemoveStopWord){
                if(stopWord.getPhrases().get(new String(sentence, i, wordNet[i]))!=null){
                    i += wordNet[i];
                    continue;
                }
            }
            Phrase term = new Phrase(new String(sentence, i, wordNet[i]),typeSet[i],frequence[i]);
            term.position = i;
            termList.add(term);
            i += wordNet[i];
        }
        return termList;
    }*/


}
