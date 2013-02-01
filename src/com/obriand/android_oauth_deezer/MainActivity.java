package com.obriand.android_oauth_deezer;

import com.deezer.sdk.DeezerConnect;
import com.deezer.sdk.DeezerConnectImpl;
import com.deezer.sdk.DeezerError;
import com.deezer.sdk.DialogError;
import com.deezer.sdk.DialogListener;
import com.deezer.sdk.OAuthException;
import com.deezer.sdk.SessionStore;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {
	
    public final static String TAG = "com.obriand.android_oauth_deezer.MainActivity";
	
	/** Your app Deezer appId. */
    public final static String APP_ID = "100113"; // ID of the o.briand@gmail.com account for application "test_orange"
    /** Permissions requested on Deezer accounts. */
    private final static String[] PERMISSIONS = new String[] {};
    /** DeezerConnect object used for auhtentification or request. */
    private DeezerConnect deezerConnect = new DeezerConnectImpl( APP_ID );

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	public void btLogin(View view) {
		Log.i(TAG, "login");
		Toast.makeText(this, "try to login", Toast.LENGTH_LONG).show();
		// Call the authorize method of deezerConnect SDK object to launch login process :
		// Use LoginDialogHandler inner class to handle callbacks methods (success, error, cancel...)
		deezerConnect.authorize( MainActivity.this, PERMISSIONS, new LoginDialogHandler() );
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
    /** Handle DeezerConnect callbacks. */
    private class LoginDialogHandler implements DialogListener {
	    @Override
	    public void onComplete(final Bundle values) {
			Log.i(TAG, "onComplete");
			Toast.makeText(MainActivity.this, "onComplete:[token:"+deezerConnect.getAccessToken()+"-appId:"+deezerConnect.getAppId()+"]", Toast.LENGTH_LONG).show();
			// Store the session because the authentication is successful
			SessionStore sessionStore = new SessionStore();
			sessionStore.save( deezerConnect, MainActivity.this );
	    }//met
	    @Override
	    public void onDeezerError(final DeezerError deezerError) {
			Log.i(TAG, "onDeezerError");
			Toast.makeText(MainActivity.this, "onDeezerError:"+deezerError.getMessage(), Toast.LENGTH_LONG).show();
	    }//met
	    @Override
	    public void onError(final DialogError dialogError) {
			Log.i(TAG, "onError");
			Toast.makeText(MainActivity.this, "onError:"+dialogError.getMessage(), Toast.LENGTH_LONG).show();
	    }//met
	    @Override
	    public void onCancel() {
			Log.i(TAG, "onCancel");
			Toast.makeText(MainActivity.this, "onCancel", Toast.LENGTH_LONG).show();
	    }//met
	    @Override
	    public void onOAuthException(OAuthException oAuthException) {
			Log.i(TAG, "onOAuthException");
			Toast.makeText(MainActivity.this, "onOAuthException", Toast.LENGTH_LONG).show();
	    }//met
    }//inner class

}
