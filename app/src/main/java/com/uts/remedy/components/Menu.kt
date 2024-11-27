package com.uts.remedy.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uts.remedy.R

@Composable
fun MenuItem(name: String, id: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Column(
            modifier = Modifier.size(50.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                modifier = Modifier.size(32.dp),
                painter = painterResource(id),
                contentDescription = "Notifications",
                tint = Color(android.graphics.Color.parseColor("#199A8E"))
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = name,
            fontSize = 14.sp,
            color = Color(android.graphics.Color.parseColor("#A1A8B0"))
        )
    }
}

@Composable
fun Menu(modifier: Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        MenuItem(name = "Medicine", id = R.drawable.pharmacy)
        MenuItem(name = "Tracking", id = R.drawable.document)
        MenuItem(name = "History", id = R.drawable.history)
    }
}

@Preview(showBackground = true)
@Composable
fun MenuPreview() {
    Menu(modifier = Modifier)
}