package org.vivecraft.provider;

public class VLoader {
    static {
        System.loadLibrary("openvr_api");
    }

    public static native long convertImgToEGLBuffer(int image);
    public static native int getNativeImage(long eglImage, int lwidth, int lheight);
}
