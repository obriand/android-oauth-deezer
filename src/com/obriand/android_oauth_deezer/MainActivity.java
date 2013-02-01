package com.obriand.android_oauth_deezer;

import com.deezer.sdk.DeezerConnect;
import com.deezer.sdk.DeezerConnectImpl;
import com.deezer.sdk.DeezerError;
import com.deezer.sdk.DialogError;
import com.deezer.sdk.DialogListener;
import com.deezer.sdk.OAuthException;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MainActivity extends Activity {
	
    /** Your app Deezer appId. */
    public final static String APP_ID = "";
    /** Permissions requested on Deezer accounts. */
    private final static String[] PERMISSIONS = new String[] {};
    /** DeezerConnect object used for auhtentification or request. */
    private DeezerConnect deezerConnect = new DeezerConnectImpl( APP_ID );

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
	}
	
	public void init() {
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
    /** Handle DeezerConnect callbacks. */
    private class MyDialogHandler implements DialogListener {
	    @Override
	    public void onComplete(final Bundle values) {
	     
	    }//met
	    @Override
	    public void onDeezerError(final DeezerError deezerError) {
	     
	    }//met
	    @Override
	    public void onError(final DialogError dialogError) {
	     
	    }//met
	    @Override
	    public void onCancel() {
	     
	    }//met
	    @Override
	    public void onOAuthException(OAuthException oAuthException) {
	     
	    }//met
    }//inner class

}
