package org.myrobotlab.service;

import java.util.concurrent.ThreadLocalRandom;

import org.myrobotlab.framework.Service;
import org.myrobotlab.logging.Level;
import org.myrobotlab.logging.LoggerFactory;
import org.myrobotlab.logging.LoggingFactory;
import org.myrobotlab.service.config.InMoov2HeadConfig;
import org.slf4j.Logger;

/**
 * InMoovHead - This is the InMoov head service. This service controls the
 * servos for the following: jaw, eyeX, eyeY, rothead and neck.
 * 
 */
public class InMoov2Head extends Service {

  private static final long serialVersionUID = 1L;

  public final static Logger log = LoggerFactory.getLogger(InMoov2Head.class);

  public InMoov2Head(String n, String id) {
    super(n, id);
  }

  public void startService() {
    super.startService();
    InMoov2HeadConfig c = (InMoov2HeadConfig) config;
    Runtime.start(c.neck);
    Runtime.start(c.jaw);
    Runtime.start(c.rollNeck);
    Runtime.start(c.rothead);
    Runtime.start(c.eyeX);
    Runtime.start(c.eyeY);
  }

  public void blink() {
    // TODO: clean stop auto blink if tracking ...
    double speed = ThreadLocalRandom.current().nextInt(40, 150 + 1);
    InMoov2HeadConfig c = (InMoov2HeadConfig) config;

    send(c.eyelidLeft, "setSpeed", speed);
    send(c.eyelidRight, "setSpeed", speed);

    // FIXME - why for heavens sake would you move blocking here
    // that's just wrong
    // moveToBlocking(180, 180);
    // moveToBlocking(0, 0);
    send(c.eyelidLeft, "moveTo", speed);
    send(c.eyelidRight, "moveTo", speed);
  }

  public void enable() {
    InMoov2HeadConfig c = (InMoov2HeadConfig) config;
    send(c.eyeX, "enable");
    send(c.eyeY, "enable");
    send(c.jaw, "enable");
    send(c.rothead, "enable");
    send(c.neck, "enable");
    send(c.rollNeck, "enable");
    send(c.eyelidLeft, "enable");
    send(c.eyelidRight, "enable");
  }

  @Override
  public void broadcastState() {
    InMoov2HeadConfig c = (InMoov2HeadConfig) config;
    send(c.eyeX, "broadcastState");
    send(c.eyeY, "broadcastState");
    send(c.jaw, "broadcastState");
    send(c.rothead, "broadcastState");
    send(c.neck, "broadcastState");
    send(c.rollNeck, "broadcastState");
    send(c.eyelidLeft, "broadcastState");
    send(c.eyelidRight, "broadcastState");
  }

  public void stop() {
    InMoov2HeadConfig c = (InMoov2HeadConfig) config;
    send(c.eyeX, "stop");
    send(c.eyeY, "stop");
    send(c.jaw, "stop");
    send(c.rothead, "stop");
    send(c.neck, "stop");
    send(c.rollNeck, "stop");
    send(c.eyelidLeft, "stop");
    send(c.eyelidRight, "stop");
  }

  public void disable() {
    stop();
    InMoov2HeadConfig c = (InMoov2HeadConfig) config;
    send(c.eyeX, "disable");
    send(c.eyeY, "disable");
    send(c.jaw, "disable");
    send(c.rothead, "disable");
    send(c.neck, "disable");
    send(c.rollNeck, "disable");
    send(c.eyelidLeft, "disable");
    send(c.eyelidRight, "disable");
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

    InMoov2HeadConfig c = (InMoov2HeadConfig) config;

    long lastActivityTime = Math.max(getLastActivityTime(c.neck), getLastActivityTime(c.rothead));
    lastActivityTime = Math.max(lastActivityTime, getLastActivityTime(c.eyeY));
    lastActivityTime = Math.max(lastActivityTime, getLastActivityTime(c.eyeY));
    lastActivityTime = Math.max(lastActivityTime, getLastActivityTime(c.rollNeck));
    lastActivityTime = Math.max(lastActivityTime, getLastActivityTime(c.eyelidLeft));
    lastActivityTime = Math.max(lastActivityTime, getLastActivityTime(c.eyelidRight));
    return lastActivityTime;
  }

  // FIXME - remove isValid() or test() - they do the same thing....
  public boolean isValid() {
    test();
    return true;
  }

  public void lookAt(Double x, Double y, Double z) {
    Double distance = Math.sqrt(Math.pow(x, 2.0) + Math.pow(y, 2.0) + Math.pow(z, 2.0));
    Double rotation = Math.toDegrees(Math.atan(y / x));
    Double colatitude = Math.toDegrees(Math.acos(z / distance));
    log.info("distance: " + distance);
    log.info("rotation: " + rotation);
    log.info("colatitude: " + colatitude);
    log.info("object distance is {},rothead servo {},neck servo {} ", distance, rotation, colatitude);
  }

  /**
   * Move the head - null will not move a servo - do not use unboxed params
   * again
   * 
   * @param neck
   * @param rothead
   */
  public void moveTo(Double neck, Double rothead) {
    moveTo(neck, rothead, null, null, null, null);
  }

  public void moveTo(Double neck, Double rothead, Double rollNeck) {
    moveTo(neck, rothead, null, null, null, rollNeck);
  }

  public void moveTo(Double neck, Double rothead, Double eyeX, Double eyeY) {
    moveTo(neck, rothead, eyeX, eyeY, null, null);
  }

  public void moveTo(Double neck, Double rothead, Double eyeX, Double eyeY, Double jaw) {
    moveTo(neck, rothead, eyeX, eyeY, jaw, null);
  }

  /**
   * Move servos of the head - null is a none move
   * 
   * @param neckPos
   * @param rotheadPos
   * @param eyeXPos
   * @param eyeYPos
   * @param jawPos
   * @param rollNeckPos
   */
  public void moveTo(Double neckPos, Double rotheadPos, Double eyeXPos, Double eyeYPos, Double jawPos, Double rollNeckPos) {

    if (log.isDebugEnabled()) {
      log.debug("head.moveTo {} {} {} {} {} {}", neckPos, rotheadPos, eyeXPos, eyeYPos, jawPos, rollNeckPos);
    }

    InMoov2HeadConfig c = (InMoov2HeadConfig) config;
    send(c.eyeX, "moveTo", eyeXPos);
    send(c.eyeY, "moveTo", eyeYPos);
    send(c.jaw, "moveTo", jawPos);
    send(c.rothead, "moveTo", rotheadPos);
    send(c.neck, "moveTo", neckPos);
    send(c.rollNeck, "moveTo", rollNeckPos);
  }

  public void moveEyelidsTo(double eyelidleftPos, double eyelidrightPos) {
    InMoov2HeadConfig c = (InMoov2HeadConfig) config;
    send(c.eyelidLeft, "moveTo", eyelidleftPos);
    send(c.eyelidRight, "moveTo", eyelidrightPos);
  }

  public void moveToBlocking(double neck, double rothead) {
    moveToBlocking(neck, rothead, null, null, null, null);
  }

  public void moveToBlocking(double neck, double rothead, Double rollNeck) {
    moveToBlocking(neck, rothead, null, null, null, rollNeck);
  }

  public void moveToBlocking(double neck, double rothead, double eyeX, double eyeY) {
    moveToBlocking(neck, rothead, eyeX, eyeY, null, null);
  }

  public void moveToBlocking(double neck, double rothead, double eyeX, double eyeY, double jaw) {
    moveToBlocking(neck, rothead, eyeX, eyeY, jaw, null);
  }

  public void moveToBlocking(Double neck, Double rothead, Double eyeX, Double eyeY, Double jaw, Double rollNeck) {
    log.info("init {} moveToBlocking ", getName());
    moveTo(neck, rothead, eyeX, eyeY, jaw, rollNeck);
    waitTargetPos();
    log.info("end {} moveToBlocking ", getName());
  }

  public void waitTargetPos() {
    InMoov2HeadConfig c = (InMoov2HeadConfig) config;
    try {
      sendBlocking(c.eyeX, "waitTargetPos");
      sendBlocking(c.eyeY, "waitTargetPos");
      sendBlocking(c.jaw, "waitTargetPos");
      sendBlocking(c.rothead, "waitTargetPos");
      sendBlocking(c.neck, "waitTargetPos");
      sendBlocking(c.rollNeck, "waitTargetPos");
    } catch (Exception e) {
      error(e);
    }
  }

  public void releaseService() {
    disable();
    InMoov2HeadConfig c = (InMoov2HeadConfig) config;
    Runtime.release(c.neck);
    Runtime.release(c.jaw);
    Runtime.release(c.rollNeck);
    Runtime.release(c.rothead);
    Runtime.release(c.eyeX);
    Runtime.release(c.eyeY);

    super.releaseService();
  }

  public void rest() {
    // FIXME - ask Gael about the setSpeed ????
    // initial positions
    // setSpeed(1.0, 1.0, 1.0, 1.0, 1.0, 1.0);
    InMoov2HeadConfig c = (InMoov2HeadConfig) config;
    send(c.eyeX, "rest");
    send(c.eyeY, "rest");
    send(c.jaw, "rest");
    send(c.rothead, "rest");
    send(c.neck, "rest");
    send(c.rollNeck, "rest");
    send(c.eyelidLeft, "rest");
    send(c.eyelidRight, "rest");
  }

  @Override
  public boolean save() {
    super.save();
    InMoov2HeadConfig c = (InMoov2HeadConfig) config;
    send(c.eyeX, "save");
    send(c.eyeY, "save");
    send(c.jaw, "save");
    send(c.rothead, "save");
    send(c.neck, "save");
    send(c.rollNeck, "save");
    send(c.eyelidLeft, "save");
    send(c.eyelidRight, "save");
    return true;
  }

  public void setAutoDisable(Boolean rothead, Boolean neck, Boolean rollNeck) {
    InMoov2HeadConfig c = (InMoov2HeadConfig) config;
    send(c.rothead, "setAutoDisable", rothead);
    send(c.neck, "setAutoDisable", neck);
    send(c.rollNeck, "setAutoDisable", rollNeck);
  }

  public void setAutoDisable(Boolean b) {
    InMoov2HeadConfig c = (InMoov2HeadConfig) config;
    send(c.eyeX, "setAutoDisable", b);
    send(c.eyeY, "setAutoDisable", b);
    send(c.jaw, "setAutoDisable", b);
    send(c.rothead, "setAutoDisable", b);
    send(c.neck, "setAutoDisable", b);
    send(c.rollNeck, "setAutoDisable", b);
    send(c.eyelidLeft, "setAutoDisable", b);
    send(c.eyelidRight, "setAutoDisable", b);
  }

  public void setLimits(double headXMin, double headXMax, double headYMin, double headYMax, double eyeXMin, double eyeXMax, double eyeYMin, double eyeYMax, double jawMin,
      double jawMax, double rollNeckMin, double rollNeckMax) {
    InMoov2HeadConfig c = (InMoov2HeadConfig) config;
    send(c.eyeX, "setMinMaxOutput", eyeXMin, eyeXMax);
    send(c.eyeY, "setMinMaxOutput", eyeYMin, eyeYMax);
    send(c.jaw, "setMinMaxOutput", jawMin, jawMax);
    send(c.rothead, "setMinMaxOutput", headXMin, headXMax);
    send(c.neck, "setMinMaxOutput", headYMin, headYMax);
    send(c.rollNeck, "setMinMaxOutput", rollNeckMin, rollNeckMax);
  }

  public void setSpeed(Double headXSpeed, Double headYSpeed, Double eyeXSpeed, Double eyeYSpeed, Double jawSpeed) {
    setSpeed(headXSpeed, headYSpeed, eyeXSpeed, eyeYSpeed, jawSpeed, null);
  }

  public void setSpeed(Double headXSpeed, Double headYSpeed, Double eyeX, Double eyeY, Double jaw, Double rollNeckSpeed) {
    log.debug(String.format("%s setSpeed %.2f %.2f %.2f %.2f %.2f %.2f", getName(), headXSpeed, headYSpeed, eyeX, eyeY, jaw, rollNeckSpeed));
    InMoov2HeadConfig c = (InMoov2HeadConfig) config;
    send(c.eyeX, "setSpeed", eyeX);
    send(c.eyeY, "setSpeed", eyeY);
    send(c.jaw, "setSpeed", jaw);
    send(c.rothead, "setSpeed", headXSpeed);
    send(c.neck, "setSpeed", headYSpeed);
    send(c.rollNeck, "setSpeed", rollNeckSpeed);
  }

  public void fullSpeed() {
    InMoov2HeadConfig c = (InMoov2HeadConfig) config;
    send(c.eyeX, "fullSpeed");
    send(c.eyeY, "fullSpeed");
    send(c.jaw, "fullSpeed");
    send(c.rothead, "fullSpeed");
    send(c.neck, "fullSpeed");
    send(c.rollNeck, "fullSpeed");
    send(c.eyelidLeft, "fullSpeed");
    send(c.eyelidRight, "fullSpeed");
  }

  public void test() {
    InMoov2HeadConfig c = (InMoov2HeadConfig) config;
    send(c.eyeX, "moveInc", 2);
    send(c.eyeY, "moveInc", 2);
    send(c.jaw, "moveInc", 2);
    send(c.rothead, "moveInc", 2);
    send(c.neck, "moveInc", 2);
    send(c.rollNeck, "moveInc", 2);
    send(c.eyelidLeft, "moveInc", 2);
    send(c.eyelidRight, "moveInc", 2);
  }

  /**
   * FIXME - implement
   * 
   * @param b
   */
  public void autoBlink(boolean b) {
    if (b) {
      addTask(1500, "blink");
    } else {
      purgeTask("blink");
    }
  }

  @Deprecated /* use setSpeed */
  public void setVelocity(Double headXSpeed, Double headYSpeed, Double eyeXSpeed, Double eyeYSpeed, Double jawSpeed) {
    setSpeed(headXSpeed, headYSpeed, eyeXSpeed, eyeYSpeed, jawSpeed, null);
  }

  @Deprecated /* use setSpeed */
  public void setVelocity(Double headXSpeed, Double headYSpeed, Double eyeXSpeed, Double eyeYSpeed, Double jawSpeed, Double rollNeckSpeed) {
    setSpeed(headXSpeed, headYSpeed, eyeXSpeed, eyeYSpeed, jawSpeed, rollNeckSpeed);
  }

  public void setPins(Integer neckPin, Integer rotheadPin, Integer eyeXPin, Integer eyeYPin, Integer jawPin, Integer rollNeckPin) {
    InMoov2HeadConfig c = (InMoov2HeadConfig) config;
    send(c.eyeX, "setPin", eyeXPin);
    send(c.eyeY, "setSpeed", eyeYPin);
    send(c.jaw, "setSpeed", jawPin);
    send(c.rothead, "setSpeed", rotheadPin);
    send(c.neck, "setSpeed", neckPin);
    send(c.rollNeck, "setSpeed", rollNeckPin);
  }

  public static void main(String[] args) {
    try {
      LoggingFactory.init(Level.DEBUG);

      Runtime.start("python", "Python");

      Servo s1 = (Servo) Runtime.start("s1", "Servo");
      // FIXME - this broke it
      s1.moveTo(0);
      s1.setSpeed(3);
      s1.moveTo(180);
      // Service.sleep(200);
      s1.waitTargetPos();

      InMoov2Head head = (InMoov2Head) Runtime.start("head", "InMoov2Head");
      Long x = head.getLastActivityTime();

      head.setSpeed(3.0, 3.0, 3.0, 3.0, 3.0);
      head.moveTo(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
      head.moveTo(180.0, 180.0, 180.0, 180.0, 180.0, 180.0);

      Runtime.start("webgui", "WebGui");

      // log.info(head.getScript("i01"));

    } catch (Exception e) {
      log.error("main threw", e);
    }
  }

}
