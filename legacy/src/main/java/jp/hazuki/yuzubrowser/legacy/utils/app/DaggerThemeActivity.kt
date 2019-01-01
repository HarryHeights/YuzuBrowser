/*
 * Copyright (C) 2017-2019 Hazuki
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.hazuki.yuzubrowser.legacy.utils.app

import android.content.Context
import android.os.Bundle
import androidx.annotation.StyleRes
import dagger.android.support.DaggerAppCompatActivity
import jp.hazuki.yuzubrowser.legacy.R
import jp.hazuki.yuzubrowser.legacy.settings.data.AppData
import jp.hazuki.yuzubrowser.legacy.theme.ThemeData
import jp.hazuki.yuzubrowser.legacy.utils.createLanguageContext

open class DaggerThemeActivity : DaggerAppCompatActivity() {

    protected open val isLoadThemeData: Boolean
        get() = false

    override fun onCreate(savedInstanceState: Bundle?) {
        if (isLoadThemeData) {
            ThemeData.createInstance(applicationContext, AppData.theme_setting.get())
        }

        if (!useDarkTheme() && useLightTheme() || ThemeData.isEnabled() && ThemeData.getInstance().lightTheme) {
            setTheme(lightThemeResource())
        }

        super.onCreate(savedInstanceState)
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(newBase.createLanguageContext(AppData.language.get()))
    }

    @StyleRes
    protected open fun lightThemeResource(): Int = R.style.CustomThemeLight

    protected open fun useLightTheme(): Boolean = false

    protected open fun useDarkTheme(): Boolean = false
}