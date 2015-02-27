package com.imagefeed;


import java.util.ArrayList;

import com.example.nasaimageoftheday.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	TextView titleView,dateView,descpView;
	ImageView imageView;
	View seperator;
	ProgressDialog dialog;
	MainActivity ref;
	String url="http://www.nasa.gov/rss/image_of_the_day.rss";
			//"http://feeds.feedburner.com/euronews/en/picture-of-the-day?format=xml";
			//"http://earthobservatory.nasa.gov/Feeds/rss/eo2.rss";
			
	IotdHandler iotdhandler;
	Handler handler;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      
        
        paintLayout();
        
        
        
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public boolean onMenuItemSelected(int featureId,MenuItem item){
    	
    	if(item.getItemId()==R.id.refresh){
    		refreshClicked(null);
    	}
		return super.onMenuItemSelected(featureId, item);    	
    }
    
   
    

	
	
	public void refreshFromFeed(){

	    iotdhandler=new IotdHandler();
		iotdhandler.processFeed(this); //start parsing
		
        	
        
	}
	
	public void drawLayout(){
		ArrayList<LinearLayout> container=new ArrayList<LinearLayout>();
		container=iotdhandler.getContainer();
		
		for(LinearLayout e:container){
			for(int i=0;i<e.getChildCount();i++){
				if(e.getChildAt(i) instanceof ImageView){
					e.getChildAt(i).setOnClickListener(new imageViewListener());
				}
			}
			((LinearLayout)findViewById(R.id.main_layout)).addView(e);
		}
		
		
		
	}
	
	public void paintLayout(){
handler=new Handler();
        
        dialog=ProgressDialog.show(this, "Loading", "Parsing RSS feed");
        Thread th=new Thread(){
        	public void run(){
        		refreshFromFeed();
        		handler.post(
        				new Runnable(){
        					public void run(){
        						drawLayout();
        						dialog.dismiss();
        						Toast.makeText(MainActivity.this, "Refreshed", Toast.LENGTH_SHORT).show();
        					}
        				});
        };
        };
        th.start();
	}
	
	public void refreshClicked(View view){
		((LinearLayout)findViewById(R.id.main_layout)).removeAllViews();
		try{
			paintLayout();
			
		}catch(Exception e){
			Toast.makeText(this, "Refresh failed", Toast.LENGTH_SHORT).show();
		}
	}
	
	private class imageViewListener implements View.OnClickListener{

		@Override
		public void onClick(View view) {
			
			Intent intent=new Intent(MainActivity.this,DisplayImage.class);
			String url=(String)view.getTag();
			intent.putExtra("imageUrl", url);
			startActivity(intent);
			
		}
		
	}
}
  
	



