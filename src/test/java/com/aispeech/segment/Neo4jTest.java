package com.aispeech.segment;

import com.aispeech.segment.neo4j.dao.MovieRepository;
import com.aispeech.segment.neo4j.dao.NodeNeo4JRepository;
import com.aispeech.segment.neo4j.dao.PersonRepository;
import com.aispeech.segment.neo4j.node.Movie;
import com.aispeech.segment.neo4j.node.NodeNeo4j;
import com.aispeech.segment.neo4j.node.Person;
import com.aispeech.segment.neo4j.node.RelationShipNode;
import com.aispeech.segment.tools.IDgenerater;
import lombok.val;
import org.apache.tomcat.util.http.fileupload.util.LimitedInputStream;
import org.elasticsearch.client.GraphClient;
import org.elasticsearch.client.license.LicensesStatus;
import org.elasticsearch.search.aggregations.bucket.missing.Missing;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.id.UuidStrategy;
import org.neo4j.ogm.model.GraphModel;
import org.neo4j.ogm.model.GraphRowModel;
import org.neo4j.ogm.model.Node;
import org.neo4j.ogm.response.model.NodeModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Huihua Niu
 * on 2019/10/16 19:42
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class Neo4jTest {

    @Autowired
    MovieRepository movieRepo;
    @Autowired
    PersonRepository personRepo;

    @Test
    public void testSaveMovie() {
        Movie m1 = new Movie("无问西东", "2018");
        Movie m2 = new Movie("罗曼蒂克消亡史", "2016");
        movieRepo.save(m1);
        movieRepo.save(m2);
    }

    @Test
    public void testSavePerson() {
        Person p1 = new Person("章子怡", "1979");
        Person p2 = new Person("李芳芳", "1976");
        Person p3 = new Person("程耳", "1970");
        Movie m1 = movieRepo.findByTitle("罗曼蒂克消亡史");
        Movie m2 = movieRepo.findByTitle("无问西东");
        if (m1!=null) {
            p1.addActor(m1);
            p3.addDirector(m1);
        }
        if (m2!=null) {
            p1.addActor(m2);
            p2.addDirector(m2);
        }
        personRepo.save(p1);
        personRepo.save(p2);
        personRepo.save(p3);
    }
    @Test
    public void  testDeleted(){
        Iterable<Person> p1 = personRepo.findAll();
        p1.forEach(val->personRepo.deleteById(val.getId()));
        movieRepo.deleteAll();
    }

    @Test
    public void  generateEntityRelationship() throws FileNotFoundException {
        InputStream in = new FileInputStream(new File("C:\\Users\\work\\segment\\src\\main\\resources\\threetuple\\own.csv"));
        OutputStream outEntity = new FileOutputStream(new File("C:\\Users\\work\\segment\\src\\main\\resources\\threetuple\\entity.csv"));
        OutputStream outRelation = new FileOutputStream(new File("C:\\Users\\work\\segment\\src\\main\\resources\\threetuple\\relationship.csv"));
        Map<String,NodeNeo4j> map = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in,"utf-8"))) {
            br.lines().forEach(val->{
                String[] phrase = val.split(",");
                if(phrase.length==3){
                    try {
                        NodeNeo4j startNode;
                        if (map.get(phrase[0])==null){
                           //map.clear();
                            startNode = new NodeNeo4j();
                            startNode.setLabel("StartNode");
                            startNode.setName(phrase[0]);
                            startNode.setId(UUID.randomUUID().hashCode());
                            map.put(phrase[0],startNode);
                            outEntity.write((startNode.getId()+","+startNode.getName()+","+startNode.getLabel()+"\r\n").getBytes());
                        }else {
                            startNode = map.get(phrase[0]);
                        }
                        startNode.setName(phrase[0]);
                        NodeNeo4j endNode = new NodeNeo4j();
                        endNode.setName(phrase[2]);
                        endNode.setLabel("EndNode");
                        endNode.setId(UUID.randomUUID().hashCode());
                        outEntity.write((endNode.getId()+","+endNode.getName()+","+endNode.getLabel()+"\r\n").getBytes());
                        outRelation.write((startNode.getId()+","+phrase[1]+","+endNode.getId()+",RELATIONSHIP"+"\r\n").getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            });
        }catch (Exception e){
            System.out.println("lockSkillTriggerWord.txt read exception, {}"+e);
        }
    }
    @Test
    public void generateRelationship() throws FileNotFoundException {
        InputStream in = new FileInputStream(new File("C:\\Users\\work\\segment\\src\\main\\resources\\threetuple\\ownthink_v2.csv"));
        // OutputStream outEntity = new FileOutputStream(new File("C:\\Users\\work\\segment\\src\\main\\resources\\threetuple\\entity.csv"));
        OutputStream outRelation = new FileOutputStream(new File("C:\\Users\\work\\segment\\src\\main\\resources\\threetuple\\relation.csv"));
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"))) {
            br.lines().forEach(val -> {
                String[] phrase = val.split(",");
                if (phrase.length == 3) {
                    boolean isEng = phrase[1].matches("^(?:(?=.*[0-9].*)(?=.*[A-Za-z].*)(?=.*[\\W].*))[\\W0-9A-Za-z]{8,16}$");
                    boolean isSymbol = phrase[1].matches("^.*[(¿)|(?)|(?)|(《)|(【)|(<)|(!)|(。)|(，)|(.)|(¡)|(!)|(!)|(！)].*$");
                    String words = phrase[1].replaceAll("\\p{P}", "\r\n");
                    if (isEng || isSymbol){
                        System.out.println(words);
                    }
                    if (!isEng) {
                        if (words.trim().length() > 1 && words.trim().length() < 6 && !words.trim().equals("描述") && !words.trim().equals("标签")) {
                            try {
                                outRelation.write((phrase[1].trim()+ "\r\n").getBytes());
                                //outRelation.write((phrase[1].trim() + "\t" + "pred" + "\t" + 5 + "\r\n").getBytes());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

            });
        } catch (Exception e) {
            System.out.println("lockSkillTriggerWord.txt read exception, {}" + e);
        }
    }

    @Test
    public void handlerRelationship() throws FileNotFoundException {
        InputStream in = new FileInputStream(new File("C:\\Users\\work\\segment\\src\\main\\resources\\threetuple\\relation.csv"));
        // OutputStream outEntity = new FileOutputStream(new File("C:\\Users\\work\\segment\\src\\main\\resources\\threetuple\\entity.csv"));
        OutputStream outRelation = new FileOutputStream(new File("C:\\Users\\work\\segment\\src\\main\\resources\\threetuple\\relationResultEn.csv"));
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"))) {
            br.lines().forEach(val -> {
                boolean isEng = val.matches(".*[A-Z]+.*");
                boolean isHanzi = val.matches("^[\\u4e00-\\u9fa5]{0,}$");
               // String words = val.replaceAll("\\p{P}", "\r\n");
                if (isEng || isHanzi){
                    System.out.println(val);
                }
                if (!isHanzi) {
                    if (val.trim().length() > 1 && val.trim().length() < 6) {
                        try {
                            outRelation.write((val.trim()+ "\r\n").getBytes());
                            //outRelation.write((phrase[1].trim() + "\t" + "pred" + "\t" + 5 + "\r\n").getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            });
        } catch (Exception e) {
            System.out.println("lockSkillTriggerWord.txt read exception, {}" + e);
        }
    }
}
