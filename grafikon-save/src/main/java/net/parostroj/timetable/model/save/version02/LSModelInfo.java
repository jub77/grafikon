/*
 * LSModelInfo.java
 * 
 * Created on 4.9.2007, 12:22:50
 */
package net.parostroj.timetable.model.save.version02;

/**
 * Model info storage class.
 * 
 * @author jub
 */
public class LSModelInfo {
    
    private double timeScale;
    
    private String scale;

    public LSModelInfo() {
    }

    public String getScale() {
        return scale;
    }

    public void setScale(String scale) {
        this.scale = scale;
    }

    public double getTimeScale() {
        return timeScale;
    }

    public void setTimeScale(double timeScale) {
        this.timeScale = timeScale;
    }
    
    public void visit(LSVisitor visitor) {
        visitor.visit(this);
    }
}
