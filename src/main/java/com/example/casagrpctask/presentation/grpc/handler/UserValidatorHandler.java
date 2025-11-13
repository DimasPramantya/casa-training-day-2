package com.example.casagrpctask.presentation.grpc.handler;

import com.example.casagrpctask.*;
import io.envoyproxy.pgv.ReflectiveValidatorIndex;
import io.envoyproxy.pgv.ValidationException;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;

import java.util.ArrayList;
import java.util.List;

@GrpcService
@Slf4j
public class UserValidatorHandler extends ValidationServiceGrpc.ValidationServiceImplBase {

    private final ReflectiveValidatorIndex validatorIndex = new ReflectiveValidatorIndex();

    @Override
    public void validateProfile(ValidationRequest request, StreamObserver<ValidationResponse> responseObserver) {
        List<String> errors = new ArrayList<>();
        boolean isValid = true;
        try {
            UserProfile profile = request.getProfile();
            validatorIndex.validatorFor(UserProfile.class).assertValid(profile);
            boolean hasMobile = request.getProfile().getPhonesList().stream()
                    .anyMatch(phone -> phone.getType() == PhoneType.MOBILE);

            if (!hasMobile) {
                isValid = false;
                errors.add("At least one phone must be of type MOBILE");
            }

            log.info("Validation completed successfully. Valid: {}", isValid);

        }  catch (ValidationException e) {
            log.error("Validation failed: {}", e.getMessage());
            isValid = false;
            errors.add(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during validation", e);
            isValid = false;
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
