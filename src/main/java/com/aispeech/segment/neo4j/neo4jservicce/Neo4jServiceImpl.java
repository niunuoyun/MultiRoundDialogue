package com.aispeech.segment.neo4j.neo4jservicce;

import com.aispeech.segment.entity.Phrase;
import com.alibaba.fastjson.JSONObject;
import org.neo4j.driver.v1.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Huihua Niu
 * on 2019/10/30 20:02
 */
@Service
public class Neo4jServiceImpl implements Neo4jService {
    private String uri="bolt://localhost:7687";  //neo4j端口
    private String username="neo4j";    //用户名
    private String password="123456";   //密码

    public Driver createDrive(){
        return GraphDatabase.driver( uri, AuthTokens.basic(username, password) );
    }
    @Override
    public JSONObject searchNode(List<Phrase> phrases) throws Exception {
        JSONObject js1 = new JSONObject();
        try (Session session = createDrive().session()) {
            String neoSql = "match(n:StartNode{name:'"+phrases.get(0).getValue()+"'})-[r:RELATIONSHIP{name:\""+phrases.get(1).getValue()+"\"}]->(m) return m.name;";
            StatementResult result = session.run(neoSql);
            List<String> answer = new ArrayList<>();
            while (result.hasNext()) {
                Record record = result.next();
                List<String> keys1 = record.keys();
                for (int i = 0; i < keys1.size(); i++) {
                    // keys1.get(i);
                    Value value = record.get(keys1.get(i));
                    answer.add(value.asString());
                    js1.put(keys1.get(i), answer);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return js1;
    }
}
