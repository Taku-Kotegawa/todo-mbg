package plugins;

import org.mybatis.generator.api.FullyQualifiedTable;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;

/**
 * MyBatis Generatorで生成するクラスに、特定のインタフェースを実装させるプラグイン。
 */
public class BeanImplementationPlugin extends PluginAdapter {

    private final FullyQualifiedJavaType bean;

    public BeanImplementationPlugin() {
        bean = new FullyQualifiedJavaType(
                "com.example.domain.common.hasWhoColumnInterface");
    }

    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass,
                                                 IntrospectedTable introspectedTable) {
        implementBean(
                topLevelClass,
                introspectedTable.getFullyQualifiedTable());
        return true;
    }

    @Override
    public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass,
                                                 IntrospectedTable introspectedTable) {
        implementBean(
                topLevelClass,
                introspectedTable.getFullyQualifiedTable());
        return true;
    }

    @Override
    public boolean modelRecordWithBLOBsClassGenerated(
            TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        implementBean(
                topLevelClass,
                introspectedTable.getFullyQualifiedTable());
        return true;
    }

    protected void implementBean(TopLevelClass topLevelClass,
                                 FullyQualifiedTable table) {
        topLevelClass.addImportedType(bean);
        topLevelClass.addSuperInterface(bean);
    }


}