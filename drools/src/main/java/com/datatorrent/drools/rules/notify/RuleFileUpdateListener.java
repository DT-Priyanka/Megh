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

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

public abstract class RuleFileUpdateListener<T> implements EventListener
{
  private List<RuleFileUpdateListener<T>> listeners = new ArrayList<>();

  public void addListener(RuleFileUpdateListener<T> listener)
  {
    listeners.add(listener);
  }

  public void removeListener(RuleFileUpdateListener<T> listener)
  {
    listeners.remove(listener);
  }

  public void notifyListeners(T event)
  {
    for (RuleFileUpdateListener<T> listener : listeners) {
      listener.ruleFileUpdated(event);
    }
  }

  public abstract void ruleFileUpdated(T event);
}
