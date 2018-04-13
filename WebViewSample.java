import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import java.io.File;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.scene.effect.Reflection;
import javafx.scene.layout.StackPane;
import javafx.scene.image.Image;
 
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javax.swing.*;
import java.io.*;
import java.awt.*;

import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;

import org.apache.http.entity.FileEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.Header;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.*;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.prefs.Preferences;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;

import java.lang.reflect.InvocationTargetException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import javafx.embed.swing.SwingFXUtils;
import java.util.prefs.Preferences;

@SuppressWarnings("deprecation")
public class WebViewSample extends Application {
    private Scene scene;
	private static Preferences prefs = Preferences.userNodeForPackage(WebViewSample.class);
	private static String uploadName;
	private static boolean running = false;
	private static Label statuss;
	
    @Override public void start(Stage stage) {
		int offset = 45;
		ClassLoader classLoader = getClass().getClassLoader();
		
        // create the scene
        stage.setTitle("LP Launcher");
		
		BufferedImage img = null;
        try{
            img = ImageIO.read(WebViewSample.class.getResourceAsStream("ico.png"));
        }catch (IOException e){}
		Image card = SwingFXUtils.toFXImage(img, null );
		stage.getIcons().add(card);
		
		StackPane pane = new StackPane();
		pane.getChildren().add(new Browser());
		
		TextField uidField = new TextField();
		uidField.setText(prefs.get("UID", "0"));
		uidField.textProperty().addListener((observable, oldValue, newValue) -> {
			prefs.put("UID", newValue);
		});
		uidField.setMaxWidth(150);
		uidField.setTranslateY(-125+offset);
		uidField.setTranslateX(15);
		pane.getChildren().add(uidField);
		Button b1 = new Button("Launch");
		b1.setTranslateY(-80+offset);
		b1.setPrefWidth(185);
		Button b2 = new Button("Reupload");
		b2.setTranslateY(-30+offset);
		b2.setPrefWidth(185);
		Button b3 = new Button("Clear WDB");
		b3.setTranslateY(20+offset);
		b3.setPrefWidth(185);
		Button b4 = new Button("Upload saved");
		b4.setTranslateY(70+offset);
		b4.setPrefWidth(185);
		
		b1.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				Launch(false);
			}
		});
		
		b2.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				FileDialog dialog = new FileDialog((Frame)null, "Select File to Open");
				dialog.setMode(FileDialog.LOAD);
				dialog.setVisible(true);
				
				String path = dialog.getFile();
				File file = new File("RPLLLogs/"+path);
				if (file.exists())
				{
					String extension = getFileExtension(file);
					if (extension.equals("zip"))
					{
						if (1000000 * 20 >= file.length())
						{
							uploadName = "RPLLLogs/"+path;
							UploadLog("RPLLLogs/"+path);
						}
					}
				}
			}
		});
		
		b3.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				File index = new File("Cache/WDB");
				if (index.exists()){
					String[] entries = index.list();
					for(String s: entries){
						File dir = new File(index.getPath(),s);
						if (dir.isDirectory())
						{
							String[] entries2 = dir.list();
							for(String s2: entries2){
								File currentFile = new File(dir.getPath(),s2);
								currentFile.delete();
							}
						}
						else
						{									
							dir.delete();
						}
					}
					JOptionPane.showMessageDialog(null, "WDB cleared");
				}
				else
				{
					JOptionPane.showMessageDialog(null, "Could not find WDB");
				}
			}
		});
		
		b4.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				//Launch(true);
				UploadLog("");
				ClearCache();
			}
		});
		
		pane.getChildren().add(b1);
		pane.getChildren().add(b2);
		pane.getChildren().add(b3);
		pane.getChildren().add(b4);
		
		
		statuss = new Label();
		statuss.setTranslateY(100 + offset);
		statuss.setText("Ready");
		statuss.setFont(new Font("Arial", 12));
		statuss.setTextFill(Color.web("#ffffff"));
		pane.getChildren().add(statuss);
        scene = new Scene(pane,250,310, Color.web("#666970"));
		
		scene.getStylesheets().add(getClass().getResource("main.css").toExternalForm());
        stage.setScene(scene);   
		stage.setResizable(false);
        stage.show();
		
		
		
		
		// Hack code, please ignore
		TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
		}
		
    }
	
	private static boolean compressFile(String path) {
		String subName = prefs.get("UID", "0")+"-"+System.currentTimeMillis();
		uploadName = "RPLLLogs/"+subName+".zip";
        String zipFile = uploadName;
		
		File logs = new File("RPLLLogs");
		if (!logs.exists()) logs.mkdir();
		
		// Renaming files
		File f1 = new File("Logs/WoWCombatLog.txt");
		File f2 = new File(subName+".txt");
		f1.renameTo(f2);
		File f3 = new File(path);
		File f4 = new File(subName+".lua");
		f3.renameTo(f4);
		
		String[] srcFiles = { subName+".txt", subName+".lua"};
		
		try {
			
			// create byte buffer
			byte[] buffer = new byte[1024];

			FileOutputStream fos = new FileOutputStream(zipFile);

			ZipOutputStream zos = new ZipOutputStream(fos);
			
			for (int i=0; i < srcFiles.length; i++) {
				
				File srcFile = new File(srcFiles[i]);

				FileInputStream fis = new FileInputStream(srcFile);

				// begin writing a new ZIP entry, positions the stream to the start of the entry data
				zos.putNextEntry(new ZipEntry(srcFile.getName()));
				
				int length;

				while ((length = fis.read(buffer)) > 0) {
					zos.write(buffer, 0, length);
				}

				zos.closeEntry();

				// close the InputStream
				fis.close();
				
			}

			// close the ZipOutputStream
			zos.close();
			
		}
		catch (IOException ioe) {
			JOptionPane.showMessageDialog(null, "Could not WoWCombatLog.txt or the addon file!");
			return false;
		}
		
		if (f2.exists()) f2.delete();
		if (f4.exists()) f4.delete();
		
		return true;
	}
	
	private static String getFileExtension(File file) {
		String name = file.getName();
		try {
			return name.substring(name.lastIndexOf(".") + 1);
		} catch (Exception e) {
			return "";
		}
	}
	
	public static void Launch(boolean nampower)
	{
		if (running)
		{
			JOptionPane.showMessageDialog(null, "You can only use one instance at the same time!");
			return;
		}
		
		ClearCache();
		try{
			Process p;
			if (nampower)
			{
				File np = new File("nampower.exe");
				if (!np.exists())
				{
					JOptionPane.showMessageDialog(null, "Could not find nampower.exe");
					return;
				}
				p = Runtime.getRuntime().exec("nampower.exe");
			}
			else
			{
				p = Runtime.getRuntime().exec("WoW.exe");
			}
			running = true;
			try {
				p.waitFor();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			UploadLog("");
			running = false;
		}
		catch (IOException e2)
		{
			JOptionPane.showMessageDialog(null, "Could not find WoW.exe");
		}
	}
	
	public static void ClearCache(){
		File dir = new File("WTF/Account");
		if (dir.exists() && dir.isDirectory())
		{
			for (File g : dir.listFiles()){
				if (g.exists())
				{
					File svd1 = new File("WTF/Account/"+g.getName()+"/SavedVariables/RPLLVanilla.lua");
					if (svd1.exists()) svd1.delete();
					
					File svd2 = new File("WTF/Account/"+g.getName()+"/SavedVariables/RPLLTBC.lua");
					if (svd2.exists()) svd2.delete();
					
					File svd3 = new File("WTF/Account/"+g.getName()+"/SavedVariables/RPLLWOTLK.lua");
					if (svd3.exists()) svd3.delete();
					
					File svd4 = new File("WTF/Account/"+g.getName()+"/SavedVariables/RPLLCATA.lua");
					if (svd4.exists()) svd4.delete();
					
					File svd5 = new File("WTF/Account/"+g.getName()+"/SavedVariables/RPLLMOP.lua");
					if (svd5.exists()) svd5.delete();
					
					File svd6 = new File("WTF/Account/"+g.getName()+"/SavedVariables/RPLLWOD.lua");
					if (svd6.exists()) svd6.delete();
				}
			}
		}
		
		File cbtLog = new File("Logs/WoWCombatLog.txt");
		if (cbtLog.exists()) cbtLog.delete();
	}
	
	// Empty argument means to find it itself
	public static void UploadLog(String path){
		// Finding path
		if (path.isEmpty())
		{
			File dir = new File("WTF/Account");
			if (dir.exists() && dir.isDirectory())
			{
				for (File g : dir.listFiles()){
					if (g.exists())
					{
						File svd1 = new File("WTF/Account/"+g.getName()+"/SavedVariables/RPLLVanilla.lua");
						if (svd1.exists()){
							path = svd1.getAbsolutePath();
							break;
						}
						
						File svd2 = new File("WTF/Account/"+g.getName()+"/SavedVariables/RPLLTBC.lua");
						if (svd2.exists()){
							path = svd2.getAbsolutePath();
							break;
						}
						
						File svd3 = new File("WTF/Account/"+g.getName()+"/SavedVariables/RPLLWOTLK.lua");
						if (svd3.exists()){
							path = svd3.getAbsolutePath();
							break;
						}
						
						File svd4 = new File("WTF/Account/"+g.getName()+"/SavedVariables/RPLLCATA.lua");
						if (svd4.exists()){
							path = svd4.getAbsolutePath();
							break;
						}
						
						File svd5 = new File("WTF/Account/"+g.getName()+"/SavedVariables/RPLLMOP.lua");
						if (svd5.exists()){
							path = svd5.getAbsolutePath();
							break;
						}
						
						File svd6 = new File("WTF/Account/"+g.getName()+"/SavedVariables/RPLLWOD.lua");
						if (svd6.exists()){
							path = svd6.getAbsolutePath();
							break;
						}
					}
				}
			}
			else
			{
				JOptionPane.showMessageDialog(null, "Could not find WTF/Account");
				return;
			}
		}
		
		if (path.isEmpty())
		{
			JOptionPane.showMessageDialog(null, "Could not find the addon data.");
			return;
		}
		
		if (!getFileExtension(new File(path)).equals("zip"))
		{
			if (!compressFile(path)) return;
		}
		
		File logs = new File("RPLLLogs");
		if (!logs.exists()) logs.mkdir();
		
		statuss.setText("Uploading...");
		Thread thread = new Thread() {
			public void run() {
				File file2 = new File(uploadName);
				String url = "https://legacyplayers.com/Upload.aspx?exp="+getExpansion();
				
				CloseableHttpClient httpClient = HttpClientBuilder.create()
						.build();

				HttpEntity requestEntity = MultipartEntityBuilder.create()
						.addBinaryBody("file", file2)
						.build();
				HttpPost post = new HttpPost(url);
				post.setEntity(requestEntity);
				
				try (CloseableHttpResponse response = httpClient.execute(post)) {
					/*
					System.out.print(response.getStatusLine());
					
					Header[] headers = post.getAllHeaders();
					String content = EntityUtils.toString(requestEntity);

					System.out.println(post.toString());
					for (Header header : headers) {
						System.out.println(header.getName() + ": " + header.getValue());
					}
					System.out.println();
					System.out.println(content);
					System.out.println();
					System.out.println();
					
					HttpEntity resEntity = response.getEntity();
					if (resEntity != null) {
						System.out.println(EntityUtils.toString(resEntity));
					}
					*/
					EntityUtils.consume(response.getEntity());
				}
				catch(IOException e)
				{
					JOptionPane.showMessageDialog(null, "Some error occoured during the upload. Please try later again!");
					return;
				}
			}
		};
		thread.start();
		
		try
		{
			thread.join();
		}catch(InterruptedException e){}
		statuss.setText("Ready");
	}
	
	private static int getExpansion(){
		File wowexe = new File("WoW.exe");
		if (wowexe.exists()){
			long len = wowexe.length()/1024;
			if (len>7600){
				return 1; // TBC
			}
			if (len>5000){
				return 2; // WOTLK
			}
		}
        return 0; // Vanilla
	}
 
    public static void main(String[] args){
        launch(args);
    }
}
class Browser extends Region {
 
    final WebView browser = new WebView();
    final WebEngine webEngine = browser.getEngine();
     
    public Browser() {
        //apply the styles
		ClassLoader classLoader = getClass().getClassLoader();
        getStyleClass().add("browser");
        // load the web page
		webEngine.load(getClass().getResource("index.html").toExternalForm());
        //add the web view to the scene
        getChildren().add(browser);
    }
    private Node createSpacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }
 
    @Override protected void layoutChildren() {
        double w = getWidth();
        double h = getHeight();
        layoutInArea(browser,0,0,w,h,0, HPos.CENTER, VPos.CENTER);
    }
 
    @Override protected double computePrefWidth(double height) {
        return 250;
    }
 
    @Override protected double computePrefHeight(double width) {
        return 300;
    }
}