package iav.de.otherleap;

/******************************************************************************\
 * Copyright (C) 2012-2013 Leap Motion, Inc. All rights reserved.               *
 * Leap Motion proprietary and confidential. Not for distribution.              *
 * Use subject to the terms of the Leap Motion SDK Agreement available at       *
 * https://developer.leapmotion.com/sdk_agreement, or another agreement         *
 * between Leap Motion and you, your company or other organization.             *
 \******************************************************************************/

import iav.de.datavalue.DataFrame;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Gesture;
import com.leapmotion.leap.Listener;

import de.iav.communication.UDPCommunication;

public class OtherLeap extends Listener implements Serializable {

	private static final long serialVersionUID = 1L;
	private Frame frame;

	@Override
	public void onInit(Controller controller) {
		System.out.println("Initialized");
	}

	@Override
	public void onConnect(Controller controller) {
		System.out.println("Connected");
		controller.enableGesture(Gesture.Type.TYPE_SWIPE);
		controller.enableGesture(Gesture.Type.TYPE_CIRCLE);
		controller.enableGesture(Gesture.Type.TYPE_SCREEN_TAP);
		controller.enableGesture(Gesture.Type.TYPE_KEY_TAP);
	}

	@Override
	public void onDisconnect(Controller controller) {
		// Note: not dispatched when running in a debugger.
		System.out.println("Disconnected");
	}

	@Override
	public void onExit(Controller controller) {
		System.out.println("Exited");
	}

	@Override
	public void onFrame(Controller controller) {
		// Get the most recent frame and report some basic information
		frame = controller.frame();
		ConvertFrameData convertData = new ConvertFrameData(frame);
		DataFrame dataFrame = convertData.convert();
		// System.out.println(dataFrame);
		UDPCommunication udpCommuniction = new UDPCommunication(
				"192.168.10.253", 8888);
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		double pitchRad = dataFrame.getHandList().getHands().get(0)
				.getFingers().getFinger(1).getTipDirection()
				.getAngleProjection().getPitch();
		double rollRad = dataFrame.getHandList().getHands().get(0).getFingers()
				.getFinger(1).getTipDirection().getAngleProjection().getRoll();
		double yawRad = dataFrame.getHandList().getHands().get(0).getFingers()
				.getFinger(1).getTipDirection().getAngleProjection().getYaw();
		double pitchAngle = Math.toDegrees(pitchRad);
		double rollAngle = Math.toDegrees(rollRad);
		// double yawAngle = Math.toDegrees(yawRad);
		// System.out.println(pitchAngle + "\n");
		System.err.println(rollAngle + "\n");
		// System.out.println(yawAngle + "\n");
		byte[] udpPacketData = serialize(dataFrame);
		udpCommuniction.send(udpPacketData);
	}

	public byte[] serialize(DataFrame dataFrame) {
		byte[] seralizedData = null;
		ObjectOutputStream o = null;
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		try {
			o = new ObjectOutputStream(b);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			o.writeObject(dataFrame);
		} catch (IOException e) {
			e.printStackTrace();
		}
		seralizedData = b.toByteArray();
		dataFrame.getHandList().getLeftHand().getFingers().getFinger(0)
				.getTipDirection().getAngleProjection().getPitch();
		return seralizedData;
	}
}

class SampleLeap {
	public static void main(String[] args) {
		// Create a sample listener and controller
		OtherLeap listener = new OtherLeap();
		Controller controller = new Controller();
		// Have the sample listener receive events from the controller
		controller.addListener(listener);
		// Keep this process running until Enter is pressed
		System.out.println("Press Enter to quit...");
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Remove the sample listener when done
		controller.removeListener(listener);
	}
}