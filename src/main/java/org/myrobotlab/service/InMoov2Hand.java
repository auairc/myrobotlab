package org.myrobotlab.service;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.myrobotlab.framework.Registration;
import org.myrobotlab.framework.Service;
import org.myrobotlab.framework.interfaces.ServiceInterface;
import org.myrobotlab.framework.interfaces.Attachable;
import org.myrobotlab.io.FileIO;
import org.myrobotlab.logging.Level;
import org.myrobotlab.logging.LoggerFactory;
import org.myrobotlab.logging.LoggingFactory;
import org.myrobotlab.service.data.LeapData;
import org.myrobotlab.service.data.LeapHand;
import org.myrobotlab.service.data.PinData;
import org.myrobotlab.service.interfaces.LeapDataListener;
import org.myrobotlab.service.interfaces.PinArrayListener;
import org.myrobotlab.service.config.InMoov2ArmConfig;
import org.myrobotlab.service.config.InMoov2HandConfig;
import org.myrobotlab.service.config.InMoov2TorsoConfig;
import org.myrobotlab.service.config.ServiceConfig;
import org.myrobotlab.service.config.ServoConfig;
import org.myrobotlab.service.interfaces.ServoControl;
import org.myrobotlab.service.interfaces.ServoController;
import org.slf4j.Logger;

/**
 * InMoovHand - The Hand sub service for the InMoov Robot. This service has 6
 * servos controlled by an ServoController.
 * thumb,index,majeure,ringFinger,pinky, and wrist
 * 
 * There is also leap motion support.
 */
public class InMoov2Hand extends Service implements LeapDataListener, PinArrayListener {

  public final static Logger log = LoggerFactory.getLogger(InMoov2Hand.class);

  private static final long serialVersionUID = 1L;

  // The pins for the finger tip sensors
  public String[] sensorPins = new String[] { "A0", "A1", "A2", "A3", "A4" };
  // public int[] sensorLastValues = new int[] {0,0,0,0,0};
  public boolean sensorsEnabled = false;
  public int[] sensorThresholds = new int[] { 500, 500, 500, 500, 500 };

  public InMoov2Hand(String n, String id) {
    super(n, id);
  }

  public void startService() {
    super.startService();
    InMoov2HandConfig c = (InMoov2HandConfig) config;
    Runtime.start(c.thumb);
    Runtime.start(c.index);
    Runtime.start(c.majeure);
    Runtime.start(c.ringFinger);
    Runtime.start(c.pinky);
    Runtime.start(c.wrist);
  }

  public void bird() {
    moveTo(150.0, 180.0, 0.0, 180.0, 180.0, 90.0);
  }

  @Override
  public void broadcastState() {
    super.broadcastState();
    InMoov2HandConfig c = (InMoov2HandConfig) config;
    send(c.thumb, "broadcastState");
    send(c.index, "broadcastState");
    send(c.majeure, "broadcastState");
    send(c.ringFinger, "broadcastState");
    send(c.pinky, "broadcastState");
    send(c.wrist, "broadcastState");
  }

  public void close() {
    moveTo(130, 180, 180, 180, 180);
  }

  public void closePinch() {
    moveTo(130, 140, 180, 180, 180);
  }

  public void releaseService() {
    try {
      disable();
      
      InMoov2HandConfig c = (InMoov2HandConfig) config;
      Runtime.release(c.thumb);
      Runtime.release(c.index);
      Runtime.release(c.majeure);
      Runtime.release(c.ringFinger);
      Runtime.release(c.pinky);
      Runtime.release(c.wrist);
      
      super.releaseService();
    } catch (Exception e) {
      error(e);
    }
  }

  public void count() {
    one();
    sleep(1);
    two();
    sleep(1);
    three();
    sleep(1);
    four();
    sleep(1);
    five();
  }

  public void devilHorns() {
    moveTo(150.0, 0.0, 180.0, 180.0, 0.0, 90.0);
  }

  public void disable() {
    InMoov2HandConfig c = (InMoov2HandConfig) config;
    send(c.thumb, "disable");
    send(c.index, "disable");
    send(c.majeure, "disable");
    send(c.ringFinger, "disable");
    send(c.pinky, "disable");
    send(c.wrist, "disable");
  }

  public boolean enable() {
    InMoov2HandConfig c = (InMoov2HandConfig) config;
    send(c.thumb, "enable");
    send(c.index, "enable");
    send(c.majeure, "enable");
    send(c.ringFinger, "enable");
    send(c.pinky, "enable");
    send(c.wrist, "enable");
    return true;
  }

  @Deprecated /* use setAutoDisable */
  public void enableAutoDisable(Boolean param) {
    setAutoDisable(param);
  }

  @Deprecated /* noop it always is auto enable */
  public void enableAutoEnable(Boolean param) {
  }

  public void five() {
    open();
  }

  public void four() {
    moveTo(150.0, 0.0, 0.0, 0.0, 0.0, 90.0);
  }

  public void fullSpeed() {
    InMoov2HandConfig c = (InMoov2HandConfig) config;
    send(c.thumb, "fullSpeed");
    send(c.index, "fullSpeed");
    send(c.majeure, "fullSpeed");
    send(c.ringFinger, "fullSpeed");
    send(c.pinky, "fullSpeed");
    send(c.wrist, "fullSpeed");
  }

  /**
   * this method returns the analog pins that the hand is listening to. The
   * InMoovHand listens on analog pins A0-A4 for the finger tip sensors.
   * 
   */
  @Override
  public String[] getActivePins() {
    // TODO Auto-generated method stub
    // for the InMoov hand, we're just going to say A0 - A4 ... for now..
    return sensorPins;
  }
  
  private Long getLastActivityTime(String name) {
    try {
      return (Long) sendBlocking(name, "getLastActivityTime");
    } catch (Exception e) {
      error(e);
    }
    return 0L;
  }

  public long getLastActivityTime() {

    InMoov2HandConfig c = (InMoov2HandConfig) config;

    long lastActivityTime = Math.max(getLastActivityTime(c.index), getLastActivityTime(c.thumb));
    lastActivityTime = Math.max(lastActivityTime, getLastActivityTime(c.index));
    lastActivityTime = Math.max(lastActivityTime, getLastActivityTime(c.majeure));
    lastActivityTime = Math.max(lastActivityTime, getLastActivityTime(c.ringFinger));
    lastActivityTime = Math.max(lastActivityTime, getLastActivityTime(c.pinky));
    lastActivityTime = Math.max(lastActivityTime, getLastActivityTime(c.wrist));
    return lastActivityTime;
  }

  public void hangTen() {
    moveTo(0.0, 180.0, 180.0, 180.0, 0.0, 90.0);
  }

  public void map(double minX, double maxX, double minY, double maxY) {
    InMoov2HandConfig c = (InMoov2HandConfig) config;
    send(c.thumb, "map", minX, maxX, minY, maxY);
    send(c.index, "map", minX, maxX, minY, maxY);
    send(c.majeure, "map", minX, maxX, minY, maxY);
    send(c.ringFinger, "map", minX, maxX, minY, maxY);
    send(c.pinky, "map", minX, maxX, minY, maxY);
  }

  // TODO - waving thread fun
  public void moveTo(double thumb, double index, double majeure, double ringFinger, double pinky) {
    moveTo(thumb, index, majeure, ringFinger, pinky, null);
  }

  public void moveTo(Double thumbPos, Double indexPos, Double majeurePos, Double ringFingerPos, Double pinkyPos, Double wristPos) {
    if (log.isDebugEnabled()) {
      log.debug("{}.moveTo {} {} {} {} {} {}", getName(), thumbPos, indexPos, majeurePos, ringFingerPos, pinkyPos, wristPos);
    }
    InMoov2HandConfig c = (InMoov2HandConfig) config;
    send(c.thumb, "moveTo", thumbPos);
    send(c.index, "moveTo", indexPos);
    send(c.majeure, "moveTo", majeurePos);
    send(c.ringFinger, "moveTo", ringFingerPos);
    send(c.pinky, "moveTo", pinkyPos);
    send(c.wrist, "moveTo", wristPos);
  }

  public void moveToBlocking(double thumb, double index, double majeure, double ringFinger, double pinky) {
    moveToBlocking(thumb, index, majeure, ringFinger, pinky, null);
  }

  public void moveToBlocking(double thumb, double index, double majeure, double ringFinger, double pinky, Double wrist) {
    log.info("init {} moveToBlocking ", getName());
    moveTo(thumb, index, majeure, ringFinger, pinky, wrist);
    waitTargetPos();
    log.info("end {} moveToBlocking ", getName());
  }

  public void ok() {
    moveTo(150.0, 180.0, 0.0, 0.0, 0.0, 90.0);
  }

  public void one() {
    moveTo(150.0, 0.0, 180.0, 180.0, 180.0, 90.0);
  }

  @Override
  public LeapData onLeapData(LeapData data) {
    String side = getName().contains("left") ? "left" : "right";
    if (!data.frame.isValid()) {
      // TODO: we could return void here? not sure
      // who wants the return value form this method.
      log.info("Leap data frame not valid.");
      return data;
    }
    LeapHand h;
    if ("right".equalsIgnoreCase(side)) {
      if (data.frame.hands().rightmost().isValid()) {
        h = data.rightHand;
      } else {
        log.info("Right hand frame not valid.");
        // return this hand isn't valid
        return data;
      }
    } else if ("left".equalsIgnoreCase(side)) {
      if (data.frame.hands().leftmost().isValid()) {
        h = data.leftHand;
      } else {
        log.info("Left hand frame not valid.");
        // return this frame isn't valid.
        return data;
      }
    } else {
      // side could be null?
      log.info("Unknown Side or side not set on hand (Side = {})", side);
      // we can default to the right side?
      // TODO: come up with a better default or at least document this
      // behavior.
      if (data.frame.hands().rightmost().isValid()) {
        h = data.rightHand;
      } else {
        log.info("Right(unknown) hand frame not valid.");
        // return this hand isn't valid
        return data;
      }
    }
    
    InMoov2HandConfig c = (InMoov2HandConfig) config;
    send(c.thumb, "moveTo", h.thumb);
    send(c.index, "moveTo", h.index);
    send(c.majeure, "moveTo", h.middle);
    send(c.ringFinger, "moveTo", h.ring);
    send(c.pinky, "moveTo", h.pinky);
    // FIXME  - no wrist ?

    return data;
  }

  // FIXME - use pub/sub attach to set this up without having this method !
  @Override
  public void onPinArray(PinData[] pindata) {

    log.info("On Pin Data: {}", pindata.length);
    if (!sensorsEnabled)
      return;
    // just return ? TOOD: maybe still track the last read values...
    // TODO : change the interface to get a map of pin data, keyed off the name.
    // ?
    for (PinData pin : pindata) {
      log.info("Pin Data: {}", pin);
    }
  }

  public void open() {
    rest();
  }

  public void openPinch() {
    moveTo(0, 0, 180, 180, 180);
  }

  public void release() {
    disable();
  }

  public void rest() {
    InMoov2HandConfig c = (InMoov2HandConfig) config;
    send(c.thumb, "fullSpeed");
    send(c.index, "fullSpeed");
    send(c.majeure, "fullSpeed");
    send(c.ringFinger, "fullSpeed");
    send(c.pinky, "fullSpeed");
    send(c.wrist, "fullSpeed");
  }

  @Override
  public boolean save() {
    super.save();
    InMoov2HandConfig c = (InMoov2HandConfig) config;
    send(c.thumb, "save");
    send(c.index, "save");
    send(c.majeure, "save");
    send(c.ringFinger, "save");
    send(c.pinky, "save");
    send(c.wrist, "save");
    return true;
  }

  public void setAutoDisable(Boolean b) {
    InMoov2HandConfig c = (InMoov2HandConfig) config;
    send(c.thumb, "moveTo", b);
    send(c.index, "moveTo", b);
    send(c.majeure, "moveTo", b);
    send(c.ringFinger, "moveTo", b);
    send(c.pinky, "moveTo", b);
    send(c.wrist, "moveTo", b);    
  }

  public void setPins(int thumbPin, int indexPin, int majeurePin, int ringFingerPin, int pinkyPin, int wristPin) {
    log.info("setPins {} {} {} {} {} {}", thumbPin, indexPin, majeurePin, ringFingerPin, pinkyPin, wristPin);
    InMoov2HandConfig c = (InMoov2HandConfig) config;
    send(c.thumb, "setPin", thumbPin);
    send(c.index, "setPin", indexPin);
    send(c.majeure, "setPin", majeurePin);
    send(c.ringFinger, "setPin", ringFingerPin);
    send(c.pinky, "setPin", pinkyPin);
    send(c.wrist, "setPin", wristPin);    
  }

  public void setRest(Double thumb, Double index, Double majeure, Double ringFinger, Double pinky) {
    setRest(thumb, index, majeure, ringFinger, pinky, null);
  }

  public void setRest(Double thumbRest, Double indexRest, Double majeureRest, Double ringFingerRest, Double pinkyRest, Double wristRest) {
    log.info("setRest {} {} {} {} {} {}", thumbRest, indexRest, majeureRest, ringFingerRest, pinkyRest, wristRest);
    InMoov2HandConfig c = (InMoov2HandConfig) config;
    send(c.thumb, "setRest", thumbRest);
    send(c.index, "setRest", indexRest);
    send(c.majeure, "setRest", majeureRest);
    send(c.ringFinger, "setRest", ringFingerRest);
    send(c.pinky, "setRest", pinkyRest);
    send(c.wrist, "setRest", wristRest);    
  }

  /**
   * @param pins
   *          Set the array of pins that should be listened to.
   * 
   */
  public void setSensorPins(String[] pins) {
    // TODO, this should probably be a sorted set.. and sensorPins itself should
    // probably be a map to keep the mapping of pin to finger
    this.sensorPins = pins;
  }

  public void setSpeed(Double thumbSpeed, Double indexSpeed, Double majeureSpeed, Double ringFingerSpeed, Double pinkySpeed, Double wristSpeed) {
    InMoov2HandConfig c = (InMoov2HandConfig) config;
    send(c.thumb, "setSpeed", thumbSpeed);
    send(c.index, "setSpeed", indexSpeed);
    send(c.majeure, "setSpeed", majeureSpeed);
    send(c.ringFinger, "setSpeed", ringFingerSpeed);
    send(c.pinky, "setSpeed", pinkySpeed);
    send(c.wrist, "setSpeed", wristSpeed); 
  }

  @Deprecated
  public void setVelocity(Double thumb, Double index, Double majeure, Double ringFinger, Double pinky, Double wrist) {
    log.warn("setspeed deprecated please use setSpeed");
    setSpeed(thumb, index, majeure, ringFinger, pinky, wrist);
  }

  // FIXME - removing this for now - I think it should be a peer of InMoov
  // that way it can manage left and right etc (among other reasons)
  
  // FIXME - if multiple systems are dependent on the ServoControl map and
  // limits to be a certain value
  // leap should change its output, and leave the map and limits here alone
  // FIXME !!! - should not have LeapMotion defined here at all - it should be
  // pub/sub !!!
//  public void startLeapTracking() throws Exception {
//    if (leap == null) {
//      leap = (LeapMotion) startPeer("leap");
//    }
//    Runtime.start("leap")
//    this.index.map(90.0, 0.0, this.index.getMin(), this.index.getMax());
//    this.thumb.map(90.0, 50.0, this.thumb.getMin(), this.thumb.getMax());
//    this.majeure.map(90.0, 0.0, this.majeure.getMin(), this.majeure.getMax());
//    this.ringFinger.map(90.0, 0.0, this.ringFinger.getMin(), this.ringFinger.getMax());
//    this.pinky.map(90.0, 0.0, this.pinky.getMin(), this.pinky.getMax());
//    leap.addLeapDataListener(this);
//    leap.startTracking();
//    return;
//  }

  public void stop() {    
    InMoov2HandConfig c = (InMoov2HandConfig) config;
    send(c.thumb, "stop");
    send(c.index, "stop");
    send(c.majeure, "stop");
    send(c.ringFinger, "stop");
    send(c.pinky, "stop");
    send(c.wrist, "stop");
  }

  // FIXME !!! - should not have LeapMotion defined here at all - it should be
  // pub/sub !!!
//  public void stopLeapTracking() {
//    leap.stopTracking();
//    index.map(index.getMin(), index.getMax(), index.getMin(), index.getMax());
//    thumb.map(thumb.getMin(), thumb.getMax(), thumb.getMin(), thumb.getMax());
//    majeure.map(majeure.getMin(), majeure.getMax(), majeure.getMin(), majeure.getMax());
//    ringFinger.map(ringFinger.getMin(), ringFinger.getMax(), ringFinger.getMin(), ringFinger.getMax());
//    pinky.map(pinky.getMin(), pinky.getMax(), pinky.getMin(), pinky.getMax());
//    rest();
//    return;
//  }

  public void test() {
    InMoov2HandConfig c = (InMoov2HandConfig) config;
    send(c.thumb, "moveInc", 2);
    send(c.index, "moveInc", 2);
    send(c.ringFinger, "moveInc", 2);
    send(c.pinky, "moveInc", 2);
    send(c.wrist, "moveInc", 2);
    info("test completed");
  }

  public void three() {
    moveTo(150.0, 0.0, 0.0, 0.0, 180.0, 90.0);
  }

  public void thumbsUp() {
    moveTo(0.0, 180.0, 180.0, 180.0, 180.0, 90.0);
  }

  public void two() {
    victory();
  }

  public void victory() {
    moveTo(150.0, 0.0, 0.0, 180.0, 180.0, 90.0);
  }

  public void waitTargetPos() {
    InMoov2HandConfig c = (InMoov2HandConfig) config;
    try {
      sendBlocking(c.thumb, "waitTargetPos");
      sendBlocking(c.index, "waitTargetPos");
      sendBlocking(c.majeure, "waitTargetPos");
      sendBlocking(c.ringFinger, "waitTargetPos");
      sendBlocking(c.pinky, "waitTargetPos");
      sendBlocking(c.wrist, "waitTargetPos");

    } catch (Exception e) {
      error(e);
    }
  }


  public static void main(String[] args) {
    LoggingFactory.init(Level.INFO);

    try {

      InMoov2 i01 = (InMoov2) Runtime.start("i01", "InMoov2");
      i01.startRightHand();

      ServoController controller = (ServoController) Runtime.getService("i01.right");

      InMoov2Hand rightHand = (InMoov2Hand) Runtime.start("r01", "InMoov2Hand");// InMoovHand("r01");
      Runtime.createAndStart("gui", "SwingGui");

      Runtime.createAndStart("webgui", "WebGui");
      // rightHand.connect("COM12"); TEST RECOVERY !!!
      rightHand.close();
      rightHand.open();
      rightHand.openPinch();
      rightHand.closePinch();
      rightHand.rest();

    } catch (Exception e) {
      log.error("main threw", e);
    }
  }
}
