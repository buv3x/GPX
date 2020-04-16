package org.bsu.gpx.calculator;

public class CalculationParams {

    private boolean xyUsed = false;

    private boolean zUsed = true;
    
    private int filter = 10;

    public boolean isXYUsed() {
        return xyUsed;
    }

    public void setXYUsed(boolean xyUsed) {
        this.xyUsed = xyUsed;
    }

    public boolean isZUsed() {
        return zUsed;
    }

    public void setZUsed(boolean zUsed) {
        this.zUsed = zUsed;
    }

	public int getFilter() {
		return filter;
	}

	public void setFilter(int filter) {
		this.filter = filter;
	}
    
    

}
