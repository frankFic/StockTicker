package com.github.premnirmal.ticker

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.util.Base64
import com.github.premnirmal.tickerwidget.R
import com.jakewharton.threetenabp.AndroidThreeTen
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import java.security.MessageDigest

/**
 * Created by premnirmal on 2/26/16.
 */
open class StocksApp : Application() {

  companion object {

    var SIGNATURE: String? = null

    fun getAppSignature(context: Context): String? {
      try {
        val packageInfo = context.packageManager
            .getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES)
        for (signature in packageInfo.signatures) {
          val md = MessageDigest.getInstance("SHA")
          md.update(signature.toByteArray())
          val currentSignature = Base64.encodeToString(md.digest(), Base64.DEFAULT).trim()
          CrashLogger.log(currentSignature)
          return currentSignature
        }
      } catch (e: Exception) {
        CrashLogger.logException(e)
      }
      return null
    }
  }

  override fun onCreate() {
    super.onCreate()
    initCrashLogger()
    initThreeTen()
    CalligraphyConfig.initDefault(
        CalligraphyConfig.Builder()
            .setDefaultFontPath("fonts/Ubuntu-Regular.ttf")
            .setFontAttrId(R.attr.fontPath)
            .build())
    Injector.init(createAppComponent())
    initAnalytics()
    SIGNATURE = getAppSignature(this)
  }

  open fun initThreeTen() {
    AndroidThreeTen.init(this)
  }

  open fun createAppComponent(): AppComponent {
    val component: AppComponent = DaggerAppComponent.builder()
        .appModule(AppModule(this))
        .build()
    return component
  }

  protected open fun initAnalytics() {
    Analytics.init(this)
  }

  protected open fun initCrashLogger() {
    CrashLogger.init(this)
  }
}