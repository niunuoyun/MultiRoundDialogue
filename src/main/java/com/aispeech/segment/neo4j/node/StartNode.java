package com.aispeech.segment.neo4j.node;

import lombok.Data;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * Created by Huihua Niu
 * on 2019/10/30 17:51
 */
@Data
@NodeEntity
public class StartNode {
    @Id
    private long id;
    String name;
    String node_id;
}
