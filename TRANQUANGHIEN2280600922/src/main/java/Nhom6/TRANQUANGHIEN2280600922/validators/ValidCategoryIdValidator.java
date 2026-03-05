package Nhom6.TRANQUANGHIEN2280600922.validators;

import Nhom6.TRANQUANGHIEN2280600922.entities.Category;
import Nhom6.TRANQUANGHIEN2280600922.validators.annotations.ValidCategoryId;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidCategoryIdValidator implements ConstraintValidator<ValidCategoryId, Category> {
    @Override
    public boolean isValid(Category category, ConstraintValidatorContext context) {
        return category != null && category.getId() != null;
    }
}