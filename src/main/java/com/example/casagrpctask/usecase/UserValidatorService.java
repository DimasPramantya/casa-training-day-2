package com.example.casagrpctask.usecase;

import com.example.casagrpctask.PhoneType;
import com.example.casagrpctask.UserProfile;
import com.example.casagrpctask.repository.UserRepository;
import io.envoyproxy.pgv.ReflectiveValidatorIndex;
import io.envoyproxy.pgv.ValidationException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserValidatorService {

    private final UserRepository userRepository;
    private final ReflectiveValidatorIndex validatorIndex = new ReflectiveValidatorIndex();

    public List<String> validate(UserProfile profile) {
        List<String> errors = new ArrayList<>();

        try {
            validatorIndex.validatorFor(UserProfile.class).assertValid(profile);
        } catch (ValidationException e) {
            errors.add(e.getMessage());
        }

        boolean hasMobile = profile.getPhonesList().stream()
                .anyMatch(phone -> phone.getType() == PhoneType.MOBILE);

        if (!hasMobile) {
            errors.add("At least one phone must be of type MOBILE");
        }


        if (errors.isEmpty() || !errors.contains("username") || !errors.contains("email")) {

            if (userRepository.findByUsername(profile.getUsername()).isPresent()) {
                errors.add("Username '" + profile.getUsername() + "' already taken.");
            }

            if (userRepository.findByEmail(profile.getEmail()).isPresent()) {
                errors.add("Email '" + profile.getEmail() + "' already registered.");
            }
        }

        return errors;
    }
}