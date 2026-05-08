package ro.go.stecker.optigraph.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ro.go.stecker.optigraph.R
import ro.go.stecker.optigraph.ui.GraphTopAppBar
import ro.go.stecker.optigraph.ui.layout.CircularLayout
import ro.go.stecker.optigraph.ui.navigation.GraphScreens
import kotlin.math.atan
import kotlin.math.roundToInt

@Composable
fun MainScreen() {
    Scaffold(
        topBar = { GraphTopAppBar(stringResource(R.string.main_screen)) },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        val density = LocalDensity.current
        val nodeRadiusDp = 16.dp
        val nodeRadiusPx = with(density) { nodeRadiusDp.toPx() }

        val offsets = remember {
            List(20) { mutableStateOf(Offset(-nodeRadiusPx, -nodeRadiusPx)) }
        }

        val coords = remember {
            List(20) { mutableStateOf(Offset(0f, 0f)) }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box {
                Canvas(
                    modifier = Modifier
                        .padding(nodeRadiusDp)
                ) {
                    val rectWidth = with(density) { 32.dp.toPx() }
                    val rectHeight = with(density) { 16.dp.toPx() }

                    repeat(19) {
                        drawLine(
                            start = coords[it].value,
                            end = coords[it + 1].value,
                            color = Color.Magenta,
                            strokeWidth = 5F
                        )

                        val rectCenterX = (coords[it].value.x + coords[it+1].value.x)
                        val rectCenterY = (coords[it].value.y + coords[it+1].value.y)
                        val rectOffset = Offset(
                            rectCenterX / 2 - rectWidth/2,
                            rectCenterY / 2 - rectHeight/2
                        )

                        rotate(
                            degrees = atan((coords[it+1].value.y - coords[it].value.y) / (coords[it+1].value.x - coords[it].value.x)) * (180f / Math.PI.toFloat()),
                            pivot = Offset(rectOffset.x + rectWidth/2, rectOffset.y + rectHeight/2)
                        ) {
                            drawRoundRect(
                                color = Color.Red,
                                topLeft = rectOffset,
                                size = Size(rectWidth, rectHeight),
                                cornerRadius = CornerRadius(5f, 5f)
                            )
                        }
                    }
                }

                CircularLayout(
                    radius = 150.dp,
                    startAngle = 270f,
                    onCoordsChange = { index, x, y ->
                        coords[index].value = Offset(x, y)
                    },
                    modifier = Modifier
                        .padding(nodeRadiusDp)
//                        .fillMaxWidth()
                ) {
                    repeat(20) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .offset {
                                    IntOffset(
                                        offsets[it].value.x.roundToInt(),
                                        offsets[it].value.y.roundToInt()
                                    )
                                }
                                .background(Color.LightGray, CircleShape)
                                .border(width = 2.dp, color = Color.Black, shape = CircleShape)
                                .size((2 * nodeRadiusDp.value).dp)
                                .pointerInput(Unit) {
                                    detectDragGestures { change, dragAmount ->
                                        change.consume()
                                        offsets[it].value += dragAmount
                                        coords[it].value += dragAmount
                                    }
                                }
                        ) {
                            Text(text = (it + 1).toString(), color = Color.Black)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SideMenu() {
    Column(
        modifier = Modifier.background(Color.Red)
    ) {
        Text(
            text="yooo",
            color=Color.Magenta,
            fontSize=30.sp
        )
    }
}

@Composable
fun ButtonCast(navController: NavController){
    Button(onClick={
        navController.navigate(GraphScreens.SideMenu.name)
    }){
        Text("Menu")
    }
}