/*
 * SonarQube CSS Plugin
 * Copyright (C) 2013-2016 Tamas Kende and David RACODON
 * mailto: kende.tamas@gmail.com and david.racodon@gmail.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.css.model.function;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.sonar.css.model.StandardCssObject;
import org.sonar.css.model.atrule.standard.Annotation;

public class StandardFunctionFactory {

  private StandardFunctionFactory() {
  }

  public static StandardFunction createStandardFunction(String functionName) {
    try {
      String className = getClassNameFromFunctionName(functionName);
      Class clazz = Class.forName("org.sonar.css.model.function.standard." + className);
      return (StandardFunction) clazz.newInstance();
    } catch (ClassNotFoundException e) {
      return new UnknownFunction(functionName);
    } catch (IllegalAccessException | InstantiationException e) {
      throw new IllegalStateException("CSS function for '" + functionName + "' cannot be created.", e);
    }
  }

  public static List<StandardCssObject> createAll() {
    try {
      List<StandardCssObject> standardFunctions = new ArrayList<>();
      ImmutableSet<ClassPath.ClassInfo> classInfos = ClassPath.from(Annotation.class.getClassLoader()).getTopLevelClasses("org.sonar.css.model.function.standard");
      for (ClassPath.ClassInfo classInfo : classInfos) {
        if (!"org.sonar.css.model.function.standard.package-info".equals(classInfo.getName())) {
          standardFunctions.add((StandardFunction) Class.forName(classInfo.getName()).newInstance());
        }
      }
      return standardFunctions;
    } catch (ClassNotFoundException | IOException | InstantiationException | IllegalAccessException e) {
      throw new IllegalStateException("CSS functions full list cannot be created.", e);
    }
  }

  private static String getClassNameFromFunctionName(String functionName) {
    return CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_CAMEL, functionName.toLowerCase(Locale.ENGLISH));
  }

}