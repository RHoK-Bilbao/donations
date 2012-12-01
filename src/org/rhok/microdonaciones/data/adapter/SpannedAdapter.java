package org.rhok.microdonaciones.data.adapter;

import java.util.ArrayList;

import org.rhok.microdonaciones.R;
import org.rhok.microdonaciones.config.Config;
import org.rhok.microdonaciones.MainActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.paypal.android.MECL.CheckoutButton;
import com.paypal.android.MECL.PayPal;

public class SpannedAdapter extends BaseAdapter {
     private LayoutInflater mInflater;
     private ArrayList<Spanned> mArticleList;
     private Context context;
     private boolean bPaypal;
     public SpannedAdapter(Context context, ArrayList<Spanned> articleList, boolean bPaypal) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
        this.bPaypal = bPaypal;
        mArticleList = articleList;
     }

     public int getCount() {
         return mArticleList.size();
     }

     public Object getItem(int position) {
         return position;
     }

     public long getItemId(int position) {
         return position;
     }

     public View getView(int position, View convertView, ViewGroup parent) {
         ViewHolder holder;
         final Spanned spanned = mArticleList.get(position);
         if (convertView == null) {
             convertView = mInflater.inflate(R.layout.md_project_row, null);
             holder = new ViewHolder();
             holder.title = (TextView) convertView.findViewById(R.id.textViewTitle);
             holder.pBar = (ProgressBar) convertView.findViewById(R.id.progressBarRecap);
             holder.pBarStart = (TextView) convertView.findViewById(R.id.textViewProgressBarStart);
             holder.pBarEnd = (TextView) convertView.findViewById(R.id.textViewProgressBarEnd);
             holder.fundation = (TextView) convertView.findViewById(R.id.textViewFundation);
             holder.description = (TextView) convertView.findViewById(R.id.textViewDescription);
             holder.moreInfoButton = (Button) convertView.findViewById(R.id.buttonInfo);
             holder.moreInfoButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					URLSpan url =  spanned.getSpans(0,
			        		 spanned.length(), URLSpan.class)[0]; 
					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Config.BASE_URL+"/proyectos/"+url.getURL()));
					context.startActivity(browserIntent);
					
				}
			});
             
             holder.image = (ImageView) convertView.findViewById(R.id.imageView);
             convertView.setTag(holder);
         } else {
             holder = (ViewHolder) convertView.getTag();
         }
         String[] sValues = spanned.toString().split("\n");
         holder.title.setText(sValues[0]);
         holder.fundation.setText(sValues[10]);
         holder.description.setText(sValues[11]);
         
         LinearLayout l = (LinearLayout)convertView.findViewById(R.id.linear);
         if(l.getChildCount()==0 && bPaypal){
        	 holder.donateButton = PayPal.getInstance().getCheckoutButton(context, PayPal.BUTTON_294x45, 1);
        	 LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        	 params.weight = 1;
        	 holder.donateButton.setLayoutParams(params);
        	 l.addView(holder.donateButton);
        	 holder.donateButton.setOnClickListener(new OnClickListener() {
     			@Override
     			public void onClick(View arg0) {
     				// TODO Auto-generated method stub
     				((MainActivity) context).callServer();
     			}
     		});
         }
 		 
         //Parseando a int la donación acumulada
         String recaped = sValues[6];
         String total = sValues[8];
         int r = Integer.parseInt(recaped.replace(".","").split(" ")[0]);
         int t = Integer.parseInt(total.replace(".","").split(" ")[1]);
         holder.pBar.setMax(t);
         holder.pBar.setProgress(r);
         holder.pBarStart.setText(recaped);
         holder.pBarEnd.setText(total);
         ImageSpan[] is =spanned.getSpans(0,
        		 spanned.length(), ImageSpan.class);
         Drawable d = is[0].getDrawable();
         //d.setBounds(90, 90, 90, 90);
         holder.image.setImageDrawable(d);
         return convertView;
     }

     static class ViewHolder {
		public TextView pBarEnd;
		public TextView pBarStart;
		TextView description;
		TextView fundation;
		ProgressBar pBar;
		TextView title;
		ImageView image;
		Button moreInfoButton;
		CheckoutButton donateButton;
     }
}