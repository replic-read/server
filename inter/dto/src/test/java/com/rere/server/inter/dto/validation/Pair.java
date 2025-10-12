package com.rere.server.inter.dto.validation;

/**
 * A typed 2-tuple implementation.
 * @param first The first value.
 * @param second The second value.
 * @param <A> Type of first value.
 * @param <B> Type of second value.
 */
public record Pair<A, B>(A first, B second) {
}
