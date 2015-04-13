package com.roamprocess1.roaming4world.ui;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class AsyncTaskProfilePicUpload extends AsyncTask<Void, Void, Boolean> {
	String imagePathUri, numb;
	private int serverResponseCode;
	Context context;

	public AsyncTaskProfilePicUpload(String uriImage , Context con , String num){
		imagePathUri = uriImage;
		numb = num;
		context = con;
	}
	
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		Log.d("onPreExecute", "onPreExecute -in");
		
		
	}
   



@SuppressLint("NewApi") 
@Override
protected void onPostExecute(Boolean result) {
	
	if(result){
		Toast.makeText(context, "Image uploaded", Toast.LENGTH_SHORT).show();
	}else{
		Toast.makeText(context, "Error uploading image", Toast.LENGTH_SHORT).show();
	}
	
	
}

@Override
protected Boolean doInBackground(Void... params) {
	// TODO Auto-generated method stub
	int response = 0;
	Log.d("doInBackgroud ", imagePathUri + " path ");
	if(imagePathUri.equals(""))
	{	return false;
	}else{
		response = uploadFile(imagePathUri, numb);
	}
	Log.d("response ", response + "  d");
	
	if (response == 200) {
		Log.d("doInBackgroud", "doInBackground");
		return true;
	} else {
		return false;
	}
	
}

private int uploadFile(String sourceFileUri, String number) {

	
	  String upLoadServerUri = "";
	  upLoadServerUri = "http://ip.roaming4world.com/esstel/profile-data/profile_pic_upload.php";
	  String fileName = sourceFileUri;

	  HttpURLConnection conn = null;
	  DataOutputStream dos = null;  
	  String lineEnd = "\r\n";
	  String twoHyphens = "--";
	  String boundary = "*****";
	  int bytesRead, bytesAvailable, bufferSize;
	  byte[] buffer;
	  int maxBufferSize = 1 * 1024 * 1024; 
	  File sourceFile = new File(sourceFileUri); 
	  if (!sourceFile.isFile()) {
	   Log.e("uploadFile", "Source File Does not exist");
	   return 0;
	  }
	    	  try { // open a URL connection to the Servlet
	    	   FileInputStream fileInputStream = new FileInputStream(sourceFile);
	    	   URL url = new URL(upLoadServerUri);
	    	   conn = (HttpURLConnection) url.openConnection(); // Open a HTTP  connection to  the URL
	    	   conn.setDoInput(true); // Allow Inputs
	    	   conn.setDoOutput(true); // Allow Outputs
	    	   conn.setUseCaches(false); // Don't use a Cached Copy
	    	   conn.setRequestMethod("POST");
	    	   conn.setRequestProperty("Connection", "Keep-Alive");
	    	   conn.setRequestProperty("ENCTYPE", "multipart/form-data");
	    	   conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
	    	   conn.setRequestProperty("uploaded_file", fileName);
	    	  
	    	   dos = new DataOutputStream(conn.getOutputStream());
	
	    	   dos.writeBytes(twoHyphens + boundary + lineEnd); 
	    	   dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""+ fileName + "-" + number + "\"" + lineEnd);
	    	   dos.writeBytes(lineEnd);
	
	    	   bytesAvailable = fileInputStream.available(); // create a buffer of  maximum size
	
	    	   bufferSize = Math.min(bytesAvailable, maxBufferSize);
	    	   buffer = new byte[bufferSize];
	
	    	   // read file and write it into form...
	    	   bytesRead = fileInputStream.read(buffer, 0, bufferSize);  
	    	    
	    	   while (bytesRead > 0) {
	    	     dos.write(buffer, 0, bufferSize);
	    	     bytesAvailable = fileInputStream.available();
	    	     bufferSize = Math.min(bytesAvailable, maxBufferSize);
	    	     bytesRead = fileInputStream.read(buffer, 0, bufferSize);	    	    
	    	    }
	
	    	   // send multipart form data necesssary after file data...
	    	   dos.writeBytes(lineEnd);
	    	   dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
	
	    	   // Responses from the server (code and message)
	    	   serverResponseCode = conn.getResponseCode();
	    	   String serverResponseMessage = conn.getResponseMessage();
	    	   
	    	   Log.i("uploadFile", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);
	    	   
	    	  
	    	   //close the streams //
	    	   fileInputStream.close();
	    	   dos.flush();
	    	   dos.close();
	    	   
	      } catch (MalformedURLException ex) {  
	    	  ex.printStackTrace();
	    	  Log.e("Upload file to server", "error: " + ex.getMessage(), ex);  
	  } catch (Exception e) {
		  e.printStackTrace();
		  Log.e("Upload file to server Exception", "Exception : " + e.getMessage(), e);  
	  }
	  return serverResponseCode;  
	 }



}
