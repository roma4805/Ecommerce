
package com.ecommerce.util;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

public class CollectionUtils {

    public static <T extends Comparable<? super T>> T max(Collection<? extends T> c) {
        return c.stream()
                .max(Comparator.naturalOrder())
                .orElseThrow(() -> new NoSuchElementException("Collection vide"));
    }

    public static <T> void copier(List<? super T> dest, List<? extends T> src) {
        dest.addAll(src);
    }
}
