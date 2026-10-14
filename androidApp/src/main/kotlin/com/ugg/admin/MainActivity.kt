package com.ugg.admin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.ugg.admin.infrastructure.api.core.TemplateCore
import com.ugg.admin.infrastructure.keystore.foundation.external.InMemoryKeystore
import com.ugg.admin.kit.core.domain.AppEnvironment
import com.ugg.admin.shared.ContentView
import com.ugg.admin.shared.TemplateApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val app = TemplateApp {
            environment = AppEnvironment.PRODUCTION
            keystore = InMemoryKeystore()
            core = TemplateCore {
                androidContext = this@MainActivity
            }
        }

        setContent {
            ContentView(app)
        }
    }
}

@Preview
@Composable
fun ContentViewAndroidPreview() {
    val app = TemplateApp()

    ContentView(app)
}
