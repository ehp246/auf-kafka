package me.ehp246.aufkafka.api.consumer;

import me.ehp246.aufkafka.api.consumer.Invoked.Completed;
import me.ehp246.aufkafka.api.consumer.Invoked.Failed;

/**
 * @author Lei Yang
 * @since 1.0
 */

public sealed interface Invoked permits Completed, Failed {
    BoundInvocable bound();

    public non-sealed interface Completed extends Invoked {
        Object returned();
    }

    public non-sealed interface Failed extends Invoked {
        Throwable thrown();
    }
}
