package me.ehp246.test.embedded.consumer.enable.basic.partition;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kafka.config")
record KafkaConfig(String topic) {
}