package com.ecommerce.util;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CollectionUtilsTest {
    @Test
    void maxEtCopieRespectentLesBornesGeneriques() {
        assertEquals(3, CollectionUtils.max(List.of(1, 3, 2)));
        List<Number> destination = new ArrayList<>();
        CollectionUtils.copier(destination, List.of(4, 5));
        assertEquals(List.of(4, 5), destination);
        assertThrows(java.util.NoSuchElementException.class,
                () -> CollectionUtils.max(List.of()));
    }
}
