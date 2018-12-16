cd  core
call mvn clean
call mvn eclipse:clean
cd..

cd demo

cd myfattest
call mvn clean
call mvn eclipse:clean
cd..

cd myfat-springboot
call mvn clean
call mvn eclipse:clean
cd..

cd myfattest
call del maven-eclipse.xml 
cd .externalToolBuilders
call del *.launch
cd .. 
call rd .externalToolBuilders
cd ..

cd myfat-springboot
call del maven-eclipse.xml 
cd .externalToolBuilders
call del *.launch
cd .. 
call rd .externalToolBuilders
cd ..

