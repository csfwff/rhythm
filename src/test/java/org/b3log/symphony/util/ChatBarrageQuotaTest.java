package org.b3log.symphony.util;

import org.testng.Assert;
import java.util.concurrent.*;

/** Concurrent adapters must not each pass the legacy limiter's separate get/put sequence. */
public class ChatBarrageQuotaTest {
    public static void main(String[] args)throws Exception {
        final String actor="ConcurrentBarrage"+System.nanoTime();final int requests=24;
        final var ready=new CountDownLatch(requests);final var start=new CountDownLatch(1);
        try(var pool=Executors.newVirtualThreadPerTaskExecutor()){
            var futures=new java.util.ArrayList<Future<Boolean>>();
            for(int i=0;i<requests;i++)futures.add(pool.submit(()->{ready.countDown();start.await();return ChatBarrageQuota.take(actor);}));
            Assert.assertTrue(ready.await(5,TimeUnit.SECONDS));start.countDown();int accepted=0;for(var result:futures)if(result.get())accepted++;Assert.assertEquals(accepted,5);
        }
        Assert.assertTrue(ChatBarrageQuota.take(actor+"other"));Assert.assertFalse(ChatBarrageQuota.take(actor));
        System.out.println("PASS concurrent legacy/v3 quota calls admit exactly the existing budget, preserve actor isolation and do not race the underlying limiter get/put");
    }
}
