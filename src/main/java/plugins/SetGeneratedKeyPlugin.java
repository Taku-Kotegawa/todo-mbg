package plugins;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.VisitableElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.StringTokenizer;

/**
 * (作りかけ) AutoIncrement項目を自動的に検出し、SQLを変更する。
 * (プロパティ)
 * autoIncrementType: AutoIncrement項目を特定するDBのデータ型
 * sqlStaetment: mybatisのGeneratedKeyプロパティの設定と同じ
 * identity: mybatisのGeneratedKeyプロパティの設定と同じ
 */
public class SetGeneratedKeyPlugin extends PluginAdapter {

    /**
     * プロパティ名
     */
    private static final String PROPERTY_AUTOINCREMENT_TYPE = "autoIncrementType";
    private static final String PROPERTY_SQL_STATEMENT = "sqlStatement";
    private static final String PROPERTY_IDENTITY = "identity";

    /**
     * バージョンカラムのリスト
     */
    private final List<String> autoIncrementTypeList = new ArrayList<>();
    private String sqlStatement;
    private Boolean identity;
    private IntrospectedColumn autoIncrement = null;

    @Override
    public boolean validate(List<String> warnings) {
        String columns = properties.getProperty(PROPERTY_AUTOINCREMENT_TYPE);
        if (columns != null && !columns.isEmpty()) {
            StringTokenizer st = new StringTokenizer(columns, ", ", false);
            while (st.hasMoreTokens()) {
                String column = st.nextToken();
                autoIncrementTypeList.add(column);
            }
        } else {
            autoIncrementTypeList.add("serial");
        }

        sqlStatement = properties.getProperty(PROPERTY_SQL_STATEMENT);
        if (sqlStatement == null || sqlStatement.isEmpty()) {
            sqlStatement = "JDBC";
        }

        identity = ("true".equals(properties.getProperty(PROPERTY_IDENTITY).toLowerCase()));

        return true;
    }

    private void setGeneratedKey(XmlElement element, IntrospectedTable introspectedTable) {

        for (IntrospectedColumn column : introspectedTable.getAllColumns()) {
            for (String autoIncrementType : autoIncrementTypeList) {
                if (autoIncrementType.equalsIgnoreCase(column.getActualTypeName())) {
                    autoIncrement = column;
                }
            }
        }

        int insertIndex = 9999;
        for (int i = 0; i < element.getElements().size(); i++) {
            VisitableElement e = element.getElements().get(i);
            if (e instanceof TextElement) {
                TextElement te = (TextElement) e;
                if (te.getContent().startsWith("insert into")) {
                    insertIndex = i;
                    break;
                }
            }
        }

        if (insertIndex != 9999) {
            switch (sqlStatement) {
                case "JDBC":
                    element.getElements().add(insertIndex,
                            new TextElement("<selectKey keyProperty=\"" + autoIncrement.getJavaProperty() + "\" order=\"AFTER\" resultType=\"" + autoIncrement.getFullyQualifiedJavaType() + "\">"));
                    element.getElements().add(insertIndex + 1, new TextElement("  SELECT SCOPE_IDENTITY()"));
                    element.getElements().add(insertIndex + 2, new TextElement("</selectKey>"));
                    break;
            }
        }
    }

    public void cutAutoIncrementColumn(XmlElement element, IntrospectedTable introspectedTable) {

        // ex) serial_number(半角スペース)
        cutFromElement(element, autoIncrement.getActualColumnName() + ", ");
        // ex) #{serialNumber,jdbcType=INTEGER},(半角スペース)
        cutFromElement(element, "#{" + autoIncrement.getJavaProperty() + ",jdbcType=" + autoIncrement.getJdbcTypeName() + "}, ");
    }

    private void cutFromElement(XmlElement element, String cutWord) {
        ListIterator<VisitableElement> it = element.getElements().listIterator();
        while (it.hasNext()) {
            VisitableElement e = it.next();
            if (e instanceof TextElement) {
                TextElement te = (TextElement) e;
                it.set(new TextElement(te.getContent().replace(cutWord, "")));
            }
        }
    }

    private void cutAutoIncrementColumnSelective(XmlElement element, IntrospectedTable introspectedTable) {

        // ex) serial_number
        cutFromElementSelective(element, 8, autoIncrement.getActualColumnName() + ",");
        // ex) #{serialNumber,jdbcType=INTEGER},
        cutFromElementSelective(element, 9, "#{" + autoIncrement.getJavaProperty() + ",jdbcType=" + autoIncrement.getJdbcTypeName() + "},");

    }

    private void cutFromElementSelective(XmlElement element, int i, String cutWord) {
        // 階層決め打ちでAutoIncrementColumnを削除する
        VisitableElement ve = element.getElements().get(i);
        if (ve instanceof XmlElement) {
            XmlElement xe = (XmlElement) ve;
            ListIterator<VisitableElement> it = xe.getElements().listIterator();
            while (it.hasNext()) {
                VisitableElement ve2 = it.next();
                XmlElement xe2 = (XmlElement) ve2;
                VisitableElement ve3 = xe2.getElements().get(0);
                if (ve3 instanceof TextElement) {
                    TextElement te = (TextElement) ve3;
                    if (te.getContent().equals(cutWord)) {
                        it.remove();
                    }
                }
            }
        }
    }

    @Override
    public boolean sqlMapInsertElementGenerated(XmlElement element,
                                                IntrospectedTable introspectedTable) {

        setGeneratedKey(element, introspectedTable);
        cutAutoIncrementColumn(element, introspectedTable);
        return true;
    }

    @Override
    public boolean sqlMapInsertSelectiveElementGenerated(XmlElement element,
                                                         IntrospectedTable introspectedTable) {
        setGeneratedKey(element, introspectedTable);
        cutAutoIncrementColumnSelective(element, introspectedTable);
        return true;
    }

}


