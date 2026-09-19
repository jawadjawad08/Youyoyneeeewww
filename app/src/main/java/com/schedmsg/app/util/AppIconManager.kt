package com.schedmsg.app.util

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager

/**
 * يدير تبديل أيقونة التطبيق الظاهرة في قائمة التطبيقات (Launcher)،
 * عبر تفعيل/تعطيل مكوّنات Activity-Alias المعرّفة في AndroidManifest.
 * هذه هي الطريقة الرسمية الوحيدة المتاحة في أندرويد لتغيير الأيقونة ديناميكيًا
 * دون الحاجة لإعادة تثبيت التطبيق.
 */
object AppIconManager {

    enum class IconOption(val aliasSuffix: String, val displayName: String) {
        JASMINE("IconAliasJasmine", "الياسمين"),
        MOON("IconAliasMoon", "الهلال والنجمة"),
        HEART("IconAliasHeart", "القلب")
    }

    private const val PREFS_NAME = "app_icon_prefs"
    private const val KEY_SELECTED_ICON = "selected_icon"

    fun getCurrentIcon(context: Context): IconOption {
        val saved = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_SELECTED_ICON, IconOption.JASMINE.name)
        return IconOption.entries.firstOrNull { it.name == saved } ?: IconOption.JASMINE
    }

    /**
     * يفعّل الـ alias المطلوب ويعطّل البقية. النظام يعيد تشغيل عملية الـ launcher
     * تلقائيًا لإظهار الأيقونة الجديدة؛ قد تحتاج الشاشة الرئيسية للهاتف بضع ثوانٍ لتحديث الأيقونة.
     */
    fun setIcon(context: Context, option: IconOption) {
        val pm = context.packageManager
        val packageName = context.packageName

        IconOption.entries.forEach { icon ->
            val componentName = ComponentName(packageName, "$packageName.${icon.aliasSuffix}")
            val state = if (icon == option) {
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            } else {
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            }
            pm.setComponentEnabledSetting(componentName, state, PackageManager.DONT_KILL_APP)
        }

        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_SELECTED_ICON, option.name)
            .apply()
    }
}
