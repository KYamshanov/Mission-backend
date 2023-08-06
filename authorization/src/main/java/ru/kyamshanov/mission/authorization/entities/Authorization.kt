package ru.kyamshanov.mission.authorization.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant


@Entity
@Table(name = "`authorization`")
data class Authorization(
    @Id
    @Column(name = "id")
    var id: String? = null,
    @Column(name = "registeredclientid")
    var registeredClientId: String? = null,
    @Column(name = "principalname")
    var principalName: String? = null,
    @Column(name = "authorizationgranttype")
    var authorizationGrantType: String? = null,

    @Column(length = 1000, name = "authorizedscopes")
    var authorizedScopes: String? = null,

    @Column(length = 4000, name = "attributes")
    var attributes: String? = null,

    @Column(length = 500, name = "state")
    var state: String? = null,

    @Column(length = 4000, name = "authorizationcodevalue")
    var authorizationCodeValue: String? = null,
    @Column(name = "authorizationcodeissuedat")
    var authorizationCodeIssuedAt: Instant? = null,
    @Column(name = "authorizationcodeexpiresat")
    var authorizationCodeExpiresAt: Instant? = null,
    @Column(name = "authorizationcodemetadata")
    var authorizationCodeMetadata: String? = null,

    @Column(length = 4000, name = "accesstokenvalue")
    var accessTokenValue: String? = null,
    @Column(name = "accesstokenissuedat")
    var accessTokenIssuedAt: Instant? = null,
    @Column(name = "accesstokenexpiresat")
    var accessTokenExpiresAt: Instant? = null,

    @Column(length = 2000, name = "accesstokenmetadata")
    var accessTokenMetadata: String? = null,
    @Column(name = "accesstokentype")
    var accessTokenType: String? = null,

    @Column(length = 1000, name = "accesstokenscopes")
    var accessTokenScopes: String? = null,

    @Column(length = 4000, name = "refreshtokenvalue")
    var refreshTokenValue: String? = null,
    @Column(name = "refreshtokenissuedat")
    var refreshTokenIssuedAt: Instant? = null,
    @Column(name = "refreshtokenexpiresat")
    var refreshTokenExpiresAt: Instant? = null,

    @Column(length = 2000, name = "refreshtokenmetadata")
    var refreshTokenMetadata: String? = null,

    @Column(length = 4000, name = "oidcidtokenvalue")
    var oidcIdTokenValue: String? = null,
    @Column(name = "oidcidtokenissuedat")
    var oidcIdTokenIssuedAt: Instant? = null,
    @Column(name = "oidcidtokenexpiresat")
    var oidcIdTokenExpiresAt: Instant? = null,

    @Column(length = 2000, name = "oidcidtokenmetadata")
    var oidcIdTokenMetadata: String? = null,

    @Column(length = 2000, name = "oidcidtokenclaims")
    var oidcIdTokenClaims: String? = null,

    @Column(length = 4000, name = "usercodevalue")
    var userCodeValue: String? = null,
    @Column(name = "usercodeissuedat")
    var userCodeIssuedAt: Instant? = null,
    @Column(name = "usercodeexpiresat")
    var userCodeExpiresAt: Instant? = null,

    @Column(length = 2000, name = "usercodemetadata")
    var userCodeMetadata: String? = null,

    @Column(length = 4000, name = "devicecodevalue")
    var deviceCodeValue: String? = null,
    @Column(name = "devicecodeissuedat")
    var deviceCodeIssuedAt: Instant? = null,
    @Column(name = "devicecodeexpiresat")
    var deviceCodeExpiresAt: Instant? = null,

    @Column(length = 2000, name = "devicecodemetadata")
    var deviceCodeMetadata: String? = null,
)