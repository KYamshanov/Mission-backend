<html lang="ru">
<head>
    <meta charset="utf-8"/>
    <link rel="stylesheet" href="css/style.css"/>
    <title>Mission ID</title>
</head>
<body class="body">
<div class="main-container">
    <div class="wrapper">
        <div class="pic" style="background-image: url(images/app-icon.svg);"></div>
        <span class="text">Mission ID</span></div>

    <#if form_visible>
        <form method="post" action="/login">

            <div class="wrapper-2">
                <div class="section"><span class="text-2">Логин</span><span
                            class="text-3">mail@gmail.ru</span></div>
                <div class="group"><span class="text-4">Пароль</span><span
                            class="text-5">*********</span></div>
                <input name="_csrf" type="hidden" value="${csrf_token}"/>

                <div class="section-2">
                    <div class="group-2">
                        <div class="group-3">
                            <label class="text-6">
                                <button class="text-wrapper-4" type="submit">Войти</button>
                            </label>
                        </div>
                    </div>
                </div>
            </div>
        </form>
    </#if>
    <#if social_services_visible>
        <div class="section-3">
            <div class="section-4"><span class="text-7"
                                         data-node-id="509:67__6">Sing-in</span><span
                        class="text-8" data-node-id="509:67__0"> </span><span class="text-9" data-node-id="509:67__7">by social service</span>
            </div>
            <form method="post" action="/auth/github">
                <input name="_csrf" type="hidden" value="${csrf_token}"/>

                <input type="image" class="img" src="images/github-logo.svg" alt="github"/>
                <span class="text-a">GitHub</span>
            </form>
        </div>
    </#if>
</div>
</body>
</html>