package com.shwy.bestjoy.view;

public interface View_HasStateListenerSupport {
    void addOnAttachStateChangeListener(View_OnAttachStateChangeListener listener);
    void removeOnAttachStateChangeListener(View_OnAttachStateChangeListener listener);
}
