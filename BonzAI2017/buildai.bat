del bin\CompetitorAI.class
del bonzai2017\ais\CompetitorAI.jar

rmdir tmp /s /q

mkdir tmp
copy src\CompetitorAI.java tmp\
cd tmp

javac -cp ".;../bin/" CompetitorAI.java
jar cf CompetitorAI.jar CompetitorAI*.class

copy CompetitorAI.jar ..\bonzai2017\ais\

cd ..
rmdir tmp /s /q

