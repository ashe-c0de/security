package org.ashe.security;

import lombok.extern.slf4j.Slf4j;
import org.ashe.security.domain.BankAccount;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * spring.profiles.active=dev
 */
@SpringBootTest
@Slf4j
class ConcurrentTest {

    @Test
    void synchronizedMethod() {
        Assertions.assertDoesNotThrow(() -> {

            // 初始化一个0元余额的银行账户
            BankAccount bankAccount = new BankAccount("0");

            // 创建多个线程执行存款、取款
            Thread t1 = new Thread(() -> {
                for (int i = 0; i < 500000; i++) {
                    // 存款
                    bankAccount.deposit(String.valueOf(1));
                }
            });

            Thread t2 = new Thread(() -> {
                for (int i = 0; i < 500000; i++) {
                    // 取款
                    bankAccount.withdraw(String.valueOf(1));
                }
            });

            t1.start();
            // 阻塞主线程，使得t1线程执行完毕才会被主线程竞争到CPU
            try {
                t1.join();
            } catch (InterruptedException e) {
                log.error("InterruptedException, Cause by", e);
            }

            t2.start();
            // 阻塞主线程，使得t2线程执行完毕才会被主线程竞争到CPU
            try {
                t2.join();
            } catch (InterruptedException e) {
                log.error("InterruptedException, Cause by", e);
            }

            /*
                直接执行，无异常，输出Balance: 0
                    t1执行完毕，账户余额50万；t2执行完毕，账户余额0；主线程执行

                若将阻塞主线程的代码都注释，输出Balance: 0 && 抛出异常提示：余额不足
                    主线程优先执行完，输出的余额是初始化的余额；后续t1、t2竞争CPU资源执行各自代码

                注释第二次阻塞主线程代码，无异常，输出Balance: 500000
                    t1线程执行完毕；主线程执行完毕；最后执行t2线程

                注释第一次阻塞主线程代码，抛出异常提示：余额不足 && 输出Balance: 1624
                    t2线程执行+抛出异常+结束；主线程竞争到CPU执行完毕；t1线程执行顺序不确定（可能最开始执行了，可能在t2和主线程之间执行了，可能在最后也执行了）
             */
            log.info("Balance: " + bankAccount.getBalance());
        });
    }

    @Test
    void CASTest(){

        AtomicStampedReference<String> casObject = new AtomicStampedReference<>("A", 0);

        // 线程1：尝试修改值
        Thread thread1 = new Thread(() -> {

            int stamp = casObject.getStamp(); // 获取当前标记值
            String expectedReference = casObject.getReference(); // 获取当前期望值

            try {
                Thread.sleep(1000); // 为了模拟thread2线程的修改
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 尝试使用 CAS 修改值
            boolean success = casObject.compareAndSet(expectedReference, "C", stamp, stamp + 1);

            log.info("Thread 1 - CAS operation success: " + success);
        });

        // 线程2：修改值为初始值
        Thread thread2 = new Thread(() -> {
            int stamp = casObject.getStamp(); // 获取当前标记值
            String expectedReference = casObject.getReference(); // 获取当前期望值

            // 将值从 A 修改为 B
            casObject.compareAndSet(expectedReference, "B", stamp, stamp + 1);
            log.info("Thread 2: Value changed to B");

            // 将值从 B 修改回 A
            stamp = casObject.getStamp();
            casObject.compareAndSet("B", "A", stamp, stamp + 1);
            log.info("Thread 2: Value changed back to A");

        });

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /*
            最后输出结果为 Final value: A
            这说明thread1线程在修改变量casObject时保证了原子性，否则就会输出Final value: C
            这是因为AtomicStampedReference不仅会比较期望值，还会比较标记值stamp，当两者都相等时才会执行Swap行为修改变量
         */
        log.info("Final value: " + casObject.getReference());
    }
}
