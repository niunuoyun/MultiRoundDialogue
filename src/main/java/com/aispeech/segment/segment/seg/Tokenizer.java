package com.aispeech.segment.segment.seg;

import com.aispeech.segment.entity.Phrase;
import com.aispeech.segment.tools.SentenceHandler;
import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.recognition.impl.StopRecognition;
import org.ansj.splitWord.analysis.DicAnalysis;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.nlpcn.commons.lang.tire.domain.Forest;
import org.nlpcn.commons.lang.tire.library.Library;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Huihua Niu
 * on 2019/8/30 14:57
 */
@Component
public class Tokenizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(Tokenizer.class);

    private static Forest forest;
    private static StopRecognition stopRecognition = new StopRecognition();

    static {
        try {
            forest = Library.makeForest("C:/Users/work/project/MultiRoundDialogue/src/main/resources/library/allPhrases.dic");
            InputStreamReader isr = new InputStreamReader(
                    new FileInputStream("C:/Users/work/project/MultiRoundDialogue/src/main/resources/library/stopWord.dic"));
            BufferedReader bf = new BufferedReader(isr);

            String stopWord = null;
            while ((stopWord = bf.readLine()) != null) {
                stopWord = stopWord.trim();
                stopRecognition.insertStopWords(stopWord);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Phrase> segment(String text, boolean isRemoveStopWord) {
        Result result = DicAnalysis.parse(SentenceHandler.getStandardSentence(text),forest);
        if (isRemoveStopWord) {
            stopRecognition.recognition(result);
        }
        List<Term> list = result.getTerms();
        List<Phrase> phrases = new ArrayList<>();
        list.forEach(val -> phrases.add(new Phrase(val.getName(), val.getNatureStr(), val.getOffe())));
        return phrases;
    }


    public static void main(String[] args) {
        try {
            forest = Library.makeForest("C:/Users/work/segment/src/main/resources/library/default.dic");
            Result result = ToAnalysis.parse("思必驰的上市时间", forest);
            List<Term> list = result.getTerms();
            System.out.println(list.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
