# iFood Backend Test - Connection

Our goal is to be the best delivery company in the world. In order to achieve that we must do our best to manage the connection between iFood platform and all of our 100.000 restaurants. 

## Tasks

Your task is to develop a microservice to handle connection between Restaurant and iFood Platform. 

You also will have to emulate a client for the Restaurant. The client must:
* guarantee “keep-alive” interactions with your microservice
* schedule and cancel unavailabilities

## Bussiness Rules

* In iFood Platform, the opening hour for the Restaurants are from 10:00 am to 11:00 pm. 
* A Restaurant may be **available**/**unavailable**.
* A Restaurant may scheduled the status **unavailable**, due to the following reasons:
  - lack of delivery staff
  - connection issues (bad internet)<sup>1</sup>
  - overloaded due to offline orders 
  - holidays
* A Restaurant may also be **online**/**offline**:
  - In order to a Restaurant be considered **online**, it must be inside the opening hour AND have interacted with our platform in the last two minutes AND its status should be **available**. Otherwise, it should be considered **offline**.
* The Restaurants are ranked according to the time they spent **offline**, inside the opening hour, without a scheduled **unavailability**.

## Non functional requirements

In order to handle 100.000 simultaneously connections, we would like to use an IOT protocol (like MQTT or XMPP) for this scenario. 

You will have to keep the connection state of the Restaurants freshly available for whoever wants to query it. We advise the use of some type of compute grid to do this processing in parallel by the instances of the service. Apache Ignite might be a good choice. 

This microservice needs to answer promptly, given a list of Restaurants whether are opened or closed (taking into account the keep-alive and unavailability status). The Restaurant client should be able to ask for its unavailability history. Furthermore, we would like to extract reports for iFood commercial team so they can measure how much our Restaurants are opened, offline and unavailable.
