package me.twodee.dowcsttp.service;

import lombok.AllArgsConstructor;
import me.twodee.dowcsttp.ResultObject;
import me.twodee.dowcsttp.crypto.CryptoUtils;
import me.twodee.dowcsttp.crypto.EncryptionFailed;
import me.twodee.dowcsttp.crypto.PemFile;
import me.twodee.dowcsttp.model.dto.Pws;
import me.twodee.dowcsttp.model.entity.PwsIdentity;
import me.twodee.dowcsttp.model.entity.UserIdentity;
import me.twodee.dowcsttp.repository.PwsIdentityRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

import static me.twodee.dowcsttp.Helper.generateUniqueId;
import static me.twodee.dowcsttp.crypto.CryptoUtils.generateSafeToken;

@Service
@AllArgsConstructor
public class Authorization {

    private PwsIdentityRepository repository;


    public ResultObject createNewPws(Pws.Registration data, UserIdentity user) throws IOException {
        try {
            if (repository.existsPwsIdentitiesByBaseUrlAndVerified(data.baseUrl, true)) {
                return ResultObject.builder().isSuccessful(false).error("An app with your base url already exists").build();
            }

            String challenge = generateSafeToken(256);
            byte[] encrypted = CryptoUtils.encryptECIES(PemFile.readPublicKey(data.pubkey), challenge);
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String id = generateUniqueId() + ".mockttp.local";
            PwsIdentity pws = PwsIdentity.builder()
                    .baseUrl(data.baseUrl)
                    .callback(data.callback)
                    .description(data.description)
                    .name(data.name)
                    .id(id)
                    .pubkey(data.pubkey)
                    .owner(user)
                    .challengeHash(encoder.encode(challenge))
                    .verified(false)
                    .build();
            repository.save(pws);

            return ResultObject.builder().isSuccessful(true).obj(new Pws.Challenge(Base64.getEncoder().encodeToString(encrypted), pws.getChallengeUrl(), id)).build();

        } catch (EncryptionFailed e) {
            return ResultObject.builder().isSuccessful(false).error("There was a problem with encryption. Check your curve25519 keys").build();
        }
    }


    public Optional<PwsIdentity> getPws(String pwsId) {
        return repository.findById(pwsId);
    }

    public void verifyPws(String id) {
        var pws = repository.findById(id).get();
        pws.setVerified(true);
        repository.save(pws);
    }
}
