package com.rommelosc.kalpaper;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.WindowManager;

public class MyAnimationWallpaper extends AnimationWallpaper {

	@Override
	public Engine onCreateEngine() {
		return new KaleidoscopeEngine();
	}

	class KaleidoscopeEngine extends AnimationEngine {

		public KaleidoscopeEngine() {

			metrics = new DisplayMetrics();
			Display display = ((WindowManager) getSystemService(WINDOW_SERVICE))
					.getDefaultDisplay();
			display.getMetrics(metrics);

			sideWith = metrics.widthPixels / 2;

			xi = 0;
			yi = 0;
			dx = 1;
			dy = 1;
			
			
			SharedPreferences sharedPreferences = getSharedPreferences("settings",
					Context.MODE_PRIVATE);
			
			String image;
			String path = sharedPreferences.getString("imageCameraOrGallery", "");
			if( !path.equals("") ){
				bmSource = BitmapFactory.decodeFile(path);
				image = path;
			}
			else{
				
				image = sharedPreferences.getString("imageDefaultPreference", "fractal");
				
				if (image.equals("fractal")) {
					bmSource = BitmapFactory.decodeResource(getResources(),
							R.drawable.fractal);
				} else if (image.equals("escher")) {
					bmSource = BitmapFactory.decodeResource(getResources(),
							R.drawable.escher);
		
				} else if (image.equals("dhali")) {
					bmSource = BitmapFactory.decodeResource(getResources(),
							R.drawable.dhali);
		
				} else if (image.equals("universo")) {
					bmSource = BitmapFactory.decodeResource(getResources(),
							R.drawable.galaxia);
				}
				
			}
			
			String delayStr = sharedPreferences.getString("delayPreference", "1300");
			delay = Integer.parseInt(delayStr);
			
			String deltaStr = sharedPreferences.getString("deltaPreference", "4");
			DELTA = Integer.parseInt(deltaStr);
			
			
			Log.i("","image = "+image);
			Log.i("","delay = "+delay);
			Log.i("","delta = "+DELTA);
	
			// bmSource = Bitmap.createScaledBitmap(myBitmap,
			// metrics.widthPixels,
			// metrics.heightPixels, false);

//			bmSource = BitmapFactory.decodeResource(getResources(),
//					R.drawable.fractal);

			kaleidoscope();
		}

		@Override
		protected void drawFrame() {

			SurfaceHolder holder = getSurfaceHolder();
			Canvas c = null;

			try {
				c = holder.lockCanvas();
				if (c != null) {
					draw(c);
				}
			} finally {
				if (c != null) {
					holder.unlockCanvasAndPost(c);
				}
			}
		}

		public void kaleidoscope() {

			Bitmap bmTemp = Bitmap.createBitmap(metrics.widthPixels,
					metrics.widthPixels, Bitmap.Config.ARGB_8888);

			int x;
			int y;

			int pixel;

			for (int j = 0; j < sideWith; j++) {
				x = xi + j;

				for (int k = 0; k <= j; k++) {
					y = yi + sideWith - k;

					pixel = bmSource.getPixel(x, y);
					bmTemp.setPixel(sideWith + j, sideWith - k, pixel);

					// Primera reflexi�n a 45�
					bmTemp.setPixel(sideWith + k, sideWith - j, pixel);
				}

			}

			// 2da reflexi�n sobre x
			for (int j = 0; j < sideWith; j++) {
				for (int k = 0; k < sideWith; k++) {
					pixel = bmTemp.getPixel(sideWith + j, sideWith - k);
					bmTemp.setPixel(sideWith - j, sideWith - k, pixel);
				}
			}

			// 3ra refelxi�n sobre y
			for (int j = 0; j < bmTemp.getWidth(); j++) {
				for (int k = 0; k < sideWith; k++) {
					pixel = bmTemp.getPixel(j, k);
					bmTemp.setPixel(j, bmTemp.getWidth() - k - 1, pixel);
				}
			}

			// bmKalei = bmTemp;
			bmKalei = Bitmap.createScaledBitmap(bmTemp, metrics.widthPixels,
					metrics.heightPixels, false);

			if (xi == 0) {
				dx = DELTA;
			}

			if (xi == (bmSource.getWidth() - sideWith - 10)) {
				dx = -DELTA;
			}

			if (yi == 0) {
				dy = DELTA;
			}

			if (yi == (bmSource.getHeight() - sideWith - 10)) {
				dy = -DELTA;
			}

			xi = xi + dx;
			yi = yi + dy;

		}

		public void draw(Canvas canvas) {
			kaleidoscope();
			canvas.drawBitmap(bmKalei, 0, 0, null);
		}

		// private static final String LOGTAG = "LogsAndroid";
		// public static Bitmap bmSource;
		private int DELTA = 5;
		private Bitmap bmSource;
		private Bitmap bmKalei;
		private DisplayMetrics metrics;
		private int sideWith;
		private int xi;
		private int yi;
		private int dx;
		private int dy;

	}

}
