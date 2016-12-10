#!/usr/bin/env python

import os

import click

from app import create_app

application = app = create_app(os.environ.get('FLASK_CONFIG', 'production'))


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
