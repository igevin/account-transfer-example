package org.gevinzone;

public class Account implements TransferAccount {
    private final long id;


    private int balance;

    public Account(long id, int balance) {
        this.id = id;
        this.balance = balance;
    }

    public int getBalance() {
        return balance;
    }

    protected void transferAccount(Account target, int amount){
        if (this.balance > amount) {
            this.balance -= amount;

            sleep();

            target.balance += amount;
        }
    }

    @Override
    public void transfer(Account target, int amount) {
        transferAccount(target, amount);
    }

    private void sleep() {
        try {
            Thread.sleep(50);
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
    }
}
