package com.aispeech.segment.neo4j.node;

import lombok.Data;

/**
 * Created by Huihua Niu
 * on 2019/10/29 16:25
 */
@Data
public class RelationShipNode {
    Long startId;
    String name;
    Long endId;
    String type = "RELATIONSHIP";
}
