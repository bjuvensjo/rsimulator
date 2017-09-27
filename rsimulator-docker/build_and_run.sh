cd rsimulator
mvn package
cd ..

docker build -t rsimulator .
./run.sh
