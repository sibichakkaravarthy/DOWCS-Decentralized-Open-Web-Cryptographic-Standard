package me.twodee.dowcsttp.repository;

import me.twodee.dowcsttp.model.entity.UserIdentity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserIdentityRepository extends JpaRepository<UserIdentity, String> {
    boolean existsUserIdentityByEmail(String email);

    Optional<UserIdentity> findByEmail(String identifier);
}
