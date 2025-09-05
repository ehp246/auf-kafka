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
@EnableForKafka({ @Inbound(value = @At(AppConfig.TOPIC), scan = FailMsg.class, invocationListener = "consumer1") })
@Import(EmbeddedKafkaConfig.class)
class AppConfig {
    final static String TOPIC = "f4f5a7d9-fe6e-4458-998f-473dac084365";
    public CompletableFuture<Pair<BoundInvocable, Failed>> consumer1Ref = new CompletableFuture<>();

    @Bean("consumer1")
    FailedListener consumer1() {
        return (b, f) -> consumer1Ref.complete(new Pair<>(b, f));
    }
}
