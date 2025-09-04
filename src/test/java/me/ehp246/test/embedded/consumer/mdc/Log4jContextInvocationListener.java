package me.ehp246.test.embedded.consumer.mdc;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.stereotype.Service;

import me.ehp246.aufkafka.api.consumer.BoundInvocable;
import me.ehp246.aufkafka.api.consumer.InvocationListener;
import me.ehp246.aufkafka.api.consumer.Invoked.Completed;
import me.ehp246.aufkafka.api.consumer.Invoked.Failed;

/**
 * @author Lei Yang
 *
 */
@Service
class Log4jContextInvocationListener implements InvocationListener.CompletedListener, InvocationListener.FailedListener,
        InvocationListener.InvokingListener {
    private CompletableFuture<Map<String, String>> ref = new CompletableFuture<>();
    private final Map<String, String> map = new HashMap<String, String>();

    void reset() {
        map.clear();
        ref = new CompletableFuture<Map<String, String>>();
    }

    @Override
    public void onInvoking(final BoundInvocable bound) {
        map.clear();
        map.putAll(ThreadContext.getContext());
    }

    @Override
    public void onFailed(final BoundInvocable bound, final Failed failed) {
        map.putAll(ThreadContext.getContext());
        ref.complete(map);
    }

    @Override
    public void onCompleted(final BoundInvocable bound, final Completed completed) {
        map.putAll(ThreadContext.getContext());
        ref.complete(map);
    }

    Map<String, String> take() {
        try {
            ref.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        ref = new CompletableFuture<Map<String, String>>();

        return map;
    }
}
