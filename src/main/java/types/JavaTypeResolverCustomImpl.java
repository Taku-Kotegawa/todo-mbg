package types;

import java.sql.Types;
import org.mybatis.generator.api.JavaTypeResolver;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.internal.types.JavaTypeResolverDefaultImpl;

public class JavaTypeResolverCustomImpl extends JavaTypeResolverDefaultImpl implements
    JavaTypeResolver {

    public JavaTypeResolverCustomImpl() {
        super();

        // typeMapのカスタマイズ
        typeMap.put(Types.BINARY, new JdbcTypeInformation("BINARY",
            new FullyQualifiedJavaType("InputStream")));

    }
}
