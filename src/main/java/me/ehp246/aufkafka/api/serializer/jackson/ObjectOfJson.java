package me.ehp246.aufkafka.api.serializer.jackson;

/**
 * @author Lei Yang
 */
public interface ObjectOfJson {
    Object value();

    default TypeOfJson typeOf() {
        return TypeOfJson.of(this.value() == null ? null : this.value().getClass(), null);
    }

    static ObjectOfJson newInstance(final Object object, final TypeOfJson typeOf) {
        return new ObjectOfJson() {

            @Override
            public Object value() {
                return object;
            }

            @Override
            public TypeOfJson typeOf() {
                return typeOf;
            }

        };
    }

    static ObjectOfJson of(Object object) {
        final var typeOf = TypeOfJson.of(object == null ? null : object.getClass(), null);

        return new ObjectOfJson() {

            @Override
            public Object value() {
                return object;
            }

            @Override
            public TypeOfJson typeOf() {
                return typeOf;
            }

        };
    }
}
