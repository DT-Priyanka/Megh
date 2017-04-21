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

import java.util.List;

/**
 * Event to notify addition/deletion/modification of rules file on hdfs.
 *
 */
public class Event
{

  private EventType eventType;
  private List<String> filesPath;

  public static enum EventType
  {
    CREATED, DELETED, MODIFIED;
  }

  public Event(EventType eventType, List<String> filesPath)
  {
    this.eventType = eventType;
    this.filesPath = filesPath;
  }

  public List<String> getFilesPath()
  {
    return filesPath;
  }

  public EventType getEventType()
  {
    return eventType;
  }

  @Override
  public String toString()
  {
    return "Event [eventType=" + eventType + ", filesPath=" + filesPath + "]";
  }
}
