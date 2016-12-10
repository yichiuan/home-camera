# Flask

## Initialize

1. `docker-compose build`
2. `docker-compose run web flask create_db`

## Run development server

```bash
export FLASK_APP=app/wsgi.py 
export FLASK_CONFIG=development
export SENDGRID_API_KEY=<your sendgrid api key>
```

`flask create_db` : create database

`flask run` : run local server

## user commands

`flask user new` : create new user
