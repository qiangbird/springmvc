package com.augmentum.upload.tool;

/**
 * restore the coodinate of the tailor area.
 * @author John.Li
 *
 */
public class TailorCoodinate {
    
    // initiate the coodinate of the tailor area in the changed
    // uploaded picture.
    double x;
    double y;
    double width;
    double height;
    
    // restore the value of width after it has been changed
    // of the original picture. Use this value to calculate
    // the aspect ratio of tailor area in the original upload
    // picture.
    double srcChangedWidth;
    
    public TailorCoodinate(double x, double y, double width, double height, double srcChangedWidth){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.srcChangedWidth = srcChangedWidth;
    }
}
