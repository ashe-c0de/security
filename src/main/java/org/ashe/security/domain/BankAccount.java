package org.ashe.security.domain;

import org.ashe.security.infra.ServiceException;

import java.math.BigDecimal;

public class BankAccount {

    private BigDecimal balance;

    public BankAccount(String initialValue) {
        this.balance = new BigDecimal(initialValue);
    }

    /**
     * 存款
     */
    public synchronized void deposit(String amount) {
        BigDecimal depositAmount = new BigDecimal(amount);
        balance = balance.add(depositAmount);
    }

    /**
     * 取款
     */
    public synchronized void withdraw(String amount) {
        BigDecimal withdraw = new BigDecimal(amount);
        if (balance.compareTo(withdraw) >= 0) {
            balance = balance.subtract(withdraw);
        } else {
            throw new ServiceException("余额不足");
        }
    }

    /**
     * 查询余额
     */
    public synchronized String getBalance() {
        return balance.toString();
    }
}