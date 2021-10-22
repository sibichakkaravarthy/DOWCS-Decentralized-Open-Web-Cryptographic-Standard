package me.twodee.dowcspws.controller;

import lombok.AllArgsConstructor;
import me.twodee.dowcspws.model.dto.ChallengeResult;
import me.twodee.dowcspws.service.TtpManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@org.springframework.web.bind.annotation.RestController
@AllArgsConstructor
public class RestController {

    private TtpManager ttpManager;

    @GetMapping("/dowcs/authorize")
    public ResponseEntity<ChallengeResult> responseToChallenge(@RequestParam("id") String id) {
        try {
            var result = ttpManager.solveChallenge(id);
            if (result.isSuccessful) {
                return new ResponseEntity<>(new ChallengeResult((String) result.obj),HttpStatus.OK);
            }
        } catch (Throwable e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
