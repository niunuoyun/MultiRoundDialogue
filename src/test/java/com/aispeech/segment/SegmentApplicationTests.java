package com.aispeech.segment;

import com.aispeech.segment.entity.Phrase;
import com.aispeech.segment.segment.seg.Tokenizer;
import com.aispeech.segment.tools.QueryCombine;
import com.github.stuxuhai.jpinyin.ChineseHelper;
import com.google.common.collect.Lists;
import joptsimple.internal.Strings;
import lombok.val;
import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.Analysis;
import org.ansj.splitWord.analysis.DicAnalysis;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nlpcn.commons.lang.tire.domain.Forest;
import org.nlpcn.commons.lang.tire.library.Library;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SegmentApplicationTests {

	@Autowired
	Tokenizer tokenizer;
	@Autowired
	QueryCombine queryCombine;
	@Test
	public void contextLoads() {
	}

/*	@Test
	public void test(){
		//List<Word> words = tokenizer.segTest("北京欢迎你");
		List<Phrase> wordsBj = tokenizer.segSentence("北京有哪些好玩的",false);
		List<Phrase> wordsSh = tokenizer.segSentence("上海呢",true);
		List<Phrase> wordsSh1 = tokenizer.segSentence("思必驰的核心技术有哪些？",true);
		List<Phrase> wordsSh2 = tokenizer.segSentence("产品呢",true);

		System.out.println(wordsBj);
		System.out.println(wordsSh);
		System.out.println(wordsSh1);
		System.out.println(wordsSh2);
	}*/
/*	@Test
	public void read(){
		//List<Word> words = tokenizer.segTest("北京欢迎你");
		List<Phrase> wordsSh1 = tokenizer.segSentence("世界上最高的山峰？",true);
		List<Phrase> wordsSh2 = tokenizer.segSentence("排名第二",true);

		System.out.println(FileUtils.questionRelatingToAbove(wordsSh2,wordsSh1));
		System.out.println(wordsSh1);
		System.out.println(wordsSh2);
	}*/

	@Test
	public void  getPhrase() throws FileNotFoundException {
		InputStream in = new FileInputStream(new File("C:\\Users\\work\\segment\\src\\main\\resources\\data\\中文分词词库整理\\30wChinsesSeqDic.txt"));
		OutputStream out = new FileOutputStream(new File("C:\\Users\\work\\segment\\src\\main\\resources\\data\\phrase.txt"));
		try (BufferedReader br = new BufferedReader(new InputStreamReader(in,"utf-8"))) {
			br.lines().forEach(val->{
				String[] phrase = val.split(":");
				try {
					out.write((phrase[1]+":"+phrase[3]+":"+phrase[2]+"\r\n").getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}

			});
		}catch (Exception e){
			System.out.println("lockSkillTriggerWord.txt read exception, {}"+e);
		}

	}


	@Test
	public void  getfoodPhrase() throws FileNotFoundException {
		InputStream in = new FileInputStream(new File("C:\\Users\\work\\segment\\src\\main\\resources\\data\\食物词库\\THUOCL_food.txt"));
		OutputStream out = new FileOutputStream(new File("C:\\Users\\work\\segment\\src\\main\\resources\\data\\food.txt"));
		try (BufferedReader br = new BufferedReader(new InputStreamReader(in,"utf-8"))) {
			br.lines().forEach(val->{
				String[] phrase = val.split(":");
				try {
					out.write((phrase[0]+":n,fd:"+phrase[1]+"\r\n").getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}

			});
		}catch (Exception e){
			System.out.println("lockSkillTriggerWord.txt read exception, {}"+e);
		}

	}

	@Test
	public void  getPersonNamePhrase() throws FileNotFoundException {
		InputStream in = new FileInputStream(new File("C:\\Users\\work\\segment\\src\\main\\resources\\data\\历史名人词库\\THUOCL_lishimingren.txt"));
		OutputStream out = new FileOutputStream(new File("C:\\Users\\work\\segment\\src\\main\\resources\\data\\personName.txt"));
		try (BufferedReader br = new BufferedReader(new InputStreamReader(in,"utf-8"))) {
			br.lines().forEach(val->{
				String[] phrase = val.split(":");
				try {
					out.write((phrase[0]+":nr:"+phrase[1]+"\r\n").getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}

			});
		}catch (Exception e){
			System.out.println("lockSkillTriggerWord.txt read exception, {}"+e);
		}

	}

	@Test
	public void  getCarPhrase() throws FileNotFoundException {
		InputStream in = new FileInputStream(new File("C:\\Users\\work\\segment\\src\\main\\resources\\data\\汽车品牌、零件词库\\car_dict.txt"));
		OutputStream out = new FileOutputStream(new File("C:\\Users\\work\\segment\\src\\main\\resources\\data\\carBrand.txt"));
		try (BufferedReader br = new BufferedReader(new InputStreamReader(in,"utf-8"))) {
			br.lines().forEach(val->{
				try {
					out.write((val+":n,cb:5"+"\r\n").getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}

			});
		}catch (Exception e){
			System.out.println("lockSkillTriggerWord.txt read exception, {}"+e);
		}

	}
	@Test
	public void  getCarRelatedPhrase() throws FileNotFoundException {
		InputStream in = new FileInputStream(new File("C:\\Users\\work\\segment\\src\\main\\resources\\data\\汽车品牌、零件词库\\THUOCL_car.txt"));
		OutputStream out = new FileOutputStream(new File("C:\\Users\\work\\segment\\src\\main\\resources\\data\\car_related.txt"));
		try (BufferedReader br = new BufferedReader(new InputStreamReader(in,"utf-8"))) {
			br.lines().forEach(val->{
				try {
					String[] phrase = val.split("\t");
					out.write((phrase[0]+":n:"+phrase[1]+"\r\n").getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}

			});
		}catch (Exception e){
			System.out.println("lockSkillTriggerWord.txt read exception, {}"+e);
		}

	}
	@Test
	public void  getPlacePhrase() throws FileNotFoundException {
		InputStream in = new FileInputStream(new File("C:\\Users\\work\\segment\\src\\main\\resources\\data\\地名词库\\THUOCL_diming.txt"));
		OutputStream out = new FileOutputStream(new File("C:\\Users\\work\\segment\\src\\main\\resources\\data\\place.txt"));
		try (BufferedReader br = new BufferedReader(new InputStreamReader(in,"utf-8"))) {
			br.lines().forEach(val->{
				try {
					String[] phrase = val.split("\t");
					out.write((phrase[0]+":ns:"+phrase[1]+"\r\n").getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}

			});
		}catch (Exception e){
			System.out.println("lockSkillTriggerWord.txt read exception, {}"+e);
		}

	}
	@Test
	public void  getPlaceStreetPhrase() throws FileNotFoundException {
		InputStream in = new FileInputStream(new File("C:\\Users\\work\\segment\\src\\main\\resources\\data\\地名词库\\青岛道路名称Tsingtao_roads.txt"));
		OutputStream out = new FileOutputStream(new File("C:\\Users\\work\\segment\\src\\main\\resources\\data\\placeStreet.txt"));
		try (BufferedReader br = new BufferedReader(new InputStreamReader(in,"utf-8"))) {
			br.lines().forEach(val->{
				try {
					String[] phrase = val.split("\t");
					out.write((val+":nts:5"+"\r\n").getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}

			});
		}catch (Exception e){
			System.out.println("lockSkillTriggerWord.txt read exception, {}"+e);
		}

	}

	@Test
	public void  getAnimalPhrase() throws FileNotFoundException {
		InputStream in = new FileInputStream(new File("C:\\Users\\work\\segment\\src\\main\\resources\\data\\动物词库\\THUOCL_animal.txt"));
		OutputStream out = new FileOutputStream(new File("C:\\Users\\work\\segment\\src\\main\\resources\\data\\animal.txt"));
		try (BufferedReader br = new BufferedReader(new InputStreamReader(in,"utf-8"))) {
			br.lines().forEach(val->{
				try {
					String[] phrase = val.split("\t");
					out.write((phrase[0]+":n,nam:"+phrase[1]+"\r\n").getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}

			});
		}catch (Exception e){
			System.out.println("lockSkillTriggerWord.txt read exception, {}"+e);
		}

	}


	@Test
	public void  getCompanyPhrase() throws FileNotFoundException {
		InputStream in = new FileInputStream(new File("C:\\Users\\work\\segment\\src\\main\\resources\\data\\公司名字词库\\Company-Shorter-Form（28W）.txt"));
		OutputStream out = new FileOutputStream(new File("C:\\Users\\work\\segment\\src\\main\\resources\\data\\companyShorter.txt"));
		try (BufferedReader br = new BufferedReader(new InputStreamReader(in,"utf-8"))) {
			br.lines().forEach(val->{
				try {
					out.write((val+":cn:5"+"\r\n").getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}

			});
		}catch (Exception e){
			System.out.println("lockSkillTriggerWord.txt read exception, {}"+e);
		}

	}

	@Test
	public void  getSogouPhrase() throws FileNotFoundException {
		InputStream in = new FileInputStream(new File("C:\\Users\\work\\segment\\src\\main\\resources\\dictionary.txt"));
		OutputStream out = new FileOutputStream(new File("C:\\Users\\work\\segment\\src\\main\\resources\\data\\sougo.txt"));
		try (BufferedReader br = new BufferedReader(new InputStreamReader(in,"utf-8"))) {
			br.lines().forEach(val->{
				try {
					String[] phrase = val.split(":");
					if (phrase.length==3) {
						String typeSet = phrase[2].toLowerCase();
						if (phrase[2].endsWith(",")){
							typeSet = phrase[2].toLowerCase().substring(0,phrase[2].length()-1);
						}
						out.write((phrase[0]+":"+typeSet+":"+phrase[1]+"\r\n").getBytes());
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

			});
		}catch (Exception e){
			System.out.println("lockSkillTriggerWord.txt read exception, {}"+e);
		}

	}

	@Test
	public void getCombinePhrase() throws IOException {
		List<String> list = new ArrayList<>();
		queryCombine.listDirectory(new File("C:\\Users\\work\\segment\\src\\main\\resources\\phrase"), list);
		OutputStream out = new FileOutputStream(new File("C:\\Users\\work\\segment\\src\\main\\resources\\phraseDic.dic"));
		Map<String,String> wordMap = new HashMap<>();
		list.stream().forEach(file -> {
			try {
				InputStream in = new FileInputStream(new File(file));
				try (BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"))) {
					br.lines().forEach(val -> {
						String[] phrase = val.split(":");
						String words = wordMap.get(phrase[0]);
						if (StringUtils.isEmpty(words)){
							wordMap.put(phrase[0],val);
						}else {
							String[] phrase1 = words.split(":");
							String[] typeArr = phrase[1].split(",");
							String[] typeArr1 = phrase1[1].split(",");
							Set<String> typeSet1 = new HashSet<>(Arrays.asList(typeArr));
							Set<String> typeSet2 = new HashSet<>(Arrays.asList(typeArr1));
							typeSet1.addAll(typeSet2);
							wordMap.put(phrase[0],phrase[0]+":"+ queryCombine.getType(typeSet1)+":"+phrase[2]);
						}

					});
				} catch (Exception e) {
					System.out.println("lockSkillTriggerWord.txt read exception, {}" + e);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

		});

		wordMap.values().stream().forEach(val -> {
			try {
				out.write((val + "\r\n").getBytes());

			} catch (IOException e) {
				e.printStackTrace();
			}
				}
		);

	}

	@Test
	public void  getDicPhrase() throws FileNotFoundException {
		InputStream in = new FileInputStream(new File("C:\\Users\\work\\segment\\src\\main\\resources\\library\\phraseDic.dic"));
		OutputStream out = new FileOutputStream(new File("C:\\Users\\AISPEECH\\Desktop\\default.dic"));
		try (BufferedReader br = new BufferedReader(new InputStreamReader(in,"utf-8"))) {
			br.lines().forEach(val->{
				try {
					String[] phrase = val.split("  ");
					if (phrase.length==3) {
						String typeSet = phrase[1].toLowerCase();
						if (phrase[1].endsWith(",")){
							typeSet = phrase[1].toLowerCase().substring(0,phrase[1].length()-1);
						}
						out.write((phrase[0]+"\t"+typeSet+"\t"+phrase[2]+"\r\n").getBytes());
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

			});
		}catch (Exception e){
			System.out.println("lockSkillTriggerWord.txt read exception, {}"+e);
		}

	}

	@Test
	public void  dicHandler() throws FileNotFoundException {
		InputStream in = new FileInputStream(new File("C:\\Users\\work\\segment\\src\\main\\resources\\library\\library.dic"));
		OutputStream out = new FileOutputStream(new File("C:\\Users\\AISPEECH\\Desktop\\n.dic"));
		try (BufferedReader br = new BufferedReader(new InputStreamReader(in,"utf-8"))) {
			br.lines().forEach(val->{
				try {
					String[] phrase = val.split("\t");
					if (phrase.length==3) {
						if (phrase[1].equals("n")){
							out.write((val+"\r\n").getBytes());
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

			});
		}catch (Exception e){
			System.out.println("lockSkillTriggerWord.txt read exception, {}"+e);
		}

	}

	@Test
	public void  dicOtherHandler() throws FileNotFoundException {
		InputStream in = new FileInputStream(new File("C:\\Users\\work\\segment\\src\\main\\resources\\library\\library.dic"));
		OutputStream out = new FileOutputStream(new File("C:\\Users\\AISPEECH\\Desktop\\other.dic"));
		try (BufferedReader br = new BufferedReader(new InputStreamReader(in,"utf-8"))) {
			br.lines().forEach(val->{
				try {
					String[] phrase = val.split("\t");
					if (phrase.length==3) {
						if (!phrase[1].contains("cn") && !phrase[1].contains("food")  &&!phrase[1].contains("n") && !phrase[1].contains("nz") && !phrase[1].contains("nt")&&!phrase[1].contains("ns") && !phrase[1].contains("idiom")){
							out.write((val+"\r\n").getBytes());
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

			});
		}catch (Exception e){
			System.out.println("lockSkillTriggerWord.txt read exception, {}"+e);
		}

	}
	@Test
	public void  dicConjHandler() throws FileNotFoundException {
		InputStream in = new FileInputStream(new File("C:\\Users\\work\\segment\\src\\main\\resources\\library\\library.dic"));
		OutputStream out = new FileOutputStream(new File("C:\\Users\\AISPEECH\\Desktop\\conj.dic"));
		try (BufferedReader br = new BufferedReader(new InputStreamReader(in,"utf-8"))) {
			br.lines().forEach(val->{
				try {
					String[] phrase = val.split("\t");
					if (phrase.length==3) {
						if (phrase[1].contains("conj")){
							out.write((val+"\r\n").getBytes());
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

			});
		}catch (Exception e){
			System.out.println("lockSkillTriggerWord.txt read exception, {}"+e);
		}

	}
	@Test
	public void  dicAdvHandler() throws FileNotFoundException {
		InputStream in = new FileInputStream(new File("C:\\Users\\work\\project\\MultiRoundDialogue\\src\\main\\resources\\library\\library.dic"));
		InputStream filter = new FileInputStream(new File("C:\\Users\\work\\project\\MultiRoundDialogue\\src\\main\\resources\\library\\allPhrases.dic"));
		OutputStream out = new FileOutputStream(new File("C:\\Users\\AISPEECH\\Desktop\\ask.dic"));
		List<String> words = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(filter,"utf-8"))) {
			br.lines().forEach(val->{
				String[] phrase = val.split("\t");
				if (phrase.length==3) {
					words.add(phrase[0]);
				}

			});
		}catch (Exception e){
			System.out.println("lockSkillTriggerWord.txt read exception, {}"+e);
		}
		try (BufferedReader br = new BufferedReader(new InputStreamReader(in,"utf-8"))) {
			br.lines().forEach(val->{
				try {
					String[] phrase = val.split("\t");
					if (phrase.length==3) {
						if (!words.contains(phrase[0])){
							out.write((val+"\r\n").getBytes());
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

			});
		}catch (Exception e){
			System.out.println("lockSkillTriggerWord.txt read exception, {}"+e);
		}

	}
	@Test
	public void  genaratePhraseDic() throws FileNotFoundException {
		File file = new File("C:\\Users\\work\\project\\MultiRoundDialogue\\src\\main\\resources\\词语梳理");
		File[] files = file.listFiles();
		OutputStream out = new FileOutputStream(new File("C:\\Users\\AISPEECH\\Desktop\\allPhrases.dic"));
		Map<String,Phrase> phraseMap = new HashMap<>();
		Arrays.stream(files).forEach(val->{
			try {
				InputStream in = new FileInputStream(val);
				try (BufferedReader br = new BufferedReader(new InputStreamReader(in,"utf-8"))) {
					br.lines().forEach(data->{
						String[] word = data.split("\t");
						if (word.length==3) {
							Phrase phrase = phraseMap.get(word[0]);
							if (phrase==null){
								phrase = new Phrase();
								phrase.setValue(word[0]);
								phrase.setTypeSet(phrase.typeToSet(word[1]));
								phrase.setFrequence(Double.parseDouble(word[2]));
							}else {
								double frequence =	Double.parseDouble(word[2])+phrase.getFrequence();
								phrase.setFrequence(frequence/2);
								phrase.getTypeSet().addAll(phrase.typeToSet(word[1]));
							}
							phraseMap.put(word[0],phrase);

						}else {
							System.out.println("错误数据："+data);
						}
					});
				}catch (Exception e){
					System.out.println("generate dictionary read exception, {}"+e);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		});

		if (phraseMap.size()>0)phraseMap.values().stream().forEach(val->{
			try {
				out.write((val.getValue()+"\t"+ Strings.join(val.getTypeSet(),",")+"\t"+val.getFrequence()+"\r\n").getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

	}

	@Test
	public void  genarateUniqPhraseDic() throws FileNotFoundException {
		InputStream filter = new FileInputStream(new File("C:\\Users\\work\\project\\MultiRoundDialogue\\src\\main\\resources\\library\\allPhrases.dic"));
		OutputStream out = new FileOutputStream(new File("C:\\Users\\AISPEECH\\Desktop\\allPhrases.dic"));
		Map<String,Phrase> phraseMap = new HashMap<>();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(filter,"utf-8"))) {
			br.lines().forEach(data->{
				String[] word = data.split("\t");
				if (word.length==3) {
					Phrase phrase = phraseMap.get(word[0]);
					Result result = ToAnalysis.parse(word[0].trim());
					List<Term> terms = result.getTerms();
					if (phrase == null) {
						phrase = new Phrase();
						phrase.setValue(word[0].trim());
						if (terms.size()==1 &&terms!=null && terms.get(0).getNatureStr().equals("a")){
							phrase.setTypeSet(phrase.typeToSet("a"));
						}else {
							phrase.setTypeSet(phrase.typeToSet(word[1]));;
						}
						phrase.setFrequence(Double.parseDouble(word[2]));
					} else {
						phrase.getTypeSet().addAll(phrase.typeToSet(word[1]));
					}
					phraseMap.put(word[0].trim(), phrase);
				}else {
					System.out.println("错误数据："+data);
				}
			});
		}catch (Exception e){
			System.out.println("generate dictionary read exception, {}"+e);
		}
		if (phraseMap.size()>0)phraseMap.values().stream().forEach(val->{
			try {
				out.write((val.getValue()+"\t"+ Strings.join(val.getTypeSet(),",")+"\t"+val.getFrequence()+"\r\n").getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

	}

	@Test
	public void handlePredicatePhraseDic() throws FileNotFoundException {
		InputStream in = new FileInputStream(new File("C:\\Users\\work\\project\\MultiRoundDialogue\\src\\main\\resources\\词语梳理\\predicate.dic"));
		OutputStream out = new FileOutputStream(new File("C:\\Users\\AISPEECH\\Desktop\\NObject.dic"));
		OutputStream out1 = new FileOutputStream(new File("C:\\Users\\AISPEECH\\Desktop\\Object.dic"));

		try (BufferedReader br = new BufferedReader(new InputStreamReader(in,"utf-8"))) {
			br.lines().forEach(data->{
				try {
					String[] word = data.split("\t");
					if (word.length==3) {
						Result result = DicAnalysis.parse(word[0].trim());
						List<Term> terms = result.getTerms();
						List<String>  lists = new ArrayList<>();
						terms.forEach(val->{
							/*if ((val.getNatureStr().equals("ns")||val.getNatureStr().equals("nr")||val.getNatureStr().equals("nrf"))&&val.getName().length()>1){
								lists.add(val.getNatureStr());
							}*/
							if ((val.getNatureStr().equals("n")||val.getNatureStr().equals("nz"))&&val.getName().length()>1){
								lists.add(val.getNatureStr());
							}
						});
					//	String simpleWord = ChineseHelper.convertToSimplifiedChinese(word[0]);

						if (lists.size()>0 && terms.size()==1){
							out1.write((word[0]+"\t"+word[1]+",n"+"\t"+word[2]+"\r\n").getBytes());
							lists.clear();
						}else {
							out.write((data+"\r\n").getBytes());
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

			});
		}catch (Exception e){
			System.out.println("generate dictionary read exception, {}"+e);
		}

	}

	public static void main(String[] args) {

		try {
			Forest forest = Library.makeForest(SegmentApplicationTests.class.getResourceAsStream("/library/allPhrases.dic"));
			String str = "最佳" ;
			Result result = DicAnalysis.parse(str);
			List<Term> terms = result.getTerms();
			terms.forEach(val->{
				System.out.println(val.getName()+"===="+val.getNatureStr());
			});
			System.out.println(result);
			String simpleWord = ChineseHelper.convertToSimplifiedChinese(str);
			System.out.println(simpleWord);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
