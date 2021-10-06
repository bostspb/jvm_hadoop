## Урок 3. Особенности Spark SQL
1. Какая связь между DataFrame и Dataset?
2. Соберите WordCount приложение, запустите на датасете ppkm_sentiment
3. * Измените WordCount так, чтобы он удалял знаки препинания и приводил все слова к единому регистру
4. * Добавьте в WordCount возможность через конфигурацию задать список стоп-слов, которые будут отфильтрованы во время работы приложения
5. Измените выход WordCount так, чтобы сортировка была по количеству повторений, а список слов был во втором столбце, а не в первом
6. * Почему в примере в выходном файле получилось 200 партиций?


### Какая связь между DataFrame и Dataset?
*Dataset* обладает всей функциональностью от *DataFrame*, но при этом еще поддерживает строгую типизацию.

### * Почему в примере в выходном файле получилось 200 партиций?
Это произошло, потому что дефолтное значение параметра `spark.sql.shuffle.partitions` (200 блоков) не было изменено.
https://spark.apache.org/docs/latest/sql-performance-tuning.html


### Собираем приложение WordCount на Java
Создаем и собираем проект [WordCount](), 

Запускаем контейнер с Hadoop и Spark, собранный на прошлых уроках.

	docker start gbhdp
	docker exec -it gbhdp bash

Перекидываем собранный JAR-файл в контейнер с Hadoop:

	docker cp build/libs/SparkDF-1.0-SNAPSHOT.jar gbhdp:/home/hduser/
	
И запускаем приложение
	
	spark-submit --class WordCount --master yarn --deploy-mode cluster SparkDF-1.0-SNAPSHOT.jar /user/hduser/ppkm /user/hduser/ppkm-df

Приложение успешно отработало, проверяем результат

	hduser@localhost:~$ hdfs dfs -ls ppkm-df
	Found 201 items
	-rw-r--r--   1 hduser supergroup          0 2021-10-05 12:46 ppkm-df/_SUCCESS
	-rw-r--r--   1 hduser supergroup        151 2021-10-05 12:44 ppkm-df/part-00000-8f05daa8-7f20-4e03-9670-056043965c66-c000.csv
	-rw-r--r--   1 hduser supergroup        134 2021-10-05 12:44 ppkm-df/part-00001-8f05daa8-7f20-4e03-9670-056043965c66-c000.csv
	-rw-r--r--   1 hduser supergroup        100 2021-10-05 12:44 ppkm-df/part-00002-8f05daa8-7f20-4e03-9670-056043965c66-c000.csv
	-rw-r--r--   1 hduser supergroup        183 2021-10-05 12:44 ppkm-df/part-00003-8f05daa8-7f20-4e03-9670-056043965c66-c000.csv
	...
	-rw-r--r--   1 hduser supergroup        137 2021-10-05 12:46 ppkm-df/part-00199-8f05daa8-7f20-4e03-9670-056043965c66-c000.csv

	
	hduser@localhost:~$ hdfs dfs -cat ppkm-df/part-00078-8f05daa8-7f20-4e03-9670-056043965c66-c000.csv
	covid,20
	masing,2
	keras,2
	PB,1
	"porkes,kegitan",1
	menentang,1
	mengobrol,1
	kece*,1
	apakah,2
	Tentang,1

	

### Собираем приложение WordCount на Scala
Создаем проект [WordCount]() 

Закидываем в контейнер

	docker cp target/scala-2.11/sparkdfscala_2.11-0.1.jar gbhdp:/home/hduser/
	
Запускаем приложение 

	spark-submit --class WordCount --master yarn --deploy-mode cluster sparkdfscala_2.11-0.1.jar /user/hduser/ppkm /user/hduser/scala-ppkm-df

Приложение успешно отработало, проверяем результат
	
	hduser@localhost:~$ hdfs dfs -ls scala-ppkm-df
	Found 201 items
	-rw-r--r--   1 hduser supergroup          0 2021-10-05 13:04 scala-ppkm-df/_SUCCESS
	-rw-r--r--   1 hduser supergroup        151 2021-10-05 13:03 scala-ppkm-df/part-00000-11e193a4-bb44-4fd9-894c-3707fc2bec8a-c000.csv
	-rw-r--r--   1 hduser supergroup        134 2021-10-05 13:03 scala-ppkm-df/part-00001-11e193a4-bb44-4fd9-894c-3707fc2bec8a-c000.csv
	-rw-r--r--   1 hduser supergroup        100 2021-10-05 13:03 scala-ppkm-df/part-00002-11e193a4-bb44-4fd9-894c-3707fc2bec8a-c000.csv
	-rw-r--r--   1 hduser supergroup        183 2021-10-05 13:03 scala-ppkm-df/part-00003-11e193a4-bb44-4fd9-894c-3707fc2bec8a-c000.csv
	-rw-r--r--   1 hduser supergroup         72 2021-10-05 13:03 scala-ppkm-df/part-00004-11e193a4-bb44-4fd9-894c-3707fc2bec8a-c000.csv
	-rw-r--r--   1 hduser supergroup         84 2021-10-05 13:03 scala-ppkm-df/part-00005-11e193a4-bb44-4fd9-894c-3707fc2bec8a-c000.csv
	...
	-rw-r--r--   1 hduser supergroup        137 2021-10-05 13:04 scala-ppkm-df/part-00199-11e193a4-bb44-4fd9-894c-3707fc2bec8a-c000.csv

	hdfs dfs -cat scala-ppkm-df/part-00001-7e7b03b9-8c39-416a-97e5-32ba55230155-c000.csv
	
	hduser@localhost:~$ hdfs dfs -cat scala-ppkm-df/part-00094-11e193a4-bb44-4fd9-894c-3707fc2bec8a-c000.csv
	amankan,2
	"\"Ingat",1
	Covid_19,1
	#banten,1
	|,1
	Anggaran,1
	tuhanku,1
	aja,12
	sekitarnya,1
	d,5
	Imbas,1
	hebih,1
	biarkan,2
	negaranya,1
	aktivitas,1


### Модифицируем приложение WordCount на Scala согласно заданиям №3, №4 и №5
- Измените WordCount так, чтобы он удалял знаки препинания и приводил все слова к единому регистру
- Добавьте в WordCount возможность через конфигурацию задать список стоп-слов, которые будут отфильтрованы во время работы приложения
- Измените выход WordCount так, чтобы сортировка была по количеству повторений, а список слов был во втором столбце, а не в первом
	
Результат модификации - https://github.com/bostspb/jvm_hadoop/blob/master/lesson03/SparkDFScala/src/main/scala/WordCount.scala	
	
Собираем получившееся приложение, кладем в контейнер с Hadoop и запускаем
	
	spark-submit --class WordCount --master yarn --deploy-mode cluster sparkdfscala_2.11-0.1.jar /user/hduser/ppkm /user/hduser/scala-ppkm-df

Смотрим результат	
	
	hduser@localhost:~$ hdfs dfs -ls scala-ppkm-df
	Found 2 items
	-rw-r--r--   1 hduser supergroup          0 2021-10-06 07:09 scala-ppkm-df/_SUCCESS
	-rw-r--r--   1 hduser supergroup      18416 2021-10-06 07:09 scala-ppkm-df/part-00000-e55ff852-071a-4c54-93e1-03b362f300fb-c000.csv
	
	hduser@localhost:~$ hdfs dfs -cat scala-ppkm-df/part-00000-e55ff852-071a-4c54-93e1-03b362f300fb-c000.csv
	134,ppkm
	110,mikro
	105,positif
	100,negatif
	100,netral
	87,dan
	77,di
	68,""
	65,masyarakat
	59,yg
	59,ppkmmikro
	56,covid-19
	44,kegiatan
	43,pembatasan
	42,perpanjangan
	33,rt
	31,ada
	31,berbasis
	29,covid
	29,yang
	28,maret
	27,untuk
	25,@humas_jogja
	25,jogjaistimewa
	22,pak

	
P.S.
- Для удобства просмотра результатов я поменял дефолтное значение параметра `spark.sql.shuffle.partitions` на `1`, чтобы на выходе был один файл.
- Задание №4 не успел сделать, но теоритически предствляю как должно быть - в файл с конфигом 
  кладем стоп-слова и подгружаем его при запуске команды `spark-submit`, 
  а внутри приложения извлекаем из файла стоп-слова и используем их при трансформации дата-фрейма. 
  Технические возможности описаны здесь - https://stackoverflow.com/questions/43024766/what-are-sparksession-config-options.