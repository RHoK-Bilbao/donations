package org.rhok.microdonaciones.data.wrappers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.rhok.microdonaciones.config.Config;
import org.rhok.microdonaciones.data.models.MDProject;
import org.xml.sax.XMLReader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;

public class MDProjectHtmlWrapper {
	ArrayList<Spanned> wrappedProjects = new ArrayList<Spanned>();
	
	public MDProjectHtmlWrapper(String page) {
		
		String[] pageParts = page.split("<div class=\"ProjectBox\">");
		for (int i=0; i<pageParts.length; i++ ){
			if (i!= 0){
				Spanned spa = Html.fromHtml(pageParts[i], new MDImageGetter(), new MDTagHandler());

				if (spa !=null)
					wrappedProjects.add(spa);
			}
		}
		
	}

	public ArrayList<Spanned> getSpannableList(){
		return wrappedProjects;
	}

	public class MDTagHandler implements Html.TagHandler{

		@Override
		public void handleTag(boolean arg0, String arg1, Editable arg2,
				XMLReader arg3) {
			// TODO Auto-generated method stub
			
		}
		
	}

	
	public class MDImageGetter implements Html.ImageGetter{
		// NOTE: The directories must already exist
		@Override
		public Drawable getDrawable(String url) {

				// get name of image
				String name = url.substring(url.lastIndexOf("/")+1);
				// cache dir + name of image + the image save format
				File f = new File(Config.CACHE_DIR+name);
				try{
				if(!f.exists())// if it does not exist in the cache folder we need to download it
					downloadImage(Config.BASE_URL+url,f);
				}catch(Exception e)
				{
					e.printStackTrace();
					return null;
				}
				Drawable d = Drawable.createFromPath(f.getAbsolutePath());
				return d;

		}
		private void downloadImage(String url,File f) throws IOException
	    {
	    	URL myFileUrl =new URL(url);   
	    	HttpURLConnection conn= (HttpURLConnection)myFileUrl.openConnection();
	        conn.setDoInput(true);
	        conn.connect();
	        InputStream is = conn.getInputStream();
	        
	        Bitmap bm = BitmapFactory.decodeStream(is);
	        FileOutputStream out = new FileOutputStream(f);   
	        if (bm.compress(Bitmap.CompressFormat.JPEG, 90, out)){
	        	Log.d("Vamos","ya");
	        }
	    }
	}


	public ArrayList<MDProject> getMDProjectList() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
