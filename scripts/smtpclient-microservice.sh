#!/bin/sh
git clone https://github.com/lcarnevale/smtpclient-microservice.git src/smtpclient-microservice
cd src/smtpclient-microservice
docker build -t lcarnevale/smtpclient-microservice .

cat << EOF > app/conf.yaml
username: YOUR_GMAIL_ACCOUNT
password: YOUR_APPLICATION_PASSWORD

sent_from: SENDER_MAIL_ADDRESS
EOF

docker run -d --rm -p 5000:5000 --name smtpclient-microservice lcarnevale/smtpclient-microservice