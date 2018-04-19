package br.com.ifood.ifoodconnection.config;

import io.moquette.server.Server;
import io.moquette.server.config.ClasspathResourceLoader;
import io.moquette.server.config.IConfig;
import io.moquette.server.config.IResourceLoader;
import io.moquette.server.config.ResourceLoaderConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;

@Profile("dev")
@Configuration
public class MqttBrokerEmbeddedConfig {

    private Server serverMqtt;

    @PostConstruct
    public void start() throws IOException {
        IResourceLoader classpathLoader = new ClasspathResourceLoader();
        IConfig classPathConfig = new ResourceLoaderConfig(classpathLoader);
        this.serverMqtt = new Server();
        this.serverMqtt.startServer(classPathConfig);
    }

    @PreDestroy
    public void stop() {
        serverMqtt.stopServer();
    }
}
