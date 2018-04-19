package br.com.ifood.ifoodconnection.config;

import br.com.ifood.ifoodconnection.mqtthandler.RestaurantConnectionMqttHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;

@Profile("!test")
@Configuration
@EnableConfigurationProperties(IfoodMqttProperty.class)
public class MqttConfig {

    @Autowired
    private IfoodMqttProperty mqttProperty;

    @Bean
    public IntegrationFlow mqttInFlow(RestaurantConnectionMqttHandler restaurantConnectionHandler) {
        return IntegrationFlows.from(mqttInbound())
                .handle(restaurantConnectionHandler.handle())
                .get();
    }

    @Bean
    public MessageProducerSupport mqttInbound() {
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(mqttProperty.getClientId(),
                mqttClientFactory(), mqttProperty.getTopics());
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        return adapter;
    }

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setServerURIs(mqttProperty.getUrl());
        factory.setUserName(mqttProperty.getUsername());
        factory.setPassword(mqttProperty.getPassword());
        return factory;
    }
}
