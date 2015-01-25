package iav.de.otherleap;

import iav.de.datavalue.AngleProjection;
import iav.de.datavalue.DataFrame;
import iav.de.datavalue.FingerType;

import java.util.ArrayList;
import java.util.List;

import com.leapmotion.leap.Finger;
import com.leapmotion.leap.FingerList;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.HandList;
import com.leapmotion.leap.Vector;

public class ConvertData {

	private final DataFrame dataFrame;

	public DataFrame getDataFrame() {
		return dataFrame;
	}

	public ConvertData(Frame frame, DataFrame dataFrame) {

		dataFrame.setTimeStamp(frame.timestamp());

		HandList handList = frame.hands();
		iav.de.datavalue.HandList hands = new iav.de.datavalue.HandList();

		configureFingerLists(handList, hands);

		dataFrame.setHandList(hands);
		this.dataFrame = dataFrame;
	}

	private void configureFingerLists(HandList hands,
			iav.de.datavalue.HandList handList) {

		handList.setCount(hands.count());

		iav.de.datavalue.Hand rightDataHand = new iav.de.datavalue.Hand();
		iav.de.datavalue.Hand leftDataHand = new iav.de.datavalue.Hand();

		Hand leftHand = hands.leftmost();
		Hand rightHand = hands.rightmost();

		configureRightHand(rightHand, rightDataHand);
		configureLeftHand(leftHand, leftDataHand);

		List<iav.de.datavalue.Hand> handsList = new ArrayList<iav.de.datavalue.Hand>();

		if (hands.count() > 1) {
			handsList.add(leftDataHand);
			handsList.add(rightDataHand);
			handList.setHands(handsList);
			for (Hand hand : hands) {
				if (hand.isLeft()) {
					iav.de.datavalue.FingerList fingersList = configureFingerList(
							hand, handList, hands.frontmost(), leftDataHand);
					leftDataHand.setFingers(fingersList);
					handList.setLeftMostHand(leftDataHand);

				} else if (hand.isRight()) {
					iav.de.datavalue.FingerList fingersList = configureFingerList(
							hand, handList, hands.frontmost(), rightDataHand);
					rightDataHand.setFingers(fingersList);
					handList.setRightMostHand(rightDataHand);
				}
			}
		}

		else if (hands.count() == 1) {
			if (hands.get(0).isLeft()) {
				handsList.add(leftDataHand);
				iav.de.datavalue.FingerList fingersList = configureFingerList(
						hands.get(0), handList, hands.frontmost(), leftDataHand);
				leftDataHand.setFingers(fingersList);
				handList.setLeftMostHand(leftDataHand);
			} else if (hands.get(0).isRight()) {
				handsList.add(rightDataHand);
				iav.de.datavalue.FingerList fingersList = configureFingerList(
						hands.get(0), handList, hands.frontmost(),
						rightDataHand);
				rightDataHand.setFingers(fingersList);
				handList.setRightMostHand(rightDataHand);
			}
			handList.setHands(handsList);
		}
	}

	private iav.de.datavalue.FingerList configureFingerList(Hand hand,
			iav.de.datavalue.HandList handList, Hand foreMostHand,
			iav.de.datavalue.Hand nHand) {
		iav.de.datavalue.FingerList fingersList = new iav.de.datavalue.FingerList();
		int count = 0;
		FingerList fingerList = hand.fingers();
		Finger frontMostFinger = fingerList.frontmost();
		for (Finger finger : fingerList) {
			count++;
			configureFinger(finger, fingersList, frontMostFinger);
		}
		if (foreMostHand == hand) {
			handList.setFrontMostHand(nHand);
		}
		fingersList.setCount(count);
		return fingersList;
		// List<iav.de.datavalue.Hand> handsList = (List<iav.de.datavalue.Hand>)
		// handList.getLeftMostHand();
		// FingerList fingerList = hand.fingers();
	}

	private iav.de.datavalue.Hand configureRightHand(Hand rightHand,
			iav.de.datavalue.Hand rightDataHand) {

		Vector rightPalmVelocity = rightHand.palmVelocity();
		double palmWidth = rightHand.palmWidth();

		iav.de.datavalue.Vector rightDataPalmVector = new iav.de.datavalue.Vector();
		rightDataPalmVector.setMagnitute(rightPalmVelocity.magnitude());

		iav.de.datavalue.Position rightDataPalmPosition = new iav.de.datavalue.Position();
		rightDataPalmPosition.setxCoordinate(rightHand.palmPosition().getX());
		rightDataPalmPosition.setyCoordinate(rightHand.palmPosition().getY());
		rightDataPalmPosition.setzCoordinate(rightHand.palmPosition().getZ());
		rightDataHand.setPalmWidth(palmWidth);

		rightDataHand.setPalmVelocity(rightDataPalmVector);
		rightDataPalmVector.setPosition(rightDataPalmPosition);
		rightDataHand.setPalmPosition(rightDataPalmVector);

		// Setting Direction for the right hand
		AngleProjection anglePalmProjection = new AngleProjection();
		anglePalmProjection.setPitch(rightHand.direction().pitch());
		anglePalmProjection.setRoll(rightHand.direction().roll());
		anglePalmProjection.setYaw(rightHand.direction().yaw());
		rightDataPalmVector.setAngleProjection(anglePalmProjection);

		rightDataHand.setDirection(rightDataPalmVector);
		return rightDataHand;
	}

	private iav.de.datavalue.Hand configureLeftHand(Hand leftHand,
			iav.de.datavalue.Hand leftDataHand) {

		Vector leftPalmVelocity = leftHand.palmVelocity();
		double palmWidth = leftHand.palmWidth();

		iav.de.datavalue.Vector leftDataPalmVector = new iav.de.datavalue.Vector();
		leftDataPalmVector.setMagnitute(leftPalmVelocity.magnitude());

		iav.de.datavalue.Position leftDataPalmPosition = new iav.de.datavalue.Position();
		leftDataPalmPosition.setxCoordinate(leftHand.palmPosition().getX());
		leftDataPalmPosition.setyCoordinate(leftHand.palmPosition().getY());
		leftDataPalmPosition.setzCoordinate(leftHand.palmPosition().getZ());
		leftDataHand.setPalmWidth(palmWidth);

		leftDataHand.setPalmVelocity(leftDataPalmVector);
		leftDataPalmVector.setPosition(leftDataPalmPosition);
		leftDataHand.setPalmPosition(leftDataPalmVector);

		// Setting Direction for the right hand
		AngleProjection anglePalmProjection = new AngleProjection();
		anglePalmProjection.setPitch(leftHand.direction().pitch());
		anglePalmProjection.setRoll(leftHand.direction().roll());
		anglePalmProjection.setYaw(leftHand.direction().yaw());
		leftDataPalmVector.setAngleProjection(anglePalmProjection);

		leftDataHand.setDirection(leftDataPalmVector);
		return leftDataHand;
	}

	private void configureFinger(Finger finger,
			iav.de.datavalue.FingerList fingersList, Finger frontMostFinger) {

		// Update Fingers
		iav.de.datavalue.Finger fingerValue = new iav.de.datavalue.Finger();

		fingerValue.setExtended(finger.isExtended());
		fingerValue.setIsVisible(finger.isValid());
		fingerValue.setLength(finger.length());
		fingerValue.setTimeVisible(finger.timeVisible());

		if (finger.type().toString().equals(FingerType.TYPE_INDEX.toString())) {
			fingerValue.setType(FingerType.TYPE_INDEX);
		} else if (finger.type().toString()
				.equals(FingerType.TYPE_MIDDLE.toString())) {
			fingerValue.setType(FingerType.TYPE_MIDDLE);
		} else if (finger.type().toString()
				.equals(FingerType.TYPE_PINKY.toString())) {
			fingerValue.setType(FingerType.TYPE_PINKY);
		} else if (finger.type().toString()
				.equals(FingerType.TYPE_RING.toString())) {
			fingerValue.setType(FingerType.TYPE_RING);
		} else if (finger.type().toString()
				.equals(FingerType.TYPE_THUMB.toString())) {
			fingerValue.setType(FingerType.TYPE_THUMB);
		}

		Vector direction = finger.direction();
		Vector tipVelocity = finger.tipVelocity();
		Vector tipPosition = finger.tipPosition();

		configureVector(direction, tipVelocity, tipPosition, fingerValue);
		fingersList.setFinger(fingerValue);
		if (finger.equals(frontMostFinger)) {
			fingersList.setFrontMost(fingerValue);
		}
	}

	private void configureVector(Vector direction, Vector tipVelocity,
			Vector tipPosition, iav.de.datavalue.Finger fingerValue) {

		iav.de.datavalue.Vector vectorData = new iav.de.datavalue.Vector();
		// Assign the Position Vector data
		iav.de.datavalue.Position position = new iav.de.datavalue.Position();
		position.setxCoordinate(tipPosition.getX());
		position.setyCoordinate(tipPosition.getY());
		position.setzCoordinate(tipPosition.getZ());
		vectorData.setPosition(position);

		// Assign the Projection Vector data
		iav.de.datavalue.AngleProjection angleProjection = new iav.de.datavalue.AngleProjection();
		angleProjection.setPitch(direction.pitch());
		angleProjection.setRoll(direction.roll());
		angleProjection.setYaw(direction.yaw());
		vectorData.setAngleProjection(angleProjection);

		// Assign the Velocity Vector data
		vectorData.setMagnitute(tipVelocity.magnitude());

		fingerValue.setPosition(vectorData);
		fingerValue.setTipVelocity(vectorData);
		fingerValue.setTipDirection(vectorData);
	}
}