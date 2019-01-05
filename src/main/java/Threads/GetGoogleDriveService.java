package Threads;

import java.sql.SQLException;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import pl.kurcaba.AccountsSupervisor;
import pl.kurcaba.ApplicationConfig;

public class GetGoogleDriveService extends Service {

	private final ApplicationConfig appConfig;
	private final String googleAccount;
	private final AccountsSupervisor accountsSupervisor;
	public GetGoogleDriveService(ApplicationConfig aApplicationConfig,String aGoogleAccount,AccountsSupervisor Aspr) {
		appConfig = aApplicationConfig;
		googleAccount = aGoogleAccount;
		accountsSupervisor = Aspr;
	}
	
	
	@Override
	protected Task createTask() {
		return new Task() {
			@Override
			protected Object call() throws Exception {
				boolean isAccountChanged = accountsSupervisor.changeDriveAccount(googleAccount);
				if(isAccountChanged) 
				{
					try {
						appConfig.changeDefaultDriveAccount(googleAccount);
					} catch (SQLException e) {
						e.printStackTrace();
						return false;
					}
				}
				return isAccountChanged;
			}
		};
	}

}
