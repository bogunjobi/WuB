package mwn.bolfab.wakeupbuddy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class SplashScreen extends Activity {

	final static int DURATION = 700;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
	}

	@Override
	protected void onResume() {
		super.onResume();
		splashWelcome(DURATION);
	}

	/* Run the splash screen for given time limit */
	protected void splashWelcome(final int limit) {
		Thread splashThread = new Thread() {
			@Override
			public void run() {
				try {
					int waited = 0;
					while (waited < limit) {
						sleep(100);
						waited += 100;
					}
				} catch (InterruptedException e) {
					Log.d("SplashScreen Error:", e.getMessage().toString());
				} finally {
					Settings.can_play= Settings.DONT_PLAY;
					Intent i = new Intent(SplashScreen.this, Main.class);
					startActivity(i);
					finish();	
				}
				
			} 
		};
		splashThread.start();
	}
	
}