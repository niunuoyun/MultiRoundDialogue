package com.aispeech.segment.neo4j.dao;

import com.aispeech.segment.neo4j.node.Movie;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Huihua Niu
 * on 2019/10/16 19:39
 */
public interface MovieRepository  extends CrudRepository<Movie, Long> {
    Movie findByTitle(String title);
}
