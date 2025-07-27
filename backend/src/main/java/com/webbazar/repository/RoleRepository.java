// 📁 com/webbazar/repository/RoleRepository.java
package com.webbazar.repository;

import com.webbazar.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
