package com.guest.cnbeta.util;

import android.app.Application;

public class BaseApplication extends Application {
	
	public static BaseApplication app;
	
	private int loadPage = 1;
	
    @Override 
    public void onCreate() { 
        super.onCreate(); 
        app = new BaseApplication();
    }  
	
	public int getLoadPage() {
		return this.loadPage;
	}

	public void setLoadPage(int loadPage) {
		this.loadPage = loadPage+1;
	}
	

//	public class BaseException implements UncaughtExceptionHandler {
//		
//		private Thread.UncaughtExceptionHandler defaultExceptionHandler;
//
//		private BaseException() {
//		}
//
//		public void uncaughtException(Thread thread, Throwable exception) {
//			// TODO Auto-generated method stub
//			if (defaultExceptionHandler != null) {
//				
//				exception.printStackTrace();
//				
//				// defaultExceptionHandler.uncaughtException(thread, exception);
//			}
//		}
//
//		public void init(Context context) {
//			defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
//			Thread.setDefaultUncaughtExceptionHandler(this);
//		}
//	}
	
}

