package me.twodee.dowcsttp.model.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserIdentity {
    @Id
    private String id;

    private String name;

    @Column(unique = true)
    private String email;

    private String hashedPassword;
}
