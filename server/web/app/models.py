import time

from flask import current_app, abort
from itsdangerous import TimedJSONWebSignatureSerializer as Serializer, BadSignature, SignatureExpired
from werkzeug.security import generate_password_hash, check_password_hash

from . import db
from .exceptions import ConfirmationError


class User(db.Model):
    """The User model."""
    __tablename__ = 'users'
    id = db.Column(db.Integer, primary_key=True)
    email = db.Column(db.String(64), unique=True, index=True)
    username = db.Column(db.String(32), nullable=False, unique=True, index=True)
    password_hash = db.Column(db.String(256), nullable=False)
    created_at = db.Column(db.Integer, default=int(time.time()))
    confirmed = db.Column(db.Boolean, default=False)

    @property
    def password(self):
        raise AttributeError('password is not a readable attribute')

    @password.setter
    def password(self, password):
        self.password_hash = generate_password_hash(password)

    def verify_password(self, password):
        return check_password_hash(self.password_hash, password)

    def generate_auth_token(self, expiration=None):
        expiration = expiration or current_app.config['TOKEN_EXPIRES_IN']
        serializer = Serializer(current_app.config['SECRET_KEY'], expires_in=expiration)
        return serializer.dumps({'username': self.username}).decode('utf-8')

    @staticmethod
    def verify_auth_token(token):
        s = Serializer(current_app.config['SECRET_KEY'])
        try:
            data = s.loads(token)
        except SignatureExpired:
            return None # valid token, but expired
        except BadSignature:
            return None # invalid token

        return User.query.filter_by(username=data['username']).first()

    def generate_confirmation_token(self, expiration=3600):
        s = Serializer(current_app.config['SECRET_KEY'], expiration)
        return s.dumps({'confirm': self.id}).decode('utf-8')

    @staticmethod
    def get_user_id_from_confirm(token):
        s = Serializer(current_app.config['SECRET_KEY'])
        try:
            data = s.loads(token)
        except:
            raise ConfirmationError('Email confirm error')

        return data.get('confirm')

    @staticmethod
    def create(data):
        """Create a new user."""
        user = User()
        user.from_dict(data, partial_update=False)
        return user

    def from_dict(self, data, partial_update=True):
        """Import user data from a dictionary."""
        for field in ['username']:
            try:
                setattr(self, field, data[field])
            except KeyError:
                if not partial_update:
                    abort(400)

    def to_dict(self):
        """Export user to a dictionary."""
        return {
            'id': self.id,
            'username': self.username,
            'email': self.email
        }
        
    @staticmethod
    def create_user(email, username, password):
        user = User(email=email,
                    username=username,
                    password=password)
        user.confirmed = True
        db.session.add(user)
        db.session.commit()