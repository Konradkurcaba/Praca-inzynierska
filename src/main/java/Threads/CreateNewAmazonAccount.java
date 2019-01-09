package Threads;

import java.sql.SQLException;

import AmazonS3.AmazonAccountInfo;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import pl.kurcaba.AccountsSupervisor;
import pl.kurcaba.ApplicationConfig;

public class CreateNewAmazonAccount extends Service {

	private final ApplicationConfig appConfig;
	private final AmazonAccountInfo newAccount;
	private final AccountsSupervisor accountsSupervisor;
	
	public CreateNewAmazonAccount(ApplicationConfig aApplicationConfig,AmazonAccountInfo aNewAccount,AccountsSupervisor Aspr) {
		appConfig = aApplicationConfig;
		newAccount = aNewAccount;
		accountsSupervisor = Aspr;
	}
	
	@Override
	protected Task createTask() {
		return new Task() {
			@Override
			protected Object call() throws Exception {
				boolean isAccountChanged = accountsSupervisor.changeAmazonAccount(newAccount);
				if(isAccountChanged)
				{
					appConfig.changeDefaults3Account(newAccount);
				}
				return newAccount;
			}
		};
	}
	
}
