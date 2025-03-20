package com.example.attendease.uicomponent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.attendease.attendancedata.Attendance
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AttendanceItem(detail: Attendance) {
    val formattedDate = remember(detail.dateTime) {
        val sdf = SimpleDateFormat("dd-MM-yy | hh.mm a", Locale.getDefault())
        sdf.format(Date(detail.dateTime))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(MaterialTheme.colorScheme.onPrimary),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = formattedDate, fontSize = 14.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(horizontal = 8.dp))
        Text(text = detail.status, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp),
            color = if (detail.status == "Present") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )
    }
}
