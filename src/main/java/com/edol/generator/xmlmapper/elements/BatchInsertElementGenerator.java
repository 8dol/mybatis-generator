package com.edol.generator.xmlmapper.elements;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.OutputUtilities;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.AbstractXmlElementGenerator;
import org.mybatis.generator.config.GeneratedKey;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Add batch insert sql generator
 * Frank 2015-09-12
 */
public class BatchInsertElementGenerator extends AbstractXmlElementGenerator {
    private boolean isSimple;

    public BatchInsertElementGenerator(boolean isSimple) {
        this.isSimple = isSimple;
    }

    public void addElements(XmlElement parentElement) {
        XmlElement answer = new XmlElement("insert");
        answer.addAttribute(new Attribute("id", "batchInsert"));

        answer.addAttribute(new Attribute("parameterType", "java.util.List"));
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

        //add foreach element
        XmlElement forEach = new XmlElement("foreach");
        forEach.addAttribute(new Attribute("collection", "list"));
        forEach.addAttribute(new Attribute("item", "item"));
        forEach.addAttribute(new Attribute("index", "index"));
        forEach.addAttribute(new Attribute("separator", ","));


        valuesClause.append("(");
        ArrayList valuesClauses = new ArrayList();
        Iterator iter = this.introspectedTable.getAllColumns().iterator();

        while (iter.hasNext()) {
            IntrospectedColumn i$ = (IntrospectedColumn) iter.next();
            if (!i$.isIdentity()) {
                insertClause1.append(MyBatis3FormattingUtilities.getEscapedColumnName(i$));
                valuesClause.append(MyBatis3FormattingUtilities.getParameterClause(i$, "item."));
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

        insertClause1.append(") values");
        answer.addElement(new TextElement(insertClause1.toString()));

        valuesClause.append(')');
        valuesClauses.add(valuesClause.toString());
        Iterator i$1 = valuesClauses.iterator();
        while (i$1.hasNext()) {
            String clause = (String) i$1.next();
            forEach.addElement(new TextElement(clause));
        }
        answer.addElement(forEach);


        if (this.context.getPlugins().sqlMapInsertElementGenerated(answer, this.introspectedTable)) {
            parentElement.addElement(answer);
        }

    }
}
