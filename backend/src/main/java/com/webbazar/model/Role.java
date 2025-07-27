package com.webbazar.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "roles") // ✅ Zorg dat dit overeenkomt met je database-tabel
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users;

    // ✅ Nodig voor Spring Security — zodat roles als "ROLE_ADMIN" blijven werken
    @Override
    public String toString() {
        return name.startsWith("ROLE_") ? name : "ROLE_" + name;
    }
}
