package ro.go.stecker.optigraph.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import ro.go.stecker.optigraph.R
import ro.go.stecker.optigraph.ui.GraphTopAppBar

@Composable
fun MainScreen() {
    Scaffold(
        topBar = { GraphTopAppBar(stringResource(R.string.main_screen)) },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            Text("BA ESTI GRAS?")
            Text("NU, NU SUNT!!!")
            Image(painter = painterResource(R.drawable.ic_launcher_foreground), contentDescription = null)
        }
    }
}