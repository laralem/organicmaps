package com.mapswithme.util;

import android.content.Context;
import android.util.Log;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mapswithme.maps.MwmApplication;
import com.mapswithme.maps.base.Initializable;
import com.mapswithme.util.log.Logger;
import com.mapswithme.util.log.LoggerFactory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public enum CrashlyticsUtils implements Initializable<Context>
{
  INSTANCE;

  @SuppressWarnings("NotNullFieldNotInitialized")
  @NonNull
  private Context mContext;
  private final static String TAG = CrashlyticsUtils.class.getSimpleName();
  private final static Logger mLogger = LoggerFactory.INSTANCE.getLogger(LoggerFactory.Type.THIRD_PARTY);

  public void logException(@NonNull Throwable exception)
  {
    if (!isEnabled())
      return;

    FirebaseCrashlytics.getInstance().recordException(exception);
  }

  public void log(int priority, @NonNull String tag, @NonNull String msg)
  {
    if (!isEnabled())
      return;

    FirebaseCrashlytics.getInstance().log(toLevel(priority) + "/" + tag + ": " + msg);
  }

  public boolean isAvailable()
  {
    return true;
  }

  public boolean isEnabled()
  {
    return SharedPropertiesUtils.isCrashlyticsEnabled(mContext);
  }

  public void setEnabled(boolean isEnabled)
  {
    SharedPropertiesUtils.setCrashlyticsEnabled(mContext, isEnabled);
    setCollectionEnabled(isEnabled);
  }

  private void setCollectionEnabled(boolean isEnabled)
  {
    if (isEnabled)
    {
      mLogger.d(TAG, "Crashlytics enabled");
    }
    else
    {
      mLogger.d(TAG, "Crashlytics disabled");
    }
    FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(isEnabled);
  }

  @NonNull
  private static String toLevel(int level)
  {
    switch (level)
    {
    case Log.VERBOSE:
      return "V";
    case Log.DEBUG:
      return "D";
    case Log.INFO:
      return "I";
    case Log.WARN:
      return "W";
    case Log.ERROR:
      return "E";
    default:
      throw new IllegalArgumentException("Undetermined log level: " + level);
    }
  }

  @Override
  public void initialize(@Nullable Context context)
  {
    mContext = MwmApplication.from(context);
    setCollectionEnabled(isEnabled());
  }

  @Override
  public void destroy()
  {
    // No op
  }
}
