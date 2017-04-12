package com.datatorrent.drools.rules;

import java.io.IOException;
import java.io.InputStream;

import org.drools.core.io.impl.InputStreamResource;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class DroolsRulesReader implements AbstractRulesReader
{
  private static Logger LOG = LoggerFactory.getLogger(DroolsRulesReader.class);

  @Override
  public void loadRulesFromDirectory(String rulesDir) throws IOException
  {
    KieFileSystem kieFileSystem = getKieFileSystem();
    FileSystem sourceFileSystem = getFSInstance(rulesDir);

    Path rulesDirPath = new Path(rulesDir);
    loadRulesInKieFileSystem(kieFileSystem, sourceFileSystem, sourceFileSystem.listStatus(rulesDirPath));
  }

  @Override
  public void loadRulesFromFiles(String[] ruleFiles) throws IOException
  {
    KieFileSystem kieFileSystem = getKieFileSystem();
    FileSystem sourceFileSystem = getFSInstance(ruleFiles[0]);

    FileStatus[] rulesfileStatus = new FileStatus[ruleFiles.length];
    int i = 0;
    for (String ruleFile : ruleFiles) {
      rulesfileStatus[i++] = sourceFileSystem.getFileStatus(new Path(ruleFile));
    }

    loadRulesInKieFileSystem(kieFileSystem, sourceFileSystem, rulesfileStatus);
  }

  /**
   * Load rules from given source {@link FileSystem} in {@link KieFileSystem}
   * 
   * @param kieFileSystem
   * @param sourceFileSystem
   * @param ruleFiles
   * @throws IOException
   */
  protected void loadRulesInKieFileSystem(KieFileSystem kieFileSystem, FileSystem sourceFileSystem,
      FileStatus[] ruleFiles) throws IOException
  {
    KieServices kieServices = KieServices.Factory.get();
    for (FileStatus rulesFileStatus : ruleFiles) {
      if (rulesFileStatus.isFile()) {
        InputStream rulesFileStream = sourceFileSystem.open(rulesFileStatus.getPath());
        kieFileSystem.write("src/main/resources/" + rulesFileStatus.getPath().getName(),
            new InputStreamResource(rulesFileStream));
      }
    }

    KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem).buildAll();

    if (kieBuilder.getResults().hasMessages(Message.Level.ERROR)) {
      LOG.error("Error loading rules. \n" + kieBuilder.getResults().getMessages());
      throw new RuntimeException("Error loading rules.");
    }
  }

  protected KieFileSystem getKieFileSystem()
  {
    KieServices kieServices = KieServices.Factory.get();
    KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
    return kieFileSystem;
  }

  /**
   * Get {@link FileSystem} instance of file system having rules
   * @param rulesFile
   * @return fileSystem
   * @throws IOException
   */
  protected FileSystem getFSInstance(String rulesFile) throws IOException
  {
    Path filePath = new Path(rulesFile);
    Configuration configuration = new Configuration();
    FileSystem fs = FileSystem.newInstance(filePath.toUri(), configuration);
    return fs;
  }
}
