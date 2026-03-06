package com.sobercompanion.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.sobercompanion.R

val fontProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage   = "com.google.android.gms",
    certificates      = R.array.com_google_android_gms_fonts_certs
)

/** 숫자/연속일수/위로 메시지/오늘 기록했어 에 사용 */
val NotoSerifKr = FontFamily(
    Font(GoogleFont("Noto Serif KR"), fontProvider, FontWeight.Light),
    Font(GoogleFont("Noto Serif KR"), fontProvider, FontWeight.Normal),
    Font(GoogleFont("Noto Serif KR"), fontProvider, FontWeight.Medium),
)

/** 버튼/본문/일반 텍스트에 사용 */
val NotoSansKr = FontFamily(
    Font(GoogleFont("Noto Sans KR"), fontProvider, FontWeight.Light),
    Font(GoogleFont("Noto Sans KR"), fontProvider, FontWeight.Normal),
    Font(GoogleFont("Noto Sans KR"), fontProvider, FontWeight.Medium),
)

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily   = NotoSansKr,
        fontWeight   = FontWeight.Normal,
        fontSize     = 16.sp,
        lineHeight   = 24.sp,
        letterSpacing = (-0.2).sp,
    ),
    bodyMedium = TextStyle(
        fontFamily   = NotoSansKr,
        fontWeight   = FontWeight.Normal,
        fontSize     = 14.sp,
        lineHeight   = 22.sp,
        letterSpacing = (-0.1).sp,
    ),
    bodySmall = TextStyle(
        fontFamily   = NotoSansKr,
        fontWeight   = FontWeight.Light,
        fontSize     = 12.sp,
        lineHeight   = 18.sp,
    ),
    titleLarge = TextStyle(
        fontFamily   = NotoSansKr,
        fontWeight   = FontWeight.Medium,
        fontSize     = 20.sp,
        lineHeight   = 28.sp,
        letterSpacing = (-0.3).sp,
    ),
    titleMedium = TextStyle(
        fontFamily   = NotoSansKr,
        fontWeight   = FontWeight.Normal,
        fontSize     = 15.5.sp,
        lineHeight   = 22.sp,
        letterSpacing = (-0.4).sp,
    ),
    titleSmall = TextStyle(
        fontFamily   = NotoSansKr,
        fontWeight   = FontWeight.Normal,
        fontSize     = 14.sp,
        lineHeight   = 20.sp,
        letterSpacing = (-0.2).sp,
    ),
    labelLarge = TextStyle(
        fontFamily   = NotoSansKr,
        fontWeight   = FontWeight.Medium,
        fontSize     = 14.sp,
        lineHeight   = 20.sp,
    ),
    labelMedium = TextStyle(
        fontFamily   = NotoSansKr,
        fontWeight   = FontWeight.Normal,
        fontSize     = 12.sp,
        lineHeight   = 16.sp,
    ),
    labelSmall = TextStyle(
        fontFamily   = NotoSansKr,
        fontWeight   = FontWeight.Normal,
        fontSize     = 11.sp,
        lineHeight   = 16.sp,
        letterSpacing = 1.2.sp,
    ),
)
