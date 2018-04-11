package com.ruyiruyi.rylibrary.ui;


import com.ruyiruyi.rylibrary.handler.MessageHandler;
import com.ruyiruyi.rylibrary.ui.cell.WheelView;

import java.util.TimerTask;

public final class SmoothScrollTimerTask extends TimerTask {

    int realTotalOffset;
    int realOffset;
    int offset;
    final WheelView wheelView;

    public SmoothScrollTimerTask(WheelView wheelview, int offset) {
        this.wheelView = wheelview;
        this.offset = offset;
        realTotalOffset = Integer.MAX_VALUE;
        realOffset = 0;
    }

    @Override
    public final void run() {
        if (realTotalOffset == Integer.MAX_VALUE) {
            realTotalOffset = offset;
        }
        realOffset = (int) ((float) realTotalOffset * 0.1F);

        if (realOffset == 0) {
            if (realTotalOffset < 0) {
                realOffset = -1;
            } else {
                realOffset = 1;
            }
        }
        if (Math.abs(realTotalOffset) <= 0) {
            wheelView.cancelFuture();
            wheelView.handler.sendEmptyMessage(MessageHandler.WHAT_ITEM_SELECTED);
        } else {
            wheelView.totalScrollY = wheelView.totalScrollY + realOffset;
            wheelView.handler.sendEmptyMessage(MessageHandler.WHAT_INVALIDATE_LOOP_VIEW);
            realTotalOffset = realTotalOffset - realOffset;
        }
    }
}
