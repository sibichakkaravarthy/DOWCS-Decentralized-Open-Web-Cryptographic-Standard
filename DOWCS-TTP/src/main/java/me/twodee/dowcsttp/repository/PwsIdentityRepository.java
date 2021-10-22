package me.twodee.dowcsttp.repository;

import me.twodee.dowcsttp.model.entity.PwsIdentity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PwsIdentityRepository extends JpaRepository<PwsIdentity, String> {
    boolean existsPwsIdentitiesByBaseUrlAndVerified(String baseUrl, boolean verified);
}
