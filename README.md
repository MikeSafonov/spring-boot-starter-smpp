# spring-boot-starter-smpp
[![Maven Central](https://img.shields.io/maven-central/v/com.github.mikesafonov/spring-boot-starter-smpp.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.mikesafonov%22%20AND%20a:%22spring-boot-starter-smpp%22)
[![codecov](https://codecov.io/gh/MikeSafonov/spring-boot-starter-smpp/branch/master/graph/badge.svg)](https://codecov.io/gh/MikeSafonov/spring-boot-starter-smpp)
[![Travis-CI](https://travis-ci.com/MikeSafonov/spring-boot-starter-smpp.svg?branch=master)](https://travis-ci.com/MikeSafonov/spring-boot-starter-smpp)
[![Conventional Commits](https://img.shields.io/badge/Conventional%20Commits-1.0.0-yellow.svg)](https://conventionalcommits.org)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=MikeSafonov_spring-boot-starter-smpp&metric=alert_status)](https://sonarcloud.io/dashboard?id=MikeSafonov_spring-boot-starter-smpp)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=MikeSafonov_spring-boot-starter-smpp&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=MikeSafonov_spring-boot-starter-smpp)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=MikeSafonov_spring-boot-starter-smpp&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=MikeSafonov_spring-boot-starter-smpp)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=MikeSafonov_spring-boot-starter-smpp&metric=security_rating)](https://sonarcloud.io/dashboard?id=MikeSafonov_spring-boot-starter-smpp)

[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=MikeSafonov_spring-boot-starter-smpp&metric=bugs)](https://sonarcloud.io/dashboard?id=MikeSafonov_spring-boot-starter-smpp)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=MikeSafonov_spring-boot-starter-smpp&metric=code_smells)](https://sonarcloud.io/dashboard?id=MikeSafonov_spring-boot-starter-smpp)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=MikeSafonov_spring-boot-starter-smpp&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=MikeSafonov_spring-boot-starter-smpp)

[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=MikeSafonov_spring-boot-starter-smpp&metric=duplicated_lines_density)](https://sonarcloud.io/dashboard?id=MikeSafonov_spring-boot-starter-smpp)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=MikeSafonov_spring-boot-starter-smpp&metric=ncloc)](https://sonarcloud.io/dashboard?id=MikeSafonov_spring-boot-starter-smpp)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=MikeSafonov_spring-boot-starter-smpp&metric=sqale_index)](https://sonarcloud.io/dashboard?id=MikeSafonov_spring-boot-starter-smpp)


This Spring Boot starter can be used by any Spring Boot application that wants to send SMS messages 
using [SMPP](https://en.wikipedia.org/wiki/Short_Message_Peer-to-Peer). [SMPP v3.4 spec](http://docs.nimta.com/SMPP_v3_4_Issue1_2.pdf)

## Features

- Sending message with delivery receipt 
- Sending datagram message (without delivery receipt)
- Sending silent message
- Sending flash message
- Cancel message
- Multiply SMPP connection
- SMPP connection load balancing

## Usage

Add the following dependency to your project:

Maven:
    
    <dependency>
      <groupId>com.github.mikesafonov</groupId>
      <artifactId>spring-boot-starter-smpp</artifactId>
      <version>1.2.1</version>
    </dependency>
    
Gradle:

    dependencies {
        implementation 'com.github.mikesafonov:spring-boot-starter-smpp:1.2.1'
    }
    
Configure spring-boot application via properties (see [Configuration](#Configuration) section).

Use [SenderManager](#SenderManager) to send SMS:

    public class RoundRobinApplicationService {
        private final SenderManager senderManager;
    
        public void sendMessage(String from, String to, String text) {
            Message message = Message.simple(text)
                    .from(from)
                    .to(to)
                    .build();
            senderManager.getClient().send(message);
        }
    }

## Configuration

The following tables show the available configuration:

| Configuration                                   | Description                                                                              | Default                                                              |
|-------------------------------------------------|------------------------------------------------------------------------------------------|----------------------------------------------------------------------|
| `smpp.defaults`                                 | Default smpp connection properties                                                       |                                                                      |
| `smpp.defaults.ucs2Only`                        | Using ucs2 encoding only or not                                                          | `false`                                                              |
| `smpp.defaults.maxTry`                          | Number of attempts to reconnect if smpp session is closed                                | `5`                                                                  |
| `smpp.defaults.connectionMode`                  | Client connection mode ([`STANDARD`](#DefaultSenderClient), [`TEST`](#TestSenderClient), [`MOCK`](#MockSenderClient))   | `STANDARD`                          |
| `smpp.defaults.windowSize`                      | Smpp connection window size                                                              | `90`                                                                 |
| `smpp.defaults.loggingPdu`                      | Is logging smpp pdu                                                                      | `false`                                                              |
| `smpp.defaults.loggingBytes`                    | Is logging smpp bytes                                                                    | `false`                                                              |
| `smpp.defaults.rebindPeriod`                    | Connection rebind period (Duration)                                                      | `90s`                                                                |
| `smpp.defaults.requestTimeout`                  | Request timeout (Duration)                                                               | `5s`                                                                 |
| `smpp.defaults.allowedPhones`                   | Array of phones to send. Using only if `connectionMode` is `TEST`                        | `[]`                                                                 |
| `smpp.defaults.connectionType`                  | Type of smpp connections(`TRANSCEIVER` or `TRANSMITTER_RECEIVER`)                        | `TRANSMITTER_RECEIVER`                                                                 |
| `smpp.connections`                              | Map of SMSC connections                                                                  |                                                                      |
| `smpp.connections.<name>.credentials`           | SMSC connection credentials                                                              |                                                                      |
| `smpp.connections.<name>.credentials.host`      | SMSC host                                                                                |                                                                      |
| `smpp.connections.<name>.credentials.port`      | SMSC port                                                                                |                                                                      |
| `smpp.connections.<name>.credentials.username`  | SMSC username                                                                            |                                                                      |
| `smpp.connections.<name>.credentials.password`  | SMSC password                                                                            |                                                                      |
| `smpp.connections.<name>.ucs2Only`              | Using ucs2 encoding only or not                                                          | `false`                                                              |
| `smpp.connections.<name>.maxTry`                | Number of attempts to reconnect if smpp session is closed                                | `5`                                                                  |
| `smpp.connections.<name>.connectionMode`        | Client`s connection mode                                                                 | `STANDARD`   see `com.github.mikesafonov.smpp.config.ConnectionMode` |
| `smpp.connections.<name>.windowSize`            | Smpp connection window size                                                              | `90`                                                                 |
| `smpp.connections.<name>.loggingPdu`            | Is logging smpp pdu | `false`                                                            |                                                                      |
| `smpp.connections.<name>.loggingBytes`          | Is logging smpp bytes | `false`                                                          |                                                                      |
| `smpp.connections.<name>.rebindPeriod`          | Connection rebind period (Duration)                                                      | `90s`                                                                |
| `smpp.connections.<name>.requestTimeout`        | Request timeout (Duration)                                                               | `5s`                                                                 |
| `smpp.connections.<name>.allowedPhones`         | Array of phones to send. Using only if `connectionMode` is `TEST`                        | `[]`                                                                 |
| `smpp.connections.<name>.connectionType`        | Type of smpp connections(`TRANSCEIVER` or `TRANSMITTER_RECEIVER`)                        |                                                                  |
| `smpp.setupRightAway`                           | Should setup smpp clients after creation and fail fast if connection cant be established | `true`                                                               |

Configuration example for `.properties` file:

    smpp.connections.one.credentials.host=localhost
    smpp.connections.one.credentials.username=user
    smpp.connections.one.credentials.password=pass
    smpp.connections.one.credentials.port=1111
    smpp.connections.two.credentials.host=localhost
    smpp.connections.two.credentials.username=user2
    smpp.connections.two.credentials.password=pass2
    smpp.connections.two.credentials.port=2222

Configuration example for `.yaml` file:

    smpp:
        default:
            maxTry: 10
            ucs2Only: true
        connections:
            one:
               credentials:
                    host: localhost
                    username: user
                    password: pass
                    port: 1111
            two:
               credentials:
                    host: localhost
                    username: user2
                    password: pass2
                    port: 2222

## Build

### Build from source

You can build application using following command:

    ./gradlew clean build -x signArchives
    
#### Requirements:

JDK >= 1.8

### Unit tests

You can run unit tests using following command:

    ./gradlew test
    
### Mutation tests

You can run mutation tests using following command:

    ./grdlew pitest

You will be able to find pitest report in `build/reports/pitest/` folder.

### Integration tests

You can run integration tests using following command:

    ./grdlew testIntegration

## Key abstractions

This starter provides several abstractions:

### SenderClient

This interface represents smpp protocol _TRANSMITTER_ or _TRANSCEIVER_ (see `connectionType` property) client. 
This is entry point to sending any messages.
Spring-boot-starter-smpp comes with several implementations:

![class diagram](http://www.plantuml.com/plantuml/proxy?src=https://raw.github.com/MikeSafonov/spring-boot-starter-smpp/master/diagrams/senderclient_class.txt)

#### DefaultSenderClient

This is `default` implementation. `DefaultSenderClient` creates real smpp connection and performing all requests.

#### TestSenderClient

`TestSenderClient` should be used for testing purpose. 
`TestSenderClient` client may provide real smpp connection via proxy implementation of `SenderClient`. 
Every incoming request will be redirected to real `SenderClient` only if destination phone contains in list of allowed phone (`smpp.connections.<name>.allowedPhones` property). 
Otherwise response will be generated by [SmppResultGenerator](#SmppResultGenerator).
  
#### MockSenderClient

`MockSenderClient` does not perform any connection via smpp and only generate response using [SmppResultGenerator](#SmppResultGenerator).

### SmppResultGenerator

Implementations of this interface is used by [MockSenderClient](#MockSenderClient) and [TestSenderClient](#TestSenderClient) clients
 to generate request response. 

Starter `by default` use **AlwaysSuccessSmppResultGenerator** which always generate success response with random smsc message id.

You can implement own **SmppResultGenerator** to add custom logic.

### TypeOfAddressParser

[DefaultSenderClient](#DefaultSenderClient) use implementation of `TypeOfAddressParser` 
to detect TON and NPI parameters for source and destination address of message.
Starter provide **DefaultTypeOfAddressParser** and **UnknownTypeOfAddressParser** implementations. 

`By default` starter use **DefaultTypeOfAddressParser**. 
**DefaultTypeOfAddressParser** supports international and alphanumeric ton parameters, otherwise return UNKNOWN ton/npi. 

**UnknownTypeOfAddressParser** always return _UNKNOWN_ ton/npi. This means what your SMS center must detect this parameters by himself.

### ResponseClient

This abstraction represent connection via SMPP with _RECEIVER_ or _TRANSCEIVER_ (see `connectionType` property) type. 
Key purpose is listening delivery receipts. 
`By default` starter use **DefaultResponseClient**. This class keeping smpp connection and pushing all incoming PDU to [SmppSessionListener](#SmppSessionListener).

### SmppSessionListener

This class dedicated to listening all incoming PDU traffic. `By default` starter use **ResponseSmppSessionHandler**. This class 
find delivery receipts and push to **DeliveryReportConsumer**.

**DeliveryReportConsumer** dedicated to handle _DeliveryReport_ on client side. Client may build custom logic on receiving delivery receipts by implementing this interface.
Starter use `by default` **NullDeliveryReportConsumer** if client doesnt provide any implementation of **DeliveryReportConsumer**.
**NullDeliveryReportConsumer** ignore any delivery receipts.

You can use custom **SmppSessionListener** by creating appropriate bean.

### SenderManager

This is high level abstraction over sender clients.

![class diagram](http://www.plantuml.com/plantuml/proxy?src=https://raw.github.com/MikeSafonov/spring-boot-starter-smpp/master/diagrams/sendermanager_class.txt)

This starter comes with one default implementation - **StrategySenderManager**. **StrategySenderManager** holds
list of smsc connections and return sender client based on some rules which implemented by **IndexDetectionStrategy**

There are two default implementation of **IndexDetectionStrategy** - **RandomIndexDetectionStrategy**(return random sender client) and **RoundRobinIndexDetectionStrategy**
(return sender client based on round and robbin algorithm). **RoundRobinIndexDetectionStrategy** strategy used `by default`.

## Contributing

Feel free to contribute. 
New feature proposals and bug fixes should be submitted as GitHub pull requests. 
Fork the repository on GitHub, prepare your change on your forked copy, and submit a pull request.

**IMPORTANT!**
>Before contributing please read about [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0-beta.2/) / [Conventional Commits RU](https://www.conventionalcommits.org/ru/v1.0.0-beta.2/)
