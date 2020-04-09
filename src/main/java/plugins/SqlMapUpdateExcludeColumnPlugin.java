package plugins;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import javax.xml.soap.Text;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.VisitableElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import java.util.Iterator;

public class SqlMapUpdateExcludeColumnPlugin extends PluginAdapter {

    private String columns;
    private List<String> columnList = new ArrayList<>();
    private List<Integer> indexList = new ArrayList<>();

    @Override
    public boolean validate(List<String> warnings) {

        columns = properties.getProperty("searchString");
        System.out.println(columns);

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

//        removeExcludeColumn(introspectedTable);

//        for(VisitableElement ve : element.getElements()) {
//            TextElement te = (TextElement)ve;
//            System.out.println(te.getContent().toString());
//        }

//        XmlElement xe = (XmlElement) element.getElements().get(5);
//
//        System.out.println(xe.getElements());
//        List<VisitableElement> xee = xe.getElements();
//
//        for(VisitableElement ve : xee) {
//            xe = (XmlElement) ve;
//            TextElement te = (TextElement) xe.getElements().get(0);
//            System.out.println(te.getContent());
//            if(te.getContent().startsWith("created_at")) {
//                System.out.println("Hit");
//            }
//        }

        accessElement2(element);

//        accessElement(element);

        return true;
    }

    private void accessElement(XmlElement element) {
        for(VisitableElement e: element.getElements()) {
            if (e.getClass().equals(XmlElement.class)) {
                XmlElement xml = (XmlElement) e;

                System.out.println(hasChildTextElement(xml, "created_at"));

                accessElement(xml);
            } else {
                TextElement text = (TextElement) e;
                System.out.println("text" + ":" + text.getContent());
            }
        }
    }

    private boolean hasChildTextElement(XmlElement xml, String content){
        for(VisitableElement e: xml.getElements()) {
            if (e.getClass().equals(TextElement.class)) {
                TextElement text = (TextElement) e;
                if (text.getContent().toLowerCase().startsWith(content.toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
    }


    private void accessElement2(XmlElement element) {

        Iterator<VisitableElement> it = element.getElements().iterator();
        while(it.hasNext()){
            VisitableElement e = it.next();
            if (e.getClass().equals(XmlElement.class)) {
                XmlElement xml = (XmlElement) e;

                if (hasChildTextElement(xml, "created_at")) {
                    it.remove();
                } else {
                    accessElement2(xml);
                }
            }
        }
    }

//    private void accessElement(XmlElement element) {
//        System.out.println(element.hasChildren());
//        if(element.hasChildren()) {
//            XmlElement child = element.getElements();
//        } else {
//            TextElement text = (TextElement)element;
//            System.out.println(text.getContent());
//        }
//    }

    private void removeExcludeColumn(IntrospectedTable introspectedTable) {
        if(columnList != null) {
            for (String column : columnList) {

//                for(IntrospectedColumn introspectedColumn : introspectedTable.getBaseColumns()) {
//                    if (introspectedColumn.e) {
//
//                    }
//
//                }



//                introspectedTable.getBaseColumns().removeIf(
//                    introspectedColumn -> introspectedColumn.getActualColumnName()
//                        .equalsIgnoreCase(column));
            }
        }
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(
        XmlElement element, IntrospectedTable introspectedTable) {

//        removeExcludeColumn(introspectedTable);

        return true;
    }

    @Override
    public boolean sqlMapUpdateByExampleSelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {

//        removeExcludeColumn(introspectedTable);

        return true;

    }

}
