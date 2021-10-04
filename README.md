# GB: JVM в экосистеме Hadoop
> **Geek University Data Engineering**

`Hadoop` `Java` `Scala` `Spark`

### Урок 1. JVM, Apache Hadoop и Scala
Разбираем как работает Java, учимся разворачивать кластер
Hadoop, пишем MapReduce приложение на Java, изучаем основы Scala.

**Задание** <br>
1. Что означает *Stop-the-World* в терминах *JVM*?
2. Разверните кластер *Hadoop*, соберите *WordCount* приложение, 
   запустите на датасете [ppkm_sentiment](https://www.kaggle.com/mochkholil/ppkm-sentiment/) 
   и выведите 10 самых редких слов
3. Перепишите *WordCount* на Scala
4. Измените маппер в *WordCount* так, чтобы он удалял знаки препинания и приводил все слова к единому регистру

**Решение** <br>
- [Подробное описание хода выполнения задания](https://github.com/bostspb/jvm_hadoop/blob/master/lesson01/README.md)


### Урок 2. Apache Spark DSL
Обзор, Архитектура, Spark Core, RDD

**Задание** <br>
1. Какие плюсы и недостатки у *Merge Sort Join* в отличии от *Hash Join*?
2. Соберите *WordCount* приложение, запустите на датасете `ppkm_sentiment`
3. Измените *WordCount* так, чтобы он удалял знаки препинания и приводил все слова к единому регистру
4. Измените выход *WordCount* так, чтобы сортировка была по количеству повторений, а список слов был во втором столбце, а не в первом
5. Измените выход *WordCount*, чтобы формат соответствовал `СSV`

**Решение** <br>
- [Подробное описание хода выполнения задания](https://github.com/bostspb/jvm_hadoop/blob/master/lesson02/README.md)
