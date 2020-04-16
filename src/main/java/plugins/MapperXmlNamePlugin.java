/**
 *    Copyright ${license.git.copyrightYears} the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package plugins;

import java.util.List;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;

/**
 * MyBatis Generator が生成するマッパーXML名の末尾を Mapper から Repository に変更するプラグイン
 *
 * @author stnetadmin
 */
public class MapperXmlNamePlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> warnings) {return true;}

    @Override
    public void initialized(IntrospectedTable table) {
        super.initialized(table);
        String name = table.getMyBatis3XmlMapperFileName();
        table.setMyBatis3XmlMapperFileName(name.replaceAll("Mapper", "Repository"));
    }
}
