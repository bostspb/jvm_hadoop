## Урок 2. Apache Spark DSL
1. Какие плюсы и недостатки у *Merge Sort Join* в отличии от *Hash Join*?
2. Соберите *WordCount* приложение, запустите на датасете `ppkm_sentiment`
3. Измените *WordCount* так, чтобы он удалял знаки препинания и приводил все слова к единому регистру
4. Измените выход *WordCount* так, чтобы сортировка была по количеству повторений, а список слов был во втором столбце, а не в первом
5. Измените выход *WordCount*, чтобы формат соответствовал `СSV`


### Какие плюсы и недостатки у *Merge Sort Join* в отличии от *Hash Join*?
*Merge Sort Join* менее эффективен в отличии от *Hash Join* в плане использования вычислительных ресурсов, но выигрывает по затратам памяти.


### Устанавливаем Apache Spark в контейнер с Hadoop
Т.к. контейнер с Hadoop у нас уже развернут с прошлого занятия, то просто запускаем контейнер и заходим в его консоль:

	docker start gbhdp
	docker exec -it gbhdp bash

Скачиваем дистрибутив с Spark 2.4.8, распаковываем и переименовываем директорию с приложением в `~/spark`

	wget https://apache-mirror.rbc.ru/pub/apache/spark/spark-2.4.8/spark-2.4.8-bin-hadoop2.7.tgz
	tar xzf spark-2.4.8-bin-hadoop2.7.tgz
	rm spark-2.4.8-bin-hadoop2.7.tgz
	mv spark-2.4.8-bin-hadoop2.7 spark

Прописываем переменные окружения

	export SPARK_HOME=/home/hduser/spark
	export PATH=$PATH:$SPARK_HOME/bin
	export HADOOP_CONF_DIR=$HADOOP_HOME/etc/hadoop/

Прописываем эти переменные в `~/.bashrc`, чтобы при каждом входе в bash заново их не прописывать.


### Собираем приложение WordCount на Java
Создаем проект [WordCount](https://github.com/bostspb/jvm_hadoop/blob/master/lesson02/SparkRDD/src/main/java/WordCount.java), 
прописываем зависимости в файле `build.gradle`. 
Также указываем, что собирать нужно под Java 8, т.к. Hadoop работает только с Java 8. 

Cобираем проект.

Перекидываем собранный JAR-файл в контейнер с Hadoop:

	docker cp build/libs/SparkRDD-1.0-SNAPSHOT.jar gbhdp:/home/hduser/
	
И запускаем приложение
	
	spark-submit --class WordCount --master yarn --deploy-mode cluster SparkRDD-1.0-SNAPSHOT.jar /user/hduser/ppkm /user/hduser/ppkm-rdd

Приложение успешно отработало, проверяем результат

	hduser@localhost:~$ hdfs dfs -ls ppkm-rdd
	Found 3 items
	-rw-r--r--   1 hduser supergroup          0 2021-10-04 04:14 ppkm-rdd/_SUCCESS
	-rw-r--r--   1 hduser supergroup      15037 2021-10-04 04:14 ppkm-rdd/part-00000
	-rw-r--r--   1 hduser supergroup      15694 2021-10-04 04:14 ppkm-rdd/part-00001

	hduser@localhost:~$ hdfs dfs -cat ppkm-rdd/* | head
	(butuh,1)
	(keluarganya,1)
	(EFEKTIF.,2)
	(positif,"Kebijakan,1)
	(izin,1)
	(Amati,1)
	(Mandiri,1)
	(rapih,1)
	(dipikir,1)
	(Kebijakan,3)
	cat: Unable to write to output stream.


### Собираем приложение WordCount на Scala
Создаем проект [WordCount](https://github.com/bostspb/jvm_hadoop/blob/master/lesson02/ScalaSparkRDDsrc/main/scala/WordCount.scala) 
под Scala версии 11, т.к. Spark в контейнере собран именно на этой версии Scala.
Также указываем, что собирать нужно под Java 8.

Cобираем проект через `sbt shell` командой `package`.

Закидываем в контейнер

	docker cp target/scala-2.11/scalasparkrdd_2.11-0.1.jar gbhdp:/home/hduser/
	
Запускаем приложение 

	spark-submit --class WordCount --master yarn --deploy-mode cluster scalasparkrdd_2.11-0.1.jar /user/hduser/ppkm /user/hduser/scala-ppkm-rdd

Приложение успешно отработало, проверяем результат
	
	hduser@localhost:~$ hdfs dfs -ls scala-ppkm-rdd
	Found 3 items
	-rw-r--r--   1 hduser supergroup          0 2021-10-04 05:10 scala-ppkm-rdd/_SUCCESS
	-rw-r--r--   1 hduser supergroup      22401 2021-10-04 05:10 scala-ppkm-rdd/part-00000
	-rw-r--r--   1 hduser supergroup       8330 2021-10-04 05:10 scala-ppkm-rdd/part-00001
	
	hduser@localhost:~$ hdfs dfs -cat scala-ppkm-rdd/* | head
	(butuh,1)
	(keluarganya,1)
	(positif,"Kebijakan,1)
	(izin,1)
	(Amati,1)
	(Mandiri,1)
	(rapih,1)
	(dipikir,1)
	(SWT...,1)
	(Bulungan,1)
	cat: Unable to write to output stream.
	
	hduser@localhost:~$ hdfs dfs -cat scala-ppkm-rdd/* | tail
	(#PPKMMikro,34)
	(Pembatasan,36)
	(Masyarakat,38)
	(Kegiatan,39)
	(,41)
	(yg,55)
	(Mikro,56)
	(di,74)
	(PPKM,84)
	(dan,84)
	

### Модифицируем приложение WordCount на Scala согласно заданиям №3, №4 и №5
- Добавляем в код очистку от спецсимволов и приведение к нижнему регистру.
- Сортировка по количеству повторений в исходнике уже была - здесь модификация не требуется.
- Меняем порядок вывода столбцов с данными - на первое место ставим количество повторений, а на второе - само слово.
- Заодно оформляем каждую строку в формате CSV - отделяем столбцы точкой с запятой и текстовое значение обрамляем кавычками.

Результат модификации - https://github.com/bostspb/jvm_hadoop/blob/master/lesson02/ScalaSparkRDDsrc/main/scala/WordCount.scala

Собираем получившееся приложение, кладем в контейнер с Hadoop и запускаем
	
	spark-submit --class WordCount --master yarn --deploy-mode cluster scalasparkrdd_2.11-0.1.jar /user/hduser/ppkm /user/hduser/scala-ppkm-rdd-2

Смотрим результат

	hduser@localhost:~$ hdfs dfs -cat scala-ppkm-rdd-2/* | head
	1; "al-"
	1; "butuh"
	1; "httpstco1badih8rdl"
	1; "keluarganya"
	1; "rapih"
	1; "dipikir"
	1; "tuhanku"
	1; "orangpake"
	1; "swt"
	1; "negatifsemakin"
	cat: Unable to write to output stream.
	
	hduser@localhost:~$ hdfs dfs -cat scala-ppkm-rdd-2/* | tail
	44; "kegiatan"
	56; "covid-19"
	57; "yg"
	60; "ppkmmikro"
	64; "masyarakat"
	65; ""
	77; "di"
	88; "dan"
	110; "mikro"
	120; "ppkm"

