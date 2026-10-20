package org.b3log.symphony.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/** RFC 7636 S256. This digest has no application-specific prefix or salt. */
public final class ApiV3Pkce {
    private ApiV3Pkce(){ }
    public static boolean validVerifier(final String value){return value!=null&&value.matches("[A-Za-z0-9._~-]{43,128}");}
    public static boolean validChallenge(final String value){
        if(value==null||!value.matches("[A-Za-z0-9_-]{43}"))return false;
        try{
            final byte[] bytes=Base64.getUrlDecoder().decode(value);
            return bytes.length==32&&Base64.getUrlEncoder().withoutPadding().encodeToString(bytes).equals(value);
        }catch(IllegalArgumentException e){return false;}
    }
    public static void requireS256(final String challenge,final String method){
        if(!"S256".equals(method)||!validChallenge(challenge))throw new IllegalArgumentException("S256 PKCE required");
    }
    public static String challenge(final String verifier){
        if(!validVerifier(verifier))throw new IllegalArgumentException("invalid PKCE verifier");
        try{return Base64.getUrlEncoder().withoutPadding().encodeToString(MessageDigest.getInstance("SHA-256").digest(verifier.getBytes(StandardCharsets.US_ASCII)));}
        catch(NoSuchAlgorithmException e){throw new IllegalStateException("SHA-256 unavailable",e);}
    }
    public static boolean matches(final String challenge,final String verifier){
        return validChallenge(challenge)&&validVerifier(verifier)
                &&MessageDigest.isEqual(challenge.getBytes(StandardCharsets.US_ASCII),challenge(verifier).getBytes(StandardCharsets.US_ASCII));
    }
}
