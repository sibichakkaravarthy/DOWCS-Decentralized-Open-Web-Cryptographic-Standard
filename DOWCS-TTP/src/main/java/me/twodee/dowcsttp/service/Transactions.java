package me.twodee.dowcsttp.service;

import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class Transactions {

//    private TransactionRepository repository;
//
//    public String generateInitiationToken(HttpSession session, String pwsIdentifier) {
//        // check db for duplicates of course
//        String token = UUID.randomUUID().toString();
//        session.setAttribute("initiationToken", token);
//        session.setAttribute("pwsIdentifier", pwsIdentifier);
//        return token;
//    }
//
//    public String getTransactionIdentity(HttpSession session, String userIdentifier) {
//        return repository.findByUserIdentifierAndPwsIdentifier(session.getAttribute("pwsIdentifier"), userIdentifier);
//    }
//
//    public String getAuthToken(String userIdentifier) {
//        String token = UUID.randomUUID().toString();
//        repository.save(new AuthToken(userIdentifier, token, LocalDateTime.now()));
//        return token;
//    }
}
