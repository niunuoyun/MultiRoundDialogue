package com.aispeech.segment;

import com.aispeech.segment.neo4j.dao.MovieRepository;
import com.aispeech.segment.neo4j.dao.PersonRepository;
import com.aispeech.segment.neo4j.dao.StartNodeRepository;
import com.aispeech.segment.neo4j.node.Movie;
import com.aispeech.segment.neo4j.node.NodeNeo4j;
import com.aispeech.segment.neo4j.node.Person;
import com.aispeech.segment.neo4j.node.StartNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.neo4j.annotation.Query;
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
public class StartNodeTest {

    @Autowired
    StartNodeRepository startNodeRepository;

    @Test
    public void testSaveMovie() {
        StartNode startNode = startNodeRepository.findByName("大龙湫");

        System.out.println(startNode);
    }

  }
