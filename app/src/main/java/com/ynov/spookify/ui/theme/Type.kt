package com.ynov.spookify.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// La maquette utilise Poppins ; FontFamily.Default sert de repli tant que la police n'est pas embarquée.
private val AppFont = FontFamily.Default

val Typography = Typography(
    headlineSmall = TextStyle(fontFamily = AppFont, fontWeight = FontWeight.SemiBold, fontSize = 24.sp, lineHeight = 32.sp),
    titleLarge = TextStyle(fontFamily = AppFont, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, lineHeight = 26.sp),
    titleMedium = TextStyle(fontFamily = AppFont, fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 24.sp),
    titleSmall = TextStyle(fontFamily = AppFont, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp),
    bodyLarge = TextStyle(fontFamily = AppFont, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontFamily = AppFont, fontWeight = FontWeight.Normal, fontSize = 13.sp, lineHeight = 18.sp),
    bodySmall = TextStyle(fontFamily = AppFont, fontWeight = FontWeight.Normal, fontSize = 11.sp, lineHeight = 15.sp),
    labelLarge = TextStyle(fontFamily = AppFont, fontWeight = FontWeight.Medium, fontSize = 13.sp, lineHeight = 18.sp),
    labelMedium = TextStyle(fontFamily = AppFont, fontWeight = FontWeight.Medium, fontSize = 11.sp, lineHeight = 16.sp),
    labelSmall = TextStyle(fontFamily = AppFont, fontWeight = FontWeight.Medium, fontSize = 10.sp, lineHeight = 14.sp),
)
