Untuk melakukan develop aplikasi ini, hal yang perlu dipersiapkan adalah:

1. JDK dan JRE ter-install dengan baik. Untuk keseragaman oleh pembuat, gunakan versi ini: https://learn.microsoft.com/en-us/java/openjdk/install. Pastikan hasil nya sama dengan mengetikkan perintah-perintah ini di terminal: 
```
>> java --version
   JRE:
        openjdk 17.0.7 2023-04-18 LTS                                                                                    
        OpenJDK Runtime Environment Microsoft-7626293 (build 17.0.7+7-LTS)                      
        OpenJDK 64-Bit Server VM Microsoft-7626293 (build 17.0.7+7-LTS, mixed mode)     
>> javac --version
   JDK: 
        javac 17.0.7 
```
2. IDE yang digunakan adalah Netbeans: Apache NetBeans IDE 17. `https://netbeans.apache.org/download/nb17/`
3. Fork dan Clone repository ini, referensi: `https://blog.devgenius.io/how-to-fork-a-repository-and-push-and-pull-with-github-48b296b2b623`
4. Setelah berhasil clone, open project menggunakan netbeans,
5. Pada window `files`, masuk ke folder `src/main/resources`, 
6. Lalu rename application.example.properties menjadi application.properties, kemudian isi dengan value yang sesuai.
7. Kalau ingin mengganti logo, bisa dilakukan rename di `src/main/resources/logo.png` dan `src/main/resources/logo.png` 
8. Untuk running aplikasi ini, langsung run project saja, secara otomatis dependency akan di download.
