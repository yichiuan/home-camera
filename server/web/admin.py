#!/usr/bin/env python

import os

import click

from app import create_app, db

application = app = create_app(os.environ.get('FLASK_CONFIG', 'production'))


@app.cli.command()
def create_db():
    database_uri = app.config['SQLALCHEMY_DATABASE_URI']

    if database_uri.startswith('sqlite:///'):
        dir_path = os.path.dirname(database_uri[len('sqlite:///'):])
        os.makedirs(dir_path, exist_ok=True)

    db.create_all()


@app.cli.group()
def user():
    pass


@user.command()
def new():
    """Create new user
    """

    email = click.prompt('Email', type=str)
    username = click.prompt('Username', type=str)
    password = click.prompt('Password', type=str)

    from app.models import User
    User.create_user(email, username, password)
