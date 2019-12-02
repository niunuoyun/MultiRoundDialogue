package com.aispeech.segment.neo4j.node;

import lombok.Data;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * Created by Huihua Niu
 * on 2019/10/16 19:36
 */
@NodeEntity
@Data
public class Movie {
    @Id
    @GeneratedValue
    private Long id;
    private String title;
    private String released;

    public Movie() {

    }

    public Movie(String title, String released) {
        this.title = title;
        this.released = released;
    }
}