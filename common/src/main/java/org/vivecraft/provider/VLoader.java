package org.vivecraft.provider;

public class VLoader {
    static {
        System.loadLibrary("openvr_api");
    }

    public static native void setEGLGlobal(long ctx, long display, long cfg);
}
