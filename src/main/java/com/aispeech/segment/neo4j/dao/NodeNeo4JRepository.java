package com.aispeech.segment.neo4j.dao;

import com.aispeech.segment.neo4j.node.NodeNeo4j;
import com.aispeech.segment.neo4j.node.Person;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Huihua Niu
 * on 2019/10/24 16:25
 */
public interface NodeNeo4JRepository extends CrudRepository<NodeNeo4j, Long> {
    Person findByName(String name);
}