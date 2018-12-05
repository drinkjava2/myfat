cd  core
call mvn clean
call mvn eclipse:clean
cd..

cd myfattest
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
