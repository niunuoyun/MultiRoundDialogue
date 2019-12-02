package com.aispeech.segment.neo4j.dao;

import com.aispeech.segment.neo4j.node.StartNode;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by Huihua Niu
 * on 2019/10/30 17:53
 */
public interface StartNodeRepository extends CrudRepository<StartNode, Long> {
    StartNode findByName(String name);
}