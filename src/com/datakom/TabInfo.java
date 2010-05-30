package com.datakom;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.datakom.POIObjects.HaggleConnector;
import com.datakom.POIObjects.POIObject;

public class TabInfo extends Activity {
	HaggleConnector conn;
	
	private float global_rating = 0;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tabinfoview);

		TableLayout tl = (TableLayout) findViewById(R.id.comment_table);
		Button showOnMap = (Button) findViewById(R.id.view_on_map_button);
		
		//First we need to get the data from the intent
		Bundle extras = getIntent().getExtras();
		if(extras != null)
		{
			String searchTitle = extras.getString(com.datakom.POIObjects.HaggleConnector.SEARCH_TITLE);
			
			ArrayList<POIObject> objects = HaggleConnector.getInstance().getPOIObjectsByName(searchTitle);
			
			for(POIObject poiobj : objects)
			{
				setTitle(poiobj.getName());
				tl.addView(addReview(poiobj.getDescription(), (int)poiobj.getRating()));
				getPicture(poiobj.getPicPath());
			}
			
			setStars();
			// Shows it on the map
			showOnMap.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					
					Bundle coordinates = new Bundle();
					coordinates.putDouble("LATITUDE", 1.00);
					coordinates.putDouble("BLA", 2.00);
					
					Intent mapview = new Intent(TabInfo.this, TabMap.class);
					mapview.putExtra("com.datakom.TabMap", coordinates);
					TabInfo.this.startActivity(mapview);
					
				}
			});
			
		} else {
			setTitle("Haggle POI Application");
			showOnMap.setVisibility(8); // Sets the visibility of this object to "GONE"
			tl.addView(addReview(
					"This is a sample application to show how Haggle    \n" +
					"can be used within a point-of-interest application\n" +
					"application. This was part of a school-project in \n" +
					"the spring of 2010.", 5));
			getPicture("/sdcard/hehe");
		}
	}
	
	
	protected void getPicture(String path) 
	{
		File imageFile = new File(path);
		ImageView iv = (ImageView) findViewById(R.id.poi_img);
		
		if(imageFile.exists())
		{
			Bitmap thePicture = BitmapFactory.decodeFile(path);
			iv.setImageBitmap(thePicture);
		} else {
			iv.setImageResource(R.drawable.btn_rating_star_off_selected);
		}
	}
	
	// Sets the title of the info tab
	protected TextView setTitle(String strtitle) {
		TextView title = (TextView) findViewById(R.id.info_title);
		title.setText(strtitle);
		
		return title;
	}
	
	// This sets the stars
	protected void setStars()
	{
		ImageView star1 = (ImageView) findViewById(R.id.star_1);
		ImageView star2 = (ImageView) findViewById(R.id.star_2);
		ImageView star3 = (ImageView) findViewById(R.id.star_3);
		ImageView star4 = (ImageView) findViewById(R.id.star_4);
		ImageView star5 = (ImageView) findViewById(R.id.star_5);

		if(global_rating>4.75){
			star5.setImageResource(R.drawable.rate_star_big_on);
		} else if(global_rating>4.25) {
			star5.setImageResource(R.drawable.rate_star_big_half);
		} else {
			star5.setImageResource(R.drawable.rate_star_big_off);
		}
		if(global_rating>3.75){
			star4.setImageResource(R.drawable.rate_star_big_on);
		} else if(global_rating>3.25) {
			star4.setImageResource(R.drawable.rate_star_big_half);
		} else {
			star4.setImageResource(R.drawable.rate_star_big_off);
		}
		if(global_rating>2.75){
			star3.setImageResource(R.drawable.rate_star_big_on);
		} else if(global_rating>2.25) {
			star3.setImageResource(R.drawable.rate_star_big_half);
		} else {
			star3.setImageResource(R.drawable.rate_star_big_off);
		}
		if(global_rating>1.75){
			star2.setImageResource(R.drawable.rate_star_big_on);
		} else if(global_rating>1.25) {
			star2.setImageResource(R.drawable.rate_star_big_half);
		} else {
			star2.setImageResource(R.drawable.rate_star_big_off);
		}
		if(global_rating>0.75){
			star1.setImageResource(R.drawable.rate_star_big_on);
		} else if(global_rating> 0.25){
			star1.setImageResource(R.drawable.rate_star_big_half);
		} else {
			star1.setImageResource(R.drawable.rate_star_big_off);
		}
	}
	
	// Sets the picture 
	protected ImageView SetPicture(String path) {

		ImageView picture = new ImageView(TabInfo.this);
		
		return picture;
	}
	
	// Inserts a row with a review
	protected TableRow addReview(String comment, int rating){
		TextView commentView = new TextView(TabInfo.this);
		commentView.setText(comment);
		
		this.global_rating = (this.global_rating+rating)/2;
		
		TextView ratingView = new TextView(TabInfo.this);
		ratingView.setText(rating + "/5");
		ratingView.setGravity(0x05);
		
		
		TableRow tr = new TableRow(TabInfo.this);
		tr.addView(commentView);
		tr.addView(ratingView);
		return tr;
	}
}