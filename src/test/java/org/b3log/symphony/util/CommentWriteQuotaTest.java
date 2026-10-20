package org.b3log.symphony.util;

import org.testng.Assert;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CommentWriteQuotaTest {
    public static void main(final String[] args) throws Exception {
        concurrent(true, 5); concurrent(false, 3);
        final String actor = UUID.randomUUID().toString();
        for (int i = 0; i < 5; i++) Assert.assertTrue(CommentWriteQuota.access(actor, true));
        Assert.assertFalse(CommentWriteQuota.access(actor, true));
        Assert.assertTrue(CommentWriteQuota.access(actor, false));
        Assert.assertTrue(CommentWriteQuota.access(UUID.randomUUID().toString(), true));
        System.out.println("PASS comment publication/edit buckets and actors remain independent");
    }
    private static void concurrent(final boolean adding, final int limit) throws Exception {
        final String actor = UUID.randomUUID().toString();
        final var ready = new CountDownLatch(64); final var start = new CountDownLatch(1);
        try (final var pool = Executors.newVirtualThreadPerTaskExecutor()) {
            final var tasks = new ArrayList<Future<Boolean>>();
            for (int i = 0; i < 64; i++) tasks.add(pool.submit(() -> { ready.countDown(); start.await(); return CommentWriteQuota.access(actor, adding); }));
            ready.await(); start.countDown(); int allowed = 0;
            for (final var task : tasks) if (task.get()) allowed++;
            Assert.assertEquals(allowed, limit); Assert.assertFalse(CommentWriteQuota.access(actor, adding));
        }
        System.out.println("PASS actual comment " + (adding ? "publish" : "edit") + " quota under 64 simultaneous requests");
    }
}
