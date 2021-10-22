package me.twodee.dowcsttp.service;

import me.twodee.dowcsttp.Helper;
import me.twodee.dowcsttp.ResultObject;
import me.twodee.dowcsttp.model.dto.User;
import me.twodee.dowcsttp.model.entity.UserIdentity;
import me.twodee.dowcsttp.repository.UserIdentityRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Service
public class Accounts {

    private final UserIdentityRepository repository;
    private final HttpSession session;

    public Accounts(UserIdentityRepository repository, HttpSession session) {
        this.repository = repository;
        this.session = session;
    }

    public boolean hasCorrectCredentials(User.LoginData loginIdentity) {

        var storedIdentity = repository.findByEmail(loginIdentity.identifier);
        return (storedIdentity.isPresent() && passwordIsCorrect(loginIdentity.password, storedIdentity.get().getHashedPassword()));
    }

    private boolean passwordIsCorrect(String plaintext, String hashed) {
        return BCrypt.checkpw(plaintext, hashed);
    }

    public ResultObject register(User.RegistrationData data) {
        try {
            if (repository.existsUserIdentityByEmail(data.email)) {
                return ResultObject.builder()
                        .isSuccessful(false)
                        .error("The email you provided is already in use")
                        .build();
            }
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String hashedPassword = encoder.encode(data.password);
            repository.save(UserIdentity.builder()
                    .id(Helper.generateUniqueId())
                    .name(data.name)
                    .hashedPassword(hashedPassword)
                    .email(data.email).build());

            return ResultObject.builder().isSuccessful(true).build();
        } catch (DataAccessException e) {
            return ResultObject.builder()
                    .isSuccessful(false)
                    .error("Something went wrong, try again")
                    .build();
        }
    }

    public void login(String identifier) {
        UserIdentity identity = repository.findByEmail(identifier).get();
        session.setAttribute("loggedIn", true);
        session.setAttribute("user", identity);
    }

    public boolean isLoggedIn() {
        return session.getAttribute("loggedIn") != null && (boolean) session.getAttribute("loggedIn");
    }

    public UserIdentity getCurrentUser() {
        return (UserIdentity) session.getAttribute("user");
    }
}
