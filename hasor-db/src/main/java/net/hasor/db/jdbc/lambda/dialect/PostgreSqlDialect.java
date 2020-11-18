/*
 * Copyright 2002-2010 the original author or authors.
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
package net.hasor.db.jdbc.lambda.dialect;
import net.hasor.db.jdbc.mapping.FieldMeta;
import net.hasor.db.jdbc.mapping.TableMeta;
import net.hasor.utils.StringUtils;

/**
 * PostgreSQL 对象名有大小写敏感不敏感的问题
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public class PostgreSqlDialect implements SqlDialect {
    @Override
    public String buildSelect(FieldMeta fieldMeta) {
        String columnName = fieldMeta.getColumnName();
        String aliasName = fieldMeta.getAliasName();
        if (StringUtils.isNotBlank(aliasName)) {
            return "\"" + columnName + "\" AS \"" + aliasName + "\"";
        } else {
            return "\"" + columnName + "\"";
        }
    }

    @Override
    public String buildTableName(TableMeta tableMeta) {
        return "\"" + tableMeta.getTable() + "\"";
    }

    @Override
    public String buildConditionName(FieldMeta columnName) {
        return "\"" + columnName.getColumnName() + "\"";
    }
}