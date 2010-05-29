package com.datakom;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.datakom.POIObjects.HaggleConnector;

public class TabInfo extends Activity {
	HaggleConnector conn;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tabinfoview);
		
		TableLayout tl = (TableLayout) findViewById(R.id.comment_table);
		TextView title = (TextView) findViewById(R.id.info_title);
		ImageView star1 = (ImageView) findViewById(R.id.star_1);
		ImageView star2 = (ImageView) findViewById(R.id.star_2);
		ImageView star3 = (ImageView) findViewById(R.id.star_3);
		ImageView star4 = (ImageView) findViewById(R.id.star_4);
		ImageView star5 = (ImageView) findViewById(R.id.star_5);
		ImageView iv = (ImageView) findViewById(R.id.poi_img);
		iv.setImageResource(R.drawable.btn_rating_star_off_selected);
		star1.setImageResource(R.drawable.rate_star_big_on);
		star2.setImageResource(R.drawable.rate_star_big_on);
		star3.setImageResource(R.drawable.rate_star_big_on);
		star4.setImageResource(R.drawable.rate_star_big_half);
		star5.setImageResource(R.drawable.rate_star_big_off);
		title.setText("Title");
		tl.addView(makeTableRow("höhö, lool", 4));
		tl.addView(makeTableRow("höhö, bra", 4));
		tl.addView(makeTableRow("höhö, bajs", 4));
		
	}
	protected TableRow makeTableRow(String comment, int rating){
		TextView commentView = new TextView(TabInfo.this);
		commentView.setText(comment);
		
		TextView ratingView = new TextView(TabInfo.this);
		ratingView.setText("dinmamma");
		ratingView.setGravity(0x05);
		
		TableRow tr = new TableRow(TabInfo.this);
		tr.addView(commentView);
		tr.addView(ratingView);
		
		return tr;
	}
}