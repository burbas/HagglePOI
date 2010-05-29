package com.datakom;


import java.io.File;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.datakom.POIObjects.HaggleConnector;

public class TabInfo extends Activity {
	HaggleConnector conn;
	
	private float global_rating = 0;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tabinfoview);

		//First we need to get the data from the intent
		
		
		
		TableLayout tl = (TableLayout) findViewById(R.id.comment_table);
		Button showOnMap = (Button) findViewById(R.id.view_on_map_button);
		
		tl.addView(addReview("En mycket trevlig resturang\n" + "Jag likert", 4));
		tl.addView(addReview("Trevlit trevlit", 4));
		tl.addView(addReview("Haj haj", 4));
		tl.addView(addReview("Haj haj", 2));
		tl.addView(addReview("Haj haj", 2));
		tl.addView(addReview("Haj haj", 1));
		
		setTitle("Riffiffi");
		getStars();
		getPicture("/sdcard/hehe");
		
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
		
	}
	
	// So we can get context from another intent
	public void onReceive(Context context, Intent intent) {
		
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
	
	// This sets the stars (Bad programming, but we'll maybe fix this later?)
	protected void getStars()
	{
		int rounded = java.lang.Math.round(global_rating);
		ImageView star1 = (ImageView) findViewById(R.id.star_1);
		ImageView star2 = (ImageView) findViewById(R.id.star_2);
		ImageView star3 = (ImageView) findViewById(R.id.star_3);
		ImageView star4 = (ImageView) findViewById(R.id.star_4);
		ImageView star5 = (ImageView) findViewById(R.id.star_5);
		
		if(rounded > 0) {
			star1.setImageResource(R.drawable.rate_star_big_on);
			if(rounded > 1) {
				star2.setImageResource(R.drawable.rate_star_big_on);
				if(rounded > 2) {
					star3.setImageResource(R.drawable.rate_star_big_on);
					if(rounded > 3) {
						star4.setImageResource(R.drawable.rate_star_big_on);
						if(rounded > 4) {
							star5.setImageResource(R.drawable.rate_star_big_on);
						} else {
							star5.setImageResource(R.drawable.rate_star_big_off);
						}
					} else {
						star4.setImageResource(R.drawable.rate_star_big_off);
						star5.setImageResource(R.drawable.rate_star_big_off);
					}
				} else {
					star3.setImageResource(R.drawable.rate_star_big_off);
					star4.setImageResource(R.drawable.rate_star_big_off);
					star5.setImageResource(R.drawable.rate_star_big_off);
				}
			} else {
				star2.setImageResource(R.drawable.rate_star_big_off);
				star3.setImageResource(R.drawable.rate_star_big_off);
				star4.setImageResource(R.drawable.rate_star_big_off);
				star5.setImageResource(R.drawable.rate_star_big_off);
			}
		} else {
			star1.setImageResource(R.drawable.rate_star_big_off);
			star2.setImageResource(R.drawable.rate_star_big_off);
			star3.setImageResource(R.drawable.rate_star_big_off);
			star4.setImageResource(R.drawable.rate_star_big_off);
			star5.setImageResource(R.drawable.rate_star_big_off);
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