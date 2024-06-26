# TFS Android Spring 2024

#### Общий стек:
- Room, Retrofit, Dagger, RxJava, CustomView, Firebase, Kaspresso, Mockito, Glide, MVI.
  
<details> 
  <summary> 
    <mark style="background-color: lightblue">
      Screenshots 
    </mark> 
  </summary> 
<img src="https://github.com/MikhailN45/MessengerFintech/assets/53788038/92f5896d-03a4-4bc7-8c7f-1586b9f2526a" width="250">
<img src="https://github.com/MikhailN45/MessengerFintech/assets/53788038/6a59756b-74da-40f2-8907-2b5da9e27492" width="250">
<img src="https://github.com/MikhailN45/MessengerFintech/assets/53788038/1da762c7-55e6-4db9-959b-929bb3681db5" width="250">
<img src="https://github.com/MikhailN45/MessengerFintech/assets/53788038/25459aba-3d15-4a0a-a3bd-5fad4dcfb532" width="250">
<img src="https://github.com/MikhailN45/MessengerFintech/assets/53788038/82cfa478-1dbe-4bca-a16f-66a747c5b31f" width="250">
<img src="https://github.com/MikhailN45/MessengerFintech/assets/53788038/76e079ab-ed2a-407a-8894-8be8e1e380a1" width="250">
<img src="https://github.com/MikhailN45/MessengerFintech/assets/53788038/dafb4aa9-f1c1-488b-b62a-890c94e8f51c" width="250">
</details>

<details> 
  <summary>
     <mark style="background-color: lightblue">
     Demo Video
    </mark>    
  </summary> 
[Demo video](https://github.com/MikhailN45/MessengerFintech/assets/53788038/5ac18eb9-48cd-433a-8c44-9c3efd739b69)
</details>

#### Особенности:
- Приложение представляет собой мессенджер на открытом Zulip API и реализовано в ходе стажировки в T-Банке (ex. Тинькофф Банк).
- Чаты сгруппированы по раскрывающимся стримам, для отображения списка используется адаптер с разными ViewType.
- Реализован поиск на экране чатов, если запрос соответствует каналу, то отображается канал, если топику в канале - он раскрывается, отображая топик, в который сразу можно перейти.
- Реализованы личные сообщения: по лонгтапу на иконку поиска на экране стримов открывается чат с самим собой, по тапу на юзера во вкладке с пользователями открывается личка. 
- На экране с пользователями реализован поиск и функционал личных сообщений, можно видеть статус и данные юзера.
- Экран профиля отображает собственные данные и статус.
- Экран чата позволяет отправлять сообщения, ставить реакции, сообщения в чате разделены по датам с помощью ItemDecoraton. Список сообщений кешируется, реализована пагинация. 
- Вью сообщения это CustomView с FlexBox для гибкого отображения и менеджмента реакций.
- Реакция устанавливается через BottomSheetDialog со списком смайликов по лонгтапу на сообщение или иконку "+".
- Навигация на фрагментах через BottomNavigationView.
- Многопоточность реализована с помощью RxJava.
- Все данные отображаются по принципу Cache-First, без доступа к сети отображаются закешированные данные.
- Многомодульность реализована на Dagger, используются сабкомпоненты и скоупы.
- Бизнес-логика и UI покрыты тестами Happy-Path, Integration, UI-тестами. Используются Kaspresso, Mockito.
- Архитектура реализована через подход MVI. Соблюдаются принципы Clean Architecture, UDF.
- Дизайн сделан по мокам в фигме, при загрузке отображаются шиммеры, прогрессбары и плейсхолдеры.
- Интегрирована крашлитика и аналитика Firebase.
- Данные между сессиями кешируются, если нет сети то пользователь оповещается об ее отсутствии и восстановлении.
- Для запуска необходимо передать в AuthInterceptor Email и APIKey.



