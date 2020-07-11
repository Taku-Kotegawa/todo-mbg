# Mybatis Generator 利用方法

## カスタムプラグインの作成・コンパイル

src/main/java/plugins 配下にカスタムプラグインを作成して、mvn packeage でビルドする。

## Mybatis generator の実行方法

generatorConfig.xml を環境に応じて編集し、プロジェクトルートフォルダをターミナルで開き、


mvn mybatis-generator:generate

を実行

★generatorConfig.xml のファイル名を変更した場合は、引数にファイル名を指定できる。

mvn -Dmybatis.generator.configurationFile=src/main/resources/generatorConfig_account.xml mybatis-generator:generate

mvn -Dmybatis.generator.configurationFile=src/main/resources/generatorConfig_account.xml mybatis-generator:generate
