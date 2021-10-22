package me.twodee.dowcspws.service;

import lombok.AllArgsConstructor;
import me.twodee.dowcspws.ResultObject;
import me.twodee.dowcspws.crypto.CryptoUtils;
import me.twodee.dowcspws.crypto.EncryptionFailed;
import me.twodee.dowcspws.crypto.PemFile;
import me.twodee.dowcspws.model.dto.Ttp;
import me.twodee.dowcspws.model.entity.TtpIdentity;
import me.twodee.dowcspws.repository.TtpIdentityRepository;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.jcajce.provider.digest.MD5;
import org.bouncycastle.jcajce.provider.digest.SHA256;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.Key;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@AllArgsConstructor
@Service
public class TtpManager {

    private TtpIdentityRepository repository;

    public ResultObject registerTtp(Ttp.Registration data) {

        var ttp = TtpIdentity.builder().id(data.getPwsId())
                .baseUrl(data.getBaseUrl())
                .name(data.getName())
                .challenge(Base64.getDecoder().decode(data.getChallenge()))
                .build();

        if (repository.findById(data.getPwsId()).isPresent()) {
            return ResultObject.builder().isSuccessful(false).error("Pws Id already exists").build();
        }

        repository.save(ttp);

        return ResultObject.builder().isSuccessful(true).build();
    }

    public ResultObject solveChallenge(String id) {
        try {
            var ttp = repository.findById(id).get();
            return ResultObject.builder().isSuccessful(true).obj(CryptoUtils.decryptECIES(getPrivateKey(), ttp.getChallenge())).build();
        } catch (Throwable e) {
            e.printStackTrace();
            return ResultObject.builder().isSuccessful(false).error("Something went wrong").build();
        }
    }

    private PrivateKey getPrivateKey() throws IOException, URISyntaxException {
        var file = new File(getClass().getClassLoader().getResource("pws_ec.pem").toURI());
        return PemFile.readPrivateKey(file);
    }

    public List<TtpIdentity> getAvailablePwsList() {
        return repository.findAll();
    }
}
