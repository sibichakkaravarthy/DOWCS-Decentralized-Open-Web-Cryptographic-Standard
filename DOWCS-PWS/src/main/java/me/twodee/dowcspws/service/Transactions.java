package me.twodee.dowcspws.service;

import org.springframework.stereotype.Service;

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
