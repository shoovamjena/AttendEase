package com.example.attendease.uicomponent

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.attendease.attendancedata.Attendance
import com.example.attendease.ui.theme.nothingFontFamily
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AttendanceItemDetailed(
    detail: Attendance,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    val isLava = Build.BRAND.equals("lava", ignoreCase = true)
    val formattedDate = remember(detail.dateTime) {
        val sdf = SimpleDateFormat("dd-MM-yy | hh.mm a", Locale.getDefault())
        sdf.format(Date(detail.dateTime))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(5.dp)),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Row (
                modifier = Modifier.fillMaxWidth()
                    .heightIn(min = 65.dp)
                    .clip(RoundedCornerShape(50))
                    .background(if(isLava){MaterialTheme.colorScheme.onSecondary}else{MaterialTheme.colorScheme.primaryContainer}),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(text = formattedDate, fontSize = 16.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(horizontal = 18.dp))
                Text(text = detail.status, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 18.dp),
                    color = if (detail.status == "Present") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    fontFamily = nothingFontFamily
                )
            }
            Spacer(modifier = Modifier.height(5.dp))
            Box(
                modifier = Modifier
                    .heightIn(min = 50.dp, max = 60.dp)
                    .widthIn(min = 100.dp , max = 130.dp)
                    .align(Alignment.End)
                    .clip(RoundedCornerShape(50))
                    .background(if(isLava){MaterialTheme.colorScheme.onSecondary.copy(0.7f)}else{MaterialTheme.colorScheme.primaryContainer.copy(0.7f)}),
            ){
                Row (
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(42.dp)
                            .clip(RoundedCornerShape(50))
                            .background(if(isLava){MaterialTheme.colorScheme.onTertiary}else{MaterialTheme.colorScheme.tertiaryContainer}),
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit Attendance Details",
                            modifier = Modifier.size(26.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(42.dp)
                            .clip(RoundedCornerShape(50))
                            .background(if(isLava){MaterialTheme.colorScheme.onTertiary}else{MaterialTheme.colorScheme.tertiaryContainer}),
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete Attendance Details",
                            modifier = Modifier.size(26.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}
