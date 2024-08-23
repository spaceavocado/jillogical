package com.spaceavocado.kernel;

import java.util.HashMap;
import java.util.Map;

public class FlattenContext<TKey, TValue> extends HashMap<TKey, TValue> {
    public static HashMap<String, Object> create(HashMap<String, Object> context) {
        if (context == null) {
            return null;
        }
        if (context instanceof FlattenContext) {
            return context;
        }

        var res = new HashMap<String, Object>();
        lookup(res, context, "");
        return res;
    }

    @SuppressWarnings("unchecked")
    private static void lookup(HashMap<String, Object> context, Object value, String path) {
        if (Primitive.isPrimitive(value) || value == null) {
            context.put(path, value);
            return;
        }
        
        if (value instanceof Map dict) {
            dict.forEach((key, inner) -> {
                lookup(context, inner, joinPath(path, key.toString()));
            });
            return;
        }
        
        if (value instanceof Object[] array) {
            for (var i = 0; i < array.length; i++) {
                lookup(context, array[i], String.format("%s[%s]", path, i));
            }
        } 
    }

    public static String joinPath(String a, String b) {
        return a.length() == 0 ? b : String.format("%s.%s", a, b); 
    }

    public static HashMap<String, Object> toHashMap(Map<String, Object> map) {
        var hashMap = new HashMap<String, Object>();
        hashMap.putAll(map);
        return hashMap;
    }

    public static FlattenContext<String, Object> toFlattenContext(Map<String, Object> map) {
        var hashMap = new FlattenContext<String, Object>();
        hashMap.putAll(map);
        return hashMap;
    }
} 
