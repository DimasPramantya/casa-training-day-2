package com.example.casagrpctask.presentation.grpc.handler;

import com.example.casagrpctask.*;
import com.example.casagrpctask.usecase.UserValidatorService;
import io.envoyproxy.pgv.ReflectiveValidatorIndex;
import io.envoyproxy.pgv.ValidationException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;

import java.util.ArrayList;
import java.util.List;

@GrpcService
@Slf4j
@RequiredArgsConstructor
public class UserValidatorHandler extends ValidationServiceGrpc.ValidationServiceImplBase {

    private final ReflectiveValidatorIndex validatorIndex = new ReflectiveValidatorIndex();


    private final UserValidatorService userValidatorService;

    @Override
    public void validateProfile(ValidationRequest request, StreamObserver<ValidationResponse> responseObserver) {
        List<String> errors;
        boolean isValid;

        try {
            UserProfile profile = request.getProfile();
            errors = userValidatorService.validate(profile);
            isValid = errors.isEmpty();

            log.info("Validation completed. Valid: {}", isValid);

        } catch (Exception e) {
            log.error("Unexpected error during validation", e);
            isValid = false;
            errors = new ArrayList<>();
            errors.add("Internal validation error: " + e.getMessage());
        }

        ValidationResponse response = ValidationResponse.newBuilder()
                .setIsValid(isValid)
                .addAllErrors(errors)
                .build();

        log.info("errors: {}", errors);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}
