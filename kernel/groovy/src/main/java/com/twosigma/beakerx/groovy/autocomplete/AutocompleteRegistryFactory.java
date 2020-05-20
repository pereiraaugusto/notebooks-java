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
package com.twosigma.beakerx.groovy.autocomplete;

import com.twosigma.beakerx.autocomplete.AutocompleteCandidate;
import com.twosigma.beakerx.autocomplete.AutocompleteRegistry;
import com.twosigma.beakerx.autocomplete.ClassUtils;

import java.util.List;

import static com.twosigma.beakerx.evaluator.Evaluator.BEAKER_VARIABLE_NAME;

public class AutocompleteRegistryFactory {

  public static AutocompleteRegistry createRegistry(GroovyClasspathScanner cps) {
    AutocompleteRegistry registry = AutocompleteRegistryFactory.create(GroovyCompletionTypes.NUM_TYPES);
    for (String pkg : cps.getPackages()) {
      String[] pkgv = pkg.split("\\.");
      AutocompleteCandidate c = new AutocompleteCandidate(GroovyCompletionTypes.PACKAGE_NAME, pkgv);
      registry.addCandidate(c);
      List<String> cls = cps.getClasses(pkg);
      if (cls != null && !cls.isEmpty()) {
        c = new AutocompleteCandidate(GroovyCompletionTypes.FQ_TYPE, pkgv);
        AutocompleteCandidate l = c;
        while (l.hasChildren()) {
          l = l.getChildrens().get(0);
        }
        for (String cl : cls) {
          l.addChildren(new AutocompleteCandidate(GroovyCompletionTypes.FQ_TYPE, cl));
        }
        registry.addCandidate(c);
      }
    }
    return registry;
  }

  private static AutocompleteRegistry create(int num) {
    AutocompleteRegistry registry = new AutocompleteRegistry(num);
    setup(registry);
    return registry;
  }

  private static void setup(AutocompleteRegistry r) {
    AutocompleteCandidate c;

    c = new AutocompleteCandidate(GroovyCompletionTypes.INITIAL, "package");
    r.addCandidate(c);

    c = new AutocompleteCandidate(GroovyCompletionTypes.TOPLEVEL, "import");
    r.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.TOPLEVEL, "class");
    r.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.TOPLEVEL, "enum");
    r.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.TOPLEVEL, "interface");
    r.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.TOPLEVEL, "def");
    r.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.TOPLEVEL, "assert");
    r.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.TOPLEVEL, "if");
    r.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.TOPLEVEL, "switch");
    r.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.TOPLEVEL, "while");
    r.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.TOPLEVEL, "for");
    r.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.TOPLEVEL, "try");
    r.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.TOPLEVEL, "extends");
    r.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.TOPLEVEL, "implements");
    r.addCandidate(c);

    c = new AutocompleteCandidate(GroovyCompletionTypes.CLASSLEVEL, "extends");
    r.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.CLASSLEVEL, "implements");
    r.addCandidate(c);

    c = new AutocompleteCandidate(GroovyCompletionTypes.STDFUNCS, "any");
    r.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.STDFUNCS, "collect");
    r.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.STDFUNCS, "each");
    r.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.STDFUNCS, "eachWithIndex");
    r.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.STDFUNCS, "every");
    r.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.STDFUNCS, "create");
    r.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.STDFUNCS, "findAll");
    r.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.STDFUNCS, "findIndexOf");
    r.addCandidate(c);

    c = new AutocompleteCandidate(GroovyCompletionTypes.TYPE, "int");
    r.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.TYPE, "float");
    r.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.TYPE, "char");
    r.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.TYPE, "byte");
    r.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.TYPE, "void");
    r.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.TYPE, "boolean");
    r.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.TYPE, "short");
    r.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.TYPE, "long");
    r.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.TYPE, "double");
    r.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.TYPE, "Boolean");
    r.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.TYPE, "Byte");
    r.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.TYPE, "Character");
    r.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.TYPE, "Double");
    r.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.TYPE, "Float");
    r.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.TYPE, "Integer");
    r.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.TYPE, "Long");
    r.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.TYPE, "Math");
    r.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.TYPE, "Number");
    r.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.TYPE, "Object");
    r.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.TYPE, "Package");
    r.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.TYPE, "Process");
    r.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.TYPE, "ProcessBuilder");
    r.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.TYPE, "Runtime");
    r.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.TYPE, "RuntimePermission");
    r.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.TYPE, "SecurityManager");
    r.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.TYPE, "Short");
    r.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.TYPE, "StackTraceElement");
    r.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.TYPE, "StrictMath");
    r.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.TYPE, "String");
    r.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.TYPE, "StringBuffer");
    r.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.TYPE, "StringBuilder");
    r.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.TYPE, "System");
    r.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.TYPE, "Thread");
    r.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.TYPE, "ThreadGroup");
    r.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.TYPE, "Throwable");
    r.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.TYPE, "Void");
    r.addCandidate(c);

    c = new AutocompleteCandidate(GroovyCompletionTypes.TYPE, "NamespaceClient");
    r.addCandidate(c);

    c = new AutocompleteCandidate(GroovyCompletionTypes.NEW, "new");
    r.addCandidate(c);
  }


  public static void setup(ClassUtils cu, AutocompleteRegistry registry) {
    cu.defineClassShortName("Boolean", "java.lang.Boolean");
    cu.defineClassShortName("Byte", "java.lang.Byte");
    cu.defineClassShortName("Character", "java.lang.Character");
    cu.defineClassShortName("Double", "java.lang.Double");
    cu.defineClassShortName("Exception", "java.lang.Exception");
    cu.defineClassShortName("Float", "java.lang.Float");
    cu.defineClassShortName("Integer", "java.lang.Integer");
    cu.defineClassShortName("Long", "java.lang.Long");
    cu.defineClassShortName("Math", "java.lang.Math");
    cu.defineClassShortName("Number", "java.lang.Number");
    cu.defineClassShortName("Object", "java.lang.Object");
    cu.defineClassShortName("Package", "java.lang.Package");
    cu.defineClassShortName("Process", "java.lang.Process");
    cu.defineClassShortName("ProcessBuilder", "java.lang.ProcessBuilder");
    cu.defineClassShortName("Runtime", "java.lang.Runtime");
    cu.defineClassShortName("RuntimePermission", "java.lang.RuntimePermission");
    cu.defineClassShortName("SecurityManager", "java.lang.SecurityManager");
    cu.defineClassShortName("Short", "java.lang.Short");
    cu.defineClassShortName("StackTraceElement", "java.lang.StackTraceElement");
    cu.defineClassShortName("StrictMath", "java.lang.StrictMath");
    cu.defineClassShortName("String", "java.lang.String");
    cu.defineClassShortName("StringBuffer", "java.lang.StringBuffer");
    cu.defineClassShortName("StringBuilder", "java.lang.StringBuilder");
    cu.defineClassShortName("System", "java.lang.System");
    cu.defineClassShortName("Thread", "java.lang.Thread");
    cu.defineClassShortName("ThreadGroup", "java.lang.ThreadGroup");
    cu.defineClassShortName("Throwable", "java.lang.Throwable");
    cu.defineClassShortName("Void", "java.lang.Void");
    cu.defineClassShortName("NamespaceClient", "com.twosigma.beaker.NamespaceClient");

    AutocompleteCandidate c;
    c = new AutocompleteCandidate(GroovyCompletionTypes.NAME, "Boolean");
    registry.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.NAME, "Byte");
    registry.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.NAME, "Character");
    registry.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.NAME, "Double");
    registry.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.NAME, "Exception");
    registry.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.NAME, "Float");
    registry.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.NAME, "Integer");
    registry.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.NAME, "Long");
    registry.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.NAME, "Number");
    registry.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.NAME, "Object");
    registry.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.NAME, "Package");
    registry.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.NAME, "Process");
    registry.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.NAME, "ProcessBuilder");
    registry.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.NAME, "Runtime");
    registry.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.NAME, "RuntimePermission");
    registry.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.NAME, "SecurityManager");
    registry.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.NAME, "Short");
    registry.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.NAME, "StackTraceElement");
    registry.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.NAME, "StrictMath");
    registry.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.NAME, "String");
    registry.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.NAME, "StringBuffer");
    registry.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.NAME, "StringBuilder");
    registry.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.NAME, "System");
    registry.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.NAME, "Thread");
    registry.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.NAME, "ThreadGroup");
    registry.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.NAME, "Throwable");
    registry.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.NAME, "Void");
    registry.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.NAME, "NamespaceClient");
    registry.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.NAME, "beaker");
    registry.addCandidate(c);
  }

  public static void addDefaultImports(ClassUtils cu, AutocompleteRegistry registry, List<String> imports, GroovyClasspathScanner cps) {
    for (String imp : imports) {
      // this imports using '*'
      if (imp.endsWith(".*")) {
        String st = imp.substring(0, imp.length() - 2);
        String[] txtv = st.split("\\.");
        AutocompleteCandidate c = new AutocompleteCandidate(GroovyCompletionTypes.PACKAGE_NAME, txtv);
        registry.addCandidate(c);
        List<String> cls = cps.getClasses(st);
        if (cls != null) {
          c = new AutocompleteCandidate(GroovyCompletionTypes.FQ_TYPE, txtv);
          AutocompleteCandidate l = c.findLeaf();
          for (String s : cls) {
            l.addChildren(new AutocompleteCandidate(GroovyCompletionTypes.CUSTOM_TYPE, s));
            registry.addCandidate(new AutocompleteCandidate(GroovyCompletionTypes.CUSTOM_TYPE, s));
            cu.defineClassShortName(s, st + "." + s);
          }
          registry.addCandidate(c);
        }
      } else {
        createImportAutocompleteCandidate(cu, registry, imp);
      }
    }
  }

  public static void createImportAutocompleteCandidate(ClassUtils cu, AutocompleteRegistry registry, String imp) {
    String[] txtv = imp.split("\\.");
    AutocompleteCandidate c = new AutocompleteCandidate(GroovyCompletionTypes.PACKAGE_NAME, txtv, txtv.length - 1);
    registry.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.FQ_TYPE, txtv);
    registry.addCandidate(c);
    c = new AutocompleteCandidate(GroovyCompletionTypes.CUSTOM_TYPE, txtv[txtv.length - 1]);
    registry.addCandidate(c);
    cu.defineClassShortName(txtv[txtv.length - 1], imp);
  }


  public static void moreSetup(ClassUtils cu) {
    cu.defineVariable(BEAKER_VARIABLE_NAME, "com.twosigma.beakerx.NamespaceClient");
  }

}
