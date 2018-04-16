package br.com.ifood.ifoodconnection.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("ifood.connection.mqtt")
public class IfoodMqttProperty {

    private String url;

    private String username;

    private String password;

    private String clientId;

    private String[] topics;

}
