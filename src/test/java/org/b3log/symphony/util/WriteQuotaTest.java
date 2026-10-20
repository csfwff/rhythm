package org.b3log.symphony.util;

import org.testng.Assert;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.ArrayList;
import java.util.function.Function;

/** Executes the actual local limiter source through all shared wrappers. */
public class WriteQuotaTest {
    public static void main(final String[] args) throws Exception {
        concurrent("article",ArticleWriteQuota::access,10);
        concurrent("profile",ProfileWriteQuota::access,5);
        concurrent("breezemoon",BreezemoonWriteQuota::access,5);
        concurrent("game submission",actor->FishGameWriteQuota.access(FishGameWriteQuota.Kind.SUBMISSION,actor),10);
        concurrent("game comment",actor->FishGameWriteQuota.access(FishGameWriteQuota.Kind.COMMENT,actor),5);
        concurrent("game vote",actor->FishGameWriteQuota.access(FishGameWriteQuota.Kind.VOTE,actor),10);
        final String actor=UUID.randomUUID().toString();
        for(int i=0;i<10;i++)Assert.assertTrue(ArticleWriteQuota.access(actor));Assert.assertFalse(ArticleWriteQuota.access(actor));
        Assert.assertTrue(ProfileWriteQuota.access(actor));Assert.assertTrue(ArticleWriteQuota.access(UUID.randomUUID().toString()));
        System.out.println("PASS independent actors and business quota buckets");
    }
    private static void concurrent(final String name, final Function<String,Boolean> access, final int limit) throws Exception {
        final String actor=UUID.randomUUID().toString();final CountDownLatch ready=new CountDownLatch(64),start=new CountDownLatch(1);
        try(final var pool=Executors.newVirtualThreadPerTaskExecutor()) {
            final ArrayList<Future<Boolean>> futures=new ArrayList<>();
            for(int i=0;i<64;i++)futures.add(pool.submit(()->{ready.countDown();start.await();return access.apply(actor);}));
            ready.await();start.countDown();int allowed=0;for(final var future:futures)if(future.get())allowed++;
            Assert.assertEquals(allowed,limit,name);Assert.assertFalse(access.apply(actor));
        }
        System.out.println("PASS actual "+name+" allowance with 64 simultaneous requests");
    }
}
