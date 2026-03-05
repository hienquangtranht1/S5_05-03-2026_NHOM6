package Nhom6.TRANQUANGHIEN2280600922.repositories;

import Nhom6.TRANQUANGHIEN2280600922.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IRoleRepository extends JpaRepository<Role, Long> {
    Role findRoleById(Long id);
}