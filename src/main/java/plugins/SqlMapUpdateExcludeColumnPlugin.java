package plugins;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.VisitableElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

/**
 * Mybatis SQL XML のupdate文で新規登録時以外変更しないカラムを更新から除外する。 ex: 作成日日や作成者 プロパティで除外するカラムを複数指定できる。(カンマ区切り)
 *
 * @author taku.kotegawa
 *
 */
public class SqlMapUpdateExcludeColumnPlugin extends PluginAdapter {

    /** 除外項目のプロパティ名 */
    private static final String PROPERTY_EXCLUDE_COLUMNS = "excludeColumns";

    /** 除外項目名のリスト */
    private List<String> columnList = new ArrayList<>();

    @Override
    public boolean validate(List<String> warnings) {

        String columns = properties.getProperty(PROPERTY_EXCLUDE_COLUMNS);

        if (columns != null) {
            StringTokenizer st = new StringTokenizer(columns, ", ", false);
            while (st.hasMoreTokens()) {
                String column = st.nextToken();
                columnList.add(column);
            }
        }

        return true;
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeySelectiveElementGenerated(
        XmlElement element, IntrospectedTable introspectedTable) {

        removeColumn(element);
        return true;
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeyWithBLOBsElementGenerated(
        XmlElement element, IntrospectedTable introspectedTable) {

        removeColumn(element);
        return true;
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(
        XmlElement element, IntrospectedTable introspectedTable) {

        removeColumn(element);
        return true;
    }

    @Override
    public boolean sqlMapUpdateByExampleSelectiveElementGenerated(XmlElement element,
        IntrospectedTable introspectedTable) {

        removeColumn(element);
        return true;

    }

    @Override
    public boolean sqlMapUpdateByExampleWithBLOBsElementGenerated(XmlElement element,
        IntrospectedTable introspectedTable) {

        removeColumn(element);
        return true;
    }

    @Override
    public boolean sqlMapUpdateByExampleWithoutBLOBsElementGenerated(XmlElement element,
        IntrospectedTable introspectedTable) {

        removeColumn(element);
        return true;
    }

    /**
     * 引数で渡されたXmlElement(SqlMapperへの出力情報)から更新を除外する項目の有無を確認する。 項目名は引数で指定できる。
     *
     * @param xml     XmlElement
     * @param columns チェックする項目名のリスト
     * @return true:項目あり, false:項目なし
     */
    private boolean hasExcludeColumn(XmlElement xml, List<String> columns) {

        if (xml == null || columns == null) {
            return false;
        }

        for (VisitableElement e : xml.getElements()) {
            if (e.getClass().equals(TextElement.class)) {
                if (hasExcludeColumn((TextElement)e, columns)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 引数で渡されたtextElementから更新を除外する項目の有無を確認する。 項目名は引数で指定できる。
     *
     * @param textElement TextElement
     * @param columns 除外する項目名のリスト
     * @return true:項目あり, false:項目なし
     */
    private boolean hasExcludeColumn(TextElement textElement, List<String> columns) {
        for (String column : columns) {
            if (textElement.getContent().toLowerCase().trim().startsWith(column.toLowerCase() + " ")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 引数で渡されたXmlElemntがtextElemntを子供に持つか確認する。
     *
     * @param xml XmlElement
     * @return true:持つ, false:持たない
     */
    private boolean hasTextElement(XmlElement xml) {

        if (xml == null) {
            return false;
        }

        for (VisitableElement e : xml.getElements()) {
            if (e.getClass().equals(TextElement.class)) {
                return true;
            }
        }
        return false;
    }

    /**
     * xmlElementから除外する項目を削除する。 ネスト構造対応
     *
     * @param rootElement xmlElement
     */
    private void removeColumn(XmlElement rootElement) {

        Iterator<VisitableElement> it = rootElement.getElements().iterator();
        while (it.hasNext()) {
            VisitableElement e = it.next();
            if (e.getClass().equals(XmlElement.class)) {
                XmlElement xml = (XmlElement) e;
                if (hasTextElement(xml)) {
                    if (hasExcludeColumn(xml, columnList)) {
                        it.remove();
                    }
                } else {
                    removeColumn(xml);
                }
            } else if (e.getClass().equals(TextElement.class)) {
                if (hasExcludeColumn((TextElement) e, columnList)) {
                    it.remove();
                }
            }
        }
    }

}
