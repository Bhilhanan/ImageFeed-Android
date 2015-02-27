package com.example.nasaimageoftheday;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.StrictMode;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

@SuppressLint("NewApi")
public class IotdHandler extends DefaultHandler {
private String url ="http://www.nasa.gov/rss/image_of_the_day.rss"; 
//"http://feeds.feedburner.com/euronews/en/picture-of-the-day?format=xml";
//"http://earthobservatory.nasa.gov/Feeds/rss/eo2.rss";
//"http://www.nasa.gov/rss/image_of_the_day.rss";
private boolean inUrl = false;
private boolean inTitle = false;
private boolean inDescription = false;
private boolean inItem = false;
private boolean inDate = false;
private Bitmap image = null;
private String imageUrl=null;
private String title = null;
private StringBuffer description = new StringBuffer();
private String date = null;


HttpURLConnection connection;


ArrayList<LinearLayout> container;

MainActivity context;
public void processFeed(MainActivity context) {
try {
	this.context=context;
	container=new ArrayList<LinearLayout>();
//This part is added to allow the network connection on a main GUI thread...    
StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
StrictMode.setThreadPolicy(policy); 
SAXParserFactory factory = SAXParserFactory.newInstance();
SAXParser parser = factory.newSAXParser();
XMLReader reader = parser.getXMLReader();
reader.setContentHandler(this);
URL urlObj = new URL(url);
InputStream inputStream = urlObj.openConnection().getInputStream();
reader.parse(new InputSource(inputStream));
} 
catch (Exception e) 
{
    e.printStackTrace();
    //System.out.println(new String("Got Exception General"));
}
}



private Bitmap getBitmap(String url) {
try {
    //System.out.println(url);
connection =
(HttpURLConnection)new URL(url).openConnection();
connection.setDoInput(true);
connection.connect();
InputStream input = connection.getInputStream();
Bitmap bitmap = BitmapFactory.decodeStream(input);
input.close();
return bitmap;
} 
catch (IOException ioe) 
{
    //System.out.println(new String("IOException in reading Image"));
    return null;
}
catch (Exception ioe) 
{
    //System.out.println(new String("IOException GENERAL"));
    return null;
}
}

public void startElement(String uri, String localName, String qName,
Attributes attributes) throws SAXException 
{
	
if (localName.equals("enclosure"))
{
    //System.out.println(new String("characters Image"));
    imageUrl = attributes.getValue("","url");
    //System.out.println(imageUrl);
    inUrl = true; 
}
else { inUrl = false; }
if (localName.startsWith("item")) { inItem = true; }
else if (inItem) {
if (localName.equals("title")) { inTitle = true; 
	}
else { inTitle = false; }
if (localName.equals("description")) { inDescription = true; }
else { inDescription = false; }
if (localName.equals("pubDate")) { inDate = true; }
else { inDate = false; }
}
}

public void characters(char ch[], int start, int length) {
    //System.out.println(new String("characters"));
String chars = new String(ch).substring(start, start + length);
//System.out.println(chars);
if (inUrl) 
{
    //System.out.println(new String("IMAGE"));
    //System.out.println(imageUrl);
    image = getBitmap(imageUrl);
    inUrl=false;
}
if (inTitle && title == null) {
    //System.out.println(new String("TITLE"));
    title = chars;
}
if (inDescription) {
	description.append(chars);
	}

if (inDate && date == null) { date = chars;}
}
int count=0;
public void endElement(String uri,String localName,String qName){
	if(localName.equals("item")){
	//if(imageUrl!=null && title!=null && description!=null && date!=null){
		//context.createNewLayout(image,title,description,date);
		/*
		 * */
		LinearLayout layout=new LinearLayout(context);
		 TextView titleView=new TextView(context);
		 TextView dateView=new TextView(context);
		 TextView descpView=new TextView(context);
		 ImageView imageView=new ImageView(context);
		 View seperator=new View(context);
		 
		 layout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
		 layout.setOrientation(1);
		 
		 //adding titleView
		 titleView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
		 titleView.setText(title);
		 titleView.setTextSize(18);
		 titleView.setTextColor(Color.CYAN);
		 //adding dateView
		 dateView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
		 dateView.setText(date);
		 dateView.setTextColor(Color.CYAN);
		 dateView.setTextSize(15);
		 //adding imageView
		 imageView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
		 imageView.setImageBitmap(image);
		 imageView.setAdjustViewBounds(true);
		 imageView.setTag(imageUrl);
		 //adding descpView
		 descpView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
		 descpView.setText(description);
		 descpView.setTextSize(15);
		 //add separator
		 seperator.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,5));
		 seperator.setBackgroundColor(Color.WHITE);
		 layout.addView(titleView);
		 layout.addView(dateView);
		 layout.addView(imageView);
		 layout.addView(descpView);
		 layout.addView(seperator);
		 container.add(layout);
		 
		 /*
		*/
		
		title=null;		
		date=null;
		description=new StringBuffer();
		imageUrl=null;
		//image=null;
		connection.disconnect();
		
	}
}


public ArrayList<LinearLayout> getContainer(){
	return container;
}


}