## Урок 4. Введение в машинное обучение на Apache Spark ML
1. Проведите анализ тональности датасета [IMDB](https://www.kaggle.com/columbine/imdb-dataset-sentiment-analysis-in-csv-format): 
   обучите модель на Train.csv, после чего проверьте её на Valid.csv
2. Посчитайте получившуюся точность (accuracy — количество правильных предсказаний от общего количества ответов) модели. 
   Ваш код должен использовать ML Pipelines и использовать трансформеры для подготовки данных к обучению


Создаем и собираем проект **SentimentAnalysis]**

Запускаем контейнер с Hadoop и Spark, собранный на прошлых уроках.

	docker start gbhdp
	docker exec -it gbhdp bash

Перекидываем дата сет и JAR-файл в контейнер с Hadoop:

	docker cp ../dataset/Train.csv gbhdp:/home/hduser/
	docker cp ../dataset/Valid.csv gbhdp:/home/hduser/	
	docker cp target/scala-2.11/mlsentimentanalysis_2.11-0.1.jar gbhdp:/home/hduser/

Заходим в консоль в контейрене с Hadoop и перекидываем датасет на HDFS, предварительно создав директорию:

    hdfs dfs -mkdir /user/hduser/imdb
    hdfs dfs -put Train.csv /user/hduser/imdb/
	hdfs dfs -put Valid.csv /user/hduser/imdb/

	
И запускаем приложение
	
	spark-submit --class SentimentAnalysis --master yarn --deploy-mode cluster mlsentimentanalysis_2.11-0.1.jar /user/hduser/imdb/Train.csv /user/hduser/imdb/Valid.csv

Приложение успешно отработало, переходим на веб-интерфейс Hadoop по трекинг-ссылке, которая была выведена в результате работы нашего приложения - http://localhost:8088/proxy/application_1633843974567_0018/. На странице переходим в логи и выбираем лог `stdout`.

	+--------------------+-----+--------------------+--------------------+--------------------+--------------------+--------------------+----------+
	|                text|label|               words|            filtered|            features|       rawPrediction|         probability|prediction|
	+--------------------+-----+--------------------+--------------------+--------------------+--------------------+--------------------+----------+
	|It's been about 1...|    0|[it's, been, abou...|[14, years, since...|(1000,[3,8,9,11,2...|[4.57864695047230...|[0.98983559497959...|       0.0|
	|someone needed to...|    0|[someone, needed,...|[someone, needed,...|(1000,[66,80,85,1...|[-1.1152389368963...|[0.24689547941899...|       1.0|
	|The Guidelines st...|    0|[the, guidelines,...|[guidelines, stat...|(1000,[23,44,84,1...|[2.08634221489135...|[0.88956861006663...|       0.0|
	|This movie is a m...|    0|[this, movie, is,...|[movie, muddled, ...|(1000,[3,5,17,51,...|[0.81518227569232...|[0.69321271505236...|       0.0|
	|Before Stan Laure...|    0|[before, stan, la...|[stan, laurel, be...|(1000,[3,6,12,17,...|[-1.7176275880351...|[0.15217699774195...|       1.0|
	|This is the best ...|    1|[this, is, the, b...|[best, movie, eve...|(1000,[20,30,62,6...|[0.14225347603530...|[0.53550351827310...|       0.0|
	|The morbid Cathol...|    1|[the, morbid, cat...|[morbid, catholic...|(1000,[2,3,5,6,18...|[-0.0166974895229...|[0.49582572460328...|       1.0|
	|"Semana Santa" or...|    0|["semana, santa",...|["semana, santa",...|(1000,[47,51,58,6...|[2.64187801759194...|[0.93350862842834...|       0.0|
	|Somebody mastered...|    1|[somebody, master...|[somebody, master...|(1000,[10,30,40,7...|[-1.7625787992906...|[0.14646765750321...|       1.0|
	|Why did I waste 1...|    0|[why, did, i, was...|[waste, 1.5, hour...|(1000,[3,5,13,23,...|[0.95524988033527...|[0.72216974477412...|       0.0|
	|This film takes y...|    1|[this, film, take...|[film, takes, one...|(1000,[3,44,115,1...|[-0.2558853321347...|[0.43637545235011...|       1.0|
	|The Russian space...|    0|[the, russian, sp...|[russian, space, ...|(1000,[1,3,18,19,...|[1.96442855808902...|[0.87701142535756...|       0.0|
	|the more i think ...|    0|[the, more, i, th...|[think, it,, noth...|(1000,[2,3,18,29,...|[3.83955438450181...|[0.97894947196087...|       0.0|
	|This is very date...|    1|[this, is, very, ...|[dated,, part, ch...|(1000,[4,7,29,32,...|[-1.4414398827643...|[0.19132247349366...|       1.0|
	|I had seen 'Kalif...|    1|[i, had, seen, 'k...|[seen, 'kaliforni...|(1000,[4,13,15,44...|[-3.9410874129250...|[0.01905685891500...|       1.0|
	|A powerful adapta...|    1|[a, powerful, ada...|[powerful, adapta...|(1000,[3,19,25,37...|[-1.9145973123454...|[0.12846524841607...|       1.0|
	|This movie's orig...|    1|[this, movie's, o...|[movie's, origins...|(1000,[0,2,3,4,5,...|[4.43434185918018...|[0.98827620706215...|       0.0|
	|I really enjoyed ...|    0|[i, really, enjoy...|[really, enjoyed,...|(1000,[3,19,44,50...|[0.16223842795557...|[0.54047087537745...|       0.0|
	|Hi, Everyone, Oh,...|    0|[hi,, everyone,, ...|[hi,, everyone,, ...|(1000,[21,23,44,5...|[1.65339904510892...|[0.83934991207010...|       0.0|
	|It takes a while ...|    1|[it, takes, a, wh...|[takes, get, adju...|(1000,[36,68,78,8...|[0.25727716364009...|[0.56396684090976...|       0.0|
	+--------------------+-----+--------------------+--------------------+--------------------+--------------------+--------------------+----------+
	only showing top 20 rows

	+--------+
	|accuracy|
	+--------+
	|  0.7624|
	+--------+

Собственно, получившаяся точность предсказания получилась **76%**