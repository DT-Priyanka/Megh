/**
 * Copyright (c) 2017 DataTorrent, Inc. ALL Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.datatorrent.drools.rules.notify;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;

import com.datatorrent.drools.rules.notify.Event.EventType;
import com.datatorrent.drools.utility.HDFSFileSystem;

public class RuleFileUpdateWatcher implements Runnable
{
  private static final Logger LOG = LoggerFactory.getLogger(RuleFileUpdateWatcherTest.class);
  private static final long DEFAULT_POLL_INTERVAL_SEC = 60;
  private long pollIntervalSecs = DEFAULT_POLL_INTERVAL_SEC;
  private HDFSRuleFileUpdateListener listener = new HDFSRuleFileUpdateListener();
  private String rulesDir;
  private FileSystem fs;
  protected transient AtomicReference<Throwable> atomicThrowable;
  protected Map<String, Long> ruleFiles = new HashMap<>();

  public RuleFileUpdateWatcher(String rulesDir)
  {
    this.rulesDir = rulesDir;
    ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    service.scheduleAtFixedRate(this, 0, pollIntervalSecs, TimeUnit.SECONDS);

    try {
      fs = HDFSFileSystem.getFSInstance(new Path(rulesDir));
    } catch (IOException e) {
      throw new RuntimeException("Error Initializing file system to load rules.", e);
    }
    atomicThrowable = new AtomicReference<>();
  }

  @Override
  public void run()
  {
    try {
      Map<String, Long> currentRuleFiles = new HashMap<>();
      RemoteIterator<LocatedFileStatus> itr = fs.listFiles(new Path(rulesDir), true);
      while (itr.hasNext()) {
        LocatedFileStatus fileStatus = itr.next();
        currentRuleFiles.put(fileStatus.getPath().toString(), fileStatus.getModificationTime());
      }
      notifyListenersForFileChanges(currentRuleFiles);
    } catch (IOException e) {
      LOG.error("Error reloading rules. ", e);
      atomicThrowable.set(e);
    }
  }

  private void notifyListenersForFileChanges(Map<String, Long> currentRuleFiles)
  {
    List<String> addedFiles = new ArrayList<>();
    List<String> modifiedFiles = new ArrayList<>();
    List<String> deletedFiles = new ArrayList<>();

    for (String ruleFile : currentRuleFiles.keySet()) {
      if (!ruleFiles.containsKey(ruleFile)) {
        addedFiles.add(ruleFile);
      } else if (ruleFiles.get(ruleFile) != currentRuleFiles.get(ruleFile)) {
        modifiedFiles.add(ruleFile);
      }
      ruleFiles.remove(ruleFile); //remove discovered files
    }
    for (String deletedRuleFile : ruleFiles.keySet()) {
      deletedFiles.add(deletedRuleFile);
    }
    ruleFiles.clear();
    ruleFiles.putAll(currentRuleFiles);

    if (addedFiles.size() > 0) {
      listener.notifyListeners(new Event(EventType.CREATED, addedFiles));
    }
    if (modifiedFiles.size() > 0) {
      listener.notifyListeners(new Event(EventType.MODIFIED, modifiedFiles));
    }
    if (deletedFiles.size() > 0) {
      listener.notifyListeners(new Event(EventType.DELETED, deletedFiles));
    }
  }

  public long getPollIntervalSecs()
  {
    return pollIntervalSecs;
  }

  public void setPollIntervalSecs(long pollIntervalSecs)
  {
    this.pollIntervalSecs = pollIntervalSecs;
  }
}
