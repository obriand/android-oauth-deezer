package com.obriand.android_oauth_deezer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.deezer.sdk.AsyncDeezerTask;
import com.deezer.sdk.DeezerConnect;
import com.deezer.sdk.DeezerConnectImpl;
import com.deezer.sdk.DeezerError;
import com.deezer.sdk.DeezerRequest;
import com.deezer.sdk.DialogError;
import com.deezer.sdk.DialogListener;
import com.deezer.sdk.OAuthException;
import com.deezer.sdk.RequestListener;
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
    /** DeezerRequestListener object used to handle requests. */
    RequestListener handler = new MyDeezerRequestHandler();
    /** DeezerTaskRequestListener object used to handle requests. */
    RequestListener deezerTaskHandler = new MyDeezerTaskHandler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Log.i(TAG, "restore");
		// Verify is already logged in using 'restore' method of the session store (if already logged in, the token is stored in the session store
		SessionStore sessionStore = new SessionStore();
		if( sessionStore.restore( deezerConnect, this ) ) {
			Toast.makeText( this, "Already logged in !", Toast.LENGTH_SHORT ).show();
		}
		
	}
	
	// Login button
	public void btLogin(View view) {
		Log.i(TAG, "login");
		// Call the authorize method of deezerConnect SDK object to launch login process :
		// Use LoginDialogHandler inner class to handle callbacks methods (success, error, cancel...)
		deezerConnect.authorize( MainActivity.this, PERMISSIONS, new LoginDialogHandler() );
	}
	
	// Sync request button (forbidden in main thread)
	public void btSynchronousRequest(View view) {
	    String userId = "2529";
	    DeezerRequest request = new DeezerRequest( "/user/"+userId+"/playlists" );
	    String result = null;
	    try {
	    	result = deezerConnect.requestSync( request );
	    	Toast.makeText(MainActivity.this, "Synchro request:"+result, Toast.LENGTH_LONG).show();
	    } catch( MalformedURLException ex ) {
	    //todo some code. warning code is executed in same thread as request.
	    } catch( IOException ex ) {
	    //todo some code. warning code is executed in same thread as request.
	    } catch (OAuthException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DeezerError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// ASync request button
	// Responses in MyDeezerRequestHandler inner class
	public void btASynchronousRequest(View view) {
		//sending requests
		String userId = "2529";
		DeezerRequest request = new DeezerRequest( "/user/"+userId+"/playlists" );
		deezerConnect.requestAsync( request, handler);
	}
	
	// AsyncDeezerTask request button
	// Responses in MyDeezerTaskHandler inner class
	public void btDeezerASyncTaskRequest(View view) { 
		//sending requests
		String userId = "2529";
		DeezerRequest request = new DeezerRequest( "/user/"+userId+"/playlists" );
		AsyncDeezerTask asyncDeezerTask = new AsyncDeezerTask( deezerConnect, deezerTaskHandler );
		//optinionally, use an executor
		BlockingQueue<Runnable> worksQueue = new ArrayBlockingQueue<Runnable>(2);
		Executor executor = new ThreadPoolExecutor(3, 3, 10, TimeUnit.SECONDS, worksQueue);
		//execute the AsyncTask with the executor
		asyncDeezerTask.executeOnExecutor(executor, request);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true; 
	}
	
    /** Handle DeezerConnect callbacks. */
	// Callbacks for Login
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
    
    
    /** Handle DeezerConnect callbacks. */
	// Callbacks for async request
    class MyDeezerRequestHandler implements RequestListener {
    	public void onComplete(String response, Object requestId) {
    		//Toast.makeText( getApplicationContext(), "onComplete request:"+response, Toast.LENGTH_SHORT ).show();
    		Log.i(TAG, "onComplete async request:"+response);
    		final String uiResponse = response;
    		runOnUiThread(new Runnable(){
    		    public void run() {
    		    	Toast.makeText( getApplicationContext(), "onComplete async request:"+uiResponse, Toast.LENGTH_SHORT ).show();
    		    }
    		});
    		//Warning code is not executed in UI Thread
	    	if( "list-playlists".equals(requestId) ) {
		    	// TODO Implement some code to parse the answer as detailed in
		    	//http://developers.deezer.com/api/user/playlists
	    	}//if
    	}
    	public void onIOException(IOException e, Object requestId) {
	    	// TODO. Implement some code to handle error. Warning code is not executed in UI Thread
    	}
    	public void onMalformedURLException(MalformedURLException e, Object requestId) {
	    	// TODO Implement some code to handle error. Warning code is not executed in UI Thread
    	}
		@Override
		public void onDeezerError(DeezerError arg0, Object arg1) {
			// TODO Auto-generated method stub
		}
		@Override
		public void onOAuthException(OAuthException arg0, Object arg1) {
			// TODO Auto-generated method stub
		}
    }//inner class
    
    class MyDeezerTaskHandler implements RequestListener {
    	public void onComplete(String response, Object requestId) {
    	//code is executed in UI Thread
    	Toast.makeText( getApplicationContext(), "onComplete deezer task:"+response, Toast.LENGTH_SHORT ).show();
    	if( "list-playlists".equals(requestId)) {
	    	//TODO. Implement some code to parse the answer as detailled in
	    	//http://developers.deezer.com/api/user/playlists
    	}//if
    	}
    	public void onIOException(IOException e, Object requestId) {
    		//TODO. Implement some code to handle error. Code is executed in UI Thread
    	}
    	public void onMalformedURLException(MalformedURLException e, Object requestId) {
    		//TODO. Implement some code to handle error. Code is executed in UI Thread
    	}
		@Override
		public void onDeezerError(DeezerError arg0, Object arg1) {
			// TODO Auto-generated method stub
		}
		@Override
		public void onOAuthException(OAuthException arg0, Object arg1) {
			// TODO Auto-generated method stub
		}
    }//inner class 

}
