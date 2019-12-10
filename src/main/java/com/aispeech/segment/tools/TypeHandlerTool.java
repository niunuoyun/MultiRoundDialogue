package com.aispeech.segment.tools;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Huihua Niu
 * on 2019/12/6 19:31
 */
public class TypeHandlerTool {
    /**
     * 和QueryCombine实现是一样的，用于类型判断的工具，后期要调整
     * @param set1
     * @param set2
     * @return
     */
    public static boolean  hasSameTypeSet(Set<String> set1, Set<String> set2) {

        if (set1 == null && set2 == null) {
            return true;
        }
        if (set1 == null || set2 == null ) {
            return false;
        }
        if (set1.size()==0 && set2.size() == 0) {
            return true;
        }
        List<String> intersection = set1.stream().filter(item -> set2.contains(item)).collect(Collectors.toList());

        if ( (intersection.size()==set1.size() && set1.size() == set2.size()) || intersection.size()>=2 ){
            return true;
        }
        intersection.remove("n");
        intersection.remove("a");
        if (intersection.size()>=1) return true;
        return false;
    }
}
