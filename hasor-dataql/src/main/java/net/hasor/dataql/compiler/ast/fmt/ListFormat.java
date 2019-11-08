/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.dataql.compiler.ast.fmt;
import net.hasor.dataql.Option;
import net.hasor.dataql.compiler.ast.AstVisitor;
import net.hasor.dataql.compiler.ast.FormatWriter;
import net.hasor.dataql.compiler.ast.InstVisitorContext;
import net.hasor.dataql.compiler.ast.RouteVariable;
import net.hasor.dataql.compiler.ast.value.ListVariable;
import net.hasor.dataql.compiler.qil.CompilerStack;
import net.hasor.dataql.compiler.qil.InstQueue;
import net.hasor.dataql.compiler.qil.Opcodes;

import java.io.IOException;

/**
 * 函数调用的返回值处理格式，List格式。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class ListFormat implements Format {
    private RouteVariable form;
    private ListVariable  formatTo;

    public ListFormat(RouteVariable form, ListVariable formatTo) {
        this.form = form;
        this.formatTo = formatTo;
    }

    @Override
    public void accept(AstVisitor astVisitor) {
        astVisitor.visitInst(new InstVisitorContext(this) {
            @Override
            public void visitChildren(AstVisitor astVisitor) {
                form.accept(astVisitor);
                formatTo.accept(astVisitor);
            }
        });
    }

    @Override
    public void doFormat(int depth, Option formatOption, FormatWriter writer) throws IOException {
        this.form.doFormat(depth, formatOption, writer);
        writer.write(" => ");
        this.formatTo.doFormat(depth, formatOption, writer);
    }

    @Override
    public void doCompiler(InstQueue queue, CompilerStack stackTree) {
        queue.inst(Opcodes.ASA, "");
        //        this.format.doCompiler(queue, stackTree);
        queue.inst(Opcodes.ASE);
    }
}