package com.aispeech.segment.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Created by Huihua Niu
 * on 2019/11/20 11:50
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "relation_mapping")
public class EntityRelationMapping {
   @Field("_id")
   private String id;
   private String  subjectType;
   private String predicateType;
   private String resultType;
}
