package me.twodee.dowcspws.repository;

import me.twodee.dowcspws.model.entity.TtpIdentity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TtpIdentityRepository extends JpaRepository<TtpIdentity, String> {
}
