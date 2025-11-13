package com.example.casagrpctask.presentation.grpc.config;

import com.example.casagrpctask.presentation.grpc.handler.UserValidatorHandler;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class ServerConfig {
    @Autowired
    private UserValidatorHandler userValidatorHandler;

    @Bean
    public Server grpcServer() throws IOException {
        return ServerBuilder.forPort(9091)
                .addService(userValidatorHandler)
                .build()
                .start();
    }
}
