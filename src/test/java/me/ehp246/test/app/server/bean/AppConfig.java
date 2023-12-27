package me.ehp246.test.app.server.bean;

import me.ehp246.aufkafka.api.annotation.EnableForKafka;
import me.ehp246.aufkafka.api.annotation.EnableForKafka.Inbound;
import me.ehp246.aufkafka.api.annotation.EnableForKafka.Inbound.From;

/**
 * @author Lei Yang
 *
 */
class AppConfig {
    @EnableForKafka({ @Inbound(@From("topic")) })
    static class Case01 {
    }

    @EnableForKafka({ @Inbound(value = @From("topic1"), name = "topic1.consumer"),
            @Inbound(@From("${topic2}")) })
    static class Case02 {
    }
}
