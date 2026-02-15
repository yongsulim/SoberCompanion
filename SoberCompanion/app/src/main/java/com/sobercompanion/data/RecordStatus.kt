package com.sobercompanion.data

enum class RecordStatus {
    SUCCESS,    // 성공적으로 금주 유지
    SHAKY,      // 흔들림 (욕구가 있었지만 참음)
    FAIL;       // 실패 (음주함)

    companion object {
        fun fromString(value: String?): RecordStatus {
            return when (value?.uppercase()) {
                "SUCCESS" -> SUCCESS
                "SHAKY" -> SHAKY
                "FAIL" -> FAIL
                else -> SUCCESS
            }
        }
    }
}
