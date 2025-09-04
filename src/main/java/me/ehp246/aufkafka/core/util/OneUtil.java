package me.ehp246.aufkafka.core.util;

import java.lang.annotation.Annotation;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import me.ehp246.aufkafka.api.annotation.ByKafka;

/**
 * @author Lei Yang
 *
 */
public final class OneUtil {
    private OneUtil() {
        super();
    }

    public static <T> Stream<T> streamOfNonNull(Collection<T> set) {
        return Optional.ofNullable(set).map(Collection::stream).orElseGet(Stream::empty).filter(Objects::nonNull);
    }

    public static String firstUpper(final String value) {
        return value == null || value.length() == 0 ? value
                : value.substring(0, 1).toUpperCase(Locale.US) + value.substring(1);
    }

    /**
     * Returns <code>null</code> if argument is <code>null</code>.
     * 
     * @param value
     * @return
     */
    public static String toString(final Object value) {
        return toString(value, null);
    }

    public static String toString(final byte[] value) {
        return value == null ? null : new String(value, StandardCharsets.UTF_8);
    }

    public static String toString(final Object value, final String def) {
        return value == null ? def : value.toString();
    }

    public static String nullIfBlank(final Object value) {
        return nullIfBlank(toString(value));
    }

    public static String nullIfBlank(final String value) {
        return value != null && !value.isBlank() ? value : null;
    }

    public static boolean hasValue(final String value) {
        return value != null && !value.isBlank();
    }

    public static boolean hasValue(final Object[] value) {
        return value != null && value.length > 0;
    }

    public static <T> T firstOrNull(final T[] value) {
        return value != null && value.length > 0 ? value[0] : null;
    }

    public static String getIfBlank(final String value, final Supplier<String> supplier) {
        if (value == null || value.isBlank()) {
            return supplier.get();
        } else {
            return value;
        }
    }

    public static String orIfBlank(final String[] values, final Supplier<String> supplier) {
        if (values == null || values.length == 0) {
            return supplier.get();
        } else {
            return values[0];
        }
    }

    public static Stream<String> streamValues(final Collection<String> values) {
        return Optional.ofNullable(values).orElseGet(ArrayList::new).stream().filter(OneUtil::hasValue);
    }

    public static List<String> listValues(final Collection<String> values) {
        return streamValues(values).toList();
    }

    public static <V> V orElse(final Callable<V> callable, final V v) {
        try {
            return callable.call();
        } catch (final Exception e) {
            return v;
        }
    }

    public static <V> V orThrow(final Callable<V> callable) {
        try {
            return callable.call();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <V> V orThrow(final Callable<V> callable, final String msg) {
        try {
            return callable.call();
        } catch (final Exception e) {
            throw new RuntimeException(msg, e);
        }
    }

    public static <V, X extends RuntimeException> V orThrow(final Callable<V> callable,
            final Function<Exception, X> fn) {
        try {
            return callable.call();
        } catch (final Exception e) {
            throw fn.apply(e);
        }
    }

    public static boolean isPresent(final List<? extends Annotation> annos, final Class<? extends Annotation> type) {
        return OneUtil.filter(annos, type).findAny().isPresent();
    }

    public static Stream<? extends Annotation> filter(final List<? extends Annotation> annos,
            final Class<? extends Annotation> type) {
        return Optional.ofNullable(annos).filter(Objects::nonNull).orElseGet(ArrayList::new).stream()
                .filter(anno -> anno.annotationType() == type);
    }

    public static String producerInterfaceBeanName(final Class<?> byRestInterface) {
        final var name = byRestInterface.getAnnotation(ByKafka.class).name();
        if (!name.isBlank()) {
            return name;
        }

        final char c[] = byRestInterface.getSimpleName().toCharArray();
        c[0] = Character.toLowerCase(c[0]);

        return new String(c);
    }

    /**
     * Parse an Integer-range string into a {@linkplain Stream} of
     * {@linkplain Integer}. E.g., "0-5,10-15".
     */
    public static Stream<Integer> parseIntegerRange(final String rangeString) {
        final var partsStrings = rangeString.split(",");
        if (partsStrings.length == 1 && !partsStrings[0].contains("-")) {
            return Stream.of(Integer.parseInt(partsStrings[0].trim()));
        }

        final List<Integer> parts = new ArrayList<>();
        for (String part : partsStrings) {
            if (part.contains("-")) {
                final var startEnd = part.split("-");

                if (startEnd.length != 2) {
                    throw new RuntimeException("Only one hyphen allowed for a range: " + part);
                }

                final var start = Integer.parseInt(startEnd[0].trim());
                final var end = Integer.parseInt(startEnd[1].trim());

                if (end < start) {
                    throw new RuntimeException("Invalid range: " + part);
                }
                for (var i = start; i <= end; i++) {
                    parts.add(i);
                }
            } else {
                parseIntegerRange(part).forEach(parts::add);
            }
        }

        return parts.stream();
    }

}
