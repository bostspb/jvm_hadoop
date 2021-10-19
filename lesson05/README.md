## Урок 5. Интерактивный анализ данных в Apache Zeppelin
1. Установите Apache Zeppelin и настройте интеграцию с Apache Spark или Apache Hive
2. Скачайте датасет [Video Game Sales](https://www.kaggle.com/gregorut/videogamesales)
3. Выведите самую продаваемую игру за всё время
4. Какая платформа самая популярная в каждом регионе (NA, EU, JP)?
5. Какой жанр популярен больше всего в каждом регионе (NA, EU, JP)?
6. Выведите самый популярный жанр на каждый год

Запускаем контейнер с Hadoop и Spark, собранный на прошлых уроках.

	docker start gbhdp
	docker exec -it gbhdp bash

Перекидываем датасет в контейнер с Hadoop:

	docker cp ./dataset/vgsales.csv gbhdp:/home/hduser/

Заходим в консоль в контейнере с Hadoop и перекидываем датасет на HDFS, предварительно создав директорию:

    hdfs dfs -mkdir /user/hduser/videogamesales
    hdfs dfs -put vgsales.csv /user/hduser/videogamesales/  

Успешно устанавливаем Hive по [инструкции](hive_install.md), запускаем Hive Server.

Успешно устанавливаем по [инструкции](zeppelin_install.md) Zeppelin и запускаем его, настраиваем интерпретаторы Spark и Hive.

Решаем в Zeppelin задачи 3 - 6: [код и результаты](zeppelin_notebook.pdf) 