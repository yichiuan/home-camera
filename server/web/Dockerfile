FROM python:3.5.2

ENV PYTHONUNBUFFERED 1

RUN mkdir /web
WORKDIR /web

ADD requirements.txt /web
RUN pip3 install -r requirements.txt

ENV FLASK_APP app/wsgi.py

VOLUME ["/web"]

EXPOSE 5000
