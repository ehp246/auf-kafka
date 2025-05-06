package me.ehp246.aufkafka.core.consumer.case02;

import me.ehp246.aufkafka.api.annotation.Execution;
import me.ehp246.aufkafka.api.annotation.ForEventType;
import me.ehp246.aufkafka.api.annotation.ForKey;
import me.ehp246.aufkafka.api.consumer.InstanceScope;
import me.ehp246.aufkafka.api.consumer.InvocationModel;

public class TestCase02 {
    @ForKey("key-test")
    public static class ForKey01 {
        public void invoke() {
        }
    }

    @ForEventType("event-type-test")
    public static class ForEventType01 {
        public void invoke() {
        }
    }

    @ForKey(value = "key-test", execution = @Execution(invocation = InvocationModel.INLINE, scope = InstanceScope.BEAN))
    @ForEventType("event-type-test")
    public static class ForCombined01 {
        public void invoke() {
        }
    }
}
