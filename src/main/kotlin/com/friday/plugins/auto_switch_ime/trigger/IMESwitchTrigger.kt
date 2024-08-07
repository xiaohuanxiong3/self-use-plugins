package com.friday.plugins.auto_switch_ime.trigger

enum class IMESwitchTrigger(val description: String) {

//    CHAR_TYPED("字符输入"),
    MOUSE_CLICKED("鼠标点击"),
    ARROW_KEYS_PRESSED("按下方向键"),
//    ENTER("按下回车键"),
    PSI_FILE_CHANGED("PsiFile发生改变");

}