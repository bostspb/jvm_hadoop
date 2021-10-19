## УСТАНОВКА APACHE ZEPPELIN

Получить ссылку для скачивания можно на: http://zeppelin.apache.org/download.html

Скачиваем и распаковываем дистрибутив:

    $ wget https://artfiles.org/apache.org/zeppelin/zeppelin-0.10.0/zeppelin-0.10.0-bin-all.tgz
    $ tar xzf zeppelin-0.10.0-bin-all.tgz
    $ rm zeppelin-0.10.0-bin-all.tgz
    $ mv zeppelin-0.10.0-bin-all zeppelin

Задаем необходимые переменные окружения:

    $ cd zeppelin
    $ export ZEPPELIN_HOME=`pwd`
    $ export PATH=$PATH:$ZEPPELIN_HOME/bin

Cоздадим файл `~/zeppelin/conf/zeppelin-env.sh` и вставим:

    #!/bin/bash
    export USE_HADOOP=true
    export ZEPPELIN_ADDR=0.0.0.0
    export ZEPPELIN_PORT=8888
    export SPARK_HOME=/home/hduser/spark
    export SPARK_APP_NAME=zeppelin-hduser
    export HADOOP_CONF_DIR=/home/hduser/hadoop/etc/hadoop

Запускаем:

    $ zeppelin-daemon.sh start
    Log dir doesn't exist, create /home/hduser/zeppelin/logs
    Pid dir doesn't exist, create /home/hduser/zeppelin/run
    Zeppelin start [ OK ]

Переходим на http://localhost:8888

Настраиваем интеграцию со Spark. Заходим в раздел Interpreters, 
находим через поиск интерпретатор Spark и ставим для параметра `spark.master` значение `yarn-cluster`.

Затем создаем новый интерпретатор с названием `hive`, группа `jdbc`.
В разделе Properties меняем параметры:

    default.url = jdbc:hive2://localhost:10000
    default.driver = org.apache.hive.jdbc.HiveDriver
    default.user - стираем значение
    zeppelin.jdbc.concurrent.use - снимаем галку

В разделе Dependencies добавляем артефакты:

    org.apache.hive:hive-jdbc:2.3.9
    org.apache.hadoop:hadoop-common:2.10.1
