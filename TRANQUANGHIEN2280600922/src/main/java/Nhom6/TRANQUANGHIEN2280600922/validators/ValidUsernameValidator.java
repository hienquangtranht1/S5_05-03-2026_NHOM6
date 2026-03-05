package Nhom6.TRANQUANGHIEN2280600922.validators;

import Nhom6.TRANQUANGHIEN2280600922.repositories.IUserRepository;
import Nhom6.TRANQUANGHIEN2280600922.validators.annotations.ValidUsername;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor
public class ValidUsernameValidator implements ConstraintValidator<ValidUsername, String> {
    @Autowired
    private IUserRepository userRepository;

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        if (userRepository == null) {
            return true; 
        }
        return userRepository.findByUsername(username).isEmpty();
    }
}