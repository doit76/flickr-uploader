package com.rafali.flickruploader;

import static org.acra.ReportField.ANDROID_VERSION;
import static org.acra.ReportField.APP_VERSION_CODE;
import static org.acra.ReportField.APP_VERSION_NAME;
import static org.acra.ReportField.AVAILABLE_MEM_SIZE;
import static org.acra.ReportField.BRAND;
import static org.acra.ReportField.BUILD;
import static org.acra.ReportField.DEVICE_FEATURES;
import static org.acra.ReportField.ENVIRONMENT;
import static org.acra.ReportField.PHONE_MODEL;
import static org.acra.ReportField.PRODUCT;
import static org.acra.ReportField.REPORT_ID;
import static org.acra.ReportField.SETTINGS_SECURE;
import static org.acra.ReportField.SETTINGS_SYSTEM;
import static org.acra.ReportField.STACK_TRACE;
import static org.acra.ReportField.THREAD_DETAILS;
import static org.acra.ReportField.TOTAL_MEM_SIZE;
import static org.acra.ReportField.USER_APP_START_DATE;
import static org.acra.ReportField.USER_CRASH_DATE;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;

import com.googlecode.androidannotations.api.BackgroundExecutor;

@ReportsCrashes(formUri = "http://ra-fa-li.appspot.com/androidCrashReport", formKey = "", mode = ReportingInteractionMode.TOAST, forceCloseDialogAfterToast = false, // optional, default false
resToastText = R.string.crash_toast_text, customReportContent = { REPORT_ID, APP_VERSION_CODE, APP_VERSION_NAME, PHONE_MODEL, ANDROID_VERSION, BUILD, BRAND, PRODUCT, TOTAL_MEM_SIZE,
		AVAILABLE_MEM_SIZE, STACK_TRACE, USER_APP_START_DATE, USER_CRASH_DATE, DEVICE_FEATURES, ENVIRONMENT, SETTINGS_SYSTEM, SETTINGS_SECURE, THREAD_DETAILS })
public class FlickrUploader extends Application {

	private static Context context;

	@Override
	public void onCreate() {
		super.onCreate();
		FlickrUploader.context = getApplicationContext();
		if (!isDebug()) {
			ACRA.init(this);
		}
		BackgroundExecutor.execute(new Runnable() {
			@Override
			public void run() {
				long versionCode = Utils.getLongProperty(STR.versionCode);
				if (versionCode == 0) {
					Mixpanel.track("First install");
					Utils.setLongProperty(STR.versionCode, (long) getVersion());
				}
			}
		});
	}

	private static int getVersion() {
		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
		} catch (Exception e) {
			Log.e("FlickrUploader", e.getMessage(), e);
		}
		return 0;
	}

	public static Context getAppContext() {
		return context;
	}

	private static Boolean DEBUG = null;

	public static boolean isDebug() {
		if (DEBUG == null) {
			DEBUG = (getAppContext().getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
		}
		return DEBUG;
	}

}
