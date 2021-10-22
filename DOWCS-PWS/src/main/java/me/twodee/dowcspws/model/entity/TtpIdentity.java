package me.twodee.dowcspws.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TtpIdentity {

    @Id
    private String id;
    private String name;
    private String baseUrl;
    @Lob
    @Column(length = 3000)
    private byte[] challenge;
}
