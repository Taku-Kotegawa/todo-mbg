package plugins;

import org.mybatis.generator.api.FullyQualifiedTable;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;
import java.util.Properties;

/**
 * MyBatis Generatorで生成するクラスに、特定のインタフェースを実装させるプラグイン。
 */
public class BeanImplementationPlugin extends PluginAdapter {

    private String interfaceName;

    private FullyQualifiedJavaType bean;


    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        for (String propertyName : properties.stringPropertyNames()) {
            if (propertyName.equals("interfaceName")) {
                this.interfaceName = properties.getProperty("interfaceName");
                break;
            }
        }

        bean = new FullyQualifiedJavaType(this.interfaceName);
    }



    public BeanImplementationPlugin() {
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