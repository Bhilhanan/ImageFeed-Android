package com.example.nasaimageoftheday;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

public class DisplayImage extends Activity{

	Bitmap image;
	String url,imgTitle;
	ImageView imageView;
	Intent intent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.display_image);
		
		getActionBar().setBackgroundDrawable(new ColorDrawable(Color.argb(128, 0, 0, 0)));
		
		intent=getIntent();
		
		url=intent.getStringExtra("imageUrl");
		image=getBitmap(url);
		
		
		imageView=((ImageView)findViewById(R.id.displayImage_image));
		//imageView.setLayoutParams(new LayoutParams(image.getWidth(), image.getHeight()));

		
		imageView.setImageBitmap(image);
			
		
	}
	
	private Bitmap getBitmap(String url) {
		try {
		    //System.out.println(url);
			
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		HttpURLConnection connection =
		(HttpURLConnection)new URL(url).openConnection();
		
		connection.setDoInput(true);
		connection.connect();
		InputStream input = connection.getInputStream();
		Bitmap bitmap = BitmapFactory.decodeStream(input);
		input.close();
		
		return bitmap;
		}
		catch (Exception ioe) 
		{
		    return null;
		}
		}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_image, menu);
        return true;
    }
    
	public void saveImage() {
		imageView.buildDrawingCache();
		Bitmap bitmap=imageView.getDrawingCache();
		
		//saving in file
		OutputStream fout=null;
		File sdImageMainDirectory=null;
		//Uri outputFileUri;
		
		SimpleDateFormat sdf=new SimpleDateFormat("MMddyyyyhhmmss");
		String format=sdf.format(new Date())+".jpg";
		
		try{
			File root=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
					+File.separator+"Image RSS Feed"+File.separator);
			root.mkdirs();
			sdImageMainDirectory=new File(root,format);
			//outputFileUri=Uri.fromFile(sdImageMainDirectory);
			fout=new FileOutputStream(sdImageMainDirectory);
			Toast.makeText(this, "Storing image...", Toast.LENGTH_SHORT).show();
		}catch(Exception e){
			Toast.makeText(this, "Error occured. Please try again", Toast.LENGTH_SHORT).show();
		}
		
		try{
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fout);
			//MediaStore.Images.Media.insertImage(getContentResolver(), sdImageMainDirectory.getAbsolutePath(), "NASA Image", "Description for NASA Image");
			fout.flush();
			fout.close();
			Toast.makeText(this, "Image stored", Toast.LENGTH_SHORT).show();
		}catch(Exception e){
		}
		Intent intent=new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		//String path=sdImageMainDirectory.getAbsolutePath();
		//File f=new File(path);
		Uri contentUri=Uri.fromFile(sdImageMainDirectory);
		intent.setData(contentUri);
		this.sendBroadcast(intent);
	}
    @Override
    public boolean onMenuItemSelected(int featureId,MenuItem item){
    	
    	if(item.getItemId()==R.id.menu_save_image){
    		saveImage();
    	}
		return super.onMenuItemSelected(featureId, item);    	
    }
}
