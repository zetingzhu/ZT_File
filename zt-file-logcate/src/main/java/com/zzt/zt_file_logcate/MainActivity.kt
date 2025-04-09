package com.zzt.zt_file_logcate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.zzt.zt_file_logcate.ui.theme.ZT_FileTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ZT_FileTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Column {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )

        Button(onClick = {

        }) {
            Text(
                text = "存储日志 LogManagerV1",
            )
        }

        Button(onClick = {}) {
            Text(
                text = "存储日志 LogManagerV2",
            )
        }


        Button(onClick = {

            var logStr = "写入日志 " + System.currentTimeMillis()

            LogManagerV1.getInstance().logMessage(logStr)

            LogManagerV2.getInstance().writeTextToFile(logStr)

            LogManagerV3.getInstance().logMessage(logStr)
        }) {
            Text(
                text = "存储日志 同时存储",
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ZT_FileTheme {
        Greeting("Android")
    }
}