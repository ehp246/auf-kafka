package me.ehp246.aufkafka.api.serializer;

/**
 * @author Lei Yang
 */
public interface ObjectOfJson {
    Object value();

    default TypeOfJson typeOf() {
        return TypeOfJson.newInstance(this.value() == null ? null : this.value().getClass(), null);
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

    static ObjectOfJson newInstance(Object object) {
        final var typeOf = TypeOfJson.newInstance(object == null ? null : object.getClass(), null);

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
