package com.datatorrent.drools.rules;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.kie.api.KieServices;
import org.kie.api.definition.KiePackage;
import org.kie.api.runtime.KieContainer;

import org.apache.commons.io.FileUtils;

public class DroolsRulesReaderTest
{

  public static class TestMeta extends TestWatcher
  {
    private String rulesDirectory;
    private AbstractRulesReader underTest;
    private String goldRuleFileName = "rulesForGold.drl";
    private String diamondRuleFileName = "rulesForDiamond.drl";

    @Override
    protected void starting(Description description)
    {
      String methodName = description.getMethodName();
      String className = description.getClassName();
      this.rulesDirectory = "target/" + className + "/" + methodName + "/rules";

      underTest = new DroolsRulesReader();
      try {
        createRulesFiles();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    @Override
    protected void finished(Description description)
    {
      FileUtils.deleteQuietly(new File("target/" + description.getClassName()));
    }

    private void createRulesFiles() throws IOException
    {
      ClassLoader classLoader = getClass().getClassLoader();
      File file = new File(classLoader.getResource("rules/" + diamondRuleFileName).getFile());
      File rulesFile = new File(rulesDirectory, diamondRuleFileName);
      FileUtils.copyFile(file, rulesFile);
      file = new File(classLoader.getResource("rules/" + goldRuleFileName).getFile());
      rulesFile = new File(rulesDirectory, goldRuleFileName);
      FileUtils.copyFile(file, rulesFile);
    }
  }

  @Rule
  public TestMeta testMeta = new TestMeta();

  @Test
  public void testLoadRulesFromDirectory() throws IOException
  {
    testMeta.underTest.loadRulesFromDirectory(testMeta.rulesDirectory);

    KieServices kieServices = KieServices.Factory.get();
    KieContainer kieContainer = kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
    Collection<KiePackage> packages = kieContainer.getKieBase().getKiePackages();
    Assert.assertEquals("testRules", packages.iterator().next().getName());

    Iterator<org.kie.api.definition.rule.Rule> rulesItr = packages.iterator().next().getRules().iterator();
    List<String> loadedRules = new ArrayList<>();
    while (rulesItr.hasNext()) {
      loadedRules.add(rulesItr.next().getName());
    }
    Assert.assertEquals(2, loadedRules.size());
    Assert.assertTrue(loadedRules.contains("Offer for Gold"));
    Assert.assertTrue(loadedRules.contains("Offer for Diamond"));
  }

  @Test
  public void loadRulesFromSingleFile() throws IOException
  {
    String goldRulesFile = testMeta.rulesDirectory + File.separator + testMeta.goldRuleFileName;
    testMeta.underTest.loadRulesFromFiles(new String[] { goldRulesFile });

    KieServices kieServices = KieServices.Factory.get();
    KieContainer kieContainer = kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
    Collection<KiePackage> packages = kieContainer.getKieBase().getKiePackages();
    Iterator<org.kie.api.definition.rule.Rule> rulesItr = packages.iterator().next().getRules().iterator();
    List<String> loadedRules = new ArrayList<>();
    while (rulesItr.hasNext()) {
      loadedRules.add(rulesItr.next().getName());
    }
    Assert.assertEquals("testRules", packages.iterator().next().getName());
    Assert.assertEquals(1, loadedRules.size());
    Assert.assertTrue(loadedRules.contains("Offer for Gold"));
  }

  @Test
  public void loadRulesFromMultipleFiles() throws IOException
  {
    String goldRulesFile = testMeta.rulesDirectory + File.separator + testMeta.goldRuleFileName;
    String diamondRulesFile = testMeta.rulesDirectory + File.separator + testMeta.diamondRuleFileName;
    testMeta.underTest.loadRulesFromFiles(new String[] { goldRulesFile, diamondRulesFile });

    KieServices kieServices = KieServices.Factory.get();
    KieContainer kieContainer = kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
    Collection<KiePackage> packages = kieContainer.getKieBase().getKiePackages();
    Iterator<org.kie.api.definition.rule.Rule> rulesItr = packages.iterator().next().getRules().iterator();
    List<String> loadedRules = new ArrayList<>();
    while (rulesItr.hasNext()) {
      loadedRules.add(rulesItr.next().getName());
    }
    Assert.assertEquals(2, loadedRules.size());
    Assert.assertTrue(loadedRules.contains("Offer for Gold"));
    Assert.assertTrue(loadedRules.contains("Offer for Diamond"));
  }

  @Test(expected = RuntimeException.class)
  public void testLoadingErrorRulesFile() throws IOException
  {
    String drl = "package org.drools.compiler\n" + "rule R1 when\n" + "   $m : Message()\n" + "then\n" + "end\n";

    File rulesFile = new File(testMeta.rulesDirectory, "errorRules.drl");
    FileUtils.write(rulesFile, drl);

    testMeta.underTest.loadRulesFromFiles(new String[] { rulesFile.getAbsolutePath() });
  }
}
