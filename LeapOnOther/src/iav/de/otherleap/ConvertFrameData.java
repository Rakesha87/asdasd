package iav.de.otherleap;

import iav.de.datavalue.DataFrame;
import iav.de.otherleap.ConvertData;

import com.leapmotion.leap.Frame;

public class ConvertFrameData {
	public DataFrame dataFrame;
	public Frame frame;
	
	public ConvertFrameData(Frame frame) {
		DataFrame dataFrame = new DataFrame();
		this.frame = frame;
		this.dataFrame = dataFrame;
	}
	
	public DataFrame convert() {
		ConvertData convertData =  new ConvertData(frame, dataFrame);
		return convertData.getDataFrame();
	}	
}