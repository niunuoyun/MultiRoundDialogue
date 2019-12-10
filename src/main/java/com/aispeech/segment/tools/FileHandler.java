package com.aispeech.segment.tools;

import com.alibaba.fastjson.JSONObject;
import org.ansj.app.keyword.KeyWordComputer;
import org.ansj.app.keyword.Keyword;
import org.ansj.app.summary.SummaryComputer;
import org.ansj.app.summary.TagContent;
import org.ansj.app.summary.pojo.Summary;
import org.ansj.domain.Result;
import org.ansj.recognition.impl.StopRecognition;
import org.ansj.splitWord.analysis.DicAnalysis;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.nlpcn.commons.lang.tire.domain.Forest;
import org.nlpcn.commons.lang.tire.library.Library;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by Huihua Niu
 * on 2019/11/19 11:44
 */
public class FileHandler {
    private static Forest forest;
    private static StopRecognition stopRecognition = new StopRecognition();

    static {
        try {
            forest = Library.makeForest("C:/Users/work/segment/src/main/resources/library/default.dic");
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
    /*  public static void main(String[] args) throws FileNotFoundException {
          InputStream in = new FileInputStream(new File("C:\\Users\\work\\segment\\src\\main\\resources\\分类词\\动物.txt"));
          OutputStream outRelation = new FileOutputStream(new File("C:\\Users\\work\\segment\\src\\main\\resources\\分类词\\animal.csv"));
          OutputStream outRelation1 = new FileOutputStream(new File("C:\\Users\\work\\segment\\src\\main\\resources\\分类词\\animalNeedHandler.csv"));
          try (BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"))) {
              br.lines().forEach(val -> {
                  boolean isEng = val.matches(".*[A-Z]+.*");
                  boolean isHanzi = val.matches("^[\\u4e00-\\u9fa5]{0,}$");
                  if (isEng) {
                      System.out.println(val);
                  }
                  try {
                      if (isHanzi && !isEng && !val.contains("亚种") && val.trim().length() > 0 && val.trim().length() < 15) {
                          outRelation.write((val.trim() + "\r\n").getBytes());
                      } else {
                          outRelation1.write((val.trim() + "\r\n").getBytes());
                      }

                  } catch (IOException e) {
                      e.printStackTrace();
                  }

              });
          } catch (Exception e) {
              System.out.println("lockSkillTriggerWord.txt read exception, {}" + e);
          }
      }
  */
    public static void main(String[] args) {
        Result result = DicAnalysis.parse(SentenceHandler.getStandardSentence("苏门答腊黑胸蜂虎"),forest);
        Result result1 = ToAnalysis.parse(SentenceHandler.getStandardSentence("苏门答腊黑胸蜂虎"),forest);
        System.out.println(result.getTerms());
        System.out.println(result1.getTerms());
        //List<Keyword> result = kwc.computeArticleTfidf(content);
       /* SummaryComputer summaryComputer = new SummaryComputer(content.length(),true,"",content);
        Summary summary = summaryComputer.toSummary(result);
        System.out.println(summary.getSummary());*/

    }
}
