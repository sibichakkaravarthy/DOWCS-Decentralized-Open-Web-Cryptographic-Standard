package me.twodee.dowcsttp.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PwsIdentity {

    @Id
    private String id;
    @ManyToOne
    private UserIdentity owner;
    private String name;
    private String baseUrl;
    private String callback;
    private String description;
    private String pubkey;
    private boolean verified = false;
    private String challengeHash;
    public String getChallengeUrl() {
        return baseUrl + callback + "?id=" + id;
    }
}
