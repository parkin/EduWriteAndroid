package com.illinois.eduwrite;

import java.net.URL;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class EduWriteAndroidActivity extends Activity {
	private static final String TAG = EduWriteAndroidActivity.class.getSimpleName();

	private WebView mWebView;

	private String ip = "107.21.246.180";
	private String port = "9001";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Add progress bar support
		this.getWindow().requestFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.splash);

		TextView txt = (TextView) findViewById(R.id.splash_text);
		Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Chunkfive.otf");
		txt.setTypeface(font);

		// Make progress bar visible
		this.getWindow().setFeatureInt(Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);

		initializeWebView();
	}

	private void initializeWebView() {
		// mWebView = (WebView) findViewById(R.id.webview);
		final View newView = LayoutInflater.from(getBaseContext()).inflate(R.layout.main, null);
		mWebView = (WebView) newView.findViewById(R.id.webview);
		WebSettings settings = mWebView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setSupportZoom(true);
		settings.setBuiltInZoomControls(true);
		mWebView.requestFocus(View.FOCUS_DOWN);

		String portstring = "";
		if (port.length() > 0)
			portstring = ":" + port;

		// Set the Chrome client
		// this updates the progress bar
		final Activity activity = this;
		mWebView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				if (newProgress == 100) {
					activity.setTitle(R.string.app_name);
					activity.setContentView(newView);
					setMyWebViewClient(newView);
				}
			}

		});
		mWebView.setWebViewClient(new WebViewClient() {

			// this passes handling web pages outside of EduWrite (like
			// google.com) to
			// the default web browser.
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				try {
					URL urlObj = new URL(url);
					if (TextUtils.equals(urlObj.getHost(), ip)) {
						// Allow the WebView in your application to do its thing
						return false;
					} else {
						// Pass it to the system, doesn't match your domain
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setData(Uri.parse(url));
						startActivity(intent);
						// Tell the WebView you took care of it.
						return true;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return false;
			}

			@Override
			public void onReceivedError(WebView view, int errorCode, String description,
					String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);
				Toast.makeText(getBaseContext(), description, Toast.LENGTH_LONG).show();
			}

		});

		mWebView.loadUrl("http://" + ip + portstring);
	}

	private void setMyWebViewClient(View view) {
		final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressbar);
		mWebView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				progressBar.setVisibility(View.VISIBLE);
				progressBar.setProgress(newProgress);
				if (newProgress == 100) {
					progressBar.setVisibility(View.GONE);
				}
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Check if the back key was pressed, and if there is web history
		if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
			mWebView.goBack();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		MenuItem menuItem = menu.findItem(R.id.menu_back);
		menuItem.setEnabled(mWebView.canGoBack());
		MenuItem forward = menu.findItem(R.id.menu_forward);
		forward.setEnabled(mWebView.canGoForward());
		return super.onMenuOpened(featureId, menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_refresh:
			mWebView.reload();
			break;
		case R.id.menu_back:
			if (mWebView.canGoBack())
				mWebView.goBack();
			break;
		case R.id.menu_forward:
			if (mWebView.canGoForward())
				mWebView.goForward();
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	// used to prevent choppy color gradients
	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		Window window = getWindow();
		window.setFormat(PixelFormat.RGBA_8888);
	}

}