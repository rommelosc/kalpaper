package com.rommelosc.kalpaper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Thumbnails;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);

		Button settingsButton = (Button) findViewById(R.id.settingsButton);
		settingsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, Settings.class));

			}
		});

		Button wallpaperButton = (Button) findViewById(R.id.setWallpaperButton);
		wallpaperButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				SharedPreferences pref = PreferenceManager
						.getDefaultSharedPreferences(MainActivity.this);

				SharedPreferences shared = getSharedPreferences("settings",
						Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = shared.edit();

				editor.putString("imageDefaultPreference",
						pref.getString("imageDefaultPreference", "fractal"));
				editor.putString("delayPreference",
						pref.getString("delayPreference", "1300"));
				editor.putString("deltaPreference",
						pref.getString("deltaPreference", "4"));

				editor.commit();

				Intent intent = new Intent(
						WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
				startActivity(intent);

			}
		});

		Button loadImageButton = (Button) findViewById(R.id.loadImageButton);
		loadImageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(0);
			}
		});

		setThumbnail();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		return new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_menu_camera)
				.setTitle("Cargar Imagen")
				.setMessage("Selecciona la Fuente de la Imagen")
				.setPositiveButton("Camara",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								Intent intentCamara = new Intent(
										MediaStore.ACTION_IMAGE_CAPTURE);
								startActivityForResult(intentCamara,
										PICTURE_CAMERA);

							}
						})
				.setNegativeButton("Galeria",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								Intent intentGaleria = new Intent(
										Intent.ACTION_PICK,
										android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
								startActivityForResult(intentGaleria,
										PICTURE_GALLERY);
							}
						}).create();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (data != null) {
			try {

				SharedPreferences shared = getSharedPreferences("settings",
						Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = shared.edit();

				String path = "";
				switch (requestCode) {
				case PICTURE_CAMERA:
					path = getLastImageId();
					break;

				case PICTURE_GALLERY:
					Uri imagenSeleccionada = data.getData();
					path = getRealPathFromURI(imagenSeleccionada);
					break;
				}

				editor.putString("imageCameraOrGallery", path);
				editor.commit();

				setThumbnail();

			} catch (Exception e) {
				Toast.makeText(getApplicationContext(), "Exception: : " + e,
						Toast.LENGTH_LONG).show();
			}
		} else
			Toast.makeText(getApplicationContext(), "No se capturo la imagen",
					Toast.LENGTH_LONG).show();
	}

	/**
	 * Gets the last image id from the media store
	 * 
	 * @return
	 */
	private String getLastImageId() {
		final String[] imageColumns = { MediaStore.Images.Media._ID,
				MediaStore.Images.Media.DATA };
		final String imageOrderBy = MediaStore.Images.Media._ID + " DESC";
		Cursor imageCursor = managedQuery(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageColumns,
				null, null, imageOrderBy);
		if (imageCursor.moveToFirst()) {
			int id = imageCursor.getInt(imageCursor
					.getColumnIndex(MediaStore.Images.Media._ID));
			String fullPath = imageCursor.getString(imageCursor
					.getColumnIndex(MediaStore.Images.Media.DATA));
			Log.d("", "getLastImageId::id " + id);
			Log.d("", "getLastImageId::path " + fullPath);
			imageCursor.close();
			return fullPath;
		} else {
			return "";
		}
	}

	public String getRealPathFromURI(Uri contentUri) {

		// can post image
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(contentUri, proj, // Which columns to
														// return
				null, // WHERE clause; which rows to return (all rows)
				null, // WHERE clause selection arguments (none)
				null); // Order-by clause (ascending by name)
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();

		return cursor.getString(column_index);
	}

	public void setThumbnail() {
		ImageView thumbnail = (ImageView) findViewById(R.id.thumbnail);

		SharedPreferences sharedPreferences = getSharedPreferences("settings",
				Context.MODE_PRIVATE);

		Bitmap bmSource = BitmapFactory.decodeResource(getResources(),
				R.drawable.fractal);
		String path = sharedPreferences.getString("imageCameraOrGallery", "");
		if (!path.equals("")) {
			bmSource = BitmapFactory.decodeFile(path);
		} else {
			String image = sharedPreferences.getString(
					"imageDefaultPreference", "fractal");

			Log.i("", "imagen por default es = " + image);

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

		thumbnail.setImageBitmap(bmSource);

	}
	
	@Override
	protected void onResume() {
		super.onResume();
		setThumbnail();
	}
	

	private static final int PICTURE_CAMERA = 0;
	private static final int PICTURE_GALLERY = 1;

}
