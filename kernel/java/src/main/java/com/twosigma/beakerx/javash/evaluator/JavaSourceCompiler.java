/*
 *  Copyright 2014-2017 TWO SIGMA OPEN SOURCE, LLC
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
package com.twosigma.beakerx.javash.evaluator;

import org.abstractmeta.toolbox.compilation.compiler.impl.JavaSourceCompilerImpl;
import org.abstractmeta.toolbox.compilation.compiler.impl.JavaSourceFileObject;
import org.abstractmeta.toolbox.compilation.compiler.registry.JavaFileObjectRegistry;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import java.util.ArrayList;

import static java.lang.Math.*;
import static java.lang.String.format;

public class JavaSourceCompiler extends JavaSourceCompilerImpl {
  private static final char POSITION_CHARACTER = '^';

  @Override
  protected boolean buildDiagnosticMessage(Diagnostic diagnostic, StringBuilder diagnosticBuilder, JavaFileObjectRegistry registry) {
    diagnosticBuilder.append(diagnostic.getMessage(null));
    diagnosticBuilder.append("\n");
    diagnosticBuilder.append(getErrorDetails(diagnostic));
    return diagnostic.getKind().equals(Diagnostic.Kind.ERROR);
  }

  @Override
  protected IllegalStateException createCompilationErrorException(JavaFileObjectRegistry registry, DiagnosticCollector<JavaFileObject> diagnostics) {
    final ArrayList<CompilationException.CompilationError> compilationErrors = new ArrayList<>();
    for (Diagnostic diagnostic : diagnostics.getDiagnostics()) {
      compilationErrors.add(new CompilationException.CompilationError(((int) diagnostic.getLineNumber()), diagnostic.getMessage(null), getErrorDetails(diagnostic)));
    }
    return new CompilationException(format("%d compilation error(s)\n", compilationErrors.size()), compilationErrors);
  }

  private String getErrorDetails(Diagnostic diagnostic) {
    String sourceErrorDetails = "";
    CharSequence sourceCode = getSourceContent(diagnostic);
    if (sourceCode != null) {
      String source = sourceCode.toString();
      int startPosition = (int) diagnostic.getStartPosition();
      int endPosition = (int) diagnostic.getEndPosition();

      if (endPosition < 0 && startPosition < 0) {
        return "";
      }
      if (startPosition < 0) {
        startPosition = endPosition;
      } else if (endPosition < 0) {
        endPosition = startPosition;
      }

      final int snippetStart = getSnippetStart(source, startPosition);
      final int snippetEnd = getSnippetEnd(source, endPosition);

      final String errorLine = source.substring(snippetStart, snippetEnd).replaceAll("\n", " ");;
      startPosition -= snippetStart;
      endPosition -= snippetStart;

      StringBuilder signStringBuilder = getSpacesStringBuilder(errorLine.length());
      signStringBuilder.insert(startPosition, POSITION_CHARACTER);
      if (startPosition != endPosition) {
        signStringBuilder.insert(endPosition, POSITION_CHARACTER);
      }

      sourceErrorDetails = errorLine + "\n" + signStringBuilder.toString() + "\n\n";
    }
    return sourceErrorDetails;
  }

  private int getSnippetEnd(String source, int endPosition) {
    final int lineEndPosition = getPositive(source.indexOf('\n', endPosition));
    final int nextSemicolonPosition = getPositive(source.indexOf(';', endPosition));
    return min(min(lineEndPosition, nextSemicolonPosition), min(source.length(), endPosition + 30));
  }

  private int getSnippetStart(String source, int startPosition) {
    startPosition = abs(startPosition - 1);
    final int lineStartPosition = source.lastIndexOf('\n', startPosition);
    final int previousSemicolonPosition = source.lastIndexOf(';', startPosition) + 1;
    return max(max(lineStartPosition, previousSemicolonPosition), abs(startPosition - 30));
  }

  private StringBuilder getSpacesStringBuilder(int length) {
    final StringBuilder builder = new StringBuilder();
    for(int i = 0; i < length; i++) {
      builder.append(' ');
    }
    return builder;
  }

  private CharSequence getSourceContent(Diagnostic diagnostic) {
    CharSequence sourceContent = null;
    Object source = diagnostic.getSource();
    if (source != null) {
      JavaSourceFileObject sourceFile = JavaSourceFileObject.class.cast(source);
      sourceContent = sourceFile.getCharContent(true);
    }
    return sourceContent;
  }

  private int getPositive(int number) {
    return number >= 0 ? number : Integer.MAX_VALUE;
  }
}
