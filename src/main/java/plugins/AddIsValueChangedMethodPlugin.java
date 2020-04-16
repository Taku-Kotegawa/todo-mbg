/**
 * Copyright 2006-2017 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package plugins;

import java.util.List;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;

public class AddIsValueChangedMethodPlugin extends PluginAdapter {

    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass,
        IntrospectedTable introspectedTable) {
        addImport(topLevelClass);
        addIsValueChangedMethod(topLevelClass);
        addIsValueChangedSelectiveMethod(topLevelClass);

        return true;
    }

    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass,
        IntrospectedTable introspectedTable) {
        addImport(topLevelClass);
        addIsValueChangedMethod(topLevelClass);
        addIsValueChangedSelectiveMethod(topLevelClass);

        return true;
    }

    @Override
    public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass,
        IntrospectedTable introspectedTable) {
        addImport(topLevelClass);
        addIsValueChangedMethod(topLevelClass);
        addIsValueChangedSelectiveMethod(topLevelClass);

        return true;
    }

    @Override
    public boolean modelRecordWithBLOBsClassGenerated(TopLevelClass topLevelClass,
        IntrospectedTable introspectedTable) {
        addImport(topLevelClass);
        addIsValueChangedMethod(topLevelClass);
        addIsValueChangedSelectiveMethod(topLevelClass);

        return true;
    }

    private void addIsValueChangedMethod(TopLevelClass topLevelClass) {
        Method valueChangeCompareMethod = new Method("isValueChanged");
        Parameter oldRecord = new Parameter(topLevelClass.getType(), "oldRecord");
        Parameter newRecord = new Parameter(topLevelClass.getType(), "newRecord");

        valueChangeCompareMethod.setStatic(true);
        valueChangeCompareMethod.setVisibility(JavaVisibility.PUBLIC);
        valueChangeCompareMethod
            .setReturnType(FullyQualifiedJavaType.getBooleanPrimitiveInstance());
        valueChangeCompareMethod.setName("isValueChanged");
        valueChangeCompareMethod.addParameter(oldRecord);
        valueChangeCompareMethod.addParameter(newRecord);

        for (Field field : topLevelClass.getFields()) {
            String fieldName = field.getName();
            String fieldNameCapFirst =
                fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

            if ("createTime".equalsIgnoreCase(fieldName) || "createIp".equalsIgnoreCase(fieldName)
                || "createUser".equalsIgnoreCase(fieldName) || "updateTime"
                .equalsIgnoreCase(fieldName)
                || "updateIp".equalsIgnoreCase(fieldName) || "updateUser"
                .equalsIgnoreCase(fieldName)) {
                // WHOカラムは除外
            } else {
                if (field.getType().equals(new FullyQualifiedJavaType("java.lang.Long")) ||
                    field.getType().equals(new FullyQualifiedJavaType("java.lang.Short")) ||
                    field.getType().equals(new FullyQualifiedJavaType("java.lang.Integer")) ||
                    field.getType().equals(new FullyQualifiedJavaType("java.math.BigDecimal"))) {

                    String typeName = field.getType().getFullyQualifiedName()
                        .replace("java.lang.", "").replace("java.math.", "");
                    valueChangeCompareMethod.addBodyLine(String.format(""));
                    valueChangeCompareMethod.addBodyLine(
                        String.format("%s old%s = oldRecord.get%s();", typeName, fieldNameCapFirst,
                            fieldNameCapFirst));
                    valueChangeCompareMethod.addBodyLine(
                        String.format("%s new%s = newRecord.get%s();", typeName, fieldNameCapFirst,
                            fieldNameCapFirst));

                    valueChangeCompareMethod
                        .addBodyLine(String.format("if (old%s == null) {", fieldNameCapFirst));
                    valueChangeCompareMethod
                        .addBodyLine(String.format("if (new%s != null) {", fieldNameCapFirst));
                    valueChangeCompareMethod.addBodyLine(String.format("return true;"));
                    valueChangeCompareMethod.addBodyLine(String.format("}"));
                    valueChangeCompareMethod.addBodyLine(String.format("} else {"));
                    valueChangeCompareMethod.addBodyLine(
                        String.format("if (old%s.equals(new%s) == false) {", fieldNameCapFirst,
                            fieldNameCapFirst));
                    valueChangeCompareMethod.addBodyLine(String.format("return true;"));
                    valueChangeCompareMethod.addBodyLine(String.format("}"));
                    valueChangeCompareMethod.addBodyLine(String.format("}"));
                } else if (field.getType().equals(new FullyQualifiedJavaType("java.lang.String"))) {
                    // null と "" は同じと判定する
                    valueChangeCompareMethod.addBodyLine(String.format(""));
                    valueChangeCompareMethod.addBodyLine(
                        String.format("String old%s = oldRecord.get%s();", fieldNameCapFirst,
                            fieldNameCapFirst));
                    valueChangeCompareMethod.addBodyLine(
                        String.format("String new%s = newRecord.get%s();", fieldNameCapFirst,
                            fieldNameCapFirst));

                    valueChangeCompareMethod
                        .addBodyLine(
                            String.format("if (StringUtils.isEmpty(old%s)) {", fieldNameCapFirst));
                    valueChangeCompareMethod.addBodyLine(
                        String.format("if (StringUtils.isEmpty(new%s) == false) {",
                            fieldNameCapFirst));
                    valueChangeCompareMethod.addBodyLine(String.format("return true;"));
                    valueChangeCompareMethod.addBodyLine(String.format("}"));
                    valueChangeCompareMethod.addBodyLine(String.format("} else {"));
                    valueChangeCompareMethod.addBodyLine(
                        String.format("if (old%s.equals(new%s) == false) {", fieldNameCapFirst,
                            fieldNameCapFirst));
                    valueChangeCompareMethod.addBodyLine(String.format("return true;"));
                    valueChangeCompareMethod.addBodyLine(String.format("}"));
                    valueChangeCompareMethod.addBodyLine(String.format("}"));
                } else if (field.getType()
                    .equals(new FullyQualifiedJavaType("java.time.LocalDateTime"))) {
                    valueChangeCompareMethod.addBodyLine(String.format(""));
                    valueChangeCompareMethod
                        .addBodyLine(String.format("LocalDateTime old%s = oldRecord.get%s();",
                            fieldNameCapFirst, fieldNameCapFirst));
                    valueChangeCompareMethod
                        .addBodyLine(String.format("LocalDateTime new%s = newRecord.get%s();",
                            fieldNameCapFirst, fieldNameCapFirst));

                    valueChangeCompareMethod
                        .addBodyLine(String.format("if (old%s == null) {", fieldNameCapFirst));
                    valueChangeCompareMethod
                        .addBodyLine(String.format("if (new%s != null) {", fieldNameCapFirst));
                    valueChangeCompareMethod.addBodyLine(String.format("return true;"));
                    valueChangeCompareMethod.addBodyLine(String.format("}"));
                    valueChangeCompareMethod.addBodyLine(String.format("} else {"));
                    valueChangeCompareMethod.addBodyLine(
                        String.format("if (old%s.equals(new%s) == false) {", fieldNameCapFirst,
                            fieldNameCapFirst));
                    valueChangeCompareMethod.addBodyLine(String.format("return true;"));
                    valueChangeCompareMethod.addBodyLine(String.format("}"));
                    valueChangeCompareMethod.addBodyLine(String.format("}"));
                } else {

                }
            }
        }

        valueChangeCompareMethod.addBodyLine(String.format(""));
        valueChangeCompareMethod.addBodyLine(String.format("return false;"));

        topLevelClass.addMethod(valueChangeCompareMethod);
    }

    private void addIsValueChangedSelectiveMethod(TopLevelClass topLevelClass) {
        Method valueChangeCompareMethod = new Method("isValueChangedSelective");
        Parameter oldRecord = new Parameter(topLevelClass.getType(), "oldRecord");
        Parameter newRecord = new Parameter(topLevelClass.getType(), "newRecord");

        valueChangeCompareMethod.setStatic(true);
        valueChangeCompareMethod.setVisibility(JavaVisibility.PUBLIC);
        valueChangeCompareMethod
            .setReturnType(FullyQualifiedJavaType.getBooleanPrimitiveInstance());
        valueChangeCompareMethod.setName("isValueChangedSelective");
        valueChangeCompareMethod.addParameter(oldRecord);
        valueChangeCompareMethod.addParameter(newRecord);

        for (Field field : topLevelClass.getFields()) {
            String fieldName = field.getName();
            String fieldNameCapFirst =
                fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

            if ("createTime".equalsIgnoreCase(fieldName) || "createIp".equalsIgnoreCase(fieldName)
                || "createUser".equalsIgnoreCase(fieldName) || "updateTime"
                .equalsIgnoreCase(fieldName)
                || "updateIp".equalsIgnoreCase(fieldName) || "updateUser"
                .equalsIgnoreCase(fieldName)) {
                // WHOカラムは除外
            } else {
                if (field.getType().equals(new FullyQualifiedJavaType("java.lang.Long")) ||
                    field.getType().equals(new FullyQualifiedJavaType("java.lang.Short")) ||
                    field.getType().equals(new FullyQualifiedJavaType("java.lang.Integer")) ||
                    field.getType().equals(new FullyQualifiedJavaType("java.math.BigDecimal"))) {

                    String typeName = field.getType().getFullyQualifiedName()
                        .replace("java.lang.", "").replace("java.math.", "");
                    valueChangeCompareMethod.addBodyLine(String.format(""));
                    valueChangeCompareMethod.addBodyLine(
                        String.format("%s old%s = oldRecord.get%s();", typeName, fieldNameCapFirst,
                            fieldNameCapFirst));
                    valueChangeCompareMethod.addBodyLine(
                        String.format("%s new%s = newRecord.get%s();", typeName, fieldNameCapFirst,
                            fieldNameCapFirst));

                    valueChangeCompareMethod
                        .addBodyLine(String.format("if (new%s != null) {", fieldNameCapFirst));
                    valueChangeCompareMethod.addBodyLine(
                        String.format("if (new%s.equals(old%s) == false) {", fieldNameCapFirst,
                            fieldNameCapFirst));
                    valueChangeCompareMethod.addBodyLine(String.format("return true;"));
                    valueChangeCompareMethod.addBodyLine(String.format("}"));
                    valueChangeCompareMethod.addBodyLine(String.format("}"));
                } else if (field.getType().equals(new FullyQualifiedJavaType("java.lang.String"))) {
                    // old : null または "", new : "" は値変更なしと判定する
                    valueChangeCompareMethod.addBodyLine(String.format(""));
                    valueChangeCompareMethod.addBodyLine(
                        String.format("String old%s = oldRecord.get%s();", fieldNameCapFirst,
                            fieldNameCapFirst));
                    valueChangeCompareMethod.addBodyLine(
                        String.format("String new%s = newRecord.get%s();", fieldNameCapFirst,
                            fieldNameCapFirst));

                    valueChangeCompareMethod
                        .addBodyLine(String.format("if (new%s != null) {", fieldNameCapFirst));
                    valueChangeCompareMethod
                        .addBodyLine(String.format("if (\"\".equals(new%s)) {", fieldNameCapFirst));
                    valueChangeCompareMethod.addBodyLine(
                        String.format("if (StringUtils.isEmpty(old%s) == false) {",
                            fieldNameCapFirst));
                    valueChangeCompareMethod.addBodyLine(String.format("return true;"));
                    valueChangeCompareMethod.addBodyLine(String.format("}"));
                    valueChangeCompareMethod.addBodyLine(String.format("} else {"));
                    valueChangeCompareMethod.addBodyLine(
                        String.format("if (new%s.equals(old%s) == false) {", fieldNameCapFirst,
                            fieldNameCapFirst));
                    valueChangeCompareMethod.addBodyLine(String.format("return true;"));
                    valueChangeCompareMethod.addBodyLine(String.format("}"));
                    valueChangeCompareMethod.addBodyLine(String.format("}"));
                    valueChangeCompareMethod.addBodyLine(String.format("}"));
                } else if (field.getType()
                    .equals(new FullyQualifiedJavaType("java.time.LocalDateTime"))) {
                    valueChangeCompareMethod.addBodyLine(String.format(""));
                    valueChangeCompareMethod
                        .addBodyLine(String.format("LocalDateTime old%s = oldRecord.get%s();",
                            fieldNameCapFirst, fieldNameCapFirst));
                    valueChangeCompareMethod
                        .addBodyLine(String.format("LocalDateTime new%s = newRecord.get%s();",
                            fieldNameCapFirst, fieldNameCapFirst));

                    valueChangeCompareMethod
                        .addBodyLine(String.format("if (new%s != null) {", fieldNameCapFirst));
                    valueChangeCompareMethod.addBodyLine(
                        String.format("if (new%s.equals(old%s) == false) {", fieldNameCapFirst,
                            fieldNameCapFirst));
                    valueChangeCompareMethod.addBodyLine(String.format("return true;"));
                    valueChangeCompareMethod.addBodyLine(String.format("}"));
                    valueChangeCompareMethod.addBodyLine(String.format("}"));
                } else {

                }
            }
        }

        valueChangeCompareMethod.addBodyLine(String.format(""));
        valueChangeCompareMethod.addBodyLine(String.format("return false;"));

        topLevelClass.addMethod(valueChangeCompareMethod);
    }

    private void addImport(TopLevelClass topLevelClass) {
        addImport(topLevelClass,
            new FullyQualifiedJavaType("org.springframework.util.StringUtils"));
    }

    /**
     * インポートを追加する
     *
     * @param topLevelClass
     * @param javaType
     */
    private void addImport(TopLevelClass topLevelClass, FullyQualifiedJavaType javaType) {
        topLevelClass.addImportedType(javaType);
    }
}
