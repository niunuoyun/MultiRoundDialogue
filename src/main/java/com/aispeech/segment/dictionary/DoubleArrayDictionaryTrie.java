package com.aispeech.segment.dictionary;

import com.aispeech.segment.entity.Phrase;
import com.aispeech.segment.entity.Word;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 双数组前缀树的Java实现
 * 用于查找一个指定的字符串是否在词典中
 * An Implementation of Double-Array Trie: http://linux.thai.net/~thep/datrie/datrie.html
 * @author huihua.niu
 */
@Component
public class DoubleArrayDictionaryTrie implements IDictionary {
    private static final Logger LOGGER = LoggerFactory.getLogger(DoubleArrayDictionaryTrie.class);
    private final static int BUF_SIZE = 16384;
    private final static int UNIT_SIZE = 8; // size of int + int

    private AtomicInteger maxLength = new AtomicInteger();
    @Value("${double.array.dictionary.trie.size}")
    private int SIZE = 260000;
    private int[] check;
    private int[] base;
    private boolean[] used;
    private int nextCheckPos;
    private int allocSize;
    private List<Word> key;
    private int keySize;
    private int length[];
    private int value[];
    private int progress;
    int error_;

    private static class Node {
        private int code;
        private int depth;
        private int left;
        private int right;

        @Override
        public String toString() {
            return "Node{" +
                    "code=" + code + "["+ (char)code + "]" +
                    ", depth=" + depth +
                    ", left=" + left +
                    ", right=" + right +
                    '}';
        }
    };



    public DoubleArrayDictionaryTrie(){
        LOGGER.info("初始化词典：" + this.getClass().getName());
    }

    private List<Node> toTree(Node parent, List<Word> words) {
        List<Node> siblings = new ArrayList<>();
        int prev = 0;

        for (int i = parent.left; i < parent.right; i++) {
            if (words.get(i).getValue().length() < parent.depth)
                continue;

            String word = words.get(i).getValue();

            int cur = 0;
            if (word.length() != parent.depth) {
                cur = (int) word.charAt(parent.depth);
            }

            if (cur != prev || siblings.isEmpty()) {
                Node node = new Node();
                node.depth = parent.depth + 1;
                node.code = cur;
                node.left = i;
                if (!siblings.isEmpty()) {
                    siblings.get(siblings.size() - 1).right = i;
                }
                siblings.add(node);
            }

            prev = cur;
        }

        if (!siblings.isEmpty()) {
            siblings.get(siblings.size() - 1).right = parent.right;
            if(LOGGER.isDebugEnabled()) {
                if (words.size()<10) {
                    LOGGER.debug("************************************************");
                    LOGGER.debug("树信息：");
                    siblings.forEach(s -> LOGGER.debug(s.toString()));
                    LOGGER.debug("************************************************");
                }
            }
        }
        return siblings;
    }
    private int toDoubleArray(List<Node> siblings, List<Word> words) {
        int begin = 0;
        int index = (siblings.get(0).code > nextCheckPos) ? siblings.get(0).code : nextCheckPos;
        boolean isFirst = true;

        outer: while (true) {
            index++;

            if (check[index] != 0) {
                continue;
            } else if (isFirst) {
                nextCheckPos = index;
                isFirst = false;
            }

            begin = index - siblings.get(0).code;

            if (used[begin]) {
                continue;
            }

            for (int i = 1; i < siblings.size(); i++) {
                if (check[begin + siblings.get(i).code] != 0) {
                    continue outer;
                }
            }

            break;
        }

        used[begin] = true;

        for (int i = 0; i < siblings.size(); i++) {
            check[begin + siblings.get(i).code] = begin;
        }

        for (int i = 0; i < siblings.size(); i++) {
            List<Node> newSiblings = toTree(siblings.get(i), words);

            if (newSiblings.isEmpty()) {
                base[begin + siblings.get(i).code] = -1;
            } else {
                int h = toDoubleArray(newSiblings, words);
                base[begin + siblings.get(i).code] = h;
            }
        }
        return begin;
    }
    private void allocate(int size){
        check = null;
        base = null;
        used = null;
        nextCheckPos = 0;

        base = new int[size];
        check = new int[size];
        used = new boolean[size];
        base[0] = 1;
    }
    private void init(List<Word> words) {
        if (words == null || words.isEmpty()) {
            return;
        }

        //前缀树的虚拟根节点
        Node rootNode = new Node();
        rootNode.left = 0;
        rootNode.right = words.size();
        rootNode.depth = 0;

        int size = SIZE;
        while (true) {
            try {
                allocate(size);
                List<Node> siblings = toTree(rootNode, words);
                toDoubleArray(siblings, words);
                break;
            } catch (Exception e) {
                size += size/10;
                LOGGER.error("分配空间不够，增加至： " + size);
            }
        }

        words.clear();
        words = null;
        used = null;
    }

    @Override
    public int getMaxLength() {
        return maxLength.get();
    }

    @Override
    public boolean contains(String item, int start, int length) {
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("开始查词典：{}", item.substring(start, start + length));
        }
        if(base==null){
            return false;
        }

        //base[0]=1
        int lastChar = base[0];
        int index;

        for (int i = start; i < start+length; i++) {
            index = lastChar + (int) item.charAt(i);
            if(index >= check.length || index < 0){
                return false;
            }
            if (lastChar == check[index]) {
                lastChar = base[index];
            }else {
                return false;
            }
        }
        index = lastChar;
        if(index >= check.length || index < 0){
            return false;
        }
        if (base[index] < 0 && index == check[index]) {
            if(LOGGER.isDebugEnabled()) {
                LOGGER.debug("在词典中查到词：{}", item.substring(start, start + length));
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean contains(String item) {
        return contains(item, 0, item.length());
    }

    @Override
    public void addAll(List<Word> items) {
        items=items
                .stream()
                .filter(item -> {
                    //统计最大词长
                    int len = item.getValue().length();
                    if(len > maxLength.get()){
                        maxLength.set(len);
                    }
                    return len > 0;
                }).sorted(Comparator.comparingDouble(val->val.getValue().length()))
                .collect(Collectors.toList());
        if(LOGGER.isDebugEnabled()){
            //for debug
            if (items.size()<10){
                items.forEach(item->LOGGER.debug(item.toString()));
            }
        }
        init(items);
    }

    @Override
    public void add(Word item) {
        throw new RuntimeException("not yet support, please use addAll method!");
    }

    @Override
    public void removeAll(List<Word> items) {
        throw new RuntimeException("not yet support menthod!");
    }

    @Override
    public void remove(Word item) {
        throw new RuntimeException("not yet support menthod!");
    }

    @Override
    public void clear() {
        check = null;
        base = null;
        used = null;
        nextCheckPos = 0;
        maxLength.set(0);
    }

    /**
     * 通用前缀匹配查找
     * @param key
     * @return
     */
    public List<Integer> commonPrefixSearch(String key)
    {
        return commonPrefixSearch(key, 0, 0, 0);
    }

    /**
     * 通用前缀匹配查找
     * @param key
     * @param pos
     * @param len
     * @param nodePos
     * @return
     */
    public List<Integer> commonPrefixSearch(String key, int pos, int len, int nodePos)
    {
        if( len <= 0 )
            len = key.length();
        if( nodePos <= 0 )
            nodePos = 0;

        LinkedList<Integer> result = new LinkedList<Integer>();

        char[] keyChars = key.toCharArray();

        int b = base[nodePos];
        int n;
        int p;

        for( int i = pos; i < len; i++ )
        {
            p = b;
            n = base[p];

            if( b == check[p] && n < 0 )
            {
                result.addFirst(-n - 1);
            }

            p = b + (int) (keyChars[i]) + 1;
            if( b == check[p] )
                b = base[p];
            else
                return result;
        }

        p = b;
        n = base[p];

        if( b == check[p] && n < 0 )
        {
            result.addFirst(-n - 1);
        }

        return result;
    }
    public int get(String item) {
        return get(item, 0, item.length());
    }
    public int get(String item, int start, int length) {
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("开始查询数据：{}", item.substring(start, start + length));
        }
        if(base==null){
            return Integer.MIN_VALUE;
        }

        //base[0]=1
        int lastChar = base[0];
        int index;

        for (int i = start; i < start+length; i++) {
            index = lastChar + (int) item.charAt(i);
            if(index >= check.length || index < 0){
                return Integer.MIN_VALUE;
            }
            if (lastChar == check[index]) {
                lastChar = base[index];
            }else {
                return Integer.MIN_VALUE;
            }
        }
        index = lastChar;
        if(index >= check.length || index < 0){
            return Integer.MIN_VALUE;
        }
        if (base[index] < 0) {
            if(LOGGER.isDebugEnabled()) {
                LOGGER.debug("在词典中查到词：{}", item.substring(start, start + length));
            }
            return check[lastChar];
        }
        return Integer.MIN_VALUE;
    }
    public static void main(String[] args) {
        IDictionary dictionary = new DoubleArrayDictionaryTrie();

        List<Word> words = new ArrayList<>();
        Word word = new Word("百度","n",200);
        Word word1 = new Word("是","adj",200);
        Word word2 = new Word("干","adj",200);
        Word word3 = new Word("搜索","v,ving",200);
        words.add(word);
        words.add(word1);
        words.add(word2);
        words.add(word3);

        //构造词典
        dictionary.addAll(words);
        System.out.println("增加数据：" + words);

        System.out.println("最大词长：" + dictionary.getMaxLength());
        System.out.println("查找 百度" +dictionary.contains("百度"));
        System.out.println("查找 百度" +((DoubleArrayDictionaryTrie) dictionary).get("百度"));
    }
}