package org.myrobotlab.service.config;

import java.util.Set;

public class RuntimeConfig extends ServiceConfig {

  // public String id; Not ready to process this ... yet
  public Boolean virtual = null;
  public boolean enableCli = true;
  public String logLevel = "info";
  public String locale;
  public String[] registry;
  // public Set<String> registry;

}
