package com.aispeech.segment.loader;/*
package com.aispeech.segment.loader;

import com.aispeech.segment.dictionary.DoubleArrayDictionaryTrie;
import com.aispeech.segment.dictionary.IDictionary;
import com.aispeech.segment.dictionary.newImp.AhoCorasickDoubleArrayTrie;
import com.aispeech.segment.entity.Word;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.*;

*/
/**
 * Created by Huihua Niu
 * on 2019/8/29 13:27
 *//*

@Component
public final class ResourceLoaderFactory {
    @Autowired
    PersonNameResourceLoader personNameResourceLoader;
    @Autowired
    QuantifierResourceLoader quantifierResourceLoader;
    @Autowired
    StopWordResourceLoader stopWordResourceLoader;
    @Autowired
    WordsDictionaryLoader wordsDictionaryLoader;
    @Autowired
    PunctuationLoader punctuationLoader;
    @Autowired
    public  AhoCorasickDoubleArrayTrie doubleArrayDictionaryTrie;

    public void loadFile(){
        wordsDictionaryLoader.loadFile();
        quantifierResourceLoader.loadFile();
        stopWordResourceLoader.loadFile();
        personNameResourceLoader.loadFile();
        punctuationLoader.loadFile();
    }

    public Map<String,Word> collectDatas(){
        loadFile();
        Map<String,Word> phrases = wordsDictionaryLoader.getPhrases();
        //phrases.putAll(personNameResourceLoader.getPhrases());
      //  phrases.putAll(quantifierResourceLoader.getPhrases());
        phrases.putAll(stopWordResourceLoader.getPhrases());
        return phrases;
    }

    public AhoCorasickDoubleArrayTrie<Word> getDoubleArrayTrie(){
        doubleArrayDictionaryTrie.build(collectDatas());
        return doubleArrayDictionaryTrie;
    }

    public Map<String,Word> getStopWord(){
        return stopWordResourceLoader.getPhrases();
    }
}
*/
