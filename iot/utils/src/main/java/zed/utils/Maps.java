package zed.utils;

import com.google.common.collect.ImmutableMap;

public final class Maps {

    private Maps() {
    }

    public static <K, V> ImmutableMap<K, V> immutableMapOf(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6) {
        return ImmutableMap.<K, V>builder().
                put(k1, v1).put(k2, v2).put(k3, v3).put(k4, v4).put(k5, v5).put(k6, v6).
                build();
    }

    public static <K, V> ImmutableMap<K, V> immutableMapOf(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7) {
        return ImmutableMap.<K, V>builder().
                put(k1, v1).put(k2, v2).put(k3, v3).put(k4, v4).put(k5, v5).put(k6, v6).put(k7, v7).
                build();
    }

}