package org.rhok.microdonaciones;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.rhok.microdonaciones.config.Config;
import org.rhok.microdonaciones.data.adapter.SpannedAdapter;
import org.rhok.microdonaciones.data.wrappers.MDProjectHtmlWrapper;
import org.rhok.microdonaciones.resultDelegate.ResultDelegate;

import android.app.ActionBar;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.paypal.android.MECL.PayPal;

public class MainActivity extends FragmentActivity implements ActionBar.OnNavigationListener , DonationInputDialog.DonationInputDialogListener{
	
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	
	//The reference token that we get from initializing the MECL library
	public static String _deviceReferenceToken;
	
	// Handler to get messages from the service
	private final IncomingHandler inHandler = new IncomingHandler(this);
	
	//The possible results from initializing MECL
	protected static final int INITIALIZE_SUCCESS = 0;
	protected static final int INITIALIZE_FAILURE = 1;
	
	private String selectedCampaign = null;
	
	private ListView lView;
	private ProgressBar pBar;
	private TextView tView;	
	private WebView _webView;
		
	static class IncomingHandler extends Handler {
		private final WeakReference<MainActivity> mActivity; 
		
		IncomingHandler(MainActivity activity) {
			mActivity = new WeakReference<MainActivity>(activity);
	    }
		
		@Override
	    public void handleMessage(Message msg)
	    {
			MainActivity activity = mActivity.get();
	         if (activity != null) {
	        	 activity.handleMessage(msg);
	         }
	    }
	}
	
	public void handleMessage(Message msg) {
		switch(msg.what){
	    	case INITIALIZE_FAILURE:
	    		//Initialization failure, app may exit...
	    		Toast.makeText(this, "Error al inicializar Paypal", Toast.LENGTH_SHORT).show();
	    		break;
		}
	}

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        waitingUiBinds();
        createSDFolderIfNoExist();
        initPaypalLibrary();
        final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		// Set up the dropdown list navigation in the action bar.
		actionBar.setListNavigationCallbacks(
		// Specify a SpinnerAdapter to populate the dropdown list.
				new ArrayAdapter<String>(actionBar.getThemedContext(),
						android.R.layout.simple_list_item_1,
						android.R.id.text1, new String[] {
								"Projectos abiertos",
								"Projectos cerrados" }), this);
    }

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current dropdown position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current dropdown position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
				.getSelectedNavigationIndex());
	}
	
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		FetchMDProjectTask task = new FetchMDProjectTask();
		switch (itemPosition){
		case 0:
	        task.execute(Config.PROJECTS_URL);
	        break;
		case 1:
	        task.execute(Config.CLOSED_PROJECTS_URL);
	        break;
	    default:
	    	return false;
		}
		return true;
	}
	
    private void createSDFolderIfNoExist() {
		// create a File object for the parent directory
	    File wallpaperDirectory = new File(Config.CACHE_DIR);
		// have the object build the directory structure, if needed.
		wallpaperDirectory.mkdirs();
	}

  private void printListView(ArrayList<Spanned> values){
	  	setContentView(R.layout.activity_main);
	  	boolean bPaypal = false;
	  	if(getActionBar().getSelectedNavigationIndex() == 0)bPaypal = true;
  		SpannedAdapter adapter = new SpannedAdapter(this, values,bPaypal);
  		lView = (ListView) findViewById(R.id.listView1);
  		lView.setAdapter(adapter);
  		lView.setVisibility(View.VISIBLE);
    }
    
  	private void waitingUiBinds() {
	  	setContentView(R.layout.activity_waiting);
	  	tView = (TextView) findViewById(R.id.textViewProgress);
	  	pBar = (ProgressBar) findViewById(R.id.progressBar1);
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
	public void showDonationDialog(String campaign){
		selectedCampaign = campaign;
		DialogFragment dialog = new DonationInputDialog();
		dialog.show(getFragmentManager(), "DonationInputDialog");
	}
	
	private class FetchMDProjectTask extends AsyncTask< String, Void, ArrayList<Spanned>>{

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			waitingUiBinds();

		}

		@Override
		protected void onPostExecute(ArrayList<Spanned> result) {
			super.onPostExecute(result);
			if (result != null){
				printListView(result);
			}
		}

		@Override
		protected ArrayList<Spanned> doInBackground(String... urls) {
			  InputStream content = null;
			  try {
			    HttpClient httpclient = new DefaultHttpClient();
			    HttpResponse response = httpclient.execute(new HttpGet(urls[0]));
			    content = response.getEntity().getContent();
			  } catch (Exception e) {
			    Log.d("[GET REQUEST]", "Network exception", e);
			  }
			  if( content != null){
				  java.util.Scanner s = new java.util.Scanner(content).useDelimiter("\\A");
				  MDProjectHtmlWrapper mDPWrapper = new  MDProjectHtmlWrapper(s.hasNext() ? s.next() : "");
				  return mDPWrapper.getSpannableList();

			  }
			  return null;

		}

	}
	
	private void initPaypalLibrary(){
    	//Create a separate thread to do the initialization
    	Thread libraryInitializationThread = new Thread() {
			public void run() {
				//Initialize the library
				
				// This is the main initialization call that takes in your Context, the Application ID, the server you would like to connect to, and your PayPalListener
				PayPal.fetchDeviceReferenceTokenWithAppID(getApplicationContext(), Config.appID, Config.server,new ResultDelegate() );
				// -- These are required settings.
				PayPal.getInstance().setLanguage("es_ES"); // Sets the language for the library.
				// The library is initialized so let's launch it by notifying our handler
				if (PayPal.getInstance().isLibraryInitialized()) {
					inHandler.sendEmptyMessage(INITIALIZE_SUCCESS);
				}
				else {
					inHandler.sendEmptyMessage(INITIALIZE_FAILURE);
				}
			}
		};
		libraryInitializationThread.start();
    }
	
	private class PostPaymentOptions{
		String amount;
		String campaing;
		
		public PostPaymentOptions(String amount, String campaing) {
			super();
			this.amount = amount;
			this.campaing = campaing;
		}
	}
	
	class PostPaymentData extends AsyncTask<PostPaymentOptions, Void, String> {
	    @Override
	    protected String doInBackground(PostPaymentOptions... params) {
	    	HttpClient httpclient = new DefaultHttpClient();
		    HttpPost httppost = new HttpPost(Config.APP_SERVER);
		    try {
		        // Add your data
		        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		        PostPaymentOptions paymentParams = params[0];
		        nameValuePairs.add(new BasicNameValuePair("amount", paymentParams.amount));
		        nameValuePairs.add(new BasicNameValuePair("item", paymentParams.campaing));
		        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		        // Execute HTTP Post Request
		        HttpResponse response = httpclient.execute(httppost);
		        Scanner s = new Scanner(response.getEntity().getContent());
		        return s.hasNext() ? s.next() : "";
		    } catch (ClientProtocolException e) {
		        // TODO Auto-generated catch block
		    	return null;
		    } catch (IOException e) {
		        // TODO Auto-generated catch block
		    	return null;
		    }
		}

	    @Override
	    protected void onPostExecute(String result) {
	    	// result es el token
	    	paypalExpressCheckout(result);
	    }
	}
	
	public void paypalExpressCheckout(String token) {
		
    	if(token == null){
    		Toast.makeText(this, "Failed to load Paypal payment gateway", Toast.LENGTH_SHORT).show();
    		return;
    	}
		//Setup the url for paypal server
		String buf = new String("https://www.sandbox.paypal.com/cgi-bin/webscr?");
		//command
		buf += "cmd=_express-checkout-mobile";
		//devicetoken
		buf += "&drt=" + _deviceReferenceToken ;
		//tokenresponse
		buf += "&token=" + token ;
		
		Intent intent = new Intent(this,DonationActivity.class);
    	intent.putExtra("URL", buf);
    	startActivity(intent);
    	setProgressBarIndeterminateVisibility(false);
    }

	@Override
	public void onDonationSelection(DialogFragment dialog, int selected) {
		// TODO Auto-generated method stub
		String amount = getResources().getStringArray(R.array.donations)[selected];
		amount = amount.replace(" €", "");
		setProgressBarIndeterminateVisibility(true);
		PostPaymentOptions params = new PostPaymentOptions(amount,this.selectedCampaign);
		new PostPaymentData().execute(params);
	}
}
