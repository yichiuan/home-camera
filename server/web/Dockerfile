FROM python:3.5.2

ENV PYTHONUNBUFFERED 1

RUN mkdir /web
WORKDIR /web

ADD requirements.txt /web
RUN pip3 install -r requirements.txt

VOLUME ["/web"]

# Expose ports.
EXPOSE 5000
