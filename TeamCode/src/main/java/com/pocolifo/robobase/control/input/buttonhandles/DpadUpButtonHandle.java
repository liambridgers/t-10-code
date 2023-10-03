package com.pocolifo.robobase.control.input.buttonhandles;

import com.qualcomm.robotcore.hardware.Gamepad;

public class DpadUpButtonHandle extends ButtonHandle {
    private Gamepad g;
    public DpadUpButtonHandle(Gamepad g) {
        this.g = g;
    }
    public float get() {
        return g.dpad_up?1f:0f;
    }
}