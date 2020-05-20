/*
 *  Copyright 2017 TWO SIGMA OPEN SOURCE, LLC
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
package com.twosigma.beakerx;

import com.twosigma.beakerx.autocomplete.AutocompleteResult;
import com.twosigma.beakerx.evaluator.Evaluator;
import com.twosigma.beakerx.evaluator.EvaluatorTest;
import com.twosigma.beakerx.evaluator.Hook;
import com.twosigma.beakerx.evaluator.InternalVariable;
import com.twosigma.beakerx.handler.Handler;
import com.twosigma.beakerx.inspect.InspectResult;
import com.twosigma.beakerx.jvm.object.SimpleEvaluationObject;
import com.twosigma.beakerx.kernel.*;
import com.twosigma.beakerx.kernel.comm.Comm;
import com.twosigma.beakerx.kernel.magic.command.MagicCommandType;
import com.twosigma.beakerx.kernel.magic.command.MagicCommandTypesFactory;
import com.twosigma.beakerx.kernel.magic.command.MagicCommandWhichThrowsException;
import com.twosigma.beakerx.kernel.magic.command.MavenJarResolver;
import com.twosigma.beakerx.kernel.magic.command.functionality.*;
import com.twosigma.beakerx.kernel.magic.command.functionality.kernelMagic.*;
import com.twosigma.beakerx.kernel.msg.JupyterMessages;
import com.twosigma.beakerx.kernel.msg.MessageCreator;
import com.twosigma.beakerx.kernel.restserver.BeakerXServer;
import com.twosigma.beakerx.kernel.threads.ExecutionResultSender;
import com.twosigma.beakerx.message.Message;
import org.apache.commons.io.FileUtils;
import org.assertj.core.util.Lists;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import static com.twosigma.beakerx.MessageFactorTest.commMsg;
import static com.twosigma.beakerx.kernel.magic.command.ClasspathAddMvnDepsMagicCommandTest.TEST_MVN_CACHE;
import static java.util.Arrays.asList;
import static java.util.Collections.synchronizedList;

public class KernelTest implements KernelFunctionality {

  private FileServiceMock fileService;
  private List<Message> publishedMessages = synchronizedList(new ArrayList<>());
  private List<Message> sentMessages = synchronizedList(new ArrayList<>());
  private String id;
  private CommRepository commRepository;
  private ExecutionResultSender executionResultSender = new ExecutionResultSender(this);
  public EvaluatorParameters evaluatorParameters;
  private Evaluator evaluator;
  private String code;
  private Path tempFolder;
  private Map<String, MagicKernelManager> magicKernels;

  public MavenJarResolver.ResolverParams mavenResolverParam = null;

  private List<MagicCommandType> magicCommandTypes = null;


  public KernelTest() {
    this("KernelTestId1", new BeakerXCommRepositoryMock());
  }

  public KernelTest(CommRepository commRepository) {
    this("KernelTestId1", commRepository);
  }

  public KernelTest(String id, CommRepository commRepository) {
    this.id = id;
    this.commRepository = commRepository;
    this.fileService = new FileServiceMock();
    initMavenResolverParam();
    initMagicCommands();
    SimpleEvaluationObject value = new SimpleEvaluationObject("ok");
    Message jupyterMessage = commMsg();
    value.setJupyterMessage(jupyterMessage);
    InternalVariable.setValue(value);
    KernelManager.register(this);

  }

  public KernelTest(String id, Evaluator evaluator) {
    this.id = id;
    this.evaluator = evaluator;
    this.commRepository = new BeakerXCommRepositoryMock();
    this.fileService = new FileServiceMock();
    initMavenResolverParam();
    initMagicCommands();
    SimpleEvaluationObject value = new SimpleEvaluationObject("ok");
    Message jupyterMessage = commMsg();
    value.setJupyterMessage(jupyterMessage);
    InternalVariable.setValue(value);
    KernelManager.register(this);
    this.magicKernels = new HashMap<>();
  }

  private void initMavenResolverParam() {
    this.mavenResolverParam = new MavenJarResolver.ResolverParams(
            new File(TEST_MVN_CACHE).getAbsolutePath(),
            getTempFolder().toString() + MavenJarResolver.MVN_DIR,
            true);
  }

  private void initMagicCommands() {
    this.magicCommandTypes = new ArrayList<>();
    this.magicCommandTypes.addAll(Lists.newArrayList(
            new MagicCommandType(JavaScriptMagicCommand.JAVASCRIPT, "", new JavaScriptMagicCommand()),
            new MagicCommandType(JSMagicCommand.JAVASCRIPT, "", new JSMagicCommand()),
            new MagicCommandType(HtmlMagicCommand.HTML, "", new HtmlMagicCommand()),
            new MagicCommandType(HtmlAliasMagicCommand.HTML, "", new HtmlAliasMagicCommand()),
            new MagicCommandType(BashMagicCommand.BASH, "", new BashMagicCommand()),
            new MagicCommandType(LsMagicCommand.LSMAGIC, "", new LsMagicCommand(this.magicCommandTypes)),
            new MagicCommandType(ClasspathAddRepoMagicCommand.CLASSPATH_CONFIG_RESOLVER, "repoName repoURL", new ClasspathAddRepoMagicCommand(this)),
            new MagicCommandType(ClasspathAddJarMagicCommand.CLASSPATH_ADD_JAR, "<jar path>", new ClasspathAddJarMagicCommand(this)),
            new MagicCommandType(ClasspathAddMvnMagicCommand.CLASSPATH_ADD_MVN, "<group name version>",
                    new ClasspathAddMvnMagicCommand(mavenResolverParam, this)),
            new MagicCommandType(ClassPathAddMvnCellMagicCommand.CLASSPATH_ADD_MVN_CELL, "<group name version>",
                    new ClassPathAddMvnCellMagicCommand(mavenResolverParam, this)),
            addClasspathReset(this, this.fileService),
            addDynamic(this),
            addMagicCommandWhichThrowsException(),
            new MagicCommandType(ClasspathShowMagicCommand.CLASSPATH_SHOW, "", new ClasspathShowMagicCommand(this)),
            new MagicCommandType(AddStaticImportMagicCommand.ADD_STATIC_IMPORT, "<classpath>", new AddStaticImportMagicCommand(this)),
            new MagicCommandType(AddImportMagicCommand.IMPORT, "<classpath>", new AddImportMagicCommand(this)),
            new MagicCommandType(UnImportMagicCommand.UNIMPORT, "<classpath>", new UnImportMagicCommand(this)),
            new MagicCommandType(TimeLineModeMagicCommand.TIME_LINE, "", new TimeLineModeMagicCommand(this)),
            new MagicCommandType(TimeCellModeMagicCommand.TIME_CELL, "", new TimeCellModeMagicCommand(this)),
            new MagicCommandType(TimeItLineModeMagicCommand.TIMEIT_LINE, "", new TimeItLineModeMagicCommand(this)),
            new MagicCommandType(TimeItCellModeMagicCommand.TIMEIT_CELL, "", new TimeItCellModeMagicCommand(this)),
            new MagicCommandType(LoadMagicMagicCommand.LOAD_MAGIC, "", new LoadMagicMagicCommand(this)),
            new MagicCommandType(KernelMagicCommand.KERNEL, "", new KernelMagicCommand(this)),
            new MagicCommandType(PythonMagicCommand.PYTHON, "", new PythonMagicCommand(this)),
            new MagicCommandType(ScalaMagicCommand.SCALA, "", new ScalaMagicCommand(this)),
            new MagicCommandType(KotlinMagicCommand.KOTLIN, "", new KotlinMagicCommand(this)),
            new MagicCommandType(JavaMagicCommand.JAVA, "", new JavaMagicCommand(this)),
            new MagicCommandType(GroovyMagicCommand.GROOVY, "", new GroovyMagicCommand(this)),
            new MagicCommandType(ClojureMagicCommand.CLOJURE, "", new ClojureMagicCommand(this)),
            MagicCommandTypesFactory.async(this)
    ));
  }

  private static MagicCommandType addClasspathReset(KernelFunctionality kernel, FileService fileService) {
    return new MagicCommandType(ClasspathResetMagicCommand.CLASSPATH_RESET, "", new ClasspathResetMagicCommand(kernel, fileService));
  }

  private static MagicCommandType addDynamic(KernelFunctionality kernel) {
    return new MagicCommandType(ClasspathAddDynamicMagicCommand.CLASSPATH_ADD_DYNAMIC, "", new ClasspathAddDynamicMagicCommand(kernel));
  }

  private static MagicCommandType addMagicCommandWhichThrowsException() {
    return new MagicCommandType(MagicCommandWhichThrowsException.MAGIC_COMMAND_WHICH_THROWS_EXCEPTION, "", new MagicCommandWhichThrowsException());
  }

  @Override
  public void publish(List<Message> message) {
    this.publishedMessages.addAll(message);
  }

  @Override
  public void send(Message message) {
    this.sentMessages.add(message);
  }

  public String getSessionId() {
    return this.id;
  }

  public Observer getExecutionResultSender() {
    return this.executionResultSender;
  }


  @Override
  public void addComm(String hash, Comm commObject) {
    commRepository.addComm(hash, commObject);
  }

  @Override
  public void removeComm(String hash) {
    commRepository.removeComm(hash);
  }

  @Override
  public Comm getComm(String hash) {
    return commRepository.getComm(hash);
  }

  @Override
  public boolean isCommPresent(String hash) {
    return commRepository.isCommPresent(hash);
  }

  @Override
  public Set<String> getCommHashSet() {
    return commRepository.getCommHashSet();
  }

  @Override
  public void updateEvaluatorParameters(EvaluatorParameters kernelParameters) {
    this.evaluatorParameters = kernelParameters;
  }

  @Override
  public List<Path> addJarsToClasspath(List<PathToJar> paths) {
    return this.evaluator.addJarsToClasspath(paths);
  }

  @Override
  public Classpath getClasspath() {
    return this.evaluator.getClasspath();
  }

  @Override
  public Imports getImports() {
    return this.evaluator.getImports();
  }

  @Override
  public AddImportStatus addImport(ImportPath anImport) {
    return this.evaluator.addImport(anImport);
  }

  @Override
  public void removeImport(ImportPath anImport) {
    this.evaluator.removeImport(anImport);
  }

  @Override
  public List<MagicCommandType> getMagicCommandTypes() {
    return magicCommandTypes;
  }

  @Override
  public Path getTempFolder() {
    if (this.tempFolder == null) {
      this.tempFolder = tempFolder();
    }
    return this.tempFolder;
  }

  @Override
  public Path getCacheFolder() {
    return getTempFolder();
  }

  @Override
  public Class<?> loadClass(String clazzName) throws ClassNotFoundException {
    return null;
  }

  @Override
  public boolean checkIfClassExistsInClassloader(String clazzName) {
    return false;
  }

  @Override
  public void registerMagicCommandType(MagicCommandType magicCommandType) {
    magicCommandTypes.add(magicCommandType);
  }

  @Override
  public String getOutDir() {
    return "";
  }

  private Path tempFolder() {
    if (this.evaluator == null) {
      return EvaluatorTest.getTestTempFolderFactory().createTempFolder();
    } else {
      return evaluator.getTempFolder();
    }
  }

  public EvaluatorParameters getEvaluatorParameters() {
    return evaluatorParameters;
  }

  public List<Message> getPublishedMessages() {
    return copy(this.publishedMessages);
  }

  public List<Message> getSentMessages() {
    return copy(this.sentMessages);
  }

  private List<Message> copy(List<Message> list) {
    return asList(list.toArray(new Message[0]));
  }

  public void clearPublishedMessages() {
    this.publishedMessages = synchronizedList(new ArrayList<>());
  }

  public void clearSentMessages() {
    this.sentMessages = synchronizedList(new ArrayList<>());
  }

  public void clearMessages() {
    clearSentMessages();
    clearPublishedMessages();
  }

  @Override
  public void cancelExecution(GroupName groupName) {
  }

  @Override
  public void killAllThreads() {

  }

  @Override
  public Handler<Message> getHandler(JupyterMessages type) {
    return null;
  }

  @Override
  public void run() {

  }

  @Override
  public TryResult executeCode(String code, SimpleEvaluationObject seo) {
    this.code = code;
    return TryResult.createResult(this.code);
  }

  @Override
  public TryResult executeCode(String code, SimpleEvaluationObject seo, ExecutionOptions executionOptions) {
    return executeCode(code, seo);
  }

  public String getCode() {
    return code;
  }

  @Override
  public AutocompleteResult autocomplete(String code, int cursorPos) {
    return this.evaluator.autocomplete(code, cursorPos);
  }

  @Override
  public InspectResult inspect(String code, int cursorPos) {
    return this.evaluator.inspect(code, cursorPos);
  }

  @Override
  public void sendBusyMessage(Message message) {
    Message busyMessage = MessageCreator.createBusyMessage(message);
    publish(Collections.singletonList(busyMessage));
  }

  @Override
  public void sendIdleMessage(Message message) {
    Message idleMessage = MessageCreator.createIdleMessage(message);
    publish(Collections.singletonList(idleMessage));
  }

  public void exit() {
    if (evaluator != null) {
      evaluator.exit();
    } else {
      removeTempFolder();
    }
    if (magicKernels != null) {
      for (MagicKernelManager manager : magicKernels.values()) {
        manager.exit();
      }
    }
  }

  private void removeTempFolder() {
    try {
      FileUtils.deleteDirectory(new File(getTempFolder().toString()));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void registerCancelHook(Hook hook) {
  }

  @Override
  public PythonEntryPoint getPythonEntryPoint(String kernelName) throws NoSuchKernelException {
    MagicKernelManager manager = magicKernels.get(kernelName);
    if (manager == null) {
      manager = new MagicKernelManager(kernelName, "kernelTestContext");
      magicKernels.put(kernelName, manager);
    }
    return manager.getPythonEntryPoint();
  }

  @Override
  public MagicKernelManager getManagerByCommId(String commId) {
    return null;
  }

  @Override
  public void addCommIdManagerMapping(String commId, String kernel) {
  }

  @Override
  public void putEvaluationInToBackground() {

  }

  @Override
  public BeakerXServer getBeakerXServer() {
    return BeakerXServerMock.create();
  }

  public FileServiceMock getFileService() {
    return fileService;
  }
}
