package com.example.backendtest.ui.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.backendtest.ui.theme.BackendTestTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun DateNavigationBar(
    selectedDate: LocalDate,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = onPreviousDay,
            content = {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Previous Day"
                )
            }
        )

        Text(
            text = selectedDate.format(dateFormatter),
            style = MaterialTheme.typography.titleMedium
        )

        IconButton(
            onClick = onNextDay,
            content = {
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = "Next Day"
                )
            }
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun DateNavigationBarPreview() {
    BackendTestTheme{
        DateNavigationBar(
            selectedDate = LocalDate.now(),
            onPreviousDay = {},
            onNextDay = {}
        )
    }
}
