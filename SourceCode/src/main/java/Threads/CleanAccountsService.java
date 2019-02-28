package Threads;

import javafx.concurrent.Task;
import pl.kurcaba.AccountsSupervisor;
import pl.kurcaba.ApplicationConfig;

import java.io.File;

import Synchronization.DatabaseSupervisor;
import javafx.concurrent.Service;


public class CleanAccountsService extends Service{

private AccountsSupervisor accountSupervisor;
private ApplicationConfig appConfig;
	
public CleanAccountsService(AccountsSupervisor aAccountsSupervisor,ApplicationConfig aConfig) {
	accountSupervisor = aAccountsSupervisor;
	appConfig = aConfig;
}


@Override
protected Task createTask() {
	return new Task(){
		@Override
		protected Object call() throws Exception {
			DatabaseSupervisor dbSupervisor = new DatabaseSupervisor();
			dbSupervisor.cleanDatabase();
			dbSupervisor.closeConnection();
			accountSupervisor.clanAccounts();
			appConfig.getConfig();
			new File("data/StoredCredential").delete();
			return null;
		}
	};
	
}
}