package com.aispeech.segment.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Set;

/**
 * on 2019/11/20 11:50
 * @author Huihua Niu
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "relation_mapping")
public class EntityRelationMapping {
   private String subjectType;
   private String predicateType;
   private String resultType;
}
