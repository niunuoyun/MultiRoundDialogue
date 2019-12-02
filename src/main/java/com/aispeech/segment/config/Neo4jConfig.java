package com.aispeech.segment.config;/*
package com.aispeech.segment.config;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

*/
/**
 * Created by Huihua Niu
 * on 2019/10/30 19:50
 *//*

@Configuration
public class Neo4jConfig {
    @Value("${spring.data.neo4j.uri}")
    private String uri;
    @Value("${spring.data.neo4j.username}")
    private String username;
    @Value("${spring.data.neo4j.password}")
    private String password;
    @Bean(name="driver")
    public Driver createDrive(){
        return GraphDatabase.driver( uri, AuthTokens.basic(username, password) );
    }
}
*/
