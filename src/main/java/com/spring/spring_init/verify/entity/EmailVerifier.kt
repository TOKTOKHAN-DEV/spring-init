package com.spring.spring_init.verify.entity

import com.spring.spring_init.common.base.BaseEntity
import jakarta.persistence.*

@Entity(name = "email_verifier")
class EmailVerifier(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "email_verifier_id", nullable = false)
    var emailVerifierId: Long? = null,

    @Column(name = "email", nullable = false)
    var email: String,

    @Column(name = "code")
    var code: String?,

    @Column(name = "token", nullable = false)
    var token: String,

    @Column(name = "purpose")
    @Enumerated(EnumType.STRING)
    var purpose: EmailVerifyPurpose?
) : BaseEntity() {

    constructor(
        email: String,
        code: String?,
        token: String,
        purpose: EmailVerifyPurpose
    ) : this(
        emailVerifierId = null,
        email = email,
        code = code,
        token = token,
        purpose = purpose
    )
}
