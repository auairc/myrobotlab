package org.myrobotlab.service;

import org.myrobotlab.framework.Service;
import org.myrobotlab.logging.LoggerFactory;
import org.myrobotlab.service.config.InMoov2ArmConfig;
import org.slf4j.Logger;

/**
 * InMoovArm - This is the Arm sub-service for the InMoov Robot. It consists of
 * 4 Servos: bicep, rotate,shoulder,omoplate It uses Arduino to control the
 * servos.
 * 
 * TODO - make this service responsible for setting up pub subs - and on any new
 * registration look for attach capablities
 * 
 * Null checking is not necessary for this "group" of servos - its assumed the
 * user would want the entire group initialized on creation and that is what
 * startPeers() does
 *
 */
public class InMoov2Arm extends
    Service /* implements IKJointAngleListener - FIXME needs more design */ {

  public final static Logger log = LoggerFactory.getLogger(InMoov2Arm.class);

  private static final long serialVersionUID = 1L;

  public InMoov2Arm(String n, String id) throws Exception {
    super(n, id);
  }

  @Override
  public void startService() {
    super.startService();
    InMoov2ArmConfig c = (InMoov2ArmConfig) config;
    Runtime.start(c.bicep);
    Runtime.start(c.rotate);
    Runtime.start(c.shoulder);
    Runtime.start(c.omoplate);
  }

  @Override
  public void broadcastState() {
    super.broadcastState();
    InMoov2ArmConfig c = (InMoov2ArmConfig) config;
    send(c.bicep, "broadcastState");
    send(c.rotate, "broadcastState");
    send(c.shoulder, "broadcastState");
    send(c.omoplate, "broadcastState");
  }

  public void disable() {
    InMoov2ArmConfig c = (InMoov2ArmConfig) config;
    send(c.bicep, "disable");
    send(c.rotate, "disable");
    send(c.shoulder, "disable");
    send(c.omoplate, "disable");
  }

  public void enable() {
    InMoov2ArmConfig c = (InMoov2ArmConfig) config;
    send(c.bicep, "enable");
    send(c.rotate, "enable");
    send(c.shoulder, "enable");
    send(c.omoplate, "enable");
  }

  public void fullSpeed() {
    InMoov2ArmConfig c = (InMoov2ArmConfig) config;
    send(c.bicep, "fullSpeed");
    send(c.rotate, "fullSpeed");
    send(c.shoulder, "fullSpeed");
    send(c.omoplate, "fullSpeed");
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

    InMoov2ArmConfig c = (InMoov2ArmConfig) config;

    long lastActivityTime = Math.max(getLastActivityTime(c.bicep), getLastActivityTime(c.rotate));
    lastActivityTime = Math.max(lastActivityTime, getLastActivityTime(c.shoulder));
    lastActivityTime = Math.max(lastActivityTime, getLastActivityTime(c.omoplate));
    return lastActivityTime;
  }

  public void moveTo(Double bicepPos, Double rotatePos, Double shoulderPos, Double omoplatePos) {
    log.debug("{} moveTo {} {} {} {}", getName(), bicepPos, rotatePos, shoulderPos, omoplatePos);

    InMoov2ArmConfig c = (InMoov2ArmConfig) config;
    send(c.bicep, "moveTo", bicepPos);
    send(c.rotate, "moveTo", rotatePos);
    send(c.shoulder, "moveTo", shoulderPos);
    send(c.omoplate, "moveTo", omoplatePos);
  }

  public void moveToBlocking(double bicep, double rotate, double shoulder, double omoplate) {
    log.info("init {} moveToBlocking", getName());
    moveTo(bicep, rotate, shoulder, omoplate);
    waitTargetPos();
    log.info("end {} moveToBlocking", getName());
  }

  // FIXME - framework should auto-release - unless configured not to
  public void releaseService() {
    try {
      // possible race condition if disable is queued
      // but release happens quicker ?
      disable();
      
      InMoov2ArmConfig c = (InMoov2ArmConfig) config;
      Runtime.start(c.bicep);
      Runtime.start(c.rotate);
      Runtime.start(c.shoulder);
      Runtime.start(c.omoplate);
      
      
      super.releaseService();
    } catch (Exception e) {
      error(e);
    }
  }

  public void rest() {
    InMoov2ArmConfig c = (InMoov2ArmConfig) config;
    send(c.bicep, "rest");
    send(c.rotate, "rest");
    send(c.shoulder, "rest");
    send(c.omoplate, "rest");
  }

  @Override
  public boolean save() {
    super.save();
    InMoov2ArmConfig c = (InMoov2ArmConfig) config;
    send(c.bicep, "save");
    send(c.rotate, "save");
    send(c.shoulder, "save");
    send(c.omoplate, "save");
    return true;
  }

  public void setAutoDisable(Boolean idleTimeoutMs) {
    InMoov2ArmConfig c = (InMoov2ArmConfig) config;
    send(c.bicep, "setAutoDisable", idleTimeoutMs);
    send(c.rotate, "setAutoDisable", idleTimeoutMs);
    send(c.shoulder, "setAutoDisable", idleTimeoutMs);
    send(c.omoplate, "setAutoDisable", idleTimeoutMs);
  }

  /**
   * This method sets the output min/max limits for all of the servos in the
   * arm. Input limits are unchanged.
   * 
   * @param bicepMin
   * @param bicepMax
   * @param rotateMin
   * @param rotateMax
   * @param shoulderMin
   * @param shoulderMax
   * @param omoplateMin
   * @param omoplateMax
   */
  public void setLimits(double bicepMin, double bicepMax, double rotateMin, double rotateMax, double shoulderMin, double shoulderMax, double omoplateMin, double omoplateMax) {
    InMoov2ArmConfig c = (InMoov2ArmConfig) config;
    send(c.bicep, "setMinMaxOutput", bicepMin, bicepMax);
    send(c.rotate, "setMinMaxOutput", rotateMin, rotateMax);
    send(c.shoulder, "setMinMaxOutput", shoulderMin, shoulderMax);
    send(c.omoplate, "setMinMaxOutput", omoplateMin, omoplateMax);
  }

  public void setSpeed(Double bicepSpeed, Double rotateSpeed, Double shoulderSpeed, Double omoplateSpeed) {
    InMoov2ArmConfig c = (InMoov2ArmConfig) config;
    send(c.bicep, "setSpeed", bicepSpeed);
    send(c.rotate, "setSpeed", rotateSpeed);
    send(c.shoulder, "setSpeed", shoulderSpeed);
    send(c.omoplate, "setSpeed", omoplateSpeed);
  }

  @Deprecated
  public void setVelocity(Double bicep, Double rotate, Double shoulder, Double omoplate) {
    setSpeed(bicep, rotate, shoulder, omoplate);
  }

  public void stop() {
    InMoov2ArmConfig c = (InMoov2ArmConfig) config;
    send(c.bicep, "stop");
    send(c.rotate, "stop");
    send(c.shoulder, "stop");
    send(c.omoplate, "stop");
  }

  public void test() {
    InMoov2ArmConfig c = (InMoov2ArmConfig) config;
    send(c.bicep, "moveInc", 2);
    send(c.rotate, "moveInc", 2);
    send(c.shoulder, "moveInc", 2);
    send(c.omoplate, "moveInc", 2);
  }

  public void waitTargetPos() {
    InMoov2ArmConfig c = (InMoov2ArmConfig) config;
    try {
      sendBlocking(c.bicep, "waitTargetPos");
      sendBlocking(c.rotate, "waitTargetPos");
      sendBlocking(c.shoulder, "waitTargetPos");
      sendBlocking(c.omoplate, "waitTargetPos");
    } catch (Exception e) {
      error(e);
    }
  }

}
