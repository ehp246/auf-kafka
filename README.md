# Auf Kafka
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=ehp246_auf-kafka&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=ehp246_auf-kafka)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=ehp246_auf-kafka&metric=bugs)](https://sonarcloud.io/summary/new_code?id=ehp246_auf-kafka)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=ehp246_auf-kafka&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=ehp246_auf-kafka)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=ehp246_auf-kafka&metric=coverage)](https://sonarcloud.io/summary/new_code?id=ehp246_auf-kafka)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=ehp246_auf-kafka&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=ehp246_auf-kafka)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=ehp246_auf-kafka&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=ehp246_auf-kafka)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=ehp246_auf-kafka&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=ehp246_auf-kafka)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=ehp246_auf-kafka&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=ehp246_auf-kafka)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=ehp246_auf-kafka&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=ehp246_auf-kafka)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=ehp246_auf-kafka&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=ehp246_auf-kafka)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=ehp246_auf-kafka&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=ehp246_auf-kafka)

## Introduction
Auf Kafka is aimed at Spring-based applications that need to implement a messaging-based architecture on top of Apache Kafka platform. It offers an annotation-driven and declarative programming model that abstracts away low-level API's. It offers a set of annotations and conventions with which application developers can declare the intentions via plain Java classes.

## Quick Start

Assuming you have a Spring Boot application ready, add dependency:

* [Auf Kafka](https://mvnrepository.com/artifact/me.ehp246/auf-kafka)

### Client Application

**Enable by `@EnableByKafka`**

```
@EnableByKafka
@SpringBootApplication
class ClientApplication {
    public static void main(final String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }
}
```

**Define `ProducerConfigProvider`**

```java
    @Bean
    ProducerConfigProvider producerConfigProvider(final KafkaConfig config) {
        final Map<String, Object> map = Map.of(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.bootStrapServers());
        return name -> map;
    }
```

**Declare by `@ByKafka`**

```
@@ByKafka("${app.task.inbox}")
public interface TaskInbox {
    void runJob(Job job);
}
```
At this point, you have a Kafka client proxy that when invoked will send a message
* to the topic named by Spring property `app.task.inbox`
* with the key as `RunJob`
* with the value as `job` serialized to JSON

The proxy won't do anything by itself, so the next step is to inject it...

```java
@Service
public class AppService {
    @Autowired
    private TaskInbox taskInbox;
    ...
}
```

### Server Application

**Enable by `@EnableForKafka`.**

```java
@EnableForKafka(@Inbound(@From("${app.task.inbox}"))
@SpringBootApplication
class ServerApplication {
    public static void main(final String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }
}
```

**Define `ConsumerConfigProvider`**

```java
    @Bean
    ConsumerConfigProvider consumerConfigProvider(final KafkaConfig config) {
        final Map<String, Object> map = Map.of(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.bootStrapServers(),
                ConsumerConfig.GROUP_ID_CONFIG, config.groupId());
        return name -> map;
    }
```


**Implement business logic by message key**

```java
@ForKey
class RunJob {
    public void invoke(Job job) {
        //Do the work
    }
}
```

Details can be found at the project's [Wiki](https://github.com/ehp246/auf-kafka/wiki).

## Runtime
The latest version requires the following to run:
* <a href='https://openjdk.org/projects/jdk/21/'>JDK 21</a>
* <a href='https://mvnrepository.com/artifact/org.springframework'>Spring 6.2</a>: Bean and Context
* <a href='https://mvnrepository.com/artifact/org.apache.kafka/kafka-clients'>Apache Kafka client</a>: Version 3.6
* <a href='https://mvnrepository.com/artifact/com.fasterxml.jackson'>Jackson 2</a>: Core and Databind

This library works with Apache Kafka client directly. It does not need <a href="https://mvnrepository.com/artifact/org.springframework.kafka/spring-kafka">Spring Kafka</a>.

## Release
The release binaries can be found on [Maven Central](https://mvnrepository.com/artifact/me.ehp246/auf-kafka).

### To build a release

Make sure GPG agent is running:

```shell
gpg-connect-agent /bye
gpg --card-status
```

Build the release:

```shell
mvn clean deploy
```

Release the build from Sonatype web site.
