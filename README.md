# iFood Backend Test - Connection

Our goal is to be the best delivery company in the world. In order to achieve that we must do our best to manage the connection between iFood platform and all of our 100.000 restaurants.

## Tasks

Your task is to develop one (or more, feel free) service(s) to:
* handle connection between Restaurant and iFood Platform
* store and delete schedules of unavailabilities
* consolidate and provide data about the restaurants' connection

You also will have to emulate a client (keep it simple) for the Restaurant. The client must:
* guarantee “keep-alive” interactions with your service
* create and cancel schedules of unavailabilities

Fork this repository and submit your code.

## Business Rules

* In iFood Platform, the opening hour for the Restaurants are from 10:00 am to 11:00 pm.
* A Restaurant may be **available**/**unavailable**.
* A Restaurant may scheduled the status **unavailable**, due to the following reasons:
  - lack of delivery staff
  - connection issues (bad internet)<sup>1</sup>
  - overloaded due to offline orders
  - holidays
* A Restaurant may also be **online**/**offline**:
  - In order to a Restaurant be considered **online**, it must be inside the opening hour AND have interacted with our platform (aka sent a keep-alive signal) in the last two minutes AND its status should be **available**. Otherwise, it should be considered **offline**.
* The Restaurants are ranked according to the time they spent **offline**, inside the opening hour, without a scheduled **unavailability**.
* Example:
![restaurant connection timeline](https://www.lucidchart.com/publicSegments/view/4d48ac9c-e543-4531-abd5-eff0d9788ea6/image.png)

## Requirements

In order to handle 100.000 simultaneously connections, we would like to use an IOT protocol (like MQTT or XMPP) for this scenario.

You will have to keep the connection state of the Restaurants freshly available for whoever wants to query it. We advise the use of some type of compute grid to do this processing in parallel by the instances of the service. Apache Ignite might be a good choice. Or not.

This service needs to answer promptly:
* Given a list of Restaurants, whether they are **online** or **offline**.
* Given a specific Restaurant, its unavailability history.
* Reports for iFood commercial team so they can measure how our Restaurants are ranked, according to the amount of time they spent **offline** (see the diagram above, the red section on the "Restaurant Status" timeline).

## Justificativas de implementação

Segui as boas práticas de design e implementação de software, principalmente as citadas durante a entrevista. 
Levei em consideração utilizar as tecnologias que são utilizadas no iFood.
O modelo foi desenhado e implementado seguindo práticas de DDD e TDD.
Implementei alguns padrões de projeto do GOF e Microservices, como strategy e Event Sourcing(explico melhor em seguida) .

Para facilidade de avaliação e execução do serviço, utilizei Docker para levantar serviço dependentes necessários (Rabbit, Mosquitto, Redis, MySQL).

Segue uma visão geral das tecnologias e frameworks utilizados:

Java 9
Spring Boot 2
Spring 5 (Web MVC, Context, Data...)
JPA e Hibernate
Flyway (migrations)
MySQL
H2 (banco para testes)
Mosquitto (Mqtt)
Redis (Opção ao Ignite)
RabbitMQ
Docker (Levantar dependencias)
Maven

Como o código está organizado:

Seguindo o conceito de agregados do DDD, identifiquei um agregado composto por Restaurante(entity root), Schedule e Histórico junto com seus objetos de valor (Status, Horário de abertura, Estado, etc…). Por tanto, existe apenas um repositório de dados que gerencia esse agregado.

As regras foram colocadas ao máximo no dominio. Regras de aplicação ou User cases que precisavam ser orquestradas de alguma forma foram colocadas em Application Services (ex: RestaurantService). Existe também Domain services que aplicam regras especificas do dominio (ex: pacote /services/status/).

Implementação Keep-Alive:

Desenvolvida utilizando o protocolo MQTT. Quando o cliente se conecta é enviada uma mensagem para o broker em um tópico (connectionActive) com o identificador do restaurante e uma flag se ele está conectado ou não. A aplicação ifood-connection escuta esse tópico e ao receber a mensagem marca a flag no restaurante e calcula se ele pode estar online naquele momento baseado na regras. O cliente foi configurado com um Keep-Alive(feature do MQTT) de 120 segundos e com uma Last Will Message quando acontecer alguma perca de conexão inesperada o broker automaticamente envia uma mensagem a outro tópico (connectionLost), com o identificador do restaurante e com a flag de conectado false. Da mesma forma o servidor escuta esse tópico e marca o Restaurante cliente como offline. o tópico connectionLost recebe a mesma mensagem quando um cliente solicitar o disconnect(fechar o aplicativo) e segue o mesmo fluxo. Dessa forma a aplicação em si consegue escalar bem pois está muito desacoplada dos clientes Restaurante e a parte mais pesada está sendo feita pelo broker, que mantém o estado das conexões.

Implementação Schedule Unavailable:

Os Schedules nesse cenário foi um bom desafio, pois pode não tratar-se de apenas 10, 100, 200 “agendamentos de job” e sim milhares de agendamentos. Existe duas maneiras clássicas de resolver esse problema: 1. pooling a cada minuto no banco de dados, verificar os schedules e aplicar os que ainda não foram aplicados. 2. Criar jobs em memória utilizando um famoso “Quartz”. Porém não são soluções elegantes e escaláveis. Por tanto, o schedules resolvi implementar utilizando Event Sourcing com RabbitMQ, onde ele me avisa quando tiver que aplicar um Schedule. Para atender isso, utilizei um plugin do Rabbit que permite publicar uma mensagem em uma fila com tempo programado. Quando o schedule é criado ele é salvo e em seguida é publicado um evento com delay da data de inicio do schedule em uma exchange(tópico) e quando esse delay é atingindo é que de fato ele publica nas filas com binding. O ifood-connection escuta uma fila e aplica o inicio da indisponibilidade e em seguida publica outra mensagem com a data de fim. Ou seja, a aplicação não mantém estado, totalmente stateless.
OBS: Rabbit armazena as mensagens em disco e caso aconteça uma queda é possível recuperar. É um broker que aguenta uma quantidade de mensagens considerável e que atende bem nesse cenário, podendo escalar em cluster.
OBS: É ciente que existem mais regras de Schedules (como a exclusão) e foi previsto na implementação.

Alta disponibilidade da informação online/offline:

Para garantir uma alta disponibilidade da informação (imagino que toda busca vai bater nesse serviço) foi coloquei uma camada de cache com Redis (Database in-memory). Todas as mudanças de estado fazem com que o cache seja “esquentado” e evite o máximo de acesso ao banco de dados. Serviços com baixa quantidade de acesso (relatórios) não foi colocado em cache para economizar recursos. É escalável e disponibiliza cluster.

Implementação Histórico:
Todas as mudanças de estado do Restaurante é adicionado ao histórico e podendo ser consultado.

Simular clientes:
Ao instalar a aplicação será inserido 9 restaurantes para realizar testes. Podem ser consultados no endpoint GET …/restaurants.
Implementei um RestaurantClient e um Rest Controller para simular o connect e disconnect de cada Restaurante. Esses estão dentro do Application mesmo, pois é apenas para o objetivo de simular o cliente.

Para ver todos os endpoints, importe uma collection do postman disponível no repositório na pasta “/postman"

Rodar a aplicação:

Precisa ter instalado:
* Docker e docker compose
* Java 9
Comandos para rodar:
* ​docker-compose up (sobe todos os serviços dependentes  verifique se as portas mapeadas no docker-compose.yml já não está em uso no host)
* ./mvnw spring-boot:run (roda a aplicação. ps: não ative nenhum profile)
