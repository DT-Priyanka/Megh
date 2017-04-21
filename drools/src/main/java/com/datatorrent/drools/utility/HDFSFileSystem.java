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
package com.datatorrent.drools.utility;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class HDFSFileSystem
{
  /**
   * Get {@link FileSystem} instance of file system having rules
   * @param rulesFile
   * @return fileSystem
   * @throws IOException
   */
  public static FileSystem getFSInstance(Path rulesFile) throws IOException
  {
    Configuration configuration = new Configuration();
    FileSystem fs = FileSystem.newInstance(rulesFile.toUri(), configuration);
    return fs;
  }

}
