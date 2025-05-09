package me.ehp246.test.embedded.consumer.listener.failed.invocation;

import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import me.ehp246.aufkafka.api.annotation.Applying;
import me.ehp246.aufkafka.api.annotation.Execution;
import me.ehp246.aufkafka.api.annotation.ForKey;
import me.ehp246.aufkafka.api.consumer.InstanceScope;

/**
 * @author Lei Yang
 *
 */
@Service
@ForKey(value = ".*", execution = @Execution(scope = InstanceScope.BEAN))
public class FailMsg {
    public final InterruptedException ex = new InterruptedException("Let it throw");

    @Applying
    public void perform() throws InterruptedException, ExecutionException {
        throw ex;
    }
}
