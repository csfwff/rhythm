package org.b3log.symphony.util;

import java.net.URI;
import java.util.Locale;
import java.util.Set;

/** Callback shape policy only. The caller must additionally require exact active registration. No DNS or fetch. */
public final class ApiV3OAuthRedirects {
    private ApiV3OAuthRedirects(){ }
    public static boolean valid(final String value,final boolean development){
        if(value==null||value.isEmpty()||value.length()>2048||value.codePoints().anyMatch(c->Character.isWhitespace(c)||Character.isISOControl(c)))return false;
        try{
            ApiV3Json.canonical(value);final URI uri=URI.create(value);
            if(uri.toASCIIString().length()>2048||!uri.isAbsolute()||uri.getRawFragment()!=null||uri.getRawUserInfo()!=null||ApiV3OAuthCallbacks.hasResponseParameters(uri))return false;
            final String scheme=uri.getScheme().toLowerCase(Locale.ROOT);
            if("https".equals(scheme)||"http".equals(scheme)){
                if(uri.getHost()==null||uri.getPort()==0||uri.getPort()>65535||uri.getRawAuthority().endsWith(":"))return false;
                return "https".equals(scheme)||development&&Set.of("localhost","127.0.0.1","[::1]").contains(uri.getHost().toLowerCase(Locale.ROOT));
            }
            // Native private schemes follow reverse-domain notation and a single-slash path (RFC 8252).
            return scheme.matches("[a-z][a-z0-9-]*(?:\\.[a-z0-9][a-z0-9-]*)+")&&!uri.isOpaque()&&uri.getRawAuthority()==null
                    &&uri.getRawPath()!=null&&uri.getRawPath().startsWith("/")&&!uri.getRawPath().startsWith("//");
        }catch(IllegalArgumentException e){return false;}
    }
}
