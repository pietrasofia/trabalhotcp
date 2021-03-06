package bank.business.domain;

import java.util.ArrayList;
import java.util.List;

import bank.business.BusinessException;

/**
 * @author Ingrid Nunes
 * 
 */
public class CurrentAccount implements Credentials {

	private double balance;
	private Client client;
	private List<Deposit> deposits;
	private CurrentAccountId id;
	private List<Transfer> transfers;
	private List<Withdrawal> withdrawals;
	private List<Pendency<?>> pendencies;

	public CurrentAccount(Branch branch, long number, Client client) {
		this.id = new CurrentAccountId(branch, number);
		branch.addAccount(this);
		this.client = client;
		client.setAccount(this);
		this.deposits = new ArrayList<>();
		this.transfers = new ArrayList<>();
		this.withdrawals = new ArrayList<>();
		this.pendencies = new ArrayList<>();
	}

	public CurrentAccount(Branch branch, long number, Client client,
			double initialBalance) {
		this(branch, number, client);
		this.balance = initialBalance;
	}

	public EnvelopeDeposit depositEnvelope(OperationLocation location, long envelope,
			double amount) throws BusinessException {
		
		EnvelopeDeposit envelopeDeposit = new EnvelopeDeposit(location, this, envelope, amount);
		this.pendencies.add(envelopeDeposit);

		return envelopeDeposit;
	}
	
	public Deposit deposit(OperationLocation location, double amount, boolean addToList) throws BusinessException {
		Deposit deposit = new Deposit(location, this, amount);
		if (addToList)
			this.deposits.add(deposit);

		depositAmount(deposit.getAmount());
		
		return deposit;
	}
	
	public Deposit deposit(OperationLocation location, double amount) throws BusinessException {
		return deposit(location, amount, true);
	}

	private void depositAmount(double amount) throws BusinessException {
		if (!isValidAmount(amount)) {
			throw new BusinessException("exception.invalid.amount");
		}
		
		this.balance += amount ;
	}

	/**
	 * @return the balance
	 */
	public double getBalance() {
		return balance;
	}

	/**
	 * @return the client
	 */
	public Client getClient() {
		return client;
	}

	/**
	 * @return the deposits
	 */
	public List<Deposit> getDeposits() {
		return deposits;
	}

	/**
	 * @return the id
	 */
	public CurrentAccountId getId() {
		return id;
	}

	public List<Transaction> getTransactions() {
		List<Transaction> transactions = new ArrayList<>(deposits.size()
				+ withdrawals.size() + transfers.size());
		transactions.addAll(deposits);
		transactions.addAll(withdrawals);
		transactions.addAll(transfers);
		transactions.addAll(pendencies);
		return transactions;
	}

	/**
	 * @return the transfers
	 */
	public List<Transfer> getTransfers() {
		return transfers;
	}

	/**
	 * @return the withdrawals
	 */
	public List<Withdrawal> getWithdrawals() {
		return withdrawals;
	}

	private boolean hasEnoughBalance(double amount) {
		return amount <= balance;
	}

	private boolean isValidAmount(double amount) {
		return amount > 0;
	}

	public Transfer transfer(OperationLocation location,
			CurrentAccount destinationAccount, double amount)
			throws BusinessException {
		Transfer transfer = new Transfer(location, this, destinationAccount,
				amount);
		this.transfers.add(transfer);
		destinationAccount.transfers.add(transfer);

		withdrawalAmount(transfer.getAmountPlusTax());
		destinationAccount.depositAmount(transfer.getAmount());

		return transfer;
	}

	public Withdrawal withdrawal(OperationLocation location, double amount)
			throws BusinessException {
		Withdrawal withdrawal = new Withdrawal(location, this, amount);
		this.withdrawals.add(withdrawal);

		withdrawalAmount(withdrawal.getAmountPlusTax());

		return withdrawal;
	}

	private void withdrawalAmount(double amount) throws BusinessException {
		if (!isValidAmount(amount)) {
			throw new BusinessException("exception.invalid.amount");
		}

		if (!hasEnoughBalance(amount)) {
			throw new BusinessException("exception.insufficient.balance");
		}

		this.balance -= amount;
	}

}
