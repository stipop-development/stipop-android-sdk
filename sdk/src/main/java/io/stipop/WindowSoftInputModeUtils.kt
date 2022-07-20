package io.stipop

/*
class WindowSoftInputModeStateEnum {
    var STATE_UNSPECIFIED = 0
    var STATE_UNCHANGED = 1
    var STATE_HIDDEN = 2
    var STATE_ALWAYS_HIDDEN = 3
    var STATE_VISIBLE = 4
    var STATE_ALWAYS_VISIBLE = 5
}

class WindowSoftInputModeAdjustEnum {
    // API <= 31
    var ADJUST_UNSPECIFIED = 0
    var ADJUST_RESIZE = 16
    var ADJUST_PAN = 32
    var ADJUST_NOTHING = 48

    // API > 31
    var ADJUST_UNSPECIFIED = 0
    var ADJUST_RESIZE = 16
    var ADJUST_PAN = 32
    var ADJUST_NOTHING = 48
}

class WindowSoftInputModeNavigationEnum {
    var IS_FORWARD_NAVIGATION = 256
}
*/

internal enum class WindowSoftInputModeAdjustEnum {
    ADJUST_UNSPECIFIED, ADJUST_RESIZE, ADJUST_PAN, ADJUST_NOTHING
}

internal class WindowSoftInputModeUtils {

    fun isInputSoftModeNothing(inputModeValue: Int): WindowSoftInputModeAdjustEnum?{
        return when (inputModeValue) {
            in 0..15, in (0 + 256)..(15 + 256) -> {
                WindowSoftInputModeAdjustEnum.ADJUST_UNSPECIFIED
            }
            in 16..31, in (16 + 256)..(31 + 256) -> {
                WindowSoftInputModeAdjustEnum.ADJUST_RESIZE
            }
            in 32..47, in (32 + 256)..(47 + 256) -> {
                WindowSoftInputModeAdjustEnum.ADJUST_PAN
            }
            in 48..63, in (48 + 256)..(63 + 256) -> {
                WindowSoftInputModeAdjustEnum.ADJUST_NOTHING
            }
            else -> WindowSoftInputModeAdjustEnum.ADJUST_NOTHING
        }
    }
}

