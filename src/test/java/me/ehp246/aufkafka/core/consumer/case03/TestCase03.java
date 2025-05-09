package me.ehp246.aufkafka.core.consumer.case03;

import me.ehp246.aufkafka.api.annotation.Applying;
import me.ehp246.aufkafka.api.annotation.Execution;
import me.ehp246.aufkafka.api.annotation.ForHeader;
import me.ehp246.aufkafka.api.annotation.ForKey;
import me.ehp246.aufkafka.api.consumer.InstanceScope;

public class TestCase03 {
    @ForKey(value = "test", execution = @Execution(scope = InstanceScope.MESSAGE))
    public interface NotMsg {
    }

    @ForHeader("test")
    public enum NoEnum {
    }

    @ForKey({ "test", "test" })
    public static class NoDup {
        public void apply() {
        }
    }

    @ForHeader("test")
    public static class NoApply {
    }

    @ForHeader("test")
    public static class ManyApply {
        public void apply() {

        }

        public void apply(int i) {

        }
    }

    @ForHeader("test")
    public static class ManyApplying {
        @Applying
        public void apply() {

        }

        @Applying
        public void apply(int i) {

        }
    }
}
