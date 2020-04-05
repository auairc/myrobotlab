package org.myrobotlab.serial;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.lang.NotImplementedException;
import org.myrobotlab.logging.LoggerFactory;
import org.slf4j.Logger;

/**
 * @author GroG
 * 
 *         A necessary class to wrap references to rxtxLib in something which
 *         can be dynamically loaded. Without this abstraction any platform
 *         which did was not supported for by rxtx would not be able to use the
 *         Serial service or ports.
 * 
 * 
 */
public class PortQueue extends Port {

  public final static Logger log = LoggerFactory.getLogger(PortQueue.class);

  private BlockingQueue<Integer> in;
  private BlockingQueue<Integer> out;

  public PortQueue(String portName) {
    super(portName);
  }

  public PortQueue(String portName, BlockingQueue<Integer> in, BlockingQueue<Integer> out) {
    super(portName);
    this.in = in;
    this.out = out;
  }

  public int available() throws IOException {
    return in.size();
  }

  @Override
  public List<String> getPortNames() {
    // no "new" ports to contribute in
    // the "pure" Java (non-JNI/JNA) world...
    return new ArrayList<String>();
  }

  public byte[] readBytes() {
    // TODO: make it a byte array queue
    try {
      Integer val = in.take();
      byte[] data = new byte[1];
      data[0] = val.byteValue();
      return data;
    } catch (InterruptedException e) {
      // we don't care, just return if we were interrupted.
      // log.debug("Interrupted PortQueue in readBytes.");
      return null;
    }
  }
  
  public boolean setParams(int rate, int databits, int stopbits, int parity) {
    log.debug("setSerialPortParams {} {} {} {}", rate, databits, stopbits, parity);
    return true;
  }

  @Override
  public void write(int data) throws IOException {
    out.add(data);
    // WOW - PipedOutputStream auto flushes about 1 time every second :P
    // we force flushing here !
  }

  public void write(byte[] data) throws IOException {
    // TODO: is there a more effecient way to do this?
    for (int i = 0; i < data.length; i++) {
      write(data[i]);
    }
  }
  
  public void write(int[] data) throws IOException {
    // TODO: is there a more effecient way to do this?
    for (int i = 0; i < data.length; i++) {
      write(data[i]);
    }
  }

  @Override
  public boolean isHardware() {
    return false;
  }

}
