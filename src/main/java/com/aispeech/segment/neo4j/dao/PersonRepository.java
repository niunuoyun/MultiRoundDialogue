package com.aispeech.segment.neo4j.dao;

import com.aispeech.segment.neo4j.node.Person;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Huihua Niu
 * on 2019/10/16 19:40
 */
public interface PersonRepository extends CrudRepository<Person, Long> {
    Person findByName(String name);
}