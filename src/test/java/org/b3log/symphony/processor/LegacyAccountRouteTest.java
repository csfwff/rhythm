package org.b3log.symphony.processor;

import org.b3log.latke.Latkes;
import org.b3log.latke.http.*;
import org.b3log.latke.http.function.Handler;
import org.testng.Assert;
import io.netty.handler.codec.http.*;
import java.util.*;

/** Actual three production route declarations and bundled Dispatcher ordering, explicit gate/controller adapters. */
public class LegacyAccountRouteTest {
    static Object field(Object owner,String key)throws Exception {final var f=owner.getClass().getDeclaredField(key);f.setAccessible(true);return f.get(owner);}
    @SuppressWarnings("unchecked")
    public static void main(String[] args)throws Exception {
        final var props=new Properties();props.setProperty("runtimeDatabase","NONE");Latkes.setLatkeProps(props);Latkes.setLocalProps(props);
        final var calls=new ArrayList<String>();final var allow=new boolean[]{false,false};
        final var controller=new LoginProcessor(){@Override public void register(RequestContext c){calls.add("register-first");}@Override public void register2(RequestContext c){calls.add("register");}@Override public void resetPwd(RequestContext c){calls.add("reset");}};
        Handler anonymous=c->{calls.add("anonymous");if(!allow[0]){c.sendRedirect("/test");c.abort();return;}c.handle();};
        Handler validation=c->{calls.add("validation");if(!allow[1]){c.abort();return;}c.handle();};
        LegacyAccountRouteBindings.register(controller,anonymous,validation,validation);
        final var registry=Dispatcher.class.getDeclaredField("routerGroups");registry.setAccessible(true);final var groups=(List<?>)registry.get(null);
        int checked=0;
        for(Object group:groups)for(Object route:(List<?>)field(group,"routers")){
            final var paths=(List<String>)field(route,"uriTemplates");if(paths.size()!=1||!Set.of("/register","/register2","/reset-pwd").contains(paths.getFirst()))continue;
            checked++;final String path=paths.getFirst();Assert.assertEquals(((List<?>)field(route,"httpRequestMethods")).getFirst(),org.b3log.latke.http.HttpMethod.POST);
            final var chain=new ArrayList<Handler>((List<Handler>)field(group,"middlewares"));Assert.assertEquals(chain.size(),path.equals("/reset-pwd")?1:2);chain.add((Handler)field(route,"handler"));
            for(int state=0;state<3;state++){
                allow[0]=state>0;allow[1]=state>1;calls.clear();final var raw=new DefaultFullHttpRequest(HttpVersion.HTTP_1_1,io.netty.handler.codec.http.HttpMethod.POST,path);
                try{
                    final var c=new RequestContext(new Request(null,raw),new Response(null,new DefaultHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.OK)){@Override public void sendRedirect(String url){setStatus(302);setHeader("Location",url);}});c.addHandlers(chain);c.handle();
                    if(state==0){Assert.assertEquals(calls,List.of("anonymous"));Assert.assertEquals(c.getResponse().getHeader("Location"),"/test");}
                    else if(path.equals("/reset-pwd"))Assert.assertEquals(calls,List.of("anonymous","reset"));
                    else Assert.assertEquals(calls,state==1?List.of("anonymous","validation"):List.of("anonymous","validation",path.equals("/register")?"register-first":"register"));
                }finally{raw.release();}
            }
        }
        Assert.assertEquals(checked,3);
        System.out.println("PASS actual legacy account route declarations register anonymous gating before validation/business in the bundled Dispatcher; blocked gates never reach either mutation and allowed requests retain the old validation order; global captcha policy remains an adapter");
    }
}
