package Threads;

import java.sql.SQLException;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import pl.kurcaba.AccountsSupervisor;
import pl.kurcaba.ApplicationConfig;

public class ChangeDriveService extends Service {

	private final ApplicationConfig appConfig;
	private final String newAccount;
	private final AccountsSupervisor accountsSupervisor;
	
	public ChangeDriveService(ApplicationConfig aApplicationConfig,String aNewAccount,AccountsSupervisor Aspr) {
		appConfig = aApplicationConfig;
		newAccount = aNewAccount;
		accountsSupervisor = Aspr;
	}
	
	@Override
	protected Task createTask() {
		return new Task() {
			@Override
			protected Object call() throws Exception {
				boolean isAccountChanged = accountsSupervisor.changeDriveAccount(newAccount);
				if(isAccountChanged)
				{
					appConfig.changeDefaultDriveAccount(newAccount);
				}
				return newAccount;
			}
		};
	}
	
}
