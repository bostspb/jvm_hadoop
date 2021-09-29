## Урок 1. JVM, Apache Hadoop и Scala
1. Что означает *Stop-the-World* в терминах **JVM**?
2. Разверните кластер **Hadoop**, соберите **WordCount** приложение, 
   запустите на датасете [ppkm_sentiment](https://www.kaggle.com/mochkholil/ppkm-sentiment/) 
   и выведите 10 самых редких слов
3. Перепишите **WordCount** на **Scala**
4. Измените маппер в **WordCount** так, чтобы он удалял знаки препинания и приводил все слова к единому регистру

### *Stop-the-World* в терминах JVM
*Stop-the-world* означает что приложение полностью остановлено и работает сборка мусора.

### Разворачиваем кластер Hadoop
Docker уже уставлен: Docker Desctop 4.0.1, Win10, WSL 2.

Cкачиваем [докер-файлы](https://drive.google.com/drive/folders/1o4YmC7fuPZ0FKDIzA1MehuQZdIgdeX-D) для развертывания Hadoop.

Собираем образ Hadoop:

    docker build -t img-hdp-hadoop

Запускаем контейнер:

    docker run -it --name gbhdp -p 50090:50090 -p 50075:50075 -p 50070:50070 -p 8042:8042 -p 8088:8088 -p 8888:8888 -p 4040:4040 -p 4044:4044 --hostname localhost img-hdp-hadoop

Скачиваем датасет [ppkm_sentiment](https://www.kaggle.com/mochkholil/ppkm-sentiment/) и закидываем в контейнер:

    docker cp ppkm_dataset.csv gbhdp:/home/hduser

Заходим в консоль в контейрене с Hadoop и перекидываем датасет на HDFS, предварительно создав директорию:

    hdfs dfs -mkdir /user/hduser/ppkm
    hdfs dfs -put ppkm_dataset.csv /user/hduser/ppkm/

Проверяем наличие файла на HDFS

    hduser@localhost:~$ hdfs dfs -cat /user/hduser/ppkm/ppkm_dataset.csv | head
    class,comment
    positif,Kami siap laksanakan Instruksi pak
    positif,Siap melaksanakan intruksi pak
    positif,Siap dukung dan sukseskan
    positif,Langkah 3M ini sudah sukses di Bali memutus penyebaran covid pak presiden
    positif,Siap amankan seluruh kebijakan kementerian Desa PDTT
    positif,Siap utk di sosialisasikan ke setiap TPP yg ada di wilayah kabupaten Boltim
    positif,Mendukung kebijakan Gus Menteri dalam upaya pencapaian SDGs Desa 3 yaitu Desa Sehat dan Sejahtera
    positif,Mari bersama cegah penyebaran covid-19
    positif,Mari kita sukseskan ppkm di masyarakat
    cat: Unable to write to output stream.

### Собираем приложение WordCount
Создаем [проект MapReduce]() со сборщиком Gradle и создаем там приложение WordCount на основе исходников с урока.

Подтягиваем зависимости в Gradle и запускаем сборку:

    ./gradlew build

Закидываем джарник в контейнер с Hadoop:

    docker cp build/libs/MapReduce-1.0-SNAPSHOT.jar gbhdp:/home/hduser/

Из консоли в контейнере с Hadoop запускаем приложение:

    hadoop jar MapReduce-1.0-SNAPSHOT.jar WordCount -Dwordcount.input=/user/hduser/ppkm -Dwordcount.output=/user/hduser/ppkm_out

Проверяем результирующие файлы:

    hduser@localhost:~$ hdfs dfs -ls /user/hduser
    Found 2 items
    drwxr-xr-x   - hduser supergroup          0 2021-09-28 04:39 /user/hduser/ppkm
    drwxr-xr-x   - hduser supergroup          0 2021-09-28 06:06 /user/hduser/ppkm_out
    hduser@localhost:~$ hdfs dfs -ls /user/hduser/ppkm_out
    Found 3 items
    -rw-r--r--   1 hduser supergroup          0 2021-09-28 06:06 /user/hduser/ppkm_out/_SUCCESS
    -rw-r--r--   1 hduser supergroup        302 2021-09-28 06:06 /user/hduser/ppkm_out/part-r-00000
    -rw-r--r--   1 hduser supergroup      23972 2021-09-28 06:06 /user/hduser/ppkm_out/part-r-00001
    hduser@localhost:~$ hdfs dfs -cat /user/hduser/ppkm_out/part-r-00001 | head
    ""kalian        1
    ""peraturan     1
    ""pilihan       1
    #adaptasikebiasaambaru  1
    #akbphandonosubiakto01  5
    #anggaran"      1
    #aniesbaswedan  1
    #astagfiraullah 1
    #atasi  1
    #ayojogoblitar  1
    hduser@localhost:~$ hdfs dfs -cat /user/hduser/ppkm_out/part-r-00000 | head
    41
    !       1
    ""      2
    &       1
    (       1
    ,       4
    -       3
    .       6
    ..      3
    0       1

Посмотрим 10 самых редких слов длиной более двух символов:
    
    hduser@localhost:~$ hdfs dfs -cat /user/hduser/ppkm_out/part-r-00001 | sort -nk2r | head
    (mencuci        9
    buat    9
    jarak)" 9
    kecil   9
    kepada  9
    kok     9
    makan   9
    mari    9
    netral,"pemberlakuan    9
    posko   9
    

### Собираем приложение *WordCount* на Scala
Устанавливаем в IDE плагин Scala.

Портируем приложение WordCount с Java [на Scala]() на основе исходников с урока и подтягиваем зависимости.

Изменяем маппер, чтобы он удалял знаки препинания и приводил все слова к нижнему регистру.

Потом заходим в **sbt shell** и запускаем сборку командой `assembly`.

Закидываем джарник в контейнер с Hadoop:

    docker cp target/scala-2.13/ScalaMapReduce-assembly-0.2.jar gbhdp:/home/hduser/    

Из консоли в контейнере с Hadoop запускаем приложение:
    
    hadoop jar ScalaMapReduce-assembly-0.2.jar -Dswap.input=/user/hduser/ppkm -Dswap.output=/user/hduser/ppkm_swap

Проверяем результат:

    hduser@localhost:~$ hdfs dfs -ls /user/hduser/ppkm_swap
    Found 3 items
    -rw-r--r--   1 hduser supergroup          0 2021-09-29 07:01 /user/hduser/ppkm_swap/_SUCCESS
    -rw-r--r--   1 hduser supergroup       1515 2021-09-29 07:01 /user/hduser/ppkm_swap/part-r-00000
    -rw-r--r--   1 hduser supergroup      49465 2021-09-29 07:01 /user/hduser/ppkm_swap/part-r-00001
    
    hduser@localhost:~$ hdfs dfs -cat /user/hduser/ppkm_swap/part-r-00001 | head
    *padahal        1
    *padahal        1
    -_-     1
    09032021        1
    101     1
    11321@yugo_bambang      1
    12321humas_poldajateng  1
    14jt    1
    19kamis 1
    2019madiuninfo  1
    cat: Unable to write to output stream.

Как видим, результат печальный - суммирование слов не отработало


hdfs dfs -rm /user/hduser/ppkm_out/*