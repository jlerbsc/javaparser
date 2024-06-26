/*
 * Copyright (C) 2013-2024 The JavaParser Team.
 *
 * This file is part of JavaParser.
 *
 * JavaParser can be used either under the terms of
 * a) the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * b) the terms of the Apache License
 *
 * You should have received a copy of both licenses in LICENCE.LGPL and
 * LICENCE.APACHE. Please refer to those files for details.
 *
 * JavaParser is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 */

package com.github.javaparser.symbolsolver;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import java.util.List;
import org.junit.jupiter.api.Test;

public class Issue2062Test extends AbstractSymbolResolutionTest {

    @Test
    public void test() {

        ParserConfiguration config = new ParserConfiguration();
        config.setSymbolResolver(new JavaSymbolSolver(new ReflectionTypeSolver(false)));
        StaticJavaParser.setConfiguration(config);

        String s = "import java.util.Optional;\n" + "public class Base{\n"
                + "    class Derived extends Base{\n"
                + "    }\n"
                + "    \n"
                + "    public void bar(Optional<Base> o) {\n"
                + "    }\n"
                + "    public void foo() {\n"
                + "        bar(Optional.of(new Derived()));\n"
                + "    }\n"
                + "}";
        CompilationUnit cu = StaticJavaParser.parse(s);
        List<MethodCallExpr> mces = cu.findAll(MethodCallExpr.class);
        assertEquals("bar(Optional.of(new Derived()))", mces.get(0).toString());
        assertEquals("Base.bar(java.util.Optional<Base>)", mces.get(0).resolve().getQualifiedSignature());
    }
}
