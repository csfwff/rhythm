package org.b3log.symphony.util;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Set;

/** Cookie consent writes trust the configured site origin, never an attacker-supplied Host header. */
public final class ApiV3ConsentSecurity {
    private ApiV3ConsentSecurity(){ }
    public static boolean sameOrigin(String configuredSite,String origin,String referer,String fetchSite){
        if(fetchSite!=null&&!Set.of("same-origin","none").contains(fetchSite))return false;
        try{
            final URI trusted=parse(configuredSite);if(trusted==null)return false;
            if(origin!=null){
                final URI source=parse(origin);
                return source!=null&&(source.getRawPath()==null||source.getRawPath().isEmpty())&&source.getRawQuery()==null&&equal(trusted,source);
            }
            final URI source=parse(referer);return source!=null&&source.getRawPath()!=null&&!source.getRawPath().isEmpty()&&equal(trusted,source);
        }catch(IllegalArgumentException e){return false;}
    }
    private static URI parse(String value){
        if(value==null||value.isEmpty()||value.length()>4096||value.codePoints().anyMatch(c->Character.isWhitespace(c)||Character.isISOControl(c)))return null;
        final URI uri=URI.create(value);
        if(!("https".equalsIgnoreCase(uri.getScheme())||"http".equalsIgnoreCase(uri.getScheme()))||uri.getHost()==null
                ||uri.getRawUserInfo()!=null||uri.getRawFragment()!=null||uri.getPort()==0||uri.getPort()>65535||uri.getRawAuthority().endsWith(":"))return null;
        return uri;
    }
    private static int port(URI uri){return uri.getPort()<0?("https".equalsIgnoreCase(uri.getScheme())?443:80):uri.getPort();}
    private static boolean equal(URI a,URI b){return a.getScheme().equalsIgnoreCase(b.getScheme())&&a.getHost().equalsIgnoreCase(b.getHost())&&port(a)==port(b);}
    public static boolean validCsrf(String expected,String presented){
        if(expected==null||expected.isEmpty()||expected.length()>256||presented==null||presented.isEmpty()||presented.length()>256)return false;
        try{ApiV3Json.canonical(expected);ApiV3Json.canonical(presented);}catch(IllegalArgumentException e){return false;}
        return MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8),presented.getBytes(StandardCharsets.UTF_8));
    }
}
