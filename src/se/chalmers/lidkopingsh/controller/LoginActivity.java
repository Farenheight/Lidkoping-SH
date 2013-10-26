package se.chalmers.lidkopingsh.controller;

import org.apache.http.auth.AuthenticationException;

import se.chalmers.lidkopingsh.app.App;
import se.chalmers.lidkopingsh.server.ServerHelper;
import se.chalmers.lidkopingsh.server.ServerHelper.ApiResponse;
import se.chalmers.lidkopingsh.server.ServerSettings;
import android.accounts.NetworkErrorException;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Activity which displays a login screen to the user
 * 
 * @author Simon Bengtsson
 * 
 */
public class LoginActivity extends Activity {

	private static final String SERVER_WEB_API_URL = "https://lidkopingsh.kimkling.net/api/";

	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mUserName;
	private String mPassword;

	// UI references.
	private EditText mUserNameView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	private NetworkWatcherChild mNetworkWatcher;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		// Saves server path
		SharedPreferences preferences = App.getContext().getSharedPreferences(
				ServerSettings.PREFERENCES_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(ServerSettings.PREFERENCES_SERVER_PATH,
				SERVER_WEB_API_URL);
		editor.commit();

		// Set up the login form.
		mUserNameView = (EditText) findViewById(R.id.email);
		mUserNameView.setText(mUserName);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
						InputMethodManager imm = (InputMethodManager) LoginActivity.this
								.getSystemService(Service.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(
								mPasswordView.getWindowToken(), 0);
						imm.hideSoftInputFromWindow(
								mUserNameView.getWindowToken(), 0);
					}
				});
	}

	@Override
	protected void onDestroy() {
		if(mAuthTask != null){
			mAuthTask.cancel(true);			
		}
		super.onDestroy();
	}

	// TODO: Provide settings in action overflow
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	/**
	 * Attempts to sign in if no errors (invalid email, missing fields, etc)
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mUserNameView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mUserName = mUserNameView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mUserName)) {
			mUserNameView.setError(getString(R.string.error_field_required));
			focusView = mUserNameView;
			cancel = true;
		}

		// Don't try login if errors
		if (cancel) {
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and start a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Android 3.0+ the ViewPropertyAnimator APIs allows
		// for easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
	
	public void networkProblem(String message) {
		Log.e("LoginAct", "Could not connect to server");
		RepeatSafeToast.show(message);
	}

	public void authenticationFailed() {
		mUserNameView
				.setError(getString(R.string.error_invalid_user_creadentials));
		mUserNameView.requestFocus();
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

		private Exception exception;

		@Override
		protected Boolean doInBackground(Void... params) {
			
			// Send to server
			try {
				ApiResponse response = new ServerHelper().getApikey(mUserName, mPassword);
				if (response != null) {
					return response.isSuccess();
				}

			} catch (Exception e) {
				exception = e;
			}

			return false;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;

			if (exception != null) {
				if (exception instanceof AuthenticationException) {
					authenticationFailed();
					Log.e("LoginAct", "Failed to login with message: "
							+ exception.getMessage());
				} else if (exception instanceof NetworkErrorException) {
					networkProblem("Kunde inte ansluta till server.");
				} else {
					throw new IllegalStateException(
							"Unhandled exception in LoginActivity", exception);
				}
			}

			if (success) {
				// On success, start getting update
				mNetworkWatcher = new NetworkWatcherChild();
				Accessor.getModel();
				Accessor.getServerConnector().addNetworkListener(mNetworkWatcher);
				Accessor.getServerConnector().update(true);
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
	
	private class NetworkWatcherChild extends NetworkWatcher {

		@Override
		public void startedUpdate() {
			TextView statusView = (TextView) LoginActivity.this
					.findViewById(R.id.login_status_message);
			statusView.setText("Hämtar data...");
		}

		@Override
		public void finishedUpdate() {
			Accessor.getServerConnector().removeNetworkStatusListener(this);
			startActivity(new Intent(LoginActivity.this, MainActivity.class));
			Log.i("DEBUG", "Login seccesful. MainActivity started");
		}

		@Override
		public void authenticationFailed() {
			// Not handled here
		}
	}
	
	public static void logout(Context context) {
		context.startActivity(new Intent(App.getContext(), LoginActivity.class));
		Editor editor = App
				.getContext()
				.getSharedPreferences(ServerSettings.PREFERENCES_NAME,
						Context.MODE_PRIVATE).edit();
		editor.clear().commit();
		Accessor.getModel().clearAllOrders();
	}

}