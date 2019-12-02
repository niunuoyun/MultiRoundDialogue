package com.aispeech.segment.neo4j.node;

import lombok.Data;
import lombok.NonNull;
import org.neo4j.ogm.annotation.*;
import org.neo4j.ogm.annotation.typeconversion.Convert;
import org.neo4j.ogm.id.UuidStrategy;
import org.neo4j.ogm.typeconversion.UuidStringConverter;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Huihua Niu
 * on 2019/10/23 18:20
 */
@Data
@NodeEntity
public class NodeNeo4j {
    @Id
    private long id;
    String name;
    String label;
}
