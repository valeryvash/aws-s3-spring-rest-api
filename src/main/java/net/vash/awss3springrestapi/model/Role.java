package net.vash.awss3springrestapi.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "roles")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Role extends BaseEntity {

    @NotNull
    @Column(name = "role_name", nullable = false, unique = true)
    private String roleName;

    @ManyToMany(
            mappedBy = "roles",
            fetch = FetchType.LAZY
    )
    @ToString.Exclude
    private List<User> users = new ArrayList<>();
}
