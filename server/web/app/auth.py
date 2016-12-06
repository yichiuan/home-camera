from flask import Blueprint, g, jsonify, request, current_app, url_for
from flask_httpauth import HTTPBasicAuth, HTTPTokenAuth

import sendgrid
from sendgrid.helpers.mail import *

from . import db
from .models import User
from .exceptions import ValidationError, ConfirmationError

auth = Blueprint('auth', __name__)
basic_auth = HTTPBasicAuth()
token_auth = HTTPTokenAuth('Bearer')


@auth.route("/token")
@basic_auth.login_required
def get_token():
    token = g.current_user.generate_auth_token()
    return jsonify(token=token)


@basic_auth.verify_password
def verify_password(username, password):
    if not username or not password:
        return False

    user = User.query.filter_by(username=username).first()
    if user is None or not user.verify_password(password):
        return False

    g.current_user = user
    return True


@basic_auth.error_handler
def password_error():
    """Return a 401 error to the client."""
    # To avoid login prompts in the browser, use the "Bearer" realm.
    return (jsonify({'error': 'authentication required'}), 401,
            {'WWW-Authenticate': 'Bearer realm="Authentication Required"'})


@token_auth.verify_token
def verify_token(token, add_to_session=False):

    if not token:
        return False

    user = User.verify_auth_token(token)
    if not user:
        return False

    g.current_user = user
    return True


@token_auth.error_handler
def token_error():
    """Return a 401 error to the client."""
    return (jsonify({'error': 'authentication required'}), 401,
            {'WWW-Authenticate': 'Bearer realm="Authentication Required"'})


@auth.route('/register', methods=['POST'])
def register():

    email = request.form['email']
    username = request.form['username']
    password = request.form['password']

    if User.query.filter_by(email=email).first():
        raise ValidationError('Email already registered.')

    if User.query.filter_by(username=username).first():
        raise ValidationError('Username already in use.')

    user = User(email=email,
                username=username,
                password=password)
    db.session.add(user)
    db.session.commit()

    token = user.generate_confirmation_token()
    send_email(user.email, token)
    return jsonify({'message': 'Please confirm email'})


@auth.errorhandler(ValidationError)
def validation_error(e):
    return jsonify({'error': 'bad request', 'message': e.args[0]}), 400


@auth.route('/confirm/<token>')
def confirm_email(token):

    user_id = User.get_user_id_from_confirm(token)
    user = User.query.get(user_id)
    if user and user.id == user_id:
        if user.confirmed:
            raise ConfirmationError('Email already confirmed.')

        user.confirmed = True
        db.session.add(user)
        db.session.commit()
    else:
        raise ConfirmationError('Email confirm error')

    return 'You have confirmed your account. Thanks!'


@auth.errorhandler(ConfirmationError)
def confirmation_error(e):
    return e.args[0], 400


def send_email(email, token):
    sg = sendgrid.SendGridAPIClient(apikey=current_app.config['SENDGRID_API_KEY'])
    from_email = Email(current_app.config['SENDGRID_DEFAULT_FROM'])
    subject = "Welcome To Flask-Boilerplate! Confirm Your Email"
    to_email = Email(email)
    content = Content("text/html", "Hello, Email!")

    url = url_for('auth.confirm_email', token=token, _external=True)

    mail = Mail(from_email, subject, to_email, content)
    mail.personalizations[0].add_substitution(Substitution("{confirm}", url))
    mail.set_template_id(current_app.config['SENDGRID_TEMPLATE_ID'])

    response = sg.client.mail.send.post(request_body=mail.get())
    return response
