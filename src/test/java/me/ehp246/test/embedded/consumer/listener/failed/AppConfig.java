package me.ehp246.test.embedded.consumer.listener.failed;

import java.util.concurrent.CompletableFuture;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import me.ehp246.aufkafka.api.annotation.EnableByKafka;
import me.ehp246.aufkafka.api.annotation.EnableForKafka;
import me.ehp246.aufkafka.api.annotation.EnableForKafka.Inbound;
import me.ehp246.aufkafka.api.annotation.EnableForKafka.Inbound.At;
import me.ehp246.aufkafka.api.common.Pair;
import me.ehp246.aufkafka.api.consumer.BoundInvocable;
import me.ehp246.aufkafka.api.consumer.InvocationListener.FailedListener;
import me.ehp246.aufkafka.api.consumer.Invoked.Failed;
import me.ehp246.test.embedded.consumer.listener.failed.invocation.FailMsg;
import me.ehp246.test.mock.EmbeddedKafkaConfig;

/**
 * @author Lei Yang
 *
 */
@ComponentScan
@EnableByKafka
@EnableForKafka({ @Inbound(value = @At("embedded"), scan = FailMsg.class, invocationListener = "consumer1") })
@Import(EmbeddedKafkaConfig.class)
class AppConfig {
    public CompletableFuture<Pair<BoundInvocable, Failed>> consumer1Ref = new CompletableFuture<>();

    @Bean("consumer1")
    FailedListener consumer1() {
        return (b, f) -> consumer1Ref.complete(new Pair<>(b, f));
    }
}
