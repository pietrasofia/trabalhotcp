package bank.ui.text.command;

import bank.business.AccountOperationService;
import bank.business.domain.Deposit;
import bank.ui.text.BankTextInterface;
import bank.ui.text.UIUtils;

public class DepositCommand extends Command {

	private final AccountOperationService accountOperationService;

	public DepositCommand(BankTextInterface bankInterface,
			AccountOperationService accountOperationService) {
		super(bankInterface);
		this.accountOperationService = accountOperationService;
	}

	@Override
	public void execute() throws Exception {
		Long branch = bankInterface.readBranchId();
		Long accountNumber = bankInterface.readCurrentAccountNumber();
		Double amount = UIUtils.INSTANCE.readDouble("amount");

		Deposit deposit = accountOperationService.deposit(bankInterface
				.getOperationLocation().getNumber(), branch, accountNumber,
				amount);

		System.out.println(getTextManager().getText(
				"message.operation.succesfull"));
		System.out.println(getTextManager().getText("deposit") + ": "
				+ deposit.getAmount());
	}

}
