package com.roamprocess1.roaming4world.roaming4world;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.widget.Toast;

public class UploadProfileImage extends Activity{
	
	
	int serverResponseCode = 0;
	ProgressDialog dialog = null;
	
	public int uploadFile(String sourceFileUri) {

	  dialog = ProgressDialog.show(getApplicationContext(), "", "Uploading file...", true);

  	  String upLoadServerUri = "http://10.0.2.2/upload_test/upload_media_test.php";
  	  upLoadServerUri = "http://ip.roaming4world.com/esstel/profile_pic_upload.php";
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
	    	   dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""+ fileName + "-918860357393" + "\"" + lineEnd);
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
	    	   if(serverResponseCode == 200){
	    		   runOnUiThread(new Runnable() {
	    			    public void run() {
	    			    	Toast.makeText(getApplicationContext(), "File Upload Complete.", Toast.LENGTH_SHORT).show();
	    			    }
	    			});	    		   
	    	   }	
	    	  
	    	   //close the streams //
	    	   fileInputStream.close();
	    	   dos.flush();
	    	   dos.close();
	    	   
	      } catch (MalformedURLException ex) {  
	    	  dialog.dismiss();  
	    	  ex.printStackTrace();
	    	  Toast.makeText(getApplicationContext(), "MalformedURLException", Toast.LENGTH_SHORT).show();
	    	  Log.e("Upload file to server", "error: " + ex.getMessage(), ex);  
  	  } catch (Exception e) {
  		  dialog.dismiss();  
  		  e.printStackTrace();
  		  Toast.makeText(getApplicationContext(), "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();
  		  Log.e("Upload file to server Exception", "Exception : " + e.getMessage(), e);  
  	  }
  	  dialog.dismiss();    	  
  	  return serverResponseCode;  
  	 } 

}
