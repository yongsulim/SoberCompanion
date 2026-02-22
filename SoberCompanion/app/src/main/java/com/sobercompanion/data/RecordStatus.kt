package com.sobercompanion.data

/**
 * 하루의 금주 상태를 나타내는 열거형.
 *
 * 홈 화면에서 사용자가 선택하는 세 가지 상태이며,
 * DataStore에 문자열로 직렬화되어 저장됩니다.
 */
enum class RecordStatus {
    /** 오늘 하루 음주 충동 없이 금주에 성공한 상태 */
    SUCCESS,

    /** 음주 충동이 있었지만 참아낸 상태. 흔들림 타이머와 위로 메시지가 활성화됨 */
    SHAKY,

    /** 음주한 상태. 스트릭이 오늘 날짜로 초기화됨 */
    FAIL;

    companion object {
        /**
         * DataStore에서 읽어온 문자열을 RecordStatus로 변환합니다.
         * 알 수 없는 값이 들어오면 기본값 SUCCESS를 반환합니다.
         *
         * @param value DataStore에 저장된 상태 문자열 (null 허용)
         */
        fun fromString(value: String?): RecordStatus {
            return when (value?.uppercase()) {
                "SUCCESS" -> SUCCESS
                "SHAKY" -> SHAKY
                "FAIL" -> FAIL
                else -> SUCCESS // null이거나 알 수 없는 값이면 성공 상태로 취급
            }
        }
    }
}
