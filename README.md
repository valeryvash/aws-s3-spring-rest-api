Install on server:

install ubuntu server

https://cloud.yandex.ru/docs/compute/operations/vm-connect/ssh
create a Ed25519 key pair as described for the link above

connect to the server by ssh via putty with keys you created above

connect via putty
install
mysql
we will create the db at the same server tool. use "mysql -u root -p" to open mysql
openjdk-17-jre-headless
we no need monitor,mouse keyboard features of jvm
git
we need download our project from github
maven
run the project
mc


connect to database via idea/workbech/datagrip
tip: I cannot connect to mysql via mysql workbech ce due to ssh key feature (some people said about passphrase).
Alternate successfull connection for human interface is DBeaver. Some features shall be added to the driver properties: allowPublicKey=true and useSSL=false.
datagrip works fine


add file "*.sh" to the "/etc/profile.d" folder with content
tip: there is no difference between . and underscore. In code I use dots ".", at server tool i use underscores "_"
"""
export vash_access_key=
export vash_bucket_name="my-first-backet"
export vash_datasource_driver="com.mysql.cj.jdbc.Driver"
export vash_datasource_password="1"
export vash_datasource_url="jdbc:mysql://localhost:3306/my-aws-s3-rest-spring"
export vash_datasource_username="root"
export vash_jwt_token_expired="3600"
export vash_jwt_token_secret=
export vash_region_static="ru-central1"
export vash_secret_key=
export vash_service_endpoint=
"""

cd to project folder
mvn install
mvn spring-boot:run

send requests to server via external ip address:port via postman