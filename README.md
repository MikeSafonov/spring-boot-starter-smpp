# spring-boot-starter-smpp

This Spring Boot starter can be used by any Spring Boot application that wants to send SMS messages 
using [SMPP](https://en.wikipedia.org/wiki/Short_Message_Peer-to-Peer). [SMPP v3.4 spec](http://docs.nimta.com/SMPP_v3_4_Issue1_2.pdf)

## Features

- [x] Sending message with delivery receipt 
- [x] Sending datagram message
- [ ] Sending silent message
- [ ] Multiply SMPP connection
- [ ] SMPP connection load balancing

## Key abstraction

This starter provide several abstraction:

### SenderClient

This interface represents smpp protocol transmitter client. This is entry point to sending any messages.
Spring-boot-starter-smpp comes with several implementations:


<dl>
  <dt>DefaultSenderClient</dt>
  <dd>This is default implementation which create smpp connection and performing all requests</dd>
  
  <dt>TestSenderClient</dt>
  <dd>Implementation of SenderClient which should be used for testing purpose. 
  This client may provide real smpp connection via incoming implementation of SenderClient. 
  Every incoming request will be redirected to real SenderClient only if list of allowed phone numbers contains message destination phone. 
  Otherwise response will be generated via **SmppResultGenerator**</dd>
    
  <dt>MockSenderClient</dt>
  <dd>Implementation of SenderClient which not perform any connection via smpp and only generate response by using SmppResultGenerator</dd>
</dl>


### SmppResultGenerator

Implementations of this interface is used by MockSenderClient or TestSenderClient clients to generate request response - MessageResponse. Starter
by default use **AlwaysSuccessSmppResultGenerator** which always generate success response with random smsc message id.

You can implement own **SmppResultGenerator** to add custom logic.

### TypeOfAddressParser

**DefaultSenderClient** use implementation of this class to detect TON and NPI parameters for source and destination address of message.
Starter provide **DefaultTypeOfAddressParser** and **UnknownTypeOfAddressParser** implementations. 
**DefaultTypeOfAddressParser** supports international and alphanumeric ton parameters, otherwise return UNKNOWN ton/npi. **UnknownTypeOfAddressParser**
always return UNKNOWN ton/npi. This means what your SMS center must detect this parameters by himself.


### ResponseClient

This abstraction represent connection via SMPP with RECEIVER type. Key purpose is listening delivery receipts. Starter implements
this interface by **DefaultResponseClient**. This class keeping smpp connection and pushing delivery receipts to **DeliveryReportConsumer**.

### DeliveryReportConsumer

This class dedicated to handle DeliveryReport on client side. Client may build custom logic on receiving delivery receipts by implementing this interface.
If client doesnt provide any implementation of **DeliveryReportConsumer** then starter use **NullDeliveryReportConsumer** which ignore any delivery receipts.

## Configuration
TODO
