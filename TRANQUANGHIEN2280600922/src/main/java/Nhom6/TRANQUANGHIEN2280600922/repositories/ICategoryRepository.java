package Nhom6.TRANQUANGHIEN2280600922.repositories;

import Nhom6.TRANQUANGHIEN2280600922.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICategoryRepository extends JpaRepository<Category, Long> {
}