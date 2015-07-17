package com.edol.generator;

import com.edol.generator.javamapper.EmptyJavaClientGenerator;
import com.edol.generator.model.SpringDataModelGenerator;
import org.mybatis.generator.api.ProgressCallback;
import org.mybatis.generator.codegen.AbstractJavaClientGenerator;
import org.mybatis.generator.codegen.AbstractJavaGenerator;
import org.mybatis.generator.codegen.mybatis3.IntrospectedTableMyBatis3Impl;
import org.mybatis.generator.codegen.mybatis3.javamapper.SimpleAnnotatedClientGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.SimpleJavaClientGenerator;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.SimpleXMLMapperGenerator;
import org.mybatis.generator.internal.ObjectFactory;

import java.util.List;

/**
 * Created by mind on 7/17/15.
 */
public class SpringDataSimpleImpl extends IntrospectedTableMyBatis3Impl {
    public SpringDataSimpleImpl() {
        super();
    }

    private void setSpringDataCommon() {
        setSelectAllStatementId("findAll");
        setDeleteByPrimaryKeyStatementId("deleteById");
        setSelectByPrimaryKeyStatementId("findById"); //$NON-NLS-1$
        setUpdateByPrimaryKeyStatementId("updateById"); //$NON-NLS-1$
    }

    @Override
    protected void calculateXmlMapperGenerator(AbstractJavaClientGenerator javaClientGenerator,
                                               List<String> warnings,
                                               ProgressCallback progressCallback) {
        setSpringDataCommon();

        if (javaClientGenerator == null) {
            if (context.getSqlMapGeneratorConfiguration() != null) {
                xmlMapperGenerator = new SimpleXMLMapperGenerator();
            }
        } else {
            xmlMapperGenerator = javaClientGenerator.getMatchedXMLGenerator();
        }

        initializeAbstractGenerator(xmlMapperGenerator, warnings,
                progressCallback);
    }

    @Override
    protected AbstractJavaClientGenerator createJavaClientGenerator() {
        if (context.getJavaClientGeneratorConfiguration() == null) {
            return null;
        }

        return new EmptyJavaClientGenerator();
    }

    @Override
    protected void calculateJavaModelGenerators(List<String> warnings,
                                                ProgressCallback progressCallback) {

        AbstractJavaGenerator javaGenerator = new SpringDataModelGenerator();
        initializeAbstractGenerator(javaGenerator, warnings,
                progressCallback);
        javaModelGenerators.add(javaGenerator);
    }
}
