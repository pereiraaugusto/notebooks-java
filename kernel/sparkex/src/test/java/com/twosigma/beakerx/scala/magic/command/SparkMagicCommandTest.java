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
package com.twosigma.beakerx.scala.magic.command;

import com.twosigma.beakerx.KernelTest;
import com.twosigma.beakerx.TryResult;
import com.twosigma.beakerx.kernel.Code;
import com.twosigma.beakerx.kernel.KernelFunctionality;
import com.twosigma.beakerx.kernel.magic.command.MagicCommandExecutionParam;
import com.twosigma.beakerx.kernel.magic.command.outcome.MagicCommandOutcomeItem;
import com.twosigma.beakerx.message.Message;
import com.twosigma.beakerx.widget.SingleSparkSession;
import com.twosigma.beakerx.widget.SparkEngine;
import com.twosigma.beakerx.widget.SparkUI;
import com.twosigma.beakerx.widget.SparkUIApi;
import com.twosigma.beakerx.widget.SparkUiDefaults;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.SparkSession;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.twosigma.beakerx.MessageFactorTest.commMsg;
import static com.twosigma.beakerx.widget.SparkUI.ONE_SPARK_SESSION_MSG_ERROR;
import static com.twosigma.beakerx.widget.TestWidgetUtils.stateContains;
import static org.assertj.core.api.Assertions.assertThat;

public class SparkMagicCommandTest {

  private SparkMagicCommand sparkMagicCommand;
  public SparkUI sparkUI;
  private KernelTest kernel;
  private SingleSparkSession singleSparkSession;

  @Before
  public void setUp() {
    singleSparkSession = new SparkMagicCommand.SingleSparkSessionImpl();
    SparkUI.SparkUIFactory sparkUIFactory = createSparkUIFactory(singleSparkSession);
    kernel = new KernelTest();
    sparkMagicCommand = new SparkMagicCommand(kernel, sparkUIFactory);
  }

  @Test
  public void connectToSpark() {
    //given
    //when
    MagicCommandOutcomeItem execute = createSparkUiAndConnectToSession();
    //then
    assertThat(execute.getStatus()).isEqualTo(MagicCommandOutcomeItem.Status.OK);
  }

  @Test
  public void returnErrorWhenConnectToExistingSparkSession() {
    //given
    createSparkUiAndConnectToSession();
    kernel.clearMessages();
    //when
    connectToSparkSecondTime();
    //then
    List<Message> messages = stateContains(kernel.getPublishedMessages(), "value", ONE_SPARK_SESSION_MSG_ERROR);
    assertThat(messages.size()).isEqualTo(1);
  }

  private MagicCommandOutcomeItem connectToSparkSecondTime() {
    Code code2 = Code.createCode("", new ArrayList<>(), new ArrayList<>(), commMsg());
    MagicCommandExecutionParam param2 = new MagicCommandExecutionParam("", "", 2, code2, true);
    MagicCommandOutcomeItem execute = sparkMagicCommand.execute(param2);
    sparkUI.getConnectButton().onClick(new HashMap(), commMsg());
    return execute;
  }

  private MagicCommandOutcomeItem createSparkUiAndConnectToSession() {
    Code code = Code.createCode("%%spark", new ArrayList<>(), new ArrayList<>(), commMsg());
    MagicCommandExecutionParam param = new MagicCommandExecutionParam("%%spark", "", 1, code, true);
    MagicCommandOutcomeItem execute = sparkMagicCommand.execute(param);
    assertThat(execute.getStatus()).isEqualTo(MagicCommandOutcomeItem.Status.OK);
    assertThat(singleSparkSession.isActive()).isFalse();
    sparkUI.getConnectButton().onClick(new HashMap(), commMsg());
    assertThat(singleSparkSession.isActive()).isTrue();
    return execute;
  }

  private SparkUI.SparkUIFactory createSparkUIFactory(SingleSparkSession singleSparkSession) {
    return new SparkUI.SparkUIFactory() {

      private SparkUI.SparkUIFactoryImpl factory = new SparkUI.SparkUIFactoryImpl(new SparkManagerFactoryTest(), new SparkUiDefaultsImplTest(), singleSparkSession);

      @Override
      public SparkUI create(SparkSession.Builder builder) {
        sparkUI = factory.create(builder);
        return sparkUI;
      }
    };
  }


  static class SparkManagerFactoryTest implements SparkEngine.SparkEngineFactory {

    @Override
    public SparkEngine create(SparkSession.Builder sparkSessionBuilder) {
      return new SparkEngine() {
        @Override
        public TryResult configure(KernelFunctionality kernel, SparkUIApi spark, Message parentMessage) {
          return TryResult.createResult("ok");
        }

        @Override
        public SparkSession getOrCreate() {
          return null;
        }

        @Override
        public SparkConf getSparkConf() {
          return new SparkConf();
        }

        @Override
        public String getSparkAppId() {
          return "sparkAppId1";
        }

        @Override
        public Map<String, String> getAdvanceSettings() {
          return new HashMap<>();
        }

        @Override
        public String getSparkUiWebUrl() {
          return "";
        }

        @Override
        public String getSparkMasterUrl() {
          return "";
        }

        @Override
        public String sparkVersion() {
          return null;
        }
      };
    }
  }

  static class SparkUiDefaultsImplTest implements SparkUiDefaults {


    @Override
    public void saveSparkConf(List<Map<String, Object>> sparkConf) {

    }

    @Override
    public void loadDefaults(SparkSession.Builder builder) {

    }

    @Override
    public List<Map<String, Object>> getProfiles() {
      return null;
    }

    @Override
    public Map<String, Object> getProfileByName(String name) {
      return null;
    }

    @Override
    public void removeSparkConf(String profileName) {

    }

    @Override
    public void loadProfiles() {

    }

    @Override
    public void saveProfile(Map<String, Object> profile) {

    }

    @Override
    public List<String> getProfileNames() {
      return null;
    }

    @Override
    public void saveProfileName(String profileName) {

    }

    @Override
    public String getCurrentProfileName() {
      return null;
    }

  }


}