package com.example.attendease.color

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

object Data {
    val colors = listOf(
        ColorModel(0, Color(0xFF00E0D3), Color(0xFF00B4D4)),
        ColorModel(1, Color(0xFF00B4D4), Color(0xFF409CAE)),
        ColorModel(2, Color(0xFF66D8A4), Color(0xFF409CAE)),
        ColorModel(3, Color(0xFFFC727B), Color(0xFFF468A0)),
        ColorModel(4, Color(0xFF8289EA), Color(0xFF7A6FC1)),
        ColorModel(5, Color(0xFFFEC7A3), Color(0xFFFD9F9C)),
        ColorModel(6, Color(0xFF00E0D3), Color(0xFF00B4D4)),
        ColorModel(7, Color(0xFF00B4D4), Color(0xFF409CAE)),
        ColorModel(8, Color(0xFF66D8A4), Color(0xFF409CAE)),
        ColorModel(9, Color(0xFFFC727B), Color(0xFFF468A0)),
        ColorModel(10, Color(0xFF8289EA), Color(0xFF7A6FC1)),
        ColorModel(11, Color(0xFFFEC7A3), Color(0xFFFD9F9C)),
    )
}

@Stable
data class ColorModel(val id: Int, val startColor: Color, val endColor: Color)