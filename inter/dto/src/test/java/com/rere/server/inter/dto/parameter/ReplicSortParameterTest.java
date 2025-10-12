package com.rere.server.inter.dto.parameter;

import com.rere.server.domain.model.impl.ReplicImpl;
import com.rere.server.domain.model.replic.Replic;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReplicSortParameterTest {

    private static List<Replic> replics = IntStream.range(0, 20)
            .mapToObj(i -> {
                try {
                    return (Replic) ReplicImpl.builder()
                            .expirationTimestamp(Instant.now().minusSeconds(i * 1000L))
                            .creationTimestamp(Instant.now().minusSeconds(i * 1000L))
                            .size(i * 1024L)
                            .originalUrl(URI.create("https://" + UUID.randomUUID() + ".com").toURL())
                            .build();
                } catch (MalformedURLException e) {
                    throw new IllegalStateException(e);
                }
            })
            .toList();

    static {
        List<Replic> shuffled = new ArrayList<>(replics);
        Collections.shuffle(shuffled);
        replics = shuffled;
    }

    <U extends Comparable<? super U>> void testWorksWith(Function<? super Replic, ? extends U> field, ReplicSortParameter parameter) {
        List<Replic> expectedAsc = replics.stream()
                .sorted(Comparator.comparing(field))
                .toList();
        List<Replic> actualAsc = replics.stream()
                .sorted(parameter.getComparator(SortDirectionParameter.ASCENDING))
                .toList();
        List<Replic> expectedDesc = replics.stream()
                .sorted(Comparator.comparing(field).reversed())
                .toList();
        List<Replic> actualDesc = replics.stream()
                .sorted(parameter.getComparator(SortDirectionParameter.DESCENDING))
                .toList();

        assertEquals(expectedAsc, actualAsc);
        assertEquals(expectedDesc, actualDesc);
    }

    @Test
    void getComparatorWorksSize() {
        testWorksWith(Replic::getSize, ReplicSortParameter.SIZE);
    }

    @Test
    void getComparatorWorksCreation() {
        testWorksWith(Replic::getCreationTimestamp, ReplicSortParameter.DATE);
    }

    @Test
    void getComparatorWorksExpiration() {
        testWorksWith(Replic::getExpirationTimestamp, ReplicSortParameter.EXPIRATION);
    }

    @Test
    void getComparatorWorksState() {
        testWorksWith(r -> r.getOriginalUrl().toString(), ReplicSortParameter.ORIGIN);
    }

}