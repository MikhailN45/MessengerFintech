# TFS Android Spring 2024

#### Особенности работы:

- Реализованы личные сообщения: по тапу на юзера во вкладке с пользователями открывается личка, а по лонгтапу на иконку поиска на экране стримов открывается чат с самим собой.
- Архитектура реализована через подход MVI. Структуру папок проекта решил не разбивать по фичам, классическое data/domain/presentation мне показалось более читаемым и удобным.
- На этапе выполнения домашнего задания с поиском я решил сделать поиск не только по стримам, но и по топикам. Если в стриме есть топики с искомой последовательностью стрим раскрывается и отображает их при поиске, можно сразу перейти в топик и общаться.
- Важный нюанс, связанный с поиском по топикам заключается в том что на момент его добавления я не подозревал что стримов станет так много, а лимит API у нас ограничен 200 запросами с довольно ощутимым кулдауном. Чтобы работал поиск по топикам мы должны сразу знать их все, соответственно для каждого стрима надо их запросить, а стримов уже как раз под 200 штук. Поэтому в теории (я не знаю сколько еще стримов будет создано) единичные стримы могут не показывать свои топики, т.к. при загрузке топиков для них мы получили ошибку 429 Too many requests.
- Мы обсудили этот момент с ментором и решили оставить функционал поиска по топикам, т.к. функционал реализован хорошо и он полезен.
- Я отключил в приложении счетчик сообщений по той же причине. API зулипа не позволяет напрямую получить количество сообщений для топика. Единственный вариант это запросить все сообщения топика и посчитать их размер. Но это еще 200 запросов, а мы ограничены лимитом API. Да и просто нецелесообразно сначала грузить все сообщения для топика только ради счетчика, потом их удалять и грузить еще раз но порциями, реализуя пагинацию. Можно конечно получать количество сообщений долистав чат до конца и сохранив счетчик, но это была бы очень странная и сомнительная по полезности реализация. Решили с ментором этот функционал убрать.
