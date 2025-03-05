package com.hm.stock.modules.common;

import lombok.Data;

@Data
public class Pair<K, V> {
    private K key;
    private V value;


    private Pair(){}

    public static <K,V> Pair<K,V> of(K key,V val){
        Pair<K,V> pair = new Pair<>();
        pair.setKey(key);
        pair.setValue(val);
        return pair;
    }
}
