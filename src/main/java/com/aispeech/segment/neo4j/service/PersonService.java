package com.aispeech.segment.neo4j.service;

import com.aispeech.segment.neo4j.node.Movie;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Huihua Niu
 * on 2019/10/16 19:41
 */
@Service
public class PersonService {
    @Relationship(type = "演员", direction = Relationship.OUTGOING)
    public Set<Movie> actors;

    public void addActor(Movie movie) {
        if (actors == null) {
            actors = new HashSet<>();
        }
        actors.add(movie);
    }

    @Relationship(type = "导演", direction = Relationship.OUTGOING)
    public Set<Movie> directors;

    public void addDirector(Movie movie) {
        if (directors == null) {
            directors = new HashSet<>();
        }
        directors.add(movie);
    }
}