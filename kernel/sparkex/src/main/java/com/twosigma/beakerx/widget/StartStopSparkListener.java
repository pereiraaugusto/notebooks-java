/*
 *  Copyright 2018 TWO SIGMA OPEN SOURCE, LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.twosigma.beakerx.widget;

import org.apache.spark.scheduler.SparkListener;
import org.apache.spark.scheduler.SparkListenerApplicationEnd;

public class StartStopSparkListener extends SparkListener {

  public static final String START_STOP_SPARK_LISTENER = "com.twosigma.beakerx.widget.StartStopSparkListener";

  public StartStopSparkListener() {
  }

  @Override
  public void onApplicationEnd(SparkListenerApplicationEnd applicationEnd) {
    super.onApplicationEnd(applicationEnd);
    SparkUIApi sparkUI = SparkVariable.getSparkUI();
    sparkUI.applicationEnd();
  }
}