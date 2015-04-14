package net.creuroja.android.model.webservice.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

public class AccountUtils {
	public static final String ACCOUNT_TYPE = "Creu Roja";
	public static final String AUTH_TOKEN_TYPE = "";

	public static Account getAccount(Context context) {
		return AccountManager.get(context).getAccountsByType(ACCOUNT_TYPE)[0];
	}

	public static void validateLogin(final Activity activity, final LoginManager entryPoint) {
		AccountManager accountManager = AccountManager.get(activity);
		MyAccountCallback callback = new MyAccountCallback(entryPoint);
		Handler handler = new MyAccountHandler();

		accountManager
				.getAuthTokenByFeatures(ACCOUNT_TYPE, AUTH_TOKEN_TYPE, null, activity, null, null,
						callback, handler);
	}

	public interface LoginManager {
		void successfulLogin();

		void failedLogin();
	}

	public static class MyAccountCallback implements AccountManagerCallback<Bundle> {
		private LoginManager entryPoint;

		public MyAccountCallback(LoginManager entryPoint) {
			this.entryPoint = entryPoint;
		}

		@Override
		public void run(AccountManagerFuture<Bundle> accountManagerFuture) {
			LoginResponseTask response = new LoginResponseTask(accountManagerFuture, entryPoint);
			response.execute();
		}
	}

	public static class MyAccountHandler extends Handler {
	}

	public static class LoginResponseTask extends AsyncTask<Void, Void, String> {
		public static final String LOGIN_TASK_TAG = "login attempt";
		private LoginManager entryPoint;
		private AccountManagerFuture<Bundle> accountManagerFuture;

		public LoginResponseTask(AccountManagerFuture<Bundle> accountManagerFuture,
								 LoginManager entryPoint) {
			this.entryPoint = entryPoint;
			this.accountManagerFuture = accountManagerFuture;
		}

		@Override
		protected String doInBackground(Void... voids) {
			Bundle bundle;
			String authToken = null;
			try {
				bundle = accountManagerFuture.getResult();
				authToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
			} catch (OperationCanceledException e) {
				Log.d(LOGIN_TASK_TAG, "Login attempt cancelled by the user");
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return authToken;
		}

		@Override
		protected void onPostExecute(String authToken) {
			super.onPostExecute(authToken);
			if (TextUtils.isEmpty(authToken)) {
				entryPoint.failedLogin();
			} else {
				entryPoint.successfulLogin();
			}
		}
	}
}
