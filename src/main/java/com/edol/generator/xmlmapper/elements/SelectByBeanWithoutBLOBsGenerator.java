package com.edol.generator.xmlmapper.elements;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.OutputUtilities;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.AbstractXmlElementGenerator;

import java.util.Iterator;

/**
 * Created by frank on 5/25/16.
 */
public class SelectByBeanWithoutBLOBsGenerator extends AbstractXmlElementGenerator {
    @Override
    public void addElements(XmlElement parentElement) {

        XmlElement answer = new XmlElement("select"); //$NON-NLS-1$

        answer.addAttribute(new Attribute("id", "queryByBean"));
        answer.addAttribute(new Attribute(
                "resultMap", introspectedTable.getBaseResultMapId())); //$NON-NLS-1$
        FullyQualifiedJavaType
                parameterType = new FullyQualifiedJavaType(
                introspectedTable.getBaseRecordType());

        answer.addAttribute(new Attribute("parameterType", //$NON-NLS-1$
                parameterType.getFullyQualifiedName()));

        context.getCommentGenerator().addComment(answer);
        answer.addElement(new TextElement("select")); //$NON-NLS-1$

        StringBuilder sb = new StringBuilder();
        sb.setLength(0);
        sb.append("from "); //$NON-NLS-1$

        XmlElement fieldInclude = new XmlElement("include"); //$NON-NLS-1$
        fieldInclude.addAttribute(new Attribute("refid", "all_fields"));
        answer.addElement(fieldInclude);

        sb.append(introspectedTable
                .getAliasedFullyQualifiedTableNameAtRuntime());
        sb.append(" where 1=1 ");
        answer.addElement((new TextElement(sb.toString())));

        sb.setLength(0);
        Iterator<IntrospectedColumn> iter = introspectedTable
                .getNonBLOBColumns().iterator();
        XmlElement ifElement = null;
        while (iter.hasNext()) {
            IntrospectedColumn introspectedColumn = iter.next();

            ifElement = new XmlElement("if");
            ifElement.addAttribute(new Attribute("test", introspectedColumn.getJavaProperty().concat("!=null")));

            sb.append(" and ");
            sb.append(MyBatis3FormattingUtilities
                    .getAliasedEscapedColumnName(introspectedColumn));
            sb.append(" = "); //$NON-NLS-1$
            sb.append(MyBatis3FormattingUtilities.getParameterClause(
                    introspectedColumn, "")); //$NON-NLS-1$

            ifElement.addElement(new TextElement(sb.toString()));
            answer.addElement(ifElement);

            // set up for the next column
            if (iter.hasNext()) {
                sb.setLength(0);
                OutputUtilities.xmlIndent(sb, 1);
            }
        }

//        ifElement = new XmlElement("if"); //$NON-NLS-1$
//        ifElement.addAttribute(new Attribute("test", "orderByClause != null")); //$NON-NLS-1$ //$NON-NLS-2$
//        ifElement.addElement(new TextElement("order by ${orderByClause}")); //$NON-NLS-1$
//        answer.addElement(ifElement);

        if (context.getPlugins()
                .sqlMapSelectByExampleWithoutBLOBsElementGenerated(answer,
                        introspectedTable)) {
            parentElement.addElement(answer);
        }
    }
}
