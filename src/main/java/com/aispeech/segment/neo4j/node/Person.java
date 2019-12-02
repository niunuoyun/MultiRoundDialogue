package com.aispeech.segment.neo4j.node;

import com.sun.org.apache.bcel.internal.generic.LLOAD;
import lombok.Data;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Huihua Niu
 * on 2019/10/16 19:35
 */
@Data
@NodeEntity
public class Person {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String born;


    public Person() {// 从 Neo4j API 2.0.5开始需要无参构造函数

    }

    public Person(String name, String born) {
        this.name = name;
        this.born = born;
    }


    @Relationship(type = "参与演员", direction = Relationship.OUTGOING)
    public Set<Movie> actors;

    public void addActor(Movie movie) {
        if (actors == null) {
            actors = new HashSet<>();
        }
        actors.add(movie);
    }

    @Relationship(type = "主演", direction = Relationship.OUTGOING)
    public Set<Movie> directors;

    public void addDirector(Movie movie) {
        if (directors == null) {
            directors = new HashSet<>();
        }
        directors.add(movie);
    }

}