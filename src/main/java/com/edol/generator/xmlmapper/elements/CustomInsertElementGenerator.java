/**
 * 版权声明  版权所有 违者必究
 * 版本号  1.0
 * 修订记录:
 * 1)更改者Lucky
 * 时　间：2016/09/28 15:19
 * 描　述：创建
 */
package com.edol.generator.xmlmapper.elements;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.OutputUtilities;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.AbstractXmlElementGenerator;
import org.mybatis.generator.config.GeneratedKey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * <pre>
 * 功能说明
 * </pre>
 * <p>
 * <br>
 * JDK版本:1.6 或更高
 *
 * @author Lucky
 * @version 1.0
 * @since 1.0
 */
public class CustomInsertElementGenerator extends AbstractXmlElementGenerator {
    private boolean isSimple;

    public CustomInsertElementGenerator(boolean isSimple) {
        this.isSimple = isSimple;
    }

    public void addElements(XmlElement parentElement) {
        XmlElement answer = new XmlElement("insert");
        answer.addAttribute(new Attribute("id", this.introspectedTable.getInsertStatementId()));
        FullyQualifiedJavaType parameterType;
        if (this.isSimple) {
            parameterType = new FullyQualifiedJavaType(this.introspectedTable.getBaseRecordType());
        } else {
            parameterType = this.introspectedTable.getRules().calculateAllFieldsClass();
        }

        answer.addAttribute(new Attribute("parameterType", parameterType.getFullyQualifiedName()));
        this.context.getCommentGenerator().addComment(answer);
        GeneratedKey gk = this.introspectedTable.getGeneratedKey();
        if (gk != null) {
            IntrospectedColumn insertClause = this.introspectedTable.getColumn(gk.getColumn());
            if (insertClause != null) {
                if (gk.isJdbcStandard()) {
                    answer.addAttribute(new Attribute("useGeneratedKeys", "true"));
                    answer.addAttribute(new Attribute("keyProperty", insertClause.getJavaProperty()));
                } else {
                    answer.addElement(this.getSelectKey(insertClause, gk));
                }
            }
        }

        StringBuilder insertClause1 = new StringBuilder();
        StringBuilder valuesClause = new StringBuilder();
        insertClause1.append("insert into ");
        insertClause1.append(this.introspectedTable.getFullyQualifiedTableNameAtRuntime());
        insertClause1.append(" (");
        valuesClause.append("values (");
        ArrayList valuesClauses = new ArrayList();
        Iterator iter = this.introspectedTable.getAllColumns().iterator();

        while (iter.hasNext()) {
            IntrospectedColumn next = (IntrospectedColumn) iter.next();
            if (!next.isIdentity()) {
                if (next.getJdbcTypeName() != null && Arrays.asList("TIMESTAMP", "TIME", "DATE").contains(next.getJdbcTypeName().toUpperCase())
                        && next.getDefaultValue() != null && !next.getDefaultValue().toUpperCase().equals("NULL")) {
                    continue;
                }

                insertClause1.append(MyBatis3FormattingUtilities.getEscapedColumnName(next));
                valuesClause.append(MyBatis3FormattingUtilities.getParameterClause(next));
                if (iter.hasNext()) {
                    insertClause1.append(", ");
                    valuesClause.append(", ");
                }

                if (valuesClause.length() > 80) {
                    answer.addElement(new TextElement(insertClause1.toString()));
                    insertClause1.setLength(0);
                    OutputUtilities.xmlIndent(insertClause1, 1);
                    valuesClauses.add(valuesClause.toString());
                    valuesClause.setLength(0);
                    OutputUtilities.xmlIndent(valuesClause, 1);
                }
            }
        }

        insertClause1.append(')');
        answer.addElement(new TextElement(insertClause1.toString()));
        valuesClause.append(')');
        valuesClauses.add(valuesClause.toString());
        Iterator iterator = valuesClauses.iterator();

        while (iterator.hasNext()) {
            String clause = (String) iterator.next();
            answer.addElement(new TextElement(clause));
        }

        if (this.context.getPlugins().sqlMapInsertElementGenerated(answer, this.introspectedTable)) {
            parentElement.addElement(answer);
        }

    }
}
