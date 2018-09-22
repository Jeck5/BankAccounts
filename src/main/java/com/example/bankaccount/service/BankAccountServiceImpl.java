package com.example.bankaccount.service;

import com.example.bankaccount.model.BankAccount;
import com.example.bankaccount.repository.BankAccountCrudRepository;
import com.example.bankaccount.util.exception.NotEnoughBalanceException;
import com.example.bankaccount.util.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.transaction.Transactional;
import java.math.BigDecimal;

import static com.example.bankaccount.util.ValidationUtil.checkNotFound;
import static com.example.bankaccount.util.ValidationUtil.checkNotFoundWithKey;

@Service/*TODO transactions*/
public class BankAccountServiceImpl implements BankAccountService {

    private final BankAccountCrudRepository repository;

    @Autowired
    public BankAccountServiceImpl(BankAccountCrudRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public BankAccount create(BankAccount bankAccount) {
        Assert.notNull(bankAccount, "bankAccount must not be null");
        return repository.save(bankAccount);
    }

    @Override
    @Transactional
    public void deposit(int id, BigDecimal sum) {
        BankAccount bankAccount = repository.findById(id).orElse(null);
        Assert.notNull(bankAccount, "bankAccount must not be null");
        BigDecimal newBalance = bankAccount.getBalance().add(sum);
        bankAccount.setBalance(newBalance);
        checkNotFoundWithKey(repository.save(bankAccount), bankAccount.getKey());
    }

    @Override
    @Transactional
    public void withdraw(int id, BigDecimal sum) {
        BankAccount bankAccount = repository.findById(id).orElse(null);
        Assert.notNull(bankAccount, "bankAccount must not be null");
        if (bankAccount.getBalance().compareTo(sum) < 0) { throw new NotEnoughBalanceException("Not enough balance");}
        BigDecimal newBalance = bankAccount.getBalance().subtract(sum);
        bankAccount.setBalance(newBalance);
        checkNotFoundWithKey(repository.save(bankAccount), bankAccount.getKey());
    }

    @Override
    public BankAccount getById(Integer id) throws NotFoundException {
        Assert.notNull(id, "id must not be null");
        return checkNotFound(repository.findById(id).orElse(null), "id=" + id);
    }
}