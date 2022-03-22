package org.myrobotlab.service;

import org.myrobotlab.framework.Service;
import org.myrobotlab.logging.Level;
import org.myrobotlab.logging.LoggerFactory;
import org.myrobotlab.logging.LoggingFactory;
import org.myrobotlab.service.config.InMoov2TorsoConfig;
import org.slf4j.Logger;

/**
 * InMoovTorso - The inmoov torso. This will allow control of the topStom,
 * midStom, and lowStom servos.
 *
 */
public class InMoov2Torso extends Service {

  private static final long serialVersionUID = 1L;

  public final static Logger log = LoggerFactory.getLogger(InMoov2Torso.class);

  public InMoov2Torso(String n, String id) {
    super(n, id);
  }

  public void startService() {
    super.startService();
    InMoov2TorsoConfig c = (InMoov2TorsoConfig) config;
    Runtime.start(c.topStom);
    Runtime.start(c.midStom);
    Runtime.start(c.lowStom);
  }

  public void releaseService() {
    try {
      disable();
      InMoov2TorsoConfig c = (InMoov2TorsoConfig) config;
      Runtime.release(c.topStom);
      Runtime.release(c.midStom);
      Runtime.release(c.lowStom);
      super.releaseService();
    } catch (Exception e) {
      error(e);
    }
  }

  public void enable() {
    InMoov2TorsoConfig c = (InMoov2TorsoConfig) config;
    send(c.topStom, "enable");
    send(c.midStom, "enable");
    send(c.lowStom, "enable");
  }

  public void setAutoDisable(Boolean b) {
    InMoov2TorsoConfig c = (InMoov2TorsoConfig) config;
    send(c.topStom, "setAutoDisable", b);
    send(c.midStom, "setAutoDisable", b);
    send(c.lowStom, "setAutoDisable", b);
  }

  @Override
  public void broadcastState() {
    super.broadcastState();
    InMoov2TorsoConfig c = (InMoov2TorsoConfig) config;
    send(c.topStom, "broadcastState");
    send(c.midStom, "broadcastState");
    send(c.lowStom, "broadcastState");
  }

  public void disable() {
    InMoov2TorsoConfig c = (InMoov2TorsoConfig) config;
    send(c.topStom, "disable");
    send(c.midStom, "disable");
    send(c.lowStom, "disable");
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

    InMoov2TorsoConfig c = (InMoov2TorsoConfig) config;

    long lastActivityTime = Math.max(getLastActivityTime(c.topStom), getLastActivityTime(c.midStom));
    lastActivityTime = Math.max(lastActivityTime, getLastActivityTime(c.lowStom));
    return lastActivityTime;
  }

  public void moveTo(Double topStomPos, Double midStomPos, Double lowStomPos) {
    if (log.isDebugEnabled()) {
      log.debug("{} moveTo {} {} {}", getName(), topStomPos, midStomPos, lowStomPos);
    }
    InMoov2TorsoConfig c = (InMoov2TorsoConfig) config;
    send(c.topStom, "moveTo", topStomPos);
    send(c.midStom, "moveTo", midStomPos);
    send(c.lowStom, "moveTo", lowStomPos);
  }

  public void moveToBlocking(Double topStomPos, Double midStomPos, Double lowStomPos) {
    log.info("init {} moveToBlocking ", getName());
    moveTo(topStomPos, midStomPos, lowStomPos);
    waitTargetPos();
    log.info("end {} moveToBlocking", getName());
  }

  public void waitTargetPos() {

    InMoov2TorsoConfig c = (InMoov2TorsoConfig) config;

    try {
      sendBlocking(c.topStom, "waitTargetPos");
      sendBlocking(c.midStom, "waitTargetPos");
      sendBlocking(c.lowStom, "waitTargetPos");
    } catch (Exception e) {
      error(e);
    }
  }

  public void rest() {
    InMoov2TorsoConfig c = (InMoov2TorsoConfig) config;
    send(c.topStom, "rest");
    send(c.midStom, "rest");
    send(c.lowStom, "rest");
  }

  @Override
  public boolean save() {
    super.save();
    InMoov2TorsoConfig c = (InMoov2TorsoConfig) config;
    send(c.topStom, "save");
    send(c.midStom, "save");
    send(c.lowStom, "save");
    return true;
  }

  /**
   * Sets the output min/max values for all servos in the torso. input limits on
   * servos are not modified in this method.
   * 
   * @param topStomMin
   * @param topStomMax
   * @param midStomMin
   * @param midStomMax
   * @param lowStomMin
   * @param lowStomMax
   */
  public void setLimits(double topStomMin, double topStomMax, double midStomMin, double midStomMax, double lowStomMin, double lowStomMax) {
    InMoov2TorsoConfig c = (InMoov2TorsoConfig) config;
    send(c.topStom, "setMinMaxOutput", topStomMin, topStomMax);
    send(c.topStom, "setMinMaxOutput", midStomMin, midStomMax);
    send(c.topStom, "setMinMaxOutput", lowStomMin, lowStomMax);
  }

  public void setPins(Integer topStomPin, Integer midStomPin, Integer lowStomPin) {
    InMoov2TorsoConfig c = (InMoov2TorsoConfig) config;
    send(c.topStom, "setPin", topStomPin);
    send(c.midStom, "setPin", midStomPin);
    send(c.lowStom, "setPin", lowStomPin);
  }

  public void setSpeed(Double topStomSpeed, Double midStomSpeed, Double lowStomSpeed) {
    InMoov2TorsoConfig c = (InMoov2TorsoConfig) config;
    send(c.topStom, "setSpeed", topStomSpeed);
    send(c.midStom, "setSpeed", midStomSpeed);
    send(c.lowStom, "setSpeed", lowStomSpeed);
  }

  public void test() {
    InMoov2TorsoConfig c = (InMoov2TorsoConfig) config;
    send(c.topStom, "moveInc", 2);
    send(c.midStom, "moveInc", 2);
    send(c.lowStom, "moveInc", 2);
  }

  @Deprecated /* use setSpeed */
  public void setVelocity(Double topStomSpeed, Double midStomSpeed, Double lowStomSpeed) {
    setSpeed(topStomSpeed, midStomSpeed, lowStomSpeed);
  }

  static public void main(String[] args) {
    LoggingFactory.init(Level.INFO);
    try {
      VirtualArduino v = (VirtualArduino) Runtime.start("virtual", "VirtualArduino");

      v.connect("COM4");
      InMoov2Torso torso = (InMoov2Torso) Runtime.start("i01.torso", "InMoovTorso");
      Runtime.start("webgui", "WebGui");
      torso.test();
    } catch (Exception e) {
      log.error("main threw", e);
    }
  }

  public void fullSpeed() {
    InMoov2TorsoConfig c = (InMoov2TorsoConfig) config;
    send(c.topStom, "fullSpeed");
    send(c.midStom, "fullSpeed");
    send(c.lowStom, "fullSpeed");
  }

  public void stop() {
    InMoov2TorsoConfig c = (InMoov2TorsoConfig) config;
    send(c.topStom, "stop");
    send(c.midStom, "stop");
    send(c.lowStom, "stop");
  }

}
