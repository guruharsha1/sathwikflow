package com.example.jiralite.common.security;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sathwikflow.security")
public class AppSecurityProperties {
    private String issuer = "sathwikflow";
    private String audience = "sathwikflow-api";
    private int accessTokenMinutes = 15;
    private int refreshTokenDays = 7;
    private String refreshCookieName = "SF_REFRESH";
    private boolean cookieSecure = false;
    private List<String> allowedOrigins = List.of("http://localhost:5173");
    private String rsaPrivateKey;
    private String rsaPublicKey;
    public String getIssuer() { return issuer; } public void setIssuer(String value) { issuer = value; }
    public String getAudience() { return audience; } public void setAudience(String value) { audience = value; }
    public int getAccessTokenMinutes() { return accessTokenMinutes; } public void setAccessTokenMinutes(int value) { accessTokenMinutes = value; }
    public int getRefreshTokenDays() { return refreshTokenDays; } public void setRefreshTokenDays(int value) { refreshTokenDays = value; }
    public String getRefreshCookieName() { return refreshCookieName; } public void setRefreshCookieName(String value) { refreshCookieName = value; }
    public boolean isCookieSecure() { return cookieSecure; } public void setCookieSecure(boolean value) { cookieSecure = value; }
    public List<String> getAllowedOrigins() { return allowedOrigins; } public void setAllowedOrigins(List<String> value) { allowedOrigins = value; }
    public String getRsaPrivateKey() { return rsaPrivateKey; } public void setRsaPrivateKey(String value) { rsaPrivateKey = value; }
    public String getRsaPublicKey() { return rsaPublicKey; } public void setRsaPublicKey(String value) { rsaPublicKey = value; }
}

