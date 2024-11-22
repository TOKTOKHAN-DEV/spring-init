package com.spring.spring_init.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "authority")
public class Authority {

    @Id
    @Column(name = "authority_id", length = 50)
    private Long authorityId;

    @Column(name = "authority_name")
    private String authorityName;

    public Authority(String authorityName) {
        this.authorityName = authorityName;
    }
}
