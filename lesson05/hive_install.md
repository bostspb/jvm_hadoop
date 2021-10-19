## УСТАНОВКА APACHE HIVE

Скачиваем и распаковываем дистрибутив:

    $ wget https://apache-mirror.rbc.ru/pub/apache/hive/hive-2.3.9/apache-hive-2.3.9-bin.tar.gz
    $ tar xzf apache-hive-2.3.9-bin.tar.gz
    $ rm apache-hive-2.3.9-bin.tar.gz
    $ mv apache-hive-2.3.9-bin hive

Задаем необходимые переменные окружения:

    $ cd hive
    $ export HIVE_HOME=`pwd`
    $ export PATH=$PATH:$HIVE_HOME/bin

Устанавливаем необходимые директории и права:

    $ hdfs dfs -mkdir -p /user/hive/warehouse
    $ hdfs dfs -chmod +w /user/hive/warehouse

Инициализируем метастор:

    $ schematool -dbType derby -initSchema


Cоздадим файл ~/hive/conf/hive-site.xml и вставим:

    <?xml version="1.0" encoding="UTF-8" standalone="no"?>
    <?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
    <configuration>
     <property>
     <name>hive.server2.enable.doAs</name>
     <value>FALSE</value>
     <description/>
     </property>
    </configuration>

Проверяем работу:

    $ hive -e 'show tables;'
    OK
    Time taken: 6.128 seconds

### НАСТРОЙКА HIVE SERVER

Запускаем в фоне Hive Server:

    $ hiveserver2 &> /dev/null &

Подключаемся через beeline cli:

    $ beeline -u jdbc:hive2://localhost:10000

Проверяем работу:

    0: jdbc:hive2://localhost:10000> show tables;
    OK
    +-----------+
    | tab_name |
    +-----------+
    +-----------+
    No rows selected (0.404 seconds)

Выходим:

    0: jdbc:hive2://localhost:10000> !q
    Closing: 0: jdbc:hive2://localhost:10000