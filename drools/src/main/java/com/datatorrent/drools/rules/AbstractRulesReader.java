package com.datatorrent.drools.rules;

import java.io.IOException;

import org.apache.hadoop.classification.InterfaceStability.Evolving;

@Evolving
public interface AbstractRulesReader
{

  /**
   * Load rules files from directory
   * @param rulesDir
   * @return kieFileSystem
   * @throws IOException
   */
  void loadRulesFromDirectory(String rulesDir) throws IOException;

  /**
   * Load rules files from list of files
   * @param ruleFiles
   * @return kieFileSystem
   * @throws IOException
   */
  void loadRulesFromFiles(String[] ruleFiles) throws IOException;

}
