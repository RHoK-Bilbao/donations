package org.rhok.microdonaciones;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class DonationActivity extends Activity {

	private WebView webView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_waiting);
		String url = getIntent().getStringExtra("URL");
		loadWebView(url);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	private void loadWebView(String url) {
		// TODO Auto-generated method stub
		webView = new WebView(this);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebChromeClient(new WebChromeClient() {
			   public void onProgressChanged(WebView view, int progress) {
			     // Activities and WebViews measure progress with different scales.
			     // The progress meter will automatically disappear when we reach 100%
			     if(progress != 100)setContentView(R.layout.activity_waiting);
			   }
			 });
		webView.setWebViewClient(new WebViewClient() {
		   public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			   Toast.makeText(DonationActivity.this, "Oh no! " + description, Toast.LENGTH_SHORT).show();
		   }
		   
		   @Override
		   public void onPageFinished(WebView view, String url) {
			   // TODO Auto-generated method stub
			   super.onPageFinished(view, url);
			   setContentView(webView);
		   }

		   @Override
		   public boolean shouldOverrideUrlLoading(WebView view, String url) {
			   // TODO Auto-generated method stub
			   view.loadUrl(url);
			   return true;
		   }   
		 });
		 webView.loadUrl(url);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_donation, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
